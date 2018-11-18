package se.krka.sc2stats;

import com.google.common.collect.ImmutableList;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.function.Function;
import org.apache.commons.codec.Charsets;

public class Util {

  public static InputStreamReader fileReader(final File input) {
    try {
      return new InputStreamReader(new FileInputStream(input), Charsets.UTF_8);
    } catch (final FileNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  public static OutputStreamWriter fileWriter(final File outputFile) {
    try {
      return new OutputStreamWriter(new FileOutputStream(outputFile), Charsets.UTF_8);
    } catch (final FileNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  // Assume input is sorted by keyFunction
  public static <T> List<List<T>> partitionByKey(
      final List<T> input,
      final int minValue,
      final int bucketSize,
      final Function<T, Integer> keyFunction) {
    final ImmutableList.Builder<List<T>> partitions = ImmutableList.builder();

    ImmutableList.Builder<T> currentPartition = ImmutableList.builder();
    int currentBucket = minValue + bucketSize;
    int prev = Integer.MIN_VALUE;
    for (T value : input) {
      int key = keyFunction.apply(value);
      if (key < prev) {
        throw new IllegalStateException("Input was not sorted");
      }
      while (key >= currentBucket) {
        partitions.add(currentPartition.build());
        currentPartition = ImmutableList.builder();
        currentBucket += bucketSize;
      }
      currentPartition.add(value);
      prev = key;
    }
    final ImmutableList<T> build = currentPartition.build();
    if (!build.isEmpty()) {
      partitions.add(build);
    }
    return partitions.build();
  }
}
