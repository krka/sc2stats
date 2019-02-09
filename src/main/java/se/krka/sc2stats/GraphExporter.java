package se.krka.sc2stats;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Resources;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.codec.Charsets;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class GraphExporter {

  private static final ImmutableMap<String, String> COLORS = ImmutableMap.of(
      "zerg", "#990099",
      "terran", "#FF3300",
      "protoss", "#FFCC00",
      "random", "gray"
  );

  public static void main(String[] args) throws IOException {
    File dir = new File("ladder");
    scan(dir);
  }

  private static void scan(File dir) throws IOException {
    for (File child : dir.listFiles()) {
      if (child.getName().equals("datapoints.csv")) {
        final File outputDir = new File(dir, "html");
        if (!outputDir.exists()) {
          generate(child, outputDir);
        }

      } else if (child.isDirectory()) {
        scan(child);
      }
    }
  }

  private static void generate(File input, File outputDir) throws IOException {
    final ImmutableList<DataPoint> datapoints = loadDatapoints(input);
    final ImmutableList<DataPoint> noRandom = datapoints.stream()
            .filter(dataPoint -> !dataPoint.getRace().equals("random"))
            .collect(ImmutableList.toImmutableList());

    final ImmutableList<OffraceDataPoint> offraceData = datapoints.stream()
            .collect(Collectors.groupingBy(DataPoint::getPlayerId))
            .entrySet().stream()
            .flatMap(entry -> {
              final DataPoint mainPoint = entry.getValue().stream().max(Comparator.comparing(DataPoint::getMmr)).get();
              return entry.getValue().stream().filter(p -> p != mainPoint).map(
                      point -> new OffraceDataPoint(mainPoint.getRace(), mainPoint.getMmr(), point.getRace(), point.getMmr())
              );
            })
            .sorted(Comparator.comparing(OffraceDataPoint::getMainMmr).thenComparing(OffraceDataPoint::getOffMmr))
            .collect(ImmutableList.toImmutableList());

    final Map<String, List<OffraceDataPoint>> offRacesByMain = offraceData.stream()
            .collect(Collectors.groupingBy(p -> p.getMainRace() + "->any"));

    final Map<String, List<OffraceDataPoint>> offRacesByOffrace = offraceData.stream()
            .collect(Collectors.groupingBy(p -> "any->" + p.getOffRace()));

    final Map<String, List<OffraceDataPoint>> specificOffRaces = offraceData.stream()
            .collect(Collectors.groupingBy(p -> p.getMainRace() + "->" + p.getOffRace()));

    final ImmutableList<DataPoint> mainRaceOnly = datapoints.stream()
            .collect(Collectors.groupingBy(DataPoint::getPlayerId))
            .entrySet().stream()
            .flatMap(entry -> entry.getValue().stream()
                    .max(Comparator.comparing(DataPoint::getMmr))
                    .map(Stream::of).orElseGet(Stream::empty))
            .collect(ImmutableList.toImmutableList());

    ImmutableList.Builder<Chart> charts = ImmutableList.builder();
    charts.addAll(createCumulativeCharts("mmr-by-race", noRandom, "MMR by race", "MMR", "players",
            DataPoint::getMmr, DataPoint::getRace, Function.identity(), GraphExporter::raceColor));
    charts.addAll(createCumulativeCharts("mmr-by-race-mainrace", mainRaceOnly, "MMR by race, main race only", "MMR", "players",
            DataPoint::getMmr, DataPoint::getRace, Function.identity(), GraphExporter::raceColor));

    charts.add(createOffraceCountChart("offrace-count-by-main", offRacesByMain, "Cumulative amount of off-races by main race"));
    charts.add(createOffraceCountChart("offrace-count-by-off", offRacesByOffrace, "Cumulative amount of off-races by off-race"));
    charts.add(createOffraceCountChart("offrace-count", specificOffRaces, "Cumulative amount of off-races by pairs"));
    charts.add(createOffraceChart("offrace-mmr-by-main", offRacesByMain, "MMR diff for off-races by main race"));
    charts.add(createOffraceChart("offrace-mmr-by-off", offRacesByOffrace, "MMR diff for off-races by off-race"));
    charts.add(createOffraceChart("offrace-mmr", specificOffRaces, "MMR diff for off-races by pairs"));

    generateJavascript(outputDir, charts.build());
  }

  private static String raceColor(final String race) {
    return COLORS.get(race);
  }

  private static void generateJavascript(File outputDir, final ImmutableList<Chart> charts) throws IOException {
    copyResource("charthelper.js", new File(outputDir,"charthelper.js"));

    final URL resource = Resources.getResource("charttemplate.html");
    final String template = Resources.toString(resource, Charsets.UTF_8);

    final ImmutableList.Builder<String> allIncludes = ImmutableList.builder();

    for (Chart chart : charts) {
      final String data = String.format("loadChart(\"%s\", \"%s\", \"%s\", \"%s\", %s);\n",
          chart.filename, chart.title, chart.xtitle, chart.ytitle, chart.series.toString(2));
      final String jsFilename = chart.filename + ".js";
      final String htmlFilename = chart.filename + ".html";
      FileUtils.write(new File(outputDir, jsFilename), data, Charsets.UTF_8);

      String include = String.format("<script src=\"%s\"></script>\n<script>attachChart(\"charts\", \"%s\")</script>",
          jsFilename, chart.filename);
      allIncludes.add(include);
      String html = template.replace("<!--INCLUDE_JS-->", include);
      FileUtils.write(new File(outputDir, htmlFilename), html, Charsets.UTF_8);
    }

    FileUtils.write(
        new File(outputDir, "allcharts.html"),
        template.replace("<!--INCLUDE_JS-->", Joiner.on('\n').join(allIncludes.build())),
        Charsets.UTF_8);
  }

  private static void copyResource(final String resourceName, final File output) throws IOException {
    final URL url = Resources.getResource(resourceName);
    final String data = Resources.toString(url, Charsets.UTF_8);
    FileUtils.write(output, data, Charsets.UTF_8);
  }

  private static <T> ImmutableList<Chart> createCumulativeCharts(
      final String baseName,
      final List<DataPoint> datapoints,
      final String title,
      final String xtitle,
      final String ytitle,
      final Function<DataPoint, Integer> xFunction,
      final Function<DataPoint, T> groupByFunction,
      final Function<T, String> seriesNameFunction,
      final Function<T, String> seriesColorFunction) {
    return ImmutableList.of(
        createCumulativeChart(baseName, false, false, datapoints, "Cumulative " + title, xtitle, "Number of " + ytitle,
            xFunction, groupByFunction, seriesNameFunction, seriesColorFunction),
        createCumulativeChart(baseName, true, false, datapoints, "Normalized cumulative " + title, xtitle, "Percentage of " + ytitle,
            xFunction, groupByFunction, seriesNameFunction, seriesColorFunction),
        createCumulativeChart(baseName, true, true, datapoints, "Relative " + title, xtitle, "Relative value",
            xFunction, groupByFunction, seriesNameFunction, seriesColorFunction)
    );
  }

  private static <T> Chart createCumulativeChart(
      final String baseName,
      final boolean normalized,
      final boolean relative,
      final List<DataPoint> datapoints,
      final String title,
      final String xtitle,
      final String ytitle,
      final Function<DataPoint, Integer> xFunction,
      final Function<DataPoint, T> groupByFunction,
      final Function<T, String> seriesNameFunction,
      final Function<T, String> seriesColorFunction) {
    int minValue = datapoints.stream()
        .map(xFunction)
        .min(Integer::compareTo).get();
    int maxValue = datapoints.stream()
        .map(xFunction)
        .max(Integer::compareTo).get();

    int bucketSize = Math.max(1, (maxValue - minValue) / 100);

    JSONArray allSeries = new JSONArray();

    final Map<T, List<DataPoint>> grouped = datapoints.stream().collect(Collectors.groupingBy(groupByFunction));
    int numPartitions = 0;
    for (Map.Entry<T, List<DataPoint>> entry : grouped.entrySet()) {
      final T group = entry.getKey();
      List<DataPoint> dataPoints = entry.getValue();
      int total = dataPoints.size();
      List<Integer> cumulative = Util.cumulativeByKey(dataPoints, minValue, bucketSize, xFunction);

        int bucket = minValue;

        JSONArray xvalues = new JSONArray();
        JSONArray yvalues = new JSONArray();

        int playersSeen = 0;
        final double factor = normalized ? 100.0 / total : 1.0;

        xvalues.put(bucket);
        yvalues.put((total - playersSeen) * factor);

        for (int count: cumulative) {
          playersSeen += count;
          bucket += bucketSize;

          xvalues.put(bucket);
          yvalues.put((total - playersSeen) * factor);
        }
        numPartitions = Math.max(numPartitions, xvalues.length());

        final String seriesName = seriesNameFunction.apply(group);
        allSeries.put(newSeries(seriesName, seriesColorFunction.apply(group), "lines", xvalues, yvalues));
    }

    if (relative) {
      for (int i = 0; i < numPartitions; i++) {
        double sum = 0;
        for (int raceIndex = 0; raceIndex < allSeries.length(); raceIndex++) {
          final JSONArray series = allSeries.getJSONObject(raceIndex).getJSONArray("y");
          double value = series.optDouble(i, 0);
          sum += value;
        }
        if (sum == 0 && i == numPartitions - 1) {
          for (int raceIndex = 0; raceIndex < allSeries.length(); raceIndex++) {
            final JSONArray series = allSeries.getJSONObject(raceIndex).getJSONArray("y");
            series.remove(i);
          }
        } else {
          double average = sum / allSeries.length();
          for (int raceIndex = 0; raceIndex < allSeries.length(); raceIndex++) {
            final JSONArray series = allSeries.getJSONObject(raceIndex).getJSONArray("y");
            double value = series.optDouble(i, 0);
            double relativeValue = value / average;
            series.put(i, relativeValue);
          }
        }
      }
    }

    final String filename = baseName + (normalized ? "-norm": "") + (relative ? "-rel": "");
    return new Chart(filename, allSeries, title, xtitle, ytitle);
  }

  private static Chart createOffraceChart(
      final String filename,
      final Map<String, List<OffraceDataPoint>> map, final String title) {
    int minValue = map.values().stream()
        .flatMap(Collection::stream)
        .map(OffraceDataPoint::getMainMmr)
        .min(Integer::compareTo).get();
    int maxValue = map.values().stream()
        .flatMap(Collection::stream)
        .map(OffraceDataPoint::getMainMmr)
        .max(Integer::compareTo).get();

    int bucketSize = Math.max(1, (maxValue - minValue) / 20);

    JSONArray allSeries = new JSONArray();

    for (Map.Entry<String, List<OffraceDataPoint>> entry : map.entrySet()) {
      List<String> races = Splitter.on("->").splitToList(entry.getKey());
      String mainRace = races.get(0);
      String offRace = races.get(1);

      List<OffraceDataPoint> dataPoints = entry.getValue();
      List<List<OffraceDataPoint>> partitioned = Util.partitionByKey(dataPoints, minValue, bucketSize, OffraceDataPoint::getMainMmr);

      int bucket = minValue;

      JSONArray xvalues = new JSONArray();
      JSONArray yvalues = new JSONArray();

      xvalues.put(bucket);
      yvalues.put(0);

      for (List<OffraceDataPoint> partition : partitioned) {
        double averageDiff = partition.stream()
            .map(p -> (p.getMainMmr() - p.getOffMmr()))
            .collect(Collectors.averagingDouble(value -> value));
        bucket += bucketSize;

        xvalues.put(bucket);
        yvalues.put(averageDiff);
      }

      String color = COLORS.get(mainRace.equals("any") ? offRace : mainRace);
      allSeries.put(newSeries(mainRace + "->" + offRace, color, "lines", xvalues, yvalues));
    }

    return new Chart(filename, allSeries, title, "Main MMR", "Offrace MMR diff");
  }

  private static Chart createOffraceCountChart(
      final String filename,
      final Map<String, List<OffraceDataPoint>> map, final String title) {
    int minValue = map.values().stream()
        .flatMap(Collection::stream)
        .map(OffraceDataPoint::getMainMmr)
        .min(Integer::compareTo).get();
    int maxValue = map.values().stream()
        .flatMap(Collection::stream)
        .map(OffraceDataPoint::getMainMmr)
        .max(Integer::compareTo).get();

    int bucketSize = Math.max(1, (maxValue - minValue) / 20);

    JSONArray allSeries = new JSONArray();

    for (Map.Entry<String, List<OffraceDataPoint>> entry : map.entrySet()) {
      List<String> races = Splitter.on("->").splitToList(entry.getKey());
      String mainRace = races.get(0);
      String offRace = races.get(1);

      List<OffraceDataPoint> dataPoints = entry.getValue();
      List<List<OffraceDataPoint>> partitioned = Util.partitionByKey(dataPoints, minValue, bucketSize, OffraceDataPoint::getMainMmr);

      int bucket = minValue;

      JSONArray xvalues = new JSONArray();
      JSONArray yvalues = new JSONArray();

      int total = dataPoints.size();
      int playersSeen = 0;

      xvalues.put(bucket);
      yvalues.put((total - playersSeen));

      for (List<OffraceDataPoint> partition : partitioned) {
        playersSeen += partition.size();
        bucket += bucketSize;

        xvalues.put(bucket);
        yvalues.put((total - playersSeen));
      }

      String color = COLORS.get(mainRace.equals("any") ? offRace : mainRace);
      allSeries.put(newSeries(mainRace + "->" + offRace, color, "lines", xvalues, yvalues));
    }

    return new Chart(filename, allSeries, title, "Main MMR", "Offrace MMR diff");
  }

  private static ImmutableList<DataPoint> loadDatapoints(File input) throws IOException {
    ImmutableList.Builder<DataPoint> builder = ImmutableList.builder();

    try (final BufferedReader reader = new BufferedReader(Util.fileReader(input))) {
      while (true) {
        final String line = reader.readLine();
        if (line == null) {
          break;
        }
        final List<String> parts = Splitter.on(',').splitToList(line);
        final int mmr = Integer.parseInt(parts.get(0));
        if (mmr > 0) {
          final String race = parts.get(1);
          final String playerId = parts.get(2);
          builder.add(new DataPoint(race, playerId, mmr));
        }
      }
    }
    return builder.build();
  }

  private static JSONObject newSeries(
      final String name,
      final String color,
      final String mode,
      final JSONArray xvalues,
      final JSONArray yvalues) {
    final JSONObject root = new JSONObject();
    root.put("x", xvalues);
    root.put("y", yvalues);
    root.put("mode", mode);
    root.put("type", "scatter");
    root.put("name", name);
    final JSONObject marker = new JSONObject();
    marker.put("color", color);
    root.put("marker", marker);
    return root;
  }

  static class DataPoint {

    private final String race;
    private final String playerId;
    private final int mmr;

    private DataPoint(final String race, final String playerId, final int mmr) {
      this.race = race;
      this.playerId = playerId;
      this.mmr = mmr;
    }

    private String getRace() {
      return race;
    }

    private String getPlayerId() {
      return playerId;
    }

    private int getMmr() {
      return mmr;
    }
  }

  static class OffraceDataPoint {

    private final String mainRace;
    private final int mainMmr;
    private final String offRace;
    private final int offMmr;

    private OffraceDataPoint(final String mainRace, final int mainMmr, final String offRace, final int offMmr) {
      this.mainRace = mainRace;
      this.mainMmr = mainMmr;
      this.offRace = offRace;
      this.offMmr = offMmr;
    }

    private String getMainRace() {
      return mainRace;
    }

    private int getMainMmr() {
      return mainMmr;
    }

    private String getOffRace() {
      return offRace;
    }

    private int getOffMmr() {
      return offMmr;
    }
  }

  private static class Chart {
    private final String filename;
    private final JSONArray series;
    private final String title;
    private final String xtitle;
    private final String ytitle;

    private Chart(final String filename, final JSONArray series, final String title, final String xtitle, final String ytitle) {
      this.filename = filename;
      this.series = series;
      this.title = title;
      this.xtitle = xtitle;
      this.ytitle = ytitle;
    }
  }
}
