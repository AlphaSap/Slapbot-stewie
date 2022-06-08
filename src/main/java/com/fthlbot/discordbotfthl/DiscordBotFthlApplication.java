package com.fthlbot.discordbotfthl;

import Core.JClash;
import Core.exception.ClashAPIException;
import com.fthlbot.discordbotfthl.Commands.CommandImpl.ClashCommandImpl.*;
import com.fthlbot.discordbotfthl.Commands.CommandImpl.FunAndRandomCommands.ImageGenCommandImpl;
import com.fthlbot.discordbotfthl.Commands.CommandImpl.FunAndRandomCommands.SuggestionImpl;
import com.fthlbot.discordbotfthl.Commands.CommandImpl.FunAndRandomCommands.PingImpl;
import com.fthlbot.discordbotfthl.Commands.CommandImpl.LeagueCommandsImpl.*;
import com.fthlbot.discordbotfthl.Commands.CommandImpl.LeagueCommandsImpl.RosterAdd.RosterAdditionImpl;
import com.fthlbot.discordbotfthl.Commands.CommandImpl.LeagueCommandsImpl.TeamRoster.TeamRoster;
import com.fthlbot.discordbotfthl.Commands.CommandImpl.LeagueCommandsImpl.UtilCommands.ShowDivisionWeekImpl;
import com.fthlbot.discordbotfthl.Commands.CommandImpl.LeagueCommandsImpl.UtilCommands.TeamInfoImpl;
import com.fthlbot.discordbotfthl.Commands.CommandImpl.LeagueCommandsImpl.RosterRemove;
import com.fthlbot.discordbotfthl.Commands.CommandImpl.StaffCommandsImpl.*;
import com.fthlbot.discordbotfthl.Commands.CommandImpl.StaffCommandsImpl.SchedulingCommands.AddDivisionWeeksImpl;
import com.fthlbot.discordbotfthl.Commands.CommandImpl.StaffCommandsImpl.SchedulingCommands.CreateMatchUps;
import com.fthlbot.discordbotfthl.Commands.CommandImpl.StaffCommandsImpl.SchedulingCommands.NegoChannelCreationImpl;
import com.fthlbot.discordbotfthl.DatabaseModels.CommandLogger.CommandLoggerService;
import com.fthlbot.discordbotfthl.Handlers.Command;
import com.fthlbot.discordbotfthl.Handlers.CommandListener;
import com.fthlbot.discordbotfthl.Handlers.MessageHandlers;
import com.fthlbot.discordbotfthl.Handlers.MessageHolder;
import com.fthlbot.discordbotfthl.RandomEvents.ServerJoinImpl;
import com.fthlbot.discordbotfthl.RandomEvents.ServerLeaveImpl;
import com.fthlbot.discordbotfthl.RandomEvents.ServerMemberJoin.ApplicantServerJoinImpl;
import com.fthlbot.discordbotfthl.RandomEvents.ServerMemberJoin.NegoServerMemberjoinImpl;
import com.fthlbot.discordbotfthl.Util.BotConfig;
import com.fthlbot.discordbotfthl.Util.SlapbotEmojis;
import com.fthlbot.discordbotfthl.Util.SlashCommandBuilder;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.activity.ActivityType;
import org.javacord.api.entity.intent.Intent;
import org.javacord.api.entity.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

@SpringBootApplication
public class DiscordBotFthlApplication {

    private final Environment env;

    private final PingImpl pingImpl;

    private final RegistrationImpl registration;

    private final RosterAdditionImpl rosterAddition;

    private final CommandLoggerService loggerService;

    private final RosterRemove rosterRemove;

    private final TeamRoster teamRoster;

    private final DefenseImpl attack;

    private final AllTeamsImpl allTeams;

    private final ChangeClanImpl changeClan;
    public static final String prefix = "+";

    private static final Logger log = LoggerFactory.getLogger(DiscordBotFthlApplication.class);
    public static JClash clash;

