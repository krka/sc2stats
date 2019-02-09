package se.krka.sc2stats.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

import java.util.Optional;

public class DivisionMember {
    private final String name;
    private final int id;
    private final String race;

    @JsonCreator
    public DivisionMember(
            @JsonProperty("legacy_link") LegacyLink legacyLink,
            @JsonProperty("played_race_count") ImmutableList<PlacedRaceCount> placedRaceCount,
            @JsonProperty("clan_link") Optional<ClanLink> clanLink) {
        this.name = clanLink.map(clan -> "[" + clan.tag + "]").orElse("") + legacyLink.name;
        this.id = legacyLink.id;
        this.race = placedRaceCount.stream().map(x -> x.race.name).findAny().get();
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public String getRace() {
        return race;
    }

    static class LegacyLink {
        private final int id;
        private final String name;

        @JsonCreator
        public LegacyLink(@JsonProperty("id") int id, @JsonProperty("name") String name) {
            this.id = id;
            this.name = name;
        }
    }

    static class ClanLink {
        private final String name;
        private final String tag;

        @JsonCreator
        public ClanLink(@JsonProperty("clan_name") String name, @JsonProperty("clan_tag") String tag) {
            this.name = name;
            this.tag = tag;
        }
    }

    static class PlacedRaceCount {
        private final Race race;

        @JsonCreator
        public PlacedRaceCount(@JsonProperty("race") Race race) {
            this.race = race;
        }
    }

    static class Race {
        private final String name;

        @JsonCreator
        public Race(@JsonProperty("en_US") String name) {
            this.name = name;
        }
    }
}
