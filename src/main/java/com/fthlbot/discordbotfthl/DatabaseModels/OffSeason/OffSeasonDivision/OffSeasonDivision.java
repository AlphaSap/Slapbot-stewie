package com.fthlbot.discordbotfthl.DatabaseModels.OffSeason.OffSeasonDivision;

import javax.persistence.*;

@Entity
@Table
public class OffSeasonDivision {
    @Id
    @Column(unique = true)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String name;
    private String alias;
    private Integer rosterSize;
    private Integer[] allowedRosterSize;
    private Integer rosterChanges;

    public OffSeasonDivision(Integer id,
                             String name,
                             String alias,
                             Integer rosterSize,
                             Integer[] allowedRosterSize,
                             Integer rosterChanges) {
        this.id = id;
        this.name = name;
        this.alias = alias;
        this.rosterSize = rosterSize;
        this.allowedRosterSize = allowedRosterSize;
        this.rosterChanges = rosterChanges;
    }

    public OffSeasonDivision(String name,
                             String alias,
                             Integer rosterSize,
                             Integer[] allowedRosterSize,
                             Integer rosterChanges) {
        this.name = name;
        this.alias = alias;
        this.rosterSize = rosterSize;
        this.allowedRosterSize = allowedRosterSize;
        this.rosterChanges = rosterChanges;
    }

    public OffSeasonDivision() {

    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAlias() {
        return alias;
    }

    public Integer getRosterSize() {
        return rosterSize;
    }

    public Integer[] getAllowedRosterSize() {
        return allowedRosterSize;
    }

    public Integer getRosterChanges() {
        return rosterChanges;
    }
}
