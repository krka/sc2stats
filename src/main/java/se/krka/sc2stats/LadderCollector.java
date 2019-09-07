package se.krka.sc2stats;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.commons.io.FileUtils;
import se.krka.sc2stats.model.Division;
import se.krka.sc2stats.model.DivisionTeam;
import se.krka.sc2stats.model.LadderSeason;
import se.krka.sc2stats.model.League;
import se.krka.sc2stats.model.LeagueDivision;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class LadderCollector {
    public static final String timestamp = Instant.now().toString().replace(":", "");
    private final File dir;
    private final StarcraftAPI api;
    private final ExecutorService executorService;
    private final Set<String> players;
    private final Map<LeagueId, MinMax> minmax = new EnumMap<LeagueId, MinMax>(LeagueId.class);

    public LadderCollector(BlizzardDataSource dataSource) throws IOException {
        dir = new File(new File("ladder", timestamp), dataSource.getRegion().name());
        FileUtils.forceMkdir(dir);
        api = new StarcraftAPI(dataSource);
        players = Sets.newConcurrentHashSet();
        executorService = Executors.newFixedThreadPool(20,
                new ThreadFactoryBuilder()
                        .setDaemon(true)
                        .build());
        for (LeagueId leagueId : LeagueId.values()) {
            minmax.put(leagueId, new MinMax());
        }
    }

    public static void main(String[] args) throws Exception {
        final BlizzardOAuth oauth = BlizzardOAuth.create();
        for (Region region : Region.values()) {
            try (BlizzardDataSource dataSource = BlizzardDataSource.create(region, oauth)) {
                LadderCollector ladderCollector = new LadderCollector(dataSource);
                ladderCollector.populateRegion();
            }
        }
    }

    private void populateRegion() throws ExecutionException, InterruptedException {
        final LadderSeason ladderSeason = api.getLadderSeason();
        final int seasonId = ladderSeason.getSeasonId();
        for (LeagueId leagueId : LeagueId.values()) {
            System.out.println("Fetching data for " + leagueId);
            League league = api.getLeague(seasonId, leagueId);
            ImmutableList<LeagueDivision> allDivisions = league.getAllDivisions();
            List<? extends Future<?>> futures = allDivisions.stream().map(division -> populateDivision(leagueId, division.getDivisionId())).collect(Collectors.toList());
            System.out.println("Found " + futures.size() + " divisions in " + leagueId);
            for (Future<?> future : futures) {
                future.get();
            }
        }

        System.out.println("Writing output with size: " + players.size());
        ArrayList<String> sortedPlayers = Lists.newArrayList(players);
        sortedPlayers.sort(Comparator.comparingLong(value -> Long.parseLong(value.split(",")[0])));

        try (PrintWriter writer = new PrintWriter(Util.fileWriter(new File(dir, "datapoints.csv")))) {
            Lists.reverse(sortedPlayers).forEach(writer::println);
        }
        try (PrintWriter writer = new PrintWriter(Util.fileWriter(new File(dir, "leagues.csv")))) {
            minmax.forEach((leagueId, minMax) -> writer.println(leagueId.name() + "," + minMax.min.get() + "," + minMax.max.get()));
        }
    }

    private Future<?> populateDivision(LeagueId leagueId, int divisionId) {
        return executorService.submit(() -> {
            Division division = api.getDivision(divisionId);
            if (division == null) {
                System.err.println("Missing division: " + divisionId);
                return;
            }
            ImmutableList<DivisionTeam> teams = division.getTeams();
            if (teams == null) {
                System.err.println("Missing teams for division " + divisionId);
                return;
            }
            teams.stream()
                    .filter(team -> team.getMmr() < 100000 && team.getMmr() > 0)
                    .forEach(team -> {
                long mmr = team.getMmr();
                team.getMembers().forEach(member -> {
                    String name = member.getName() + ":" + member.getId();
                    String race = member.getRace();
                    players.add(String.format(Locale.ROOT, "%d,%s,%s", mmr, race, name));
                    minmax.get(leagueId).update((int) mmr);
                });
            });
        });
    }

    private class MinMax {
        private final AtomicInteger min = new AtomicInteger(Integer.MAX_VALUE);
        private final AtomicInteger max = new AtomicInteger(Integer.MIN_VALUE);

        void update(int value) {
            min.updateAndGet(operand -> Math.min(operand, value));
            max.updateAndGet(operand -> Math.max(operand, value));
        }
    }
}
