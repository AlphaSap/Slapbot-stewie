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
    @ElementCollection
    private List<Integer> allowedTownHall = new ArrayList<>();


    public Division() {
    }

    public Division(String name, String alias, Integer rosterSize, Integer allowedRosterChanges, List<Integer> allowedTownHall) {
        this.name = name;
        this.alias = alias;
        this.rosterSize = rosterSize;
        this.allowedRosterChanges = allowedRosterChanges;
        this.allowedTownHall = allowedTownHall;
    }

    public List<Integer> getAllowedTownHall() {
        return allowedTownHall;
    }

    public Division(Integer id, String name, String alias, Integer rosterSize, Integer allowedRosterChanges, List<Integer> allowedTownHall) {
        this.id = id;
        this.name = name;
        this.alias = alias;
        this.rosterSize = rosterSize;
        this.allowedRosterChanges = allowedRosterChanges;
        this.allowedTownHall = allowedTownHall;
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

    public Integer getAllowedRosterChanges() {
        return allowedRosterChanges;
    }

}
