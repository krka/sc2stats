package se.krka.sc2stats.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

import java.util.Optional;
import java.util.stream.Stream;

public class League {

  private final ImmutableList<LeagueTier> tiers;
  private final ImmutableList<LeagueDivision> allDivisions;

  @JsonCreator
  public League(
      @JsonProperty("tier") final ImmutableList<LeagueTier> tiers) {
    this.tiers = tiers;
    allDivisions = tiers.stream()
            .flatMap(leagueTier -> leagueTier.getDivisions().stream())
            .collect(ImmutableList.toImmutableList());
  }

  public ImmutableList<LeagueTier> getTiers() {
    return tiers;
  }

  public ImmutableList<LeagueDivision> getAllDivisions() {
    return allDivisions;
  }
}
