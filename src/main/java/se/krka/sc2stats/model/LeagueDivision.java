package se.krka.sc2stats.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LeagueDivision {
    private final int divisionId;

    @JsonCreator
    public LeagueDivision(@JsonProperty("ladder_id") int divisionId) {
        this.divisionId = divisionId;
    }

    public int getDivisionId() {
        return divisionId;
    }
}
