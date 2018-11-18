package se.krka.sc2stats.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import java.util.Optional;

public class Ladder {

  private final ImmutableList<Team> ladderTeams;
  private final String league;
  private final Optional<String> localizedDivision;
  private final Optional<Integer> mmr;

  // This already exists in ladder-summary so skip it
  // private final ImmutableList<Membership> allLadderMemberships;

  // (Useless) profile data, so skip it
  // private final ImmutableList<RankAndPool> ranksAndPools;
  // private final Membership currentLadderMembership;

  @JsonCreator
  public Ladder(
      @JsonProperty("ladderTeams") final ImmutableList<Team> ladderTeams,
      @JsonProperty("league") final String league,
      @JsonProperty("localizedDivision") final Optional<String> localizedDivision) {
    this.ladderTeams = ladderTeams;
    this.league = league;
    this.localizedDivision = localizedDivision;

    this.mmr = ladderTeams.stream().map(Team::getMmr)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .findFirst();
  }

  public boolean is1on1() {
    if (ladderTeams.isEmpty()) {
      return false;
    }
    return ladderTeams.get(0).getTeamMembers().size() == 1;
  }

  public Optional<Integer> getMMR() {
    return mmr;
  }

  public ImmutableList<Team> getLadderTeams() {
    return ladderTeams;
  }

  public String getLeague() {
    return league;
  }

  public Optional<String> getLocalizedDivision() {
    return localizedDivision;
  }

  @Override
  public String toString() {
    return "Ladder{" +
           "ladderTeams=" + ladderTeams +
           ", league='" + league + '\'' +
           ", localizedDivision=" + localizedDivision +
           '}';
  }

  public static class Team {
    private final ImmutableList<Member> teamMembers;
    private final int previousRank;
    private final int points;
    private final int wins;
    private final int losses;
    private final Optional<Integer> mmr;
    private final long joinTimestamp;

    @JsonCreator
    public Team(
        @JsonProperty("teamMembers") final ImmutableList<Member> teamMembers,
        @JsonProperty("previousRank") final int previousRank,
        @JsonProperty("points") final int points,
        @JsonProperty("wins") final int wins,
        @JsonProperty("losses") final int losses,
        @JsonProperty("mmr") final Optional<Integer> mmr,
        @JsonProperty("joinTimestamp") final long joinTimestamp) {
      this.teamMembers = teamMembers;
      this.previousRank = previousRank;
      this.points = points;
      this.wins = wins;
      this.losses = losses;
      this.mmr = mmr;
      this.joinTimestamp = joinTimestamp;
    }

    public ImmutableList<Member> getTeamMembers() {
      return teamMembers;
    }

    public int getPreviousRank() {
      return previousRank;
    }

    public int getPoints() {
      return points;
    }

    public int getWins() {
      return wins;
    }

    public int getLosses() {
      return losses;
    }

    public Optional<Integer> getMmr() {
      return mmr;
    }

    public long getJoinTimestamp() {
      return joinTimestamp;
    }

    @Override
    public String toString() {
      return "Team{" +
             "teamMembers=" + teamMembers +
             ", previousRank=" + previousRank +
             ", points=" + points +
             ", wins=" + wins +
             ", losses=" + losses +
             ", mmr=" + mmr +
             ", joinTimestamp=" + joinTimestamp +
             '}';
    }
  }

  public static class Member {
    private final String id;
    private final int realm;
    private final String displayName;
    private final Optional<String> clanTag;
    private final String favoriteRace;

    @JsonCreator
    public Member(
        @JsonProperty("id") final String id,
        @JsonProperty("realm") final int realm,
        @JsonProperty("displayName") final String displayName,
        @JsonProperty("clanTag") final Optional<String> clanTag,
        @JsonProperty("favoriteRace") final String favoriteRace) {
      this.id = id;
      this.realm = realm;
      this.displayName = displayName;
      this.clanTag = clanTag;
      this.favoriteRace = favoriteRace;
    }

    public String getId() {
      return id;
    }

    public int getRealm() {
      return realm;
    }

    public String getDisplayName() {
      return displayName;
    }

    private Optional<String> getClanTag() {
      return clanTag;
    }

    public String getFavoriteRace() {
      return favoriteRace;
    }

    @Override
    public String toString() {
      return "Member{" +
             "id='" + id + '\'' +
             ", realm=" + realm +
             ", displayName='" + displayName + '\'' +
             ", favoriteRace='" + favoriteRace + '\'' +
             '}';
    }
  }

  public static class RankAndPool {
    private final int rank;
    private final int mmr;
    private final Optional<Integer> bonusPool;

    @JsonCreator
    public RankAndPool(
        @JsonProperty("rank") final int rank,
        @JsonProperty("mmr") final int mmr,
        @JsonProperty("bonusPool") final Optional<Integer> bonusPool) {
      this.rank = rank;
      this.mmr = mmr;
      this.bonusPool = bonusPool;
    }

    public int getRank() {
      return rank;
    }

    public int getMmr() {
      return mmr;
    }

    private Optional<Integer> getBonusPool() {
      return bonusPool;
    }

    @Override
    public String toString() {
      return "RankAndPool{" +
             "rank=" + rank +
             ", mmr=" + mmr +
             ", bonusPool=" + bonusPool +
             '}';
    }
  }
}
