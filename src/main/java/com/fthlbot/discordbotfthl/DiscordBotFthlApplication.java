package com.fthlbot.discordbotfthl;

import Core.JClash;
import Core.exception.ClashAPIException;
import com.fthlbot.discordbotfthl.Commands.CommandImpl.ClashCommandImpl.DefenseImpl;
import com.fthlbot.discordbotfthl.Commands.CommandImpl.LeagueCommandsImpl.*;
import com.fthlbot.discordbotfthl.Commands.CommandImpl.LeagueCommandsImpl.RosterAdd.RosterAdditionImpl;
import com.fthlbot.discordbotfthl.Commands.CommandImpl.LeagueCommandsImpl.TeamRoster.TeamRoster;
import com.fthlbot.discordbotfthl.Commands.CommandImpl.StaffCommandsImpl.ChangeAliasImpl;
import com.fthlbot.discordbotfthl.Commands.CommandImpl.StaffCommandsImpl.ChangeClanImpl;
import com.fthlbot.discordbotfthl.Commands.CommandImpl.StaffCommandsImpl.ChangeRepImpl;
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
import org.javacord.api.interaction.SlashCommandOptionChoice;
import org.javacord.api.interaction.SlashCommandOptionType;
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

import static java.util.Arrays.asList;

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





    public DiscordBotFthlApplication(Environment env, PingImpl pingImpl, RegistrationImpl registration, RosterAdditionImpl rosterAddition, CommandLoggerService loggerService, RosterRemove rosterRemove, TeamRoster teamRoster, DefenseImpl attack, AllTeamsImpl allTeams, ChangeClanImpl changeClan, BotConfig config, ChangeRepImpl changeRep, ChangeAliasImpl changeAlias) {
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
                this.changeAlias
        ));
        //Making help command
        HelpImpl help = new HelpImpl(commandList);
        //Add help command
        commandList.add(help);

        MessageHandlers messageHandlers = new MessageHandlers(commandList);

        MessageHolder messageHolder = messageHandlers.setCommands();
        CommandListener commandListener = new CommandListener(messageHolder, loggerService, config);

        api.addListener(commandListener);
        SlashCommand command = SlashCommand
                .with("change-alias", "staff only command to change alias of a team")
                .setOptions(List.of(
                        SlashCommandOption.createWithChoices(SlashCommandOptionType.STRING,
                                "division",
                                "choose from one of the following division",
                                true,
                                asList(
                                        SlashCommandOptionChoice.create("f8", "f8"),
                                        SlashCommandOptionChoice.create("f5", "f5"),
                                        SlashCommandOptionChoice.create("f9", "f9"),
                                        SlashCommandOptionChoice.create("f11", "f11"),
                                        SlashCommandOptionChoice.create("f10", "f10"),
                                        SlashCommandOptionChoice.create("fmix", "fmix")
                                )
                        ),SlashCommandOption.create(SlashCommandOptionType.STRING,
                                "team-identifier",
                                "Enter the name of your team or its alias",
                                true
                        ),
                        SlashCommandOption.create(SlashCommandOptionType.STRING,
                                "new-alias",
                                "enter the new alias for your team",
                                true
                        )
                ))
                .createForServer(api.getServerById(testID).get())
                .join();

        return api;
    }


}
