package se.krka.sc2stats;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.google.common.base.Charsets;
import com.google.common.util.concurrent.RateLimiter;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.io.*;
import java.net.SocketException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class BlizzardDataSource implements AutoCloseable {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
      .registerModule(new GuavaModule())
      .registerModule(new Jdk8Module());

  private final HttpHost apiHost;

  private final CloseableHttpClient client;

  private final Region region;
  private final BlizzardOAuth blizzardOAuth;
  private final RateLimiter rateLimiter;

  public static BlizzardDataSource create(final Region region, BlizzardOAuth blizzardOAuth) throws IOException {
    return new BlizzardDataSource(region, blizzardOAuth);
  }

  public BlizzardDataSource(Region region, BlizzardOAuth blizzardOAuth) {
    this.apiHost = new HttpHost(region.getApiHost(), 443, "https");
    this.region = region;
    this.blizzardOAuth = blizzardOAuth;

    client = HttpClients.custom()
            .setMaxConnPerRoute(100)
            .setMaxConnTotal(100)
            .build();

    // 36,000 per hour -> 10 per second
    rateLimiter = RateLimiter.create(10);
  }

  public <T> T getTypedData(final Class<T> clazz, final String url, final Consumer<JSONObject> reducer) {
    return getData(url, content -> readValue(clazz, content), reducer);
  }

  private <T> T readValue(final Class<T> clazz, final Reader content) {
    try {
      return OBJECT_MAPPER.readValue(content, clazz);
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  public JSONObject getUntypedData(final String url, final Consumer<JSONObject> reducer) {
    return getData(url, content -> new JSONObject(new JSONTokener(content)), reducer);
  }

  private <T> T getData(final String url, final Function<Reader, T> fun, final Consumer<JSONObject> reducer) {
    return fun.apply(new StringReader(getRawData(url, reducer)));
  }

  public String getRawData(final String url) {
    return getRawData(url, jsonObject -> {});
  }

  public String getRawData(final String url, final Consumer<JSONObject> reducer) {
    long sleepTime = 1000;
    while (true) {
      final AccessToken accessToken = blizzardOAuth.ensureAccessToken();

      rateLimiter.acquire();

      System.out.println("Requesting " + url);

      final HttpGet request = new HttpGet(url);
      request.addHeader("Authorization", "Bearer " + accessToken.getAccessToken());
      try (final CloseableHttpResponse response = client.execute(apiHost, request)) {
        final int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode == 200) {
          try (final Reader reader = reader(response)) {
            final JSONObject obj = new JSONObject(new JSONTokener(reader));
            reducer.accept(obj);
            return obj.toString(2);
          } catch (final IOException e) {
            throw new RuntimeException(e);
          }
        } else if (statusCode == 401) {
          blizzardOAuth.refreshAccesstoken();
          // Fall through and try again
        } else if (shouldRetry(statusCode)) {
          final String payload = getResponseString(response);
          sleep(url, sleepTime, response.getStatusLine() + ": " + payload);
          sleepTime *= 2;
          // Fall through and try again
        } else if (statusCode == 500) {
          final String payload = getResponseString(response);
          if (payload.equals("Downstream Error")) {
            // This happened consistently for a specific request: /sc2/profile/2/1/8266884
            throw new DownstreamErrorException(url);
          }
          throw new RuntimeException(response.getStatusLine().toString());
        } else if (statusCode == 503) {
          final String payload = getResponseString(response);
          if (payload.equals("Service Unavailable")) {
            // This happened consistently for a specific request: /sc2/profile/2/2/2207416
            throw new DownstreamErrorException(url);
          }
          throw new RuntimeException(response.getStatusLine().toString());
        } else {
          throw new RuntimeException(response.getStatusLine().toString());
        }
      } catch (final SocketException | JSONException e) {
        if (e.getMessage().equals("Connection reset")) {
          sleep(url, sleepTime, e.getMessage());
          sleepTime *= 2;
          // Fall through and try again
        } else {
          throw new RuntimeException(e);
        }
        // Try again
      } catch (final IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private void sleep(final String url, final long sleepTime, final String error) {
    try {
      System.out.println("Got " + error + " for " + url + ", sleeping for " +
                         sleepTime);
      Thread.sleep(sleepTime);
    } catch (InterruptedException e) {
    }
  }

  private boolean shouldRetry(final int statusCode) {
    return statusCode == 429 || statusCode == 504;
  }

  private static String getResponseString(final CloseableHttpResponse response) {
    try (final Reader reader = reader(response)) {
      final StringWriter writer = new StringWriter();
      IOUtils.copy(reader, writer);
      return writer.toString();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static InputStreamReader reader(final CloseableHttpResponse response) {
    try {
      return new InputStreamReader(response.getEntity().getContent(), Charsets.UTF_8);
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void close() throws Exception {
    client.close();
  }

  public Region getRegion() {
    return region;
  }
}
