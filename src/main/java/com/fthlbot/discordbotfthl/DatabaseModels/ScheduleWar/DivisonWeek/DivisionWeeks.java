package com.fthlbot.discordbotfthl.DatabaseModels.ScheduleWar.DivisonWeek;

import com.fthlbot.discordbotfthl.DatabaseModels.Division.Division;
import com.fthlbot.discordbotfthl.DatabaseModels.Team.Team;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Table
@Entity
public class DivisionWeeks {
    @Id
    @Column(unique = true)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer ID;
    private Integer weekNumber;
    private Date weekStartDate;
    private Date weekEndDate;
    @ManyToOne
    private Division division;
    @OneToMany
    private List<Team> team = new ArrayList<>();
    private Boolean isByeWeek;

    @Deprecated
    public DivisionWeeks(){

    }
    public Boolean getByeWeek() {
        return isByeWeek;
    }

    public Integer getWeekNumber() {
        return weekNumber;
    }

    public Integer getID() {
        return ID;
    }

    public Date getWeekStartDate() {
        return weekStartDate;
    }

    public Date getWeekEndDate() {
        return weekEndDate;
    }

    public Division getDivision() {
        return division;
    }

    public List<Team> getTeam() {
        return team;
    }
}
