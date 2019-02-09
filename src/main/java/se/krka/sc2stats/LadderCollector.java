package se.krka.sc2stats;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.commons.io.FileUtils;
import se.krka.sc2stats.model.LadderSeason;
import se.krka.sc2stats.model.League;
import se.krka.sc2stats.model.LeagueDivision;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class LadderCollector {
    public static final String timestamp = Instant.now().toString().replace(":", "");
    private final File dir;
    private final StarcraftAPI api;
    private final ExecutorService executorService;
    private final Set<String> players;

    public LadderCollector(BlizzardDataSource dataSource) throws IOException {
        dir = new File(new File("ladder", timestamp), dataSource.getRegion().name());
        FileUtils.forceMkdir(dir);
        api = new StarcraftAPI(dataSource);
        players = Sets.newConcurrentHashSet();
        executorService = Executors.newFixedThreadPool(20,
                new ThreadFactoryBuilder()
                        .setDaemon(true)
                        .build());
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
            List<? extends Future<?>> futures = allDivisions.stream().map(division -> populateDivision(division.getDivisionId())).collect(Collectors.toList());
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
    }

    private Future<?> populateDivision(int divisionId) {
        return executorService.submit(() -> api.getDivision(divisionId).getTeams()
                .stream()
                .filter(team -> team.getMmr() < 100000 && team.getMmr() > 0)
                .forEach(team -> {
            long mmr = team.getMmr();
            team.getMembers().forEach(member -> {
                String name = member.getName() + ":" + member.getId();
                String race = member.getRace();
                players.add(String.format(Locale.ROOT, "%d,%s,%s", mmr, race, name));
            });
        }));
    }

}
