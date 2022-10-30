package com.fthlbot.discordbotfthl.DatabaseModels.OffSeason.OffSeasonTeam;

import com.fthlbot.discordbotfthl.DatabaseModels.OffSeason.OffSeasonDivision.OffSeasonDivision;

import javax.persistence.*;

@Entity
@Table
public class OffSeasonTeam {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer Id;
    private String name;
    private String alias;
    private String tag;
    private Long repOneID;
    private Long repTwoID;
    @ManyToOne(cascade = CascadeType.MERGE)
    private OffSeasonDivision offSeasonDivision;
    @Column(nullable = true)
    private Long registrationChannelID;

    public OffSeasonTeam(Integer id,
                         String name,
                         String alias,
                         String tag,
                         Long repOneID,
                         Long repTwoID,
                         OffSeasonDivision offSeasonDivision,
                         Long registrationChannelID) {
        Id = id;
        this.name = name;
        this.alias = alias;
        this.tag = tag;
        this.repOneID = repOneID;
        this.repTwoID = repTwoID;
        this.offSeasonDivision = offSeasonDivision;
        this.registrationChannelID = registrationChannelID;
    }

    public OffSeasonTeam(String name,
                         String alias,
                         String tag,
                         Long repOneID,
                         Long repTwoID,
                         OffSeasonDivision offSeasonDivision,
                         Long registrationChannelID) {
        this.name = name;
        this.alias = alias;
        this.tag = tag;
        this.repOneID = repOneID;
        this.repTwoID = repTwoID;
        this.offSeasonDivision = offSeasonDivision;
        this.registrationChannelID = registrationChannelID;
    }

    public Integer getId() {
        return Id;
    }

    public String getName() {
        return name;
    }

    public String getAlias() {
        return alias;
    }

    public String getTag() {
        return tag;
    }

    public Long getRepOneID() {
        return repOneID;
    }

    public Long getRepTwoID() {
        return repTwoID;
    }

    public OffSeasonDivision getOffSeasonDivision() {
        return offSeasonDivision;
    }

    public Long getRegistrationChannelID() {
        return registrationChannelID;
    }
}
