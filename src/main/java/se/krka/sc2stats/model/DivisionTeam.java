package se.krka.sc2stats.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

public class DivisionTeam {
    private final long mmr;
    private final ImmutableList<DivisionMember> members;

    @JsonCreator
    public DivisionTeam(@JsonProperty("rating") long mmr, @JsonProperty("member") ImmutableList<DivisionMember> members) {
        this.mmr = mmr;
        this.members = members;
    }

    public long getMmr() {
        return mmr;
    }

    public ImmutableList<DivisionMember> getMembers() {
        return members;
    }
}
