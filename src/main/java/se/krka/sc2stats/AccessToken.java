package se.krka.sc2stats;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import org.json.JSONObject;
import org.json.JSONTokener;

public class AccessToken {
  private final String accessToken;
  private final String type;
  private final Instant expiration;

  public AccessToken(final String accessToken, final String type, final Instant expiration) {
    this.accessToken = accessToken;
    this.type = type;
    this.expiration = expiration;
  }

  public AccessToken() {
    this("", "", Instant.MIN);
  }

  public boolean isValid() {
    return Instant.now().isBefore(expiration);
  }

  public String getAccessToken() {
    return accessToken;
  }

  public static AccessToken fromFile(final File file) throws IOException {
    try (final FileReader reader = new FileReader(file)) {
      final JSONObject data = new JSONObject(new JSONTokener(reader));
      final String accessToken = data.getString("access_token");
      final String type = data.getString("type");
      final Instant expiration = Instant.parse(data.getString("expiration"));
      return new AccessToken(accessToken, type, expiration);
    }
  }

  void toFile(final File file) {
    try (final FileWriter writer = new FileWriter(file)) {
      final JSONObject data = new JSONObject();
      data.put("access_token", accessToken);
      data.put("type", type);
      data.put("expiration", expiration.toString());
      data.write(writer, 2, 0);
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  // Fetch access token
  public static void main(String[] args) throws Exception {
    try (final BlizzardDataSource dataSource = BlizzardDataSource.create()) {
      dataSource.refreshAccesstoken();
    }
  }
}
