package se.krka.sc2stats;

import org.json.JSONObject;
import se.krka.sc2stats.model.*;

public class StarcraftAPI {

  private final BlizzardDataSource blizzardDataSource;
  private final Region region;

  public StarcraftAPI(final BlizzardDataSource blizzardDataSource) {
    this.blizzardDataSource = blizzardDataSource;
    region = blizzardDataSource.getRegion();
  }

  public Static getStatic() {
    final String url = "/sc2/static/profile/" + region.getRegionId();
    return blizzardDataSource.getTypedData(
        Static.class, url, StarcraftAPI::dontReduce);
  }

  public LadderSeason getLadderSeason() {
    final String url = "/sc2/ladder/season/" + region.getRegionId();
    return blizzardDataSource.getTypedData(
        LadderSeason.class, url, StarcraftAPI::dontReduce);
  }

  public League getLeague(final int season, final LeagueId leagueId) {
    String queueId = "201"; // LOTV
    String type = "0"; // Arranged
    String url = "/data/sc2/league/" + season + "/" + queueId + "/" + type + "/" + leagueId.getId();
    return blizzardDataSource.getTypedData(League.class, url, jsonObject -> {});
  }

  public Division getDivision(final int divisionId) {
    String url = "/data/sc2/ladder/" + divisionId;
    return blizzardDataSource.getTypedData(Division.class, url, jsonObject -> {});

  }

  public ProfileMetadata getProfileMetadata(final String profileId, final int realm) {
    final String url = "/sc2/metadata/profile/" + region.getRegionId() + "/" + realm + "/" + profileId;
    return blizzardDataSource.getTypedData(ProfileMetadata.class, url, StarcraftAPI::dontReduce);
  }

  public Profile getProfile(final String profileId, final int realm) {
    final String url = "/sc2/profile/" + region.getRegionId() + "/" + realm + "/" + profileId;
    return blizzardDataSource.getTypedData(Profile.class, url, StarcraftAPI::reduceProfile);
  }

  private static boolean reduceProfile(final JSONObject obj) {
    boolean modified = false;
    modified |= null != obj.remove("achievementShowcase");
    modified |= null != obj.remove("campaign");
    modified |= null != obj.remove("categoryPointProgress");
    modified |= null != obj.remove("earnedAchievements");
    modified |= null != obj.remove("earnedRewards");
    modified |= null != obj.remove("snapshot");
    modified |= null != obj.remove("swarmLevels");
    return modified;
  }

  public Ladder getLadder(final String profileId, final int realm, final String ladderId) {
    final String cacheKey = "/sc2/ladder/" + region.getRegionId() + "/" + realm + "/" + ladderId;
    final String url = "/sc2/profile/" + region.getRegionId() + "/" + realm + "/" + profileId + "/ladder/" + ladderId;
    return blizzardDataSource.getTypedData(Ladder.class, url, StarcraftAPI::reduceLadder);
  }

  private static boolean reduceLadder(final JSONObject obj) {
    boolean modified = false;
    modified |= null != obj.remove("allLadderMemberships");
    modified |= null != obj.remove("currentLadderMembership");
    modified |= null != obj.remove("ranksAndPools");
    return modified;
  }

  public LadderSummary getLadderSummary(final String profileId, final int realm) {
    final String url = "/sc2/profile/" + region.getRegionId() + "/" + realm + "/" + profileId + "/ladder/summary";
    return blizzardDataSource.getTypedData(LadderSummary.class, url, StarcraftAPI::reduceLadderSummary);
  }

  private static boolean reduceLadderSummary(final JSONObject obj) {
    boolean modified = false;
    modified |= null != obj.remove("placementMatches");
    modified |= null != obj.remove("showCaseEntries");
    return modified;
  }

  public Ladder getGrandmaster() {
    final String url = "/sc2/ladder/grandmaster/" + region.getRegionId();
    return blizzardDataSource.getTypedData(Ladder.class, url, StarcraftAPI::dontReduce);
  }

  private static void dontReduce(JSONObject obj) {
  }

}