    private final BotConfig config;

    private final ChangeRepImpl changeRep;

    private final ChangeAliasImpl changeAlias;

    private final AddDivisionWeeksImpl addDivisionWeeks;

    private final CreateMatchUps createMatchUps;

    private final NegoChannelCreationImpl negoChannelCreation;

    private final ShowDivisionWeekImpl showDivisionWeek;

    private final PlayerImpl player;

    private final RemoveAllChannelFromACategoryImpl removeAllChannelFromACategory;

    private final TeamInfoImpl teamInfo;

    private final SlashCommandBuilder builder;

    private final CreateAllDivisionsImpl createAllDivisions;

    private final DeleteATeamImpl deleteATeam;

    private final StatsImpl stats;

    private final AttackImpl attackImpl;

    private final ImageGenCommandImpl imageGenCommand;

    private final FairPlayCheckOnAllTeamImpl fairPlayCheckOnAllTeam;
    private final CheckLineUpImpl checkLineUp;

    private final ClanInfoImpl clanInfo;

    private final SuggestionImpl suggestionImpl;

    private final NegoServerMemberjoinImpl serverMemberJoin;

    private final ApplicantServerJoinImpl applicantServerJoin;

    private final ServerJoinImpl serverJoin;
    private final ServerLeaveImpl serverLeave;

    private final DivisionEditorImpl divisionEditor;

    public DiscordBotFthlApplication(Environment env, PingImpl pingImpl, RegistrationImpl registration, RosterAdditionImpl rosterAddition, CommandLoggerService loggerService, RosterRemove rosterRemove, TeamRoster teamRoster, DefenseImpl attack, AllTeamsImpl allTeams, ChangeClanImpl changeClan, BotConfig config, ChangeRepImpl changeRep, ChangeAliasImpl changeAlias, AddDivisionWeeksImpl addDivisionWeeks, CreateMatchUps createMatchUps, NegoChannelCreationImpl negoChannelCreation, ShowDivisionWeekImpl showDivisionWeek, PlayerImpl player, RemoveAllChannelFromACategoryImpl removeAllChannelFromACategory, TeamInfoImpl teamInfo, SlashCommandBuilder builder, CreateAllDivisionsImpl createAllDivisions, DeleteATeamImpl deleteATeam, StatsImpl stats, AttackImpl attackImpl, ImageGenCommandImpl imageGenCommand, FairPlayCheckOnAllTeamImpl fairPlayCheckOnAllTeam, CheckLineUpImpl checkLineUp, ClanInfoImpl clanInfo, SuggestionImpl suggestionImpl, NegoServerMemberjoinImpl serverMemberJoin, ApplicantServerJoinImpl applicantServerJoin, ServerJoinImpl serverJoin, ServerLeaveImpl serverLeave, DivisionEditorImpl divisionEditor) {
        this.env = env;
        this.pingImpl = pingImpl;
        this.registration = registration;
        this.rosterAddition = rosterAddition;
        this.loggerService = loggerService;
        this.rosterRemove = rosterRemove;
        this.teamRoster = teamRoster;
        this.attack = attack;
        this.allTeams = allTeams;
        this.changeClan = changeClan;
        this.config = config;
        this.changeRep = changeRep;
        this.changeAlias = changeAlias;
        this.addDivisionWeeks = addDivisionWeeks;
        this.createMatchUps = createMatchUps;
        this.negoChannelCreation = negoChannelCreation;
        this.showDivisionWeek = showDivisionWeek;
        this.player = player;
        this.removeAllChannelFromACategory = removeAllChannelFromACategory;
        this.teamInfo = teamInfo;
        this.builder = builder;
        this.createAllDivisions = createAllDivisions;
        this.deleteATeam = deleteATeam;
        this.stats = stats;
        this.attackImpl = attackImpl;
        this.imageGenCommand = imageGenCommand;
        this.fairPlayCheckOnAllTeam = fairPlayCheckOnAllTeam;
        this.checkLineUp = checkLineUp;
        this.clanInfo = clanInfo;
        this.suggestionImpl = suggestionImpl;
        this.serverMemberJoin = serverMemberJoin;
        this.applicantServerJoin = applicantServerJoin;
        this.serverJoin = serverJoin;
        this.serverLeave = serverLeave;
        this.divisionEditor = divisionEditor;
    }


