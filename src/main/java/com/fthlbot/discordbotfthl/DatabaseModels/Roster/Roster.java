package com.fthlbot.discordbotfthl.DatabaseModels.Roster;

import com.fthlbot.discordbotfthl.DatabaseModels.Division.Division;
import com.fthlbot.discordbotfthl.DatabaseModels.Team.Team;

import javax.persistence.*;

@Table
@Entity
public class Roster {

    @Id
    @Column(unique = true)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer ID;
    private String playerName;
    private String playerTag;
    private Integer townHallLevel;
    @ManyToOne(cascade = CascadeType.MERGE)
    private Team team;
    @ManyToOne(cascade = CascadeType.MERGE)
    private Division division;

    public Roster() {
    }

    public Roster(String playerName, String playerTag, Integer townHallLevel, Team team) {
        this.playerName = playerName;
        this.playerTag = playerTag;
        this.townHallLevel = townHallLevel;
        this.team = team;
        this.division = team.getDivision();
    }

    public Roster(Integer ID, String playerName, String playerTag, Integer townHallLevel, Team team) {
        this.ID = ID;
        this.playerName = playerName;
        this.playerTag = playerTag;
        this.townHallLevel = townHallLevel;
        this.team = team;
        this.division = team.getDivision();
    }

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerTag() {
        return playerTag;
    }

    public void setPlayerTag(String playerTag) {
        this.playerTag = playerTag;
    }

    public Integer getTownHallLevel() {
        return townHallLevel;
    }

    public void setTownHallLevel(Integer townHallLevel) {
        this.townHallLevel = townHallLevel;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Division getDivision(){
        return this.division;
    }
}
