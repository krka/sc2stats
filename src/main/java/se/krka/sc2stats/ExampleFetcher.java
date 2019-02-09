package se.krka.sc2stats;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import org.apache.commons.codec.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.FileWriterWithEncoding;

public class ExampleFetcher {

  public static void main(String[] args) throws Exception {
    try (final BlizzardDataSource dataSource = BlizzardDataSource.create(Region.Europe, BlizzardOAuth.create())) {
      final File dir = new File("samples");
      dir.mkdir();

      writeFile(dataSource.getRawData("/sc2/ladder/season/1"), new File(dir, "season-region-1.json"));
      writeFile(dataSource.getRawData("/sc2/ladder/season/2"), new File(dir, "season-region-2.json"));
      writeFile(dataSource.getRawData("/sc2/ladder/season/3"), new File(dir, "season-region-3.json"));
      writeFile(dataSource.getRawData("/sc2/ladder/season/100"), new File(dir, "season-region-100.json"));

      writeFile(dataSource.getRawData("/sc2/ladder/grandmaster/2"), new File(dir, "grandmaster.json"));
      writeFile(dataSource.getRawData("/data/sc2/season/current"), new File(dir, "season-current-old-api.json"));
      writeFile(dataSource.getRawData("/data/sc2/season/38"), new File(dir, "season-38-old-api.json"));

      writeFile(dataSource.getRawData("/data/sc2/league/38/201/0/5"), new File(dir, "season-38-league-master-old-api.json"));
      writeFile(dataSource.getRawData("/data/sc2/league/38/201/0/4"), new File(dir, "season-38-league-diamond-old-api.json"));
      writeFile(dataSource.getRawData("/data/sc2/ladder/209872"), new File(dir, "season-38-ladder-209872-old-api.json"));
    }
  }

  private static void writeFile(final String data, final File file) throws IOException {
    try (final Writer writer = new FileWriterWithEncoding(file, Charsets.UTF_8)) {
      IOUtils.write(data, writer);
    }
  }
}
