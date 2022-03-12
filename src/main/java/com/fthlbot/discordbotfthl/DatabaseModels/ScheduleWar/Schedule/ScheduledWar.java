package com.fthlbot.discordbotfthl.DatabaseModels.ScheduleWar.Schedule;

import com.fthlbot.discordbotfthl.DatabaseModels.ScheduleWar.DivisonWeek.DivisionWeeks;
import com.fthlbot.discordbotfthl.DatabaseModels.Team.Team;

import javax.persistence.*;

@Table
@Entity
public class ScheduledWar {
    @Id
    @Column(unique = true)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer ID;

    @OneToOne
    private DivisionWeeks divisionWeeks;

    @OneToOne
    private Team teamA;
    @OneToOne
    private Team teamB;

    public ScheduledWar(Integer ID, DivisionWeeks divisionWeeks, Team teamA, Team teamB) {
        this.ID = ID;
        this.divisionWeeks = divisionWeeks;
        this.teamA = teamA;
        this.teamB = teamB;
    }

    public ScheduledWar(DivisionWeeks divisionWeeks, Team teamA, Team teamB) {
        this.divisionWeeks = divisionWeeks;
        this.teamA = teamA;
        this.teamB = teamB;
    }

    @Deprecated
    public ScheduledWar() {

    }

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public DivisionWeeks getDivisionWeeks() {
        return divisionWeeks;
    }

    public void setDivisionWeeks(DivisionWeeks divisionWeeks) {
        this.divisionWeeks = divisionWeeks;
    }

    public Team getTeamA() {
        return teamA;
    }

    public void setTeamA(Team teamA) {
        this.teamA = teamA;
    }

    public Team getTeamB() {
        return teamB;
    }

    public void setTeamB(Team teamB) {
        this.teamB = teamB;
    }
}
