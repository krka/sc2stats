package se.krka.sc2stats.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LadderSeason {
  private final int number;
  private final String year;

  @JsonCreator
  public LadderSeason(
      @JsonProperty("number") final int number,
      @JsonProperty("year") final String year) {
    this.number = number;
    this.year = year;
  }

  @Override
  public String toString() {
    return "LadderSeason{" +
           "number=" + number +
           ", year='" + year + '\'' +
           '}';
  }
}
