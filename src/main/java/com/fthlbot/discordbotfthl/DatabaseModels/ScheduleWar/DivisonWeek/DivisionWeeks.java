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
    private Boolean isByeWeek;

    @Deprecated
    public DivisionWeeks(){

    }

    public DivisionWeeks(Integer weekNumber, Date weekStartDate, Date weekEndDate, Division division, Boolean isByeWeek) {
        this.weekNumber = weekNumber;
        this.weekStartDate = weekStartDate;
        this.weekEndDate = weekEndDate;
        this.division = division;
        this.isByeWeek = isByeWeek;
    }

    public DivisionWeeks(Integer ID, Integer weekNumber, Date weekStartDate, Date weekEndDate, Division division, Boolean isByeWeek) {
        this.ID = ID;
        this.weekNumber = weekNumber;
        this.weekStartDate = weekStartDate;
        this.weekEndDate = weekEndDate;
        this.division = division;
        this.isByeWeek = isByeWeek;
    }

    public Boolean isByeWeek() {
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

}
