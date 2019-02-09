package se.krka.sc2stats.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LadderSeason {
  private final int number;
  private final int seasonId;
  private final String year;
  private final long endDate;
  private final long startDate;

  @JsonCreator
  public LadderSeason(
      @JsonProperty("number") final int number,
      @JsonProperty("seasonId") final int seasonId,
      @JsonProperty("year") final String year,
      @JsonProperty("endDate") final long endDate,
      @JsonProperty("startDate") final long startDate) {
    this.number = number;
    this.seasonId = seasonId;
    this.year = year;
    this.endDate = endDate;
    this.startDate = startDate;
  }

  public int getNumber() {
    return number;
  }

  public int getSeasonId() {
    return seasonId;
  }

  public String getYear() {
    return year;
  }

  public long getEndDate() {
    return endDate;
  }

  public long getStartDate() {
    return startDate;
  }

  @Override
  public String toString() {
    return "LadderSeason{" +
            "number=" + number +
            ", seasonId=" + seasonId +
            ", year='" + year + '\'' +
            ", endDate=" + endDate +
            ", startDate=" + startDate +
            '}';
  }
}
