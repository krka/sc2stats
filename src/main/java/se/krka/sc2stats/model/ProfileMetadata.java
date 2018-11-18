package se.krka.sc2stats.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ProfileMetadata {
  private final String profileUrl;
  private final int realmId;
  private final String avatarUrl;
  private final int regionId;
  private final String profileId;
  private final String name;


  @JsonCreator
  public ProfileMetadata(
      @JsonProperty("profileUrl") final String profileUrl,
      @JsonProperty("realmId") final int realmId,
      @JsonProperty("avatarUrl") final String avatarUrl,
      @JsonProperty("regionId") final int regionId,
      @JsonProperty("profileId") final String profileId,
      @JsonProperty("name") final String name) {
    this.profileUrl = profileUrl;
    this.realmId = realmId;
    this.avatarUrl = avatarUrl;
    this.regionId = regionId;
    this.profileId = profileId;
    this.name = name;
  }

  public String getProfileUrl() {
    return profileUrl;
  }

  public int getRealmId() {
    return realmId;
  }

  public String getAvatarUrl() {
    return avatarUrl;
  }

  public int getRegionId() {
    return regionId;
  }

  public String getProfileId() {
    return profileId;
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return "ProfileMetadata{" +
           "profileUrl='" + profileUrl + '\'' +
           ", realmId=" + realmId +
           ", avatarUrl='" + avatarUrl + '\'' +
           ", regionId=" + regionId +
           ", profileId='" + profileId + '\'' +
           ", name='" + name + '\'' +
           '}';
  }
}
