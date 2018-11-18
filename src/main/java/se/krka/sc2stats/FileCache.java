package se.krka.sc2stats;

import com.google.common.base.Splitter;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.time.Instant;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

public class FileCache {

  private static final Splitter SLASH_SPLITTER = Splitter.on('/');
  private final File baseDir;

  private final ConcurrentMap<String, CountDownLatch> inprogress = new ConcurrentHashMap<>();

  public FileCache(final File baseDir) {
    this.baseDir = baseDir;
    this.baseDir.mkdir();
  }

  public Reader getContent(final String key) {
    final File f = dataFile(createPath(key));
    if (f.exists()) {
      return Util.fileReader(f);
    }
    return null;
  }

  private File createPath(final String key) {
    File f = baseDir;
    for (String part : SLASH_SPLITTER.split(key)) {
      f = new File(f, part);
      if (f.exists()) {
        if (!f.isDirectory()) {
          throw new RuntimeException(f.getAbsolutePath() + " must be a directory");
        }
      } else {
        f.mkdir();
      }
    }
    return f;
  }

  public void writeContent(final String key, final String data, final String url) {
    final File path = createPath(key);
    try (final Writer writer = Util.fileWriter(dataFile(path))) {
      IOUtils.write(data, writer);
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
    writeMeta(path, url, Instant.now());
  }

  public static void writeMeta(final File path, final String url, final Instant timestamp) {
    try (final Writer writer = Util.fileWriter(metaFile(path))) {
      JSONObject jsonObject = new JSONObject();
      jsonObject.put("url", url);
      jsonObject.put("timestamp", timestamp.toString());
      IOUtils.write(jsonObject.toString(2), writer);
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  private File dataFile(final File path) {
    return new File(path, "data.json");
  }

  private static File metaFile(final File path) {
    return new File(path, "meta.json");
  }

  public Reader getOrCreate(final String cacheKey, final String url, final Callable<String> resolver) {
    CountDownLatch latch;
    while (true) {
      final Reader cached = getContent(cacheKey);
      if (cached != null) {
        return cached;
      }
      final CountDownLatch newLatch = new CountDownLatch(1);
      latch = inprogress.putIfAbsent(cacheKey, newLatch);
      if (latch == null) {
        latch = newLatch;
        break;
      }
      try {
        latch.await();
      } catch (final InterruptedException e) {
      }
    }
    try {
      final String s = resolver.call();
      writeContent(cacheKey, s, url);
      return new StringReader(s);
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      inprogress.remove(cacheKey);
      latch.countDown();
    }
  }
}
