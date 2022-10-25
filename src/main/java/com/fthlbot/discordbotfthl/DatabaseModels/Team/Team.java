package com.fthlbot.discordbotfthl.DatabaseModels.Team;

import com.fthlbot.discordbotfthl.DatabaseModels.Division.Division;

import javax.persistence.*;
import java.util.Optional;

@Table
@Entity
public class Team {

    @Id
    @Column(unique = true)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer ID;
    private String name;
    private String tag;
    private String alias;
    @ManyToOne(cascade = CascadeType.MERGE)
    private Division division;
    private Long rep1ID;
    private Long rep2ID;
    private Integer allowRosterChangesLeft;

    @Column(nullable = true)
    private Long registrationChannelID;

    public Team(Integer ID,
                String name,
                String tag,
                String alias,
                Division division,
                Long rep1ID,
                Long rep2ID,
                Integer allowRosterChangesLeft
    ) {
        this.ID = ID;
        this.name = name;
        this.tag = tag;
        this.alias = alias;
        this.division = division;
        this.rep1ID = rep1ID;
        this.rep2ID = rep2ID;
        this.allowRosterChangesLeft = allowRosterChangesLeft;
        this.registrationChannelID = null;
    }

    public Team(String name,
                String tag,
                String alias,
                Division division,
                Long rep1ID,
                Long rep2ID,
                Integer allowRosterChangesLeft
    ) {
        this.name = name;
        this.tag = tag;
        this.alias = alias;
        this.division = division;
        this.rep1ID = rep1ID;
        this.rep2ID = rep2ID;
        this.allowRosterChangesLeft = allowRosterChangesLeft;
        this.registrationChannelID = null;
    }

    public Team() {
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Long getRep1ID() {
        return rep1ID;
    }

    public void setRep1ID(Long rep1ID) {
        this.rep1ID = rep1ID;
    }

    public Long getRep2ID() {
        return rep2ID;
    }

    public void setRep2ID(Long rep2ID) {
        this.rep2ID = rep2ID;
    }

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Division getDivision() {
        return division;
    }

    public void setDivision(Division division) {
        this.division = division;
    }

    public Integer getAllowRosterChangesLeft() {
        return allowRosterChangesLeft;
    }

    public void setAllowRosterChangesLeft(Integer allowRosterChangesLeft) {
        this.allowRosterChangesLeft = allowRosterChangesLeft;
    }

    public Optional<Long> getRegistrationChannelID() {
        return Optional.ofNullable(registrationChannelID);
    }


    /***
     * Must set this parameter after creating the Teams object, and not before
     * @param registrationChannelID - ID of the private registration channel the user will have for his team!
     */
    public Team setRegistrationChannelID(Long registrationChannelID) {
        this.registrationChannelID = registrationChannelID;
        return this;
    }
}
