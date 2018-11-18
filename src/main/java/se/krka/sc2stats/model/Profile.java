package se.krka.sc2stats.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Profile {
  private final Summary summary;
  private final Career career;

  @JsonCreator
  public Profile(
      @JsonProperty("summary") final Summary summary,
      @JsonProperty("career") final Career career) {
    this.summary = summary;
    this.career = career;
  }

  public Summary getSummary() {
    return summary;
  }

  public Career getCareer() {
    return career;
  }

  @Override
  public String toString() {
    return "Profile{" +
           "summary=" + summary +
           ", career=" + career +
           '}';
  }

  public static class Summary {
    private final String id;
    private final int realm;
    private final String displayName;

    public Summary(
        @JsonProperty("id") final String id,
        @JsonProperty("realm") final int realm,
        @JsonProperty("displayName") final String displayName) {
      this.id = id;
      this.realm = realm;
      this.displayName = displayName;
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

    @Override
    public String toString() {
      return "Summary{" +
             "id='" + id + '\'' +
             ", realm=" + realm +
             ", displayName='" + displayName + '\'' +
             '}';
    }
  }

  public static class Career {
    private final int terranWins;
    private final int zergWins;
    private final int protossWins;
    private final int totalCareerGames;
    private final int totalGamesThisSeason;

    @JsonCreator
    public Career(
        @JsonProperty("terranWins") final int terranWins,
        @JsonProperty("zergWins") final int zergWins,
        @JsonProperty("protossWins") final int protossWins,
        @JsonProperty("totalCareerGames")final int totalCareerGames,
        @JsonProperty("totalGamesThisSeason") final int totalGamesThisSeason) {
      this.terranWins = terranWins;
      this.zergWins = zergWins;
      this.protossWins = protossWins;
      this.totalCareerGames = totalCareerGames;
      this.totalGamesThisSeason = totalGamesThisSeason;
    }

    public int getTerranWins() {
      return terranWins;
    }

    public int getZergWins() {
      return zergWins;
    }

    public int getProtossWins() {
      return protossWins;
    }

    public int getTotalCareerGames() {
      return totalCareerGames;
    }

    public int getTotalGamesThisSeason() {
      return totalGamesThisSeason;
    }
  }
}
