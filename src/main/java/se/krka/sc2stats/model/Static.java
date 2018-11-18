package se.krka.sc2stats.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import java.util.Optional;

public class Static {
  private final ImmutableList<Achievement> achievements;
  private final ImmutableList<Categories> categories;
  private final ImmutableList<Criteria> criteria;
  private final ImmutableList<Reward> rewards;

  @JsonCreator
  public Static(
      @JsonProperty("achievements") final ImmutableList<Achievement> achievements,
      @JsonProperty("categories") final ImmutableList<Categories> categories,
      @JsonProperty("criteria") final ImmutableList<Criteria> criteria,
      @JsonProperty("rewards") final ImmutableList<Reward> rewards) {
    this.achievements = achievements;
    this.categories = categories;
    this.criteria = criteria;
    this.rewards = rewards;
  }

  @Override
  public String toString() {
    return "Static{" +
           "achievements=" + achievements +
           ",\ncategories=" + categories +
           ",\ncriteria=" + criteria +
           ",\nrewards=" + rewards +
           '}';
  }

  public static class Categories {
    private final ImmutableList<String> childrenCategoryIds;
    private final String name;
    private final String parentCategoryId;
    private final String id;
    private final int uiOrderHint;
    private final String featuredAchievementId;
    private final int points;

    private final ImmutableList<Integer> medalTiers;

    @JsonCreator
    public Categories(
        @JsonProperty("childrenCategoryIds") final ImmutableList<String> childrenCategoryIds,
        @JsonProperty("name") final String name,
        @JsonProperty("parentCategoryId") final String parentCategoryId,
        @JsonProperty("id") final String id,
        @JsonProperty("uiOrderHint") final int uiOrderHint,
        @JsonProperty("featuredAchievementId") final String featuredAchievementId,
        @JsonProperty("points") final int points,
        @JsonProperty("medalTiers") final ImmutableList<Integer> medalTiers) {
      this.childrenCategoryIds = childrenCategoryIds;
      this.name = name;
      this.parentCategoryId = parentCategoryId;
      this.id = id;
      this.uiOrderHint = uiOrderHint;
      this.featuredAchievementId = featuredAchievementId;
      this.points = points;
      this.medalTiers = medalTiers;
    }

    public ImmutableList<String> getChildrenCategoryIds() {
      return childrenCategoryIds;
    }

    public String getName() {
      return name;
    }

    public String getParentCategoryId() {
      return parentCategoryId;
    }

    public String getId() {
      return id;
    }

    public int getUiOrderHint() {
      return uiOrderHint;
    }

    public String getFeaturedAchievementId() {
      return featuredAchievementId;
    }

    public int getPoints() {
      return points;
    }

    public ImmutableList<Integer> getMedalTiers() {
      return medalTiers;
    }

    @Override
    public String toString() {
      return "Categories{" +
             "childrenCategoryIds=" + childrenCategoryIds +
             ", name='" + name + '\'' +
             ", parentCategoryId='" + parentCategoryId + '\'' +
             ", id='" + id + '\'' +
             ", uiOrderHint=" + uiOrderHint +
             ", featuredAchievementId='" + featuredAchievementId + '\'' +
             ", points=" + points +
             ", medalTiers=" + medalTiers +
             '}';
    }
  }

  public static class Criteria {
    private final int flags;
    private final String achievementId;
    private final String description;
    private final String evaluationClass;
    private final String id;
    private final int uiOrderHint;
    private final int necessaryQuantity;

    @JsonCreator
    public Criteria(
        @JsonProperty("flags") final int flags,
        @JsonProperty("achievementId") final String achievementId,
        @JsonProperty("description") final String description,
        @JsonProperty("evaluationClass") final String evaluationClass,
        @JsonProperty("id") final String id,
        @JsonProperty("uiOrderHint") final int uiOrderHint,
        @JsonProperty("necessaryQuantity") final int necessaryQuantity) {
      this.flags = flags;
      this.achievementId = achievementId;
      this.description = description;
      this.evaluationClass = evaluationClass;
      this.id = id;
      this.uiOrderHint = uiOrderHint;
      this.necessaryQuantity = necessaryQuantity;
    }

    public int getFlags() {
      return flags;
    }

    public String getAchievementId() {
      return achievementId;
    }

    public String getDescription() {
      return description;
    }

    public String getEvaluationClass() {
      return evaluationClass;
    }

    public String getId() {
      return id;
    }

    public int getUiOrderHint() {
      return uiOrderHint;
    }

    public int getNecessaryQuantity() {
      return necessaryQuantity;
    }

    @Override
    public String toString() {
      return "Criteria{" +
             "flags=" + flags +
             ", achievementId='" + achievementId + '\'' +
             ", description='" + description + '\'' +
             ", evaluationClass='" + evaluationClass + '\'' +
             ", id='" + id + '\'' +
             ", uiOrderHint=" + uiOrderHint +
             ", necessaryQuantity=" + necessaryQuantity +
             '}';
    }
  }

