package se.krka.sc2stats;

public enum LeagueId {
    BRONZE(0),
    SILVER(1),
    GOLD(2),
    PLATINUM(3),
    DIAMOND(4),
    MASTER(5),
    GRANDMASTER(6);

    private final int id;

    LeagueId(int id) {

        this.id = id;
    }

    public int getId() {
        return id;
    }
}
