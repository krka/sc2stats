package se.krka.sc2stats.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Optional;

public class Membership {
  private final String ladderId;
  private final String localizedGameMode;
  private final Optional<Integer> rank;

  @JsonCreator
  public Membership(
      @JsonProperty("ladderId") final String ladderId,
      @JsonProperty("localizedGameMode") final String localizedGameMode,
      @JsonProperty("rank") final Optional<Integer> rank) {
    this.ladderId = ladderId;
    this.localizedGameMode = localizedGameMode;
    this.rank = rank;
  }

  public String getLadderId() {
    return ladderId;
  }

  public String getLocalizedGameMode() {
    return localizedGameMode;
  }

  public Optional<Integer> getRank() {
    return rank;
  }

  @Override
  public String toString() {
    return "Membership{" +
           "ladderId='" + ladderId + '\'' +
           ", localizedGameMode='" + localizedGameMode + '\'' +
           ", rank=" + rank +
           '}';
  }
}
