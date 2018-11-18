package se.krka.sc2stats;

import org.json.JSONObject;
import se.krka.sc2stats.model.Ladder;
import se.krka.sc2stats.model.LadderSeason;
import se.krka.sc2stats.model.LadderSummary;
import se.krka.sc2stats.model.Profile;
import se.krka.sc2stats.model.ProfileMetadata;
import se.krka.sc2stats.model.Static;

public class StarcraftAPI {

  private final BlizzardDataSource blizzardDataSource;

  public StarcraftAPI(final BlizzardDataSource blizzardDataSource) {
    this.blizzardDataSource = blizzardDataSource;
  }

  public Static getStatic(final int region) {
    final String url = "/sc2/static/profile/" + region;
    return blizzardDataSource.getTypedData(
        Static.class, url, url, StarcraftAPI::dontReduce);
  }

  public LadderSeason getLadderSeason(final int region) {
    final String url = "/sc2/ladder/season/" + region;
    return blizzardDataSource.getTypedData(
        LadderSeason.class, url, url, StarcraftAPI::dontReduce);
  }

  public ProfileMetadata getProfileMetadata(final ProfileMetadata profileMetadata) {
    final int regionId = profileMetadata.getRegionId();
    final int realmId = profileMetadata.getRealmId();
    final String profileId = profileMetadata.getProfileId();
    final String url = "/sc2/metadata/profile/" + regionId + "/" + realmId + "/" + profileId;
    return blizzardDataSource.getTypedData(ProfileMetadata.class, url, url, StarcraftAPI::dontReduce);
  }

  public ProfileMetadata getProfileMetadata(final String profileId, final int realm, final int region) {
    final String url = "/sc2/metadata/profile/" + region + "/" + realm + "/" + profileId;
    return blizzardDataSource.getTypedData(ProfileMetadata.class, url, url, StarcraftAPI::dontReduce);
  }

  public Profile getProfile(final String profileId, final int realm, final int region) {
    final String url = "/sc2/profile/" + region + "/" + realm + "/" + profileId;
    return blizzardDataSource.getTypedData(Profile.class, url, url, StarcraftAPI::reduceProfile);
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

  public Ladder getLadder(final String profileId, final int realm, final int region, final String ladderId) {
    final String cacheKey = "/sc2/ladder/" + region + "/" + realm + "/" + ladderId;
    final String url = "/sc2/profile/" + region + "/" + realm + "/" + profileId + "/ladder/" + ladderId;
    return blizzardDataSource.getTypedData(Ladder.class, cacheKey, url, StarcraftAPI::reduceLadder);
  }

  private static boolean reduceLadder(final JSONObject obj) {
    boolean modified = false;
    modified |= null != obj.remove("allLadderMemberships");
    modified |= null != obj.remove("currentLadderMembership");
    modified |= null != obj.remove("ranksAndPools");
    return modified;
  }

  public LadderSummary getLadderSummary(final String profileId, final int realm, final int region) {
    final String url = "/sc2/profile/" + region + "/" + realm + "/" + profileId + "/ladder/summary";
    return blizzardDataSource.getTypedData(LadderSummary.class, url, url, StarcraftAPI::reduceLadderSummary);
  }

  private static boolean reduceLadderSummary(final JSONObject obj) {
    boolean modified = false;
    modified |= null != obj.remove("placementMatches");
    modified |= null != obj.remove("showCaseEntries");
    return modified;
  }

  public Ladder getGrandmaster(final int region) {
    final String url = "/sc2/ladder/grandmaster/" + region;
    return blizzardDataSource.getTypedData(Ladder.class, url, url, StarcraftAPI::dontReduce);
  }

  private static void dontReduce(JSONObject obj) {
  }

}
