package se.krka.sc2stats.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

public class Division {
    private final ImmutableList<DivisionTeam> teams;

    @JsonCreator
    public Division(@JsonProperty("team") ImmutableList<DivisionTeam> teams) {
        this.teams = teams;
    }

    public ImmutableList<DivisionTeam> getTeams() {
        return teams;
    }
}
