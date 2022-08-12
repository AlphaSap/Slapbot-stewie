package com.fthlbot.discordbotfthl.DatabaseModels.BannedReps;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;
import java.util.Optional;

@Entity
@Table
public class BannedRep { //this class is used to represent as a list of banned reps who will never be allowed to rep for a team!
    @Id
    private Long ID;

    private Long discordUserID;
    @Column(nullable = true)
    private String reason;
    private String staffDiscordID; //the staff member who has banned the person!
    private Date bannedDate;
    private String teamName;
    private String divisionName;
    @Column(nullable = true)
    private String notes;

    public BannedRep() {

    }

    public Long getID() {
        return ID;
    }

    public Long getDiscordUserID() {
        return discordUserID;
    }

    public Optional<String> getReason() {
        return Optional.ofNullable(reason);
    }

    public String getStaffDiscordID() {
        return staffDiscordID;
    }

    public Date getBannedDate() {
        return bannedDate;
    }

    public String getTeamName() {
        return teamName;
    }

    public String getDivisionName() {
        return divisionName;
    }

    public Optional<String> getNotes() {
        return Optional.ofNullable(notes);
    }

    public BannedRep(Long discordUserID, String reason, String staffDiscordID, Date bannedDate, String teamName, String divisionName, String notes) {
        this.discordUserID = discordUserID;
        this.reason = reason;
        this.staffDiscordID = staffDiscordID;
        this.bannedDate = bannedDate;
        this.teamName = teamName;
        this.divisionName = divisionName;
        this.notes = notes;
    }

    public BannedRep(Long ID, Long discordUserID, String reason, String staffDiscordID, Date bannedDate, String teamName, String divisionName, String notes) {
        this.ID = ID;
        this.discordUserID = discordUserID;
        this.reason = reason;
        this.staffDiscordID = staffDiscordID;
        this.bannedDate = bannedDate;
        this.teamName = teamName;
        this.divisionName = divisionName;
        this.notes = notes;
    }
}
