package se.krka.sc2stats;

import com.google.common.base.Charsets;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.atomic.AtomicReference;

public class BlizzardOAuth {
    private final HttpHost accessTokenHost = new HttpHost("us.battle.net", 443, "https");

    private final String clientId;
    private final String clientSecret;
    private final File accessTokenFile;

    private final AtomicReference<AccessToken> accessToken = new AtomicReference<>(new AccessToken());

    public static BlizzardOAuth create() throws IOException {
        final Config config = ConfigFactory.parseFile(new File("secrets.yaml"));
        final String clientId = config.getString("clientid");
        final String clientSecret = config.getString("clientsecret");
        return new BlizzardOAuth(clientId, clientSecret);
    }

    public BlizzardOAuth(String clientId, String clientSecret) throws IOException {
        this.clientId = clientId;
        this.clientSecret = clientSecret;

        accessTokenFile = new File("accesstoken.json");
        if (accessTokenFile.exists()) {
            accessToken.set(AccessToken.fromFile(accessTokenFile));
        }

    }

    private AccessToken getAccessToken() {
        System.out.println("Requesting new access token");
        final String auth = Base64.encodeBase64String((clientId + ":" + clientSecret).getBytes(Charsets.US_ASCII));

        final HttpPost request = new HttpPost("/oauth/token");
        request.addHeader("Authorization", "Basic " + auth);
        request.setEntity(new StringEntity("grant_type=client_credentials", ContentType.APPLICATION_FORM_URLENCODED));

        try (CloseableHttpClient client = HttpClients.custom().setMaxConnPerRoute(100).setMaxConnTotal(100).build()) {
            try (final CloseableHttpResponse response = client.execute(accessTokenHost, request)) {
                if (response.getStatusLine().getStatusCode() != 200) {
                    throw new RuntimeException(response.getStatusLine().toString());
                }

                final JSONObject object = new JSONObject(new JSONTokener(response.getEntity().getContent()));
                final String accessToken = object.getString("access_token");
                final String tokenType = object.getString("token_type");
                final long expiresIn = object.getLong("expires_in");
                final Instant expiration = Instant.now().plus(expiresIn, ChronoUnit.SECONDS);
                final AccessToken token = new AccessToken(accessToken, tokenType, expiration);
                token.toFile(accessTokenFile);
                return token;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public AccessToken ensureAccessToken() {
        if (!accessToken.get().isValid()) {
            refreshAccesstoken();
        }
        if (!accessToken.get().isValid()) {
            throw new RuntimeException("Could not get a new access token");
        }
        return accessToken.get();
    }

    public void refreshAccesstoken() {
        accessToken.set(getAccessToken());
    }
}
