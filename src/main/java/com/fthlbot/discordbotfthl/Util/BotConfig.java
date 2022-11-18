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
    @Value("${channel.registrationAndRosterLogChannel}")
    private long registrationAndRosterLogChannelID;
    @Value("${channel.errorLogsAndInfo}")
    private long errorLogChannelID;
    @Value("${channel.suggestionChannel}")
    private long suggestionChannelID;


    //Role IDs
    @Value("${role.fthlServer.staff}")
    private long fthlServerStaffRoleID;
    @Value("${role.fthlServer.Representative}")
    private long fthlServerRepresentativeRoleID;
    @Value("${role.negoServer.staff}")
    private long negoServerStaffRoleID;
    @Value("${role.negoServer.Representative}")
    private long negoServerRepresentativeRoleID;
    @Value("${role.applicantServer.staff}")
    private long applicantServerStaffRoleID;
    @Value("${role.applicantServer.Applicant}")
    private long applicantServerApplicantRoleID;

    //ServerIDs
    @Value("${server.fthlServer}")
    private long fthlServerID;
    @Value("${server.testServer}")
    private long testServerID;
    @Value("${server.applicantServer}")
    private long applicantServerID;
    @Value("${server.NegotiationServer}")
    private long negoServerID;
    @Value("${server.EmojiServer}")
    private long emojiServerID;

    public long getEmojiServerID() {
        return emojiServerID;
    }

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
    @Value("${elite.startTime}")
    private String eliteStartDate;
    @Value("${elite.endTime}")
    private String eliteEndDate;

    @Value("${lite.startTime}")
    private String liteStartDate;

    @Value("${lite.endTime}")
    private String liteEndDate;

    @Value("${league.RegistrationEndDate}")
    private String leagueRegistrationEndDate;
    @Value("${league.RegistrationStartDate}")
    private String leagueRegistrationStartDate;

    @Value("${league.GreyPeriodStartDate}")
    private String leagueGrepPeriodStartDate;

     @Value("${league.GreyPeriodEndDate}")
    private String leagueGrepPeriodEndDate;

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

    public Date getEliteStartDate() throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(eliteStartDate);
    }

    public Date getEliteEndDate() throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(eliteEndDate);
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

    public long getRegistrationAndRosterLogChannelID() {
        return registrationAndRosterLogChannelID;
    }

    public long getFthlServerStaffRoleID() {
        return fthlServerStaffRoleID;
    }

    public long getFTHLServerRepresentativeRoleID() {
        return fthlServerRepresentativeRoleID;
    }

    public long getNegoServerStaffRoleID() {
        return negoServerStaffRoleID;
    }

    public long getNegoServerRepresentativeRoleID() {
        return negoServerRepresentativeRoleID;
    }

    public long getApplicantServerStaffRoleID() {
        return applicantServerStaffRoleID;
    }

    public long getApplicantServerApplicantRoleID() {
        return applicantServerApplicantRoleID;
    }

    public long getNegoServerID() {
        return negoServerID;
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
         case "elite" -> {
             dates[0] = this.getEliteStartDate();
             dates[1] = this.getEliteEndDate();
         }
         case "lite" -> {
             dates[0] = this.getLiteStartDate();
             dates[1] = this.getLiteEndDate();
         }
         default -> {
             throw new IllegalStateException("Incorrect division alias");
         }
     }
     return dates;
    }

    public Date getLeagueRegistrationEndDate() throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(leagueRegistrationEndDate);
    }
    public Date getRegistrationDate() throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(leagueRegistrationStartDate);
    }

    public long getSuggestionChannelID() {
        return suggestionChannelID;
    }

    public Date getLiteStartDate() throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(liteStartDate);
    }

    public Date getLiteEndDate() throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(liteEndDate);
    }

    public Date getLeagueGrepPeriodStartDate() throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(leagueGrepPeriodStartDate);
    }

    public Date getLeagueGrepPeriodEndDate() throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(leagueGrepPeriodEndDate);
    }
}
