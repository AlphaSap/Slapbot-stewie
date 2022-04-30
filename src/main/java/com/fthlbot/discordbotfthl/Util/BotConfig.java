package com.fthlbot.discordbotfthl.Util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Configuration
public class BotConfig {
    //TODO: check all the values and make sure they are correct

    //Channel IDs
    @Value("${channel.registrationChannel}")
    private long registrationChannelID;
    @Value("${channel.transactionChannel}")
    private long transactionChannelID;
    @Value("${channel.errorLogsAndInfo}")
    private long errorLogChannelID;



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
    @Value("${server.NegotiationServer}")
    private long negoServerID;

    //Dates
    @Value("${f5.startDate}")
    private String f5StartDate;
    @Value("${f5.endDate}")
    private String f5EndDate;

    @Value("${f8.startTime}")
    private String f8StartDate;
    @Value("${f8.endTime}")
    private String f8EndDate;
    @Value("${f9.startTime}")
    private String f9StartDate;
    @Value("${f9.endTime}")
    private String f9EndDate;
    @Value("${f10.startTime}")
    private String f10StartDate;
    @Value("${f10.endTime}")
    private String f10EndDate;
    @Value("${f11.startTime}")
    private String f11StartDate;
    @Value("${f11.endTime}")
    private String f11EndDate;
    @Value("${fmix.startTime}")
    private String fmixStartDate;
    @Value("${fmix.endTime}")
    private String fmixEndDate;
    @Value("${league.startDate}")
    private String leagueStartDate;
    @Value("${league.RegistrationStartDate}")
    private String leagueRegistrationStartDate;


    public Date getF8StartDate() throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(f8StartDate);
    }

    public Date getF8EndDate() throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(f8EndDate);
    }

    public Date getF9StartDate() throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(f9StartDate);
    }

    public Date getF9EndDate() throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(f9EndDate);
    }

    public Date getF10StartDate() throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(f10StartDate);
    }

    public Date getF10EndDate() throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(f10EndDate);
    }

    public Date getF11StartDate() throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(f11StartDate);
    }

    public Date getF11EndDate() throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(f11EndDate);
    }

    public Date getFmixStartDate() throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(fmixStartDate);
    }

    public Date getFmixEndDate() throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(fmixEndDate);
    }

    public Date getF5StartDate() throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(f5StartDate);
    }

    public Date getF5EndDate() throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(f5EndDate);
    }

    public long getRegistrationChannelID() {
        return registrationChannelID;
    }

    public long getTransactionChannelID() {
        return transactionChannelID;
    }

    public long getStaffRoleID() {
        return staffRoleID;
    }

    public long getNegoServerID() {
        return negoServerID;
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

    public long getErrorLogChannelID() { return errorLogChannelID; }

    public Date[] getDateByDivision(String key) throws ParseException {
     Date[] dates = {null, null};
     key = key.toLowerCase(Locale.ROOT);
     switch (key){
         case "f5" -> {
             dates[0] = this.getF5StartDate();
             dates[1] = this.getF5EndDate();
         }
         case "f8" -> {
             dates[0] = this.getF8StartDate();
             dates[1] = this.getF8EndDate();
         }
         case "f9" -> {
             dates[0] = this.getF9StartDate();
             dates[1] = this.getF9StartDate();
         }
         case "f10" -> {
             dates[0] = this.getF10StartDate();
             dates[1] = this.getF10EndDate();
         }
         case "f11" -> {
             dates[0] = this.getF11StartDate();
             dates[1] = this.getF11EndDate();
         }
         case "fmix" -> {
             dates[0] = this.getFmixStartDate();
             dates[1] = this.getFmixEndDate();
         }
         default -> {
             throw new IllegalStateException("Incorrect division alias");
         }
     }
     return dates;
    }

    public Date getLeagueStartDate() throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(leagueStartDate);
    }
    public Date getRegistrationDate() throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(leagueRegistrationStartDate);
    }
}