  public static class Reward {
    private final String unlockableType;
    private final String imageUrl;
    private final int flags;
    private final String name;
    private final String achievementId;
    private final String id;
    private final int uiOrderHint;
    private final boolean isSkin;
    private final Optional<String> command;

    @JsonCreator
    public Reward(
        @JsonProperty("unlockableType") final String unlockableType,
        @JsonProperty("imageUrl") final String imageUrl,
        @JsonProperty("flags") final int flags,
        @JsonProperty("name") final String name,
        @JsonProperty("achievementId") final String achievementId,
        @JsonProperty("id") final String id,
        @JsonProperty("uiOrderHint") final int uiOrderHint,
        @JsonProperty("isSkin") final boolean isSkin,
        @JsonProperty("command") final Optional<String> command) {
      this.unlockableType = unlockableType;
      this.imageUrl = imageUrl;
      this.flags = flags;
      this.name = name;
      this.achievementId = achievementId;
      this.id = id;
      this.uiOrderHint = uiOrderHint;
      this.isSkin = isSkin;
      this.command = command;
    }

    public String getUnlockableType() {
      return unlockableType;
    }

    public String getImageUrl() {
      return imageUrl;
    }

    public int getFlags() {
      return flags;
    }

    public String getName() {
      return name;
    }

    public String getAchievementId() {
      return achievementId;
    }

    public String getId() {
      return id;
    }

    public int getUiOrderHint() {
      return uiOrderHint;
    }

    public boolean isSkin() {
      return isSkin;
    }

    public Optional<String> getCommand() {
      return command;
    }

    @Override
    public String toString() {
      return "Reward{" +
             "unlockableType='" + unlockableType + '\'' +
             ", imageUrl='" + imageUrl + '\'' +
             ", flags=" + flags +
             ", name='" + name + '\'' +
             ", achievementId='" + achievementId + '\'' +
             ", id='" + id + '\'' +
             ", uiOrderHint=" + uiOrderHint +
             ", isSkin=" + isSkin +
             ", command=" + command +
             '}';
    }
  }

  public static class Achievement {
    private final String imageUrl;
    private final int flags;
    private final String description;
    private final int chainRewardSize;
    private final ImmutableList<String> criteriaIds;
    private final String id;
    private final int uiOrderHint;
    private final boolean isChained;
    private final String title;
    private final String categoryId;
    private final ImmutableList<String> chainAchievementIds;
    private final int points;

    @JsonCreator
    public Achievement(
        @JsonProperty("imageUrl") final String imageUrl,
        @JsonProperty("flags") final int flags,
        @JsonProperty("description") final String description,
        @JsonProperty("chainRewardSize") final int chainRewardSize,
        @JsonProperty("criteriaIds") final ImmutableList<String> criteriaIds,
        @JsonProperty("id") final String id,
        @JsonProperty("uiOrderHint") final int uiOrderHint,
        @JsonProperty("isChained") final boolean isChained,
        @JsonProperty("title") final String title,
        @JsonProperty("categoryId") final String categoryId,
        @JsonProperty("chainAchievementIds") final ImmutableList<String> chainAchievementIds,
        @JsonProperty("points") final int points) {
      this.imageUrl = imageUrl;
      this.flags = flags;
      this.description = description;
      this.chainRewardSize = chainRewardSize;
      this.criteriaIds = criteriaIds;
      this.id = id;
      this.uiOrderHint = uiOrderHint;
      this.isChained = isChained;
      this.title = title;
      this.categoryId = categoryId;
      this.chainAchievementIds = chainAchievementIds;
      this.points = points;
    }

    public String getImageUrl() {
      return imageUrl;
    }

    public int getFlags() {
      return flags;
    }

    public String getDescription() {
      return description;
    }

    public int getChainRewardSize() {
      return chainRewardSize;
    }

    public ImmutableList<String> getCriteriaIds() {
      return criteriaIds;
    }

    public String getId() {
      return id;
    }

    public int getUiOrderHint() {
      return uiOrderHint;
    }

    public boolean isChained() {
      return isChained;
    }

    public String getTitle() {
      return title;
    }

    public String getCategoryId() {
      return categoryId;
    }

    public ImmutableList<String> getChainAchievementIds() {
      return chainAchievementIds;
    }

    public int getPoints() {
      return points;
    }

    @Override
    public String toString() {
      return "Achievement{" +
             "imageUrl='" + imageUrl + '\'' +
             ", flags=" + flags +
             ", description='" + description + '\'' +
             ", chainRewardSize=" + chainRewardSize +
             ", criteriaIds=" + criteriaIds +
             ", id='" + id + '\'' +
             ", uiOrderHint=" + uiOrderHint +
             ", isChained=" + isChained +
             ", title='" + title + '\'' +
             ", categoryId='" + categoryId + '\'' +
             ", chainAchievementIds=" + chainAchievementIds +
             ", points=" + points +
             '}';
    }
  }
}
