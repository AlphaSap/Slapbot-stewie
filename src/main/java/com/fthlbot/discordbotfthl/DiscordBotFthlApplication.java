package com.fthlbot.discordbotfthl;

import Core.JClash;
import Core.exception.ClashAPIException;
import com.fthlbot.discordbotfthl.Commands.CommandImpl.ClashCommandImpl.DefenseImpl;
import com.fthlbot.discordbotfthl.Commands.CommandImpl.ClashCommandImpl.PlayerImpl;
import com.fthlbot.discordbotfthl.Commands.CommandImpl.LeagueCommandsImpl.*;
import com.fthlbot.discordbotfthl.Commands.CommandImpl.LeagueCommandsImpl.RosterAdd.RosterAdditionImpl;
import com.fthlbot.discordbotfthl.Commands.CommandImpl.LeagueCommandsImpl.TeamRoster.TeamRoster;
import com.fthlbot.discordbotfthl.Commands.CommandImpl.LeagueCommandsImpl.UtilCommands.ShowDivisionWeekImpl;
import com.fthlbot.discordbotfthl.Commands.CommandImpl.StaffCommandsImpl.ChangeAliasImpl;
import com.fthlbot.discordbotfthl.Commands.CommandImpl.StaffCommandsImpl.ChangeClanImpl;
import com.fthlbot.discordbotfthl.Commands.CommandImpl.StaffCommandsImpl.ChangeRepImpl;
import com.fthlbot.discordbotfthl.Commands.CommandImpl.StaffCommandsImpl.RemoveAllChannelFromACategoryImpl;
import com.fthlbot.discordbotfthl.Commands.CommandImpl.StaffCommandsImpl.SchedulingCommands.AddDivisionWeeksImpl;
import com.fthlbot.discordbotfthl.Commands.CommandImpl.StaffCommandsImpl.SchedulingCommands.CreateMatchUps;
import com.fthlbot.discordbotfthl.Commands.CommandImpl.StaffCommandsImpl.SchedulingCommands.NegoChannelCreationImpl;
import com.fthlbot.discordbotfthl.DatabaseModels.CommandLogger.CommandLoggerService;
import com.fthlbot.discordbotfthl.Handlers.Command;
import com.fthlbot.discordbotfthl.Handlers.CommandListener;
import com.fthlbot.discordbotfthl.Handlers.MessageHandlers;
import com.fthlbot.discordbotfthl.Handlers.MessageHolder;
import com.fthlbot.discordbotfthl.Util.BotConfig;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.intent.Intent;
import org.javacord.api.entity.server.Server;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.SlashCommandOptionType;
import org.javacord.api.interaction.SlashCommandUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.text.ParseException;
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

    public DiscordBotFthlApplication(Environment env, PingImpl pingImpl, RegistrationImpl registration, RosterAdditionImpl rosterAddition, CommandLoggerService loggerService, RosterRemove rosterRemove, TeamRoster teamRoster, DefenseImpl attack, AllTeamsImpl allTeams, ChangeClanImpl changeClan, BotConfig config, ChangeRepImpl changeRep, ChangeAliasImpl changeAlias, AddDivisionWeeksImpl addDivisionWeeks, CreateMatchUps createMatchUps, NegoChannelCreationImpl negoChannelCreation, ShowDivisionWeekImpl showDivisionWeek, PlayerImpl player, RemoveAllChannelFromACategoryImpl removeAllChannelFromACategory) {
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
    }


    public static void main(String[] args) {
        SpringApplication.run(DiscordBotFthlApplication.class, args);
    }

    @Bean
    @ConfigurationProperties(value = "discord-bot")
    public DiscordApi api() throws ClashAPIException, IOException {
        log.info(config.getNegoStaffRoleID() + " id");
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneOffset.UTC));

        long testID = config.getTestServerID();


        clash = new JClash(env.getProperty("CLASH_EMAIL"), env.getProperty("CLASH_PASS"));

        DiscordApi api = new DiscordApiBuilder()
                .setToken(env.getProperty("TOKEN_TEST_BOT"))
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
        ArrayList<Server> servers = new ArrayList<>(api.getServers());
        log.info("Logged in as {}", api.getYourself().getDiscriminatedName());
        log.info("Watching servers {}", servers.size());
        try {
            log.info(config.getF5EndDate() + "");
        } catch (ParseException e) {
            e.printStackTrace();
        }


        //Add commands to the handler
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
                this.removeAllChannelFromACategory
        ));
        //Making help command
        HelpImpl help = new HelpImpl(commandList);
        //Add help command
        commandList.add(help);

        MessageHandlers messageHandlers = new MessageHandlers(commandList);

        MessageHolder messageHolder = messageHandlers.setCommands();
        CommandListener commandListener = new CommandListener(messageHolder, loggerService, config);

        api.addListener(commandListener);

//        SlashCommand command = SlashCommand.with("remove-channels-from-category", "Staff only command to delete channels from a category with logs!")
//                .setOptions(List.of(
//                        SlashCommandOption.create(SlashCommandOptionType.LONG,
//                                "category-id",
//                                "Enter the ID of the category you want to delete all channels from",
//                                true),
//                        SlashCommandOption.create(SlashCommandOptionType.BOOLEAN,
//                                "preserve-logs",
//                                "Set this to true to preserve logs of all channels (True by default)",
//                                false)
//                )).createForServer(api.getServerById(testID).get())
//                .join();
//        SlashCommandUpdater slashCommandUpdater = new SlashCommandUpdater(command.getId());
//        slashCommandUpdater.setName(command.getName());
//        slashCommandUpdater.setDescription(command.getDescription());
//        slashCommandUpdater.setSlashCommandOptions(List.of(
//                SlashCommandOption.create(SlashCommandOptionType.STRING,
//                        "category-id",
//                        "Enter the ID of the category you want to delete all channels from",
//                        true),
//                SlashCommandOption.create(SlashCommandOptionType.BOOLEAN,
//                        "preserve-logs",
//                        "Set this to true to preserve logs of all channels (True by default)",
//                        false)
//        )).updateForServer(api.getServerById(testID).get()).join();

        return api;
    }


}
