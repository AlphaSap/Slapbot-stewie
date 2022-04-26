package com.fthlbot.discordbotfthl.MinionBotAPI;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Optional;

public class MinionBotPlayer {

    @Expose
    @SerializedName("name")
    private String name;
    @Expose
    @SerializedName("expirationDate")
    private String expirationDate;
    @Expose
    @SerializedName("id")
    private long id;
    @Expose
    @SerializedName("startDate")
    private String startDate;
    @Expose
    @SerializedName("claimedBy")
    private long claimedBy;
    @Expose
    @SerializedName("createdBy")
    private long createdBy;
    @Expose
    @SerializedName("lengthOfBanInDays")
    private long lengthOfBanInDays;
    @Expose
    @SerializedName("orgID")
    private long orgID;
    @Expose
    @SerializedName("orgName")
    private String orgName;
    @Expose
    @SerializedName("orgInitials")
    private String orgInitials;
    @Expose
    @SerializedName("tag")
    private String tag;
    @Expose
    @SerializedName("mustLeaveClan")
    private boolean mustLeaveClan;
    @Expose
    @SerializedName("mustNotBeInAnyWar")
    private boolean mustNotBeInAnyWar;

    //make getters, all getters should be Wrapper in Optional.class
    public Optional<String> getName() {
        return Optional.ofNullable(name);
    }

    public Optional<String> getExpirationDate() {
        return Optional.ofNullable(expirationDate);
    }

    public long getId() {
        return id;
    }

    public String getStartDate() {
        return startDate;
    }

    public Optional<Long> getClaimedBy() {
        return Optional.ofNullable(claimedBy);
    }

    public long getCreatedBy() {
        return createdBy;
    }

    public Optional<Long> getLengthOfBanInDays() {
        return Optional.ofNullable(lengthOfBanInDays);
    }

    public long getOrgID() {
        return orgID;
    }

    public Optional<String> getOrgName() {
        return Optional.ofNullable(orgName);
    }

    public Optional<String> getOrgInitials() {
        return Optional.ofNullable(orgInitials);
    }

    public Optional<String> getTag() {
        return Optional.ofNullable(tag);
    }

    //make getters for final two booleans
    public boolean getMustLeaveClan() {
        return mustLeaveClan;
    }

    public boolean getMustNotBeInAnyWar() {
        return mustNotBeInAnyWar;
    }

    @Override
    public String toString() {
        return "MinionBotPlayer{" +
                "name='" + name + '\'' +
                ", expirationDate='" + expirationDate + '\'' +
                ", id=" + id +
                ", startDate='" + startDate + '\'' +
                ", claimedBy=" + claimedBy +
                ", createdBy=" + createdBy +
                ", lengthOfBanInDays=" + lengthOfBanInDays +
                ", orgID=" + orgID +
                ", orgName='" + orgName + '\'' +
                ", orgInitials='" + orgInitials + '\'' +
                ", tag='" + tag + '\'' +
                ", mustLeaveClan=" + mustLeaveClan +
                ", mustNotBeInAnyWar=" + mustNotBeInAnyWar +
                '}';
    }
}
