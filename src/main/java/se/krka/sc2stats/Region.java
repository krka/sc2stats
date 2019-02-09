package se.krka.sc2stats;

public enum Region {
    US(1, "us.api.blizzard.com"),
    Europe(2, "eu.api.blizzard.com"),
    Korea(3, "kr.api.blizzard.com"),
    ;

    private final int regionId;
    private final String apiHost;

    Region(int regionId, String apiHost) {

        this.regionId = regionId;
        this.apiHost = apiHost;
    }

    public int getRegionId() {
        return regionId;
    }

    public String getApiHost() {
        return apiHost;
    }
}
