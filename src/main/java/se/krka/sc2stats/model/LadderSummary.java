package se.krka.sc2stats.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import java.util.Optional;

public class LadderSummary {
  //private final ImmutableList<ShowCaseEntry> showCaseEntries;
  //private final ImmutableList<PlacementMatch> placementMatches;
  private final ImmutableList<Membership> allLadderMemberships;

  @JsonCreator
  public LadderSummary(
      @JsonProperty("allLadderMemberships") final ImmutableList<Membership> allLadderMemberships) {
    this.allLadderMemberships = allLadderMemberships;
  }

  public ImmutableList<Membership> getAllLadderMemberships() {
    return allLadderMemberships;
  }

  @Override
  public String toString() {
    return "LadderSummary{" +
           ", allLadderMemberships=" + allLadderMemberships +
           '}';
  }

  public static class ShowCaseEntry {
    private final String ladderId;
    private final Team team;
    private final String leagueName;
    private final String localizedDivisionName;
    private final int rank;
    private final int wins;
    private final int losses;

    @JsonCreator
    public ShowCaseEntry(
        @JsonProperty("ladderId") final String ladderId,
        @JsonProperty("team") final Team team,
        @JsonProperty("leagueName") final String leagueName,
        @JsonProperty("localizedDivisionName") final String localizedDivisionName,
        @JsonProperty("rank") final int rank,
        @JsonProperty("wins") final int wins,
        @JsonProperty("losses") final int losses) {
      this.ladderId = ladderId;
      this.team = team;
      this.leagueName = leagueName;
      this.localizedDivisionName = localizedDivisionName;
      this.rank = rank;
      this.wins = wins;
      this.losses = losses;
    }

    public String getLadderId() {
      return ladderId;
    }

    public Team getTeam() {
      return team;
    }

    public String getLeagueName() {
      return leagueName;
    }

    public String getLocalizedDivisionName() {
      return localizedDivisionName;
    }

    public int getRank() {
      return rank;
    }

    public int getWins() {
      return wins;
    }

    public int getLosses() {
      return losses;
    }

    @Override
    public String toString() {
      return "ShowCaseEntry{" +
             "ladderId='" + ladderId + '\'' +
             ", team=" + team +
             ", leagueName='" + leagueName + '\'' +
             ", localizedDivisionName='" + localizedDivisionName + '\'' +
             ", rank=" + rank +
             ", wins=" + wins +
             ", losses=" + losses +
             '}';
    }
  }

  public static class Team {
    private final String localizedGameMode;
    private final ImmutableList<Member> members;

    @JsonCreator
    public Team(
        @JsonProperty("localizedGameMode") final String localizedGameMode,
        @JsonProperty("members") final ImmutableList<Member> members) {
      this.localizedGameMode = localizedGameMode;
      this.members = members;
    }

    public String getLocalizedGameMode() {
      return localizedGameMode;
    }

    public ImmutableList<Member> getMembers() {
      return members;
    }

    @Override
    public String toString() {
      return "Team{" +
             "localizedGameMode='" + localizedGameMode + '\'' +
             ", members=" + members +
             '}';
    }
  }

  public static class Member {
    private final Optional<String> favoriteRace;
    private final String name;
    private final String playerId;
    private final int region;

    @JsonCreator
    public Member(
        @JsonProperty("favoriteRace") final Optional<String> favoriteRace,
        @JsonProperty("name") final String name,
        @JsonProperty("playerId") final String playerId,
        @JsonProperty("region") final int region) {
      this.favoriteRace = favoriteRace;
      this.name = name;
      this.playerId = playerId;
      this.region = region;
    }

    public Optional<String> getFavoriteRace() {
      return favoriteRace;
    }

    public String getName() {
      return name;
    }

    public String getPlayerId() {
      return playerId;
    }

    public int getRegion() {
      return region;
    }

    @Override
    public String toString() {
      return "Member{" +
             "favoriteRace='" + favoriteRace + '\'' +
             ", name='" + name + '\'' +
             ", playerId='" + playerId + '\'' +
             ", region=" + region +
             '}';
    }
  }

  public static class PlacementMatch {
    private final String localizedGameMode;
    private final ImmutableList<Member> members;
    private final int gamesRemaining;

    @JsonCreator
    public PlacementMatch(
        @JsonProperty("localizedGameMode") final String localizedGameMode,
        @JsonProperty("members") final ImmutableList<Member> members,
        @JsonProperty("gamesRemaining") final int gamesRemaining) {
      this.localizedGameMode = localizedGameMode;
      this.members = members;
      this.gamesRemaining = gamesRemaining;
    }

    public String getLocalizedGameMode() {
      return localizedGameMode;
    }

    public ImmutableList<Member> getMembers() {
      return members;
    }

    public int getGamesRemaining() {
      return gamesRemaining;
    }

    @Override
    public String toString() {
      return "PlacementMatch{" +
             "localizedGameMode='" + localizedGameMode + '\'' +
             ", members=" + members +
             ", gamesRemaining=" + gamesRemaining +
             '}';
    }
  }
}
