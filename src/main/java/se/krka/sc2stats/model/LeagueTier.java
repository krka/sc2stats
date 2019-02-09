package se.krka.sc2stats.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

public class LeagueTier {
    private final ImmutableList<LeagueDivision> divisions;

    @JsonCreator
    public LeagueTier(@JsonProperty("division") ImmutableList<LeagueDivision> divisions) {
        this.divisions = divisions;
    }

    public ImmutableList<LeagueDivision> getDivisions() {
        return divisions;
    }
}
