package com.fthlbot.discordbotfthl.DatabaseModels.OffSeason.OffSeasonRoster;

import com.fthlbot.discordbotfthl.DatabaseModels.OffSeason.OffSeasonDivision.OffSeasonDivision;
import com.fthlbot.discordbotfthl.DatabaseModels.OffSeason.OffSeasonTeam.OffSeasonTeam;

import javax.persistence.*;

@Entity
@Table
public class OffSeasonRoster {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer Id;
    private String tag;
    private String name;
    private Integer townHallLevel;

    @ManyToOne(cascade = CascadeType.MERGE)
    private OffSeasonTeam offSeasonTeam;
    @ManyToOne(cascade = CascadeType.MERGE)
    private OffSeasonDivision offSeasonDivision;

    public OffSeasonRoster(Integer id,
                           String tag,
                           String name,
                           Integer townHallLevel,
                           OffSeasonTeam offSeasonTeam,
                           OffSeasonDivision offSeasonDivision) {
        Id = id;
        this.tag = tag;
        this.name = name;
        this.townHallLevel = townHallLevel;
        this.offSeasonTeam = offSeasonTeam;
        this.offSeasonDivision = offSeasonDivision;
    }

    public OffSeasonRoster(String tag,
                           String name,
                           Integer townHallLevel,
                           OffSeasonTeam offSeasonTeam,
                           OffSeasonDivision offSeasonDivision) {
        this.tag = tag;
        this.name = name;
        this.townHallLevel = townHallLevel;
        this.offSeasonTeam = offSeasonTeam;
        this.offSeasonDivision = offSeasonDivision;
    }

    public OffSeasonRoster() {

    }

    public Integer getId() {
        return Id;
    }

    public String getTag() {
        return tag;
    }

    public String getName() {
        return name;
    }

    public Integer getTownHallLevel() {
        return townHallLevel;
    }

    public OffSeasonTeam getOffSeasonTeam() {
        return offSeasonTeam;
    }

    public OffSeasonDivision getOffSeasonDivision() {
        return offSeasonDivision;
    }
}
