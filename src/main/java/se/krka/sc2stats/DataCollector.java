package se.krka.sc2stats;

import com.google.common.util.concurrent.RateLimiter;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import se.krka.sc2stats.model.Ladder;
import se.krka.sc2stats.model.LadderSummary;
import se.krka.sc2stats.model.Membership;
import se.krka.sc2stats.model.Profile;

public class DataCollector {

  public static void main(String[] args) throws IOException {
    final Config config = ConfigFactory.parseFile(new File("secrets.yaml"));
    final String clientId = config.getString("clientid");
    final String clientSecret = config.getString("clientsecret");

    final BlizzardDataSource dataSource = new BlizzardDataSource(clientId, clientSecret);
    final StarcraftAPI api = new StarcraftAPI(dataSource);

    final int region = 2;

    final PriorityQueue<Ladder> ladders = new PriorityQueue<>(
        Comparator.comparing((Ladder ladder) -> ladder.getMMR().orElse(0)).reversed());
    ladders.add(api.getGrandmaster(region));

    final ConcurrentMap<String, String> visitedLadders = new ConcurrentHashMap<>();
    final ConcurrentMap<String, String> visitedPlayers = new ConcurrentHashMap<>();

    final ExecutorService executorService =
        Executors.newFixedThreadPool(100, new ThreadFactoryBuilder().setDaemon(true).setNameFormat("blizzard-api-%d").build());

    final ConcurrentLinkedDeque<Future<?>> futures = new ConcurrentLinkedDeque<>();

    final RateLimiter flushRate = RateLimiter.create(1);
    final RateLimiter infoRate = RateLimiter.create(0.1);

    final PrintWriter writer = new PrintWriter(new FileWriter("datapoints.csv"));

    try {
      while (!ladders.isEmpty() || !futures.isEmpty()) {
        while (!futures.isEmpty() && futures.getFirst().isDone()) {
          futures.removeFirst();
        }
        if (infoRate.tryAcquire()) {
          System.out.println(futures.size() + " jobs in queue, " + ladders.size() + " ladders in queue");
        }
        final Ladder ladder;
        synchronized (ladders) {
          ladder = ladders.poll();
        }
        if (ladder != null) {
          futures.offerLast(executorService.submit(() -> {
            for (Ladder.Team team : ladder.getLadderTeams()) {
              for (Ladder.Member member : team.getTeamMembers()) {
                final String playerIdentifier = getPlayerIdentifier(member);
                if (visitedPlayers.putIfAbsent(playerIdentifier, "") == null) {
                  tryAddPlayer(api, region, executorService, flushRate, writer, ladder, team, member);
                }
                futures.offerLast(executorService.submit(() -> {
                  try {
                    final LadderSummary ladderSummary = api.getLadderSummary(member.getId(), member.getRealm(), region);
                    for (final Membership membership : ladderSummary.getAllLadderMemberships()) {
                      String ladderId = membership.getLadderId();
                      if (visitedLadders.putIfAbsent(ladderId, "") == null) {
                        //System.out.println("Visiting ladder: " + ladderId);
                        final Ladder nextLadder = api.getLadder(member.getId(), member.getRealm(), region, ladderId);
                        synchronized (ladders) {
                          ladders.add(nextLadder);
                        }
                      }
                    }
                  } catch (DownstreamErrorException e) {
                    // Just skip this Ladder summary
                    System.err.println("Ignoring exception: " + e.getMessage());
                  }
                }));
              }
            }
          }));
        } else {
          System.out.println("Waiting for workers to finish...");
          try {
            Thread.sleep(1000);
          } catch (InterruptedException e) {
          }
        }
      }
    } finally {
      System.out.println("Shutting down");
      executorService.shutdown();
      writer.close();
    }
  }

  private static void tryAddPlayer(final StarcraftAPI api, final int region, final ExecutorService executorService,
                                   final RateLimiter flushRate, final PrintWriter writer, final Ladder ladder,
                                   final Ladder.Team team, final Ladder.Member member) {
    if (ladder.is1on1()) {
      team.getMmr().ifPresent(mmr -> executorService.submit(() -> {
        try {
          final Profile profile = api.getProfile(member.getId(), member.getRealm(),
              region);
          int gamesPlayed = profile.getCareer().getTotalCareerGames();
          synchronized (writer) {
            writer.printf("%d,%d,%s\n", mmr, gamesPlayed, getPlayerIdentifier(member));
            if (flushRate.tryAcquire()) {
              writer.flush();
            }
          }
        } catch (DownstreamErrorException e) {
          // Just skip this profile
          System.err.println("Ignoring exception: " + e.getMessage());
        }
      }));
    }
  }

  private static String getPlayerIdentifier(final Ladder.Member member) {
    return member.getFavoriteRace() + "," +
                                    member.getDisplayName() + ":" + member.getId() + ":" + member.getRealm();
  }

}
