package com.fthlbot.discordbotfthl.Util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BotConfig {
    //Channel IDs
    @Value("${channel.registrationChannel}")
    private long registrationChannelID;
    @Value("${channel.transactionChannel}")
    private long transactionChannelID;

    //Role IDs
    @Value("${role.staff}")
    private long staffRoleID;
    @Value("${role.applicant}")
    private long applicantRoleID;
    @Value("${role.Representative}")
    private long representativeRoleID;
    @Value("${role.techTeam}")
    private long techTeamRoleID;
    @Value("${role.negoStaff}")
    private long negoStaffRoleID;

    //ServerIDs
    @Value("${server.fthlServer}")
    private long fthlServerID;
    @Value("${server.testServer}")
    private long testServerID;
    @Value("${server.applicantServer}")
    private long applicantServerID;

    public long getRegistrationChannelID() {
        return registrationChannelID;
    }

    public long getTransactionChannelID() {
        return transactionChannelID;
    }

    public long getStaffRoleID() {
        return staffRoleID;
    }

    public long getApplicantRoleID() {
        return applicantRoleID;
    }

    public long getRepresentativeRoleID() {
        return representativeRoleID;
    }

    public long getTechTeamRoleID() {
        return techTeamRoleID;
    }

    public long getNegoStaffRoleID() {
        return negoStaffRoleID;
    }

    public long getFthlServerID() {
        return fthlServerID;
    }

    public long getTestServerID() {
        return testServerID;
    }

    public long getApplicantServerID() {
        return applicantServerID;
    }
}
