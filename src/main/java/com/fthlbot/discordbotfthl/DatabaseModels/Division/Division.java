package com.fthlbot.discordbotfthl.DatabaseModels.Division;


import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Table(uniqueConstraints = {})
@Entity
public class Division {
    @Id
    @Column(unique = true)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String name;
    private String alias;
    private Integer rosterSize;
    private Integer allowedRosterChanges;
    private Integer[] allowedTownHall;

    public Division() {
    }

    public Division(String name, String alias, Integer rosterSize, Integer allowedRosterChanges, Integer[] allowedTownHall) {
        this.name = name;
        this.alias = alias;
        this.rosterSize = rosterSize;
        this.allowedRosterChanges = allowedRosterChanges;
        this.allowedTownHall = allowedTownHall;
    }

    public Integer[] getAllowedTownHall() {
        return this.allowedTownHall;
    }

    public Division(Integer id, String name, String alias, Integer rosterSize, Integer allowedRosterChanges, Integer[] allowedTownHall) {
        this.id = id;
        this.name = name;
        this.alias = alias;
        this.rosterSize = rosterSize;
        this.allowedRosterChanges = allowedRosterChanges;
        this.allowedTownHall = allowedTownHall;
    }

    public Integer getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getAlias() {
        return this.alias;
    }

    public Integer getRosterSize() {
        return this.rosterSize;
    }

    public Integer getAllowedRosterChanges() {
        return this.allowedRosterChanges;
    }

}