    public static void main(String[] args) {
        SpringApplication.run(DiscordBotFthlApplication.class, args);
    }

    @Bean
    @ConfigurationProperties(value = "discord-bot")
    public DiscordApi api() throws ClashAPIException, IOException {
        log.info(config.getNegoServerStaffRoleID() + " id");
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneOffset.UTC));

        long testID = config.getTestServerID();


        clash = new JClash(env.getProperty("CLASH_EMAIL"), env.getProperty("CLASH_PASS"));
        DiscordApi api = new DiscordApiBuilder()
                .setToken(env.getProperty("TOKEN_BOT"))
                .setUserCacheEnabled(false)
                .setAllIntentsExcept(
                        Intent.GUILD_WEBHOOKS,
                        Intent.GUILD_INTEGRATIONS,
                        Intent.DIRECT_MESSAGE_TYPING,
                        Intent.DIRECT_MESSAGE_REACTIONS,
                        Intent.DIRECT_MESSAGES,
                        Intent.DIRECT_MESSAGE_TYPING,
                        Intent.GUILD_MESSAGE_TYPING
                ).login()
                .join();

        builder.setApi(api);
        builder.deleteACommand("division-editor");
        builder.createDivisionEditor();
        ArrayList<Server> servers = new ArrayList<>(api.getServers());

        //Adding commands to the handler
        CommandListener commandListener = registerCommands();

        api.addListener(commandListener);
        api.addListener(serverMemberJoin);
        api.addListener(applicantServerJoin);
        //To log the servers the bot is in
        api.addListener(serverJoin);
        api.addListener(serverLeave);



        api.updateActivity(ActivityType.LISTENING, "Slash commands!");

        SlapbotEmojis.setEmojis( api.getServerById(config.getEmojiServerID()).get().getCustomEmojis().stream().toList());
        log.info("Logged in as {}", api.getYourself().getDiscriminatedName());
        log.info("Watching servers {}", servers.size());
        printMemoryUsage();
        return api;
    }
    private CommandListener registerCommands() {
        List<Command> commandList = new ArrayList<>(List.of(
                this.pingImpl,
                this.registration,
                this.rosterAddition,
                this.rosterRemove,
                this.teamRoster,
                this.attack,
                this.allTeams,
                this.changeRep,
                this.changeClan,
                this.changeAlias,
                this.addDivisionWeeks,
                this.createMatchUps,
                this.negoChannelCreation,
                this.showDivisionWeek,
                this.player,
                this.removeAllChannelFromACategory,
                this.teamInfo,
                this.createAllDivisions,
                this.deleteATeam,
                this.stats,
                this.attackImpl,
                this.imageGenCommand,
                this.checkLineUp,
                this.fairPlayCheckOnAllTeam,
                this.clanInfo,
                this.suggestionImpl,
                this.divisionEditor
        ));
        log.info("Commands added Size: " + commandList.size());
        //Making help command
        HelpImpl help = new HelpImpl(commandList);
        //Add help command
        commandList.add(help);

        MessageHandlers messageHandlers = new MessageHandlers(commandList);

        MessageHolder messageHolder = messageHandlers.setCommands();
        return new CommandListener(messageHolder, loggerService, config);
    }

    private void printMemoryUsage(){
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long allocatedMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        log.info("Max Memory: " + maxMemory);
        log.info("Allocated Memory: " + allocatedMemory);
        log.info("Free Memory: " + freeMemory);
    }


}
