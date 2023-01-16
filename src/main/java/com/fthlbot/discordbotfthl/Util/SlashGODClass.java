package com.fthlbot.discordbotfthl.Util;

import com.fthlbot.discordbotfthl.Util.SlashCommandGenerics.SlashCommandTempImpl;
import org.javacord.api.DiscordApi;
import org.javacord.api.interaction.*;
import org.javacord.api.util.logging.ExceptionLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static org.javacord.api.interaction.SlashCommandOptionType.*;

@Component
public class SlashGODClass {

    Logger log = LoggerFactory.getLogger(SlashGODClass.class);
    private DiscordApi api;

    private final BotConfig botConfig;

    public SlashGODClass(BotConfig botConfig) {
        this.botConfig = botConfig;
    }

    public SlashGODClass setApi(DiscordApi api) {
        this.api = api;
        return this;
    }

    public DiscordApi getApi() {
        return api;
    }

    public SlashCommandBuilder createParseScheduleCommand() {
        return SlashCommand.with("parse-schedule", "parse json for scheduling command").setOptions(
                List.of(
                        SlashCommandOption.create(STRING, "json", "json to parse", true)
                )
        );
    }

    public SlashCommandBuilder createShowScheduleCommand(){
        return SlashCommand.with("show-schedule-wars", "show schedule").setOptions(
                List.of(
                        SlashCommandOption.create(LONG, "division-week-id", "division week to show", true)
                )
        );
    }

    public SlashCommandBuilder createRemoveChannelsFromCategoryExceptOne() {
        return SlashCommand.with("delete-category-with-channel", "Deletes multiple channels")
                .setOptions(
                        List.of(
                                SlashCommandOption.create(STRING, "categories", "categories to except", false)

                        )
                );
    }

    public SlashCommandBuilder createRegistrationCommand() {
        return SlashCommand
                .with("register", "command to register")
                .setOptions(List.of(
                        SlashCommandOption.create(STRING, "clan-tag", "tag of your main clan!", true),

                        new SlashCommandTempImpl().getDivisions(),
                        new SlashCommandTempImpl().getTeamName(),
                        SlashCommandOption.create(SlashCommandOptionType.USER,
                                "second-rep",
                                "mention the second representative for your team",
                                false
                        )
                ));
    }

    public SlashCommand serverCreateGiveRoleToRep() {
        return SlashCommand
                .with("give-role-to-rep", "command to give role to rep")
                .createForServer(api.getServerById(botConfig.getFthlServerID()).get()).join();
    }

    public SlashCommandBuilder createHelpCommand() {
        return SlashCommand.with("help", "command to get help")
                .setOptions(List.of(
                        SlashCommandOption.create(STRING,
                                "command",
                                "enter the command you want to get help for",
                                false
                        )
                ));
    }

    public SlashCommandBuilder createRosterAddCommand() {
        return SlashCommand.with("roster-add", "command to add accounts to your roster")
                .setOptions(List.of(
                        new SlashCommandTempImpl().getDivisions(),
                        new SlashCommandTempImpl().getTeamName(),
                        SlashCommandOption.create(
                                STRING,
                                "tags",
                                "Enter player tags, separated by a space",
                                true
                        )
                ));

    }

    public SlashCommandBuilder createRosterRemoveCommand() {
        return SlashCommand
                .with("roster-remove", "command to remove accounts from your roster")
                .setOptions(List.of(
                        new SlashCommandTempImpl().getDivisions(),
                        new SlashCommandTempImpl().getTeamName(),
                        SlashCommandOption.create(
                                STRING,
                                "tags",
                                "Enter player tags, separated by a space",
                                true
                        )
                ));

    }

    public SlashCommandBuilder createTeamRosterCommand() {
        return SlashCommand
                .with("team-roster", "A command to view the roster for a team!")
                .setOptions(List.of(
                        new SlashCommandTempImpl().getDivisions(),
                        new SlashCommandTempImpl().getTeamName()
                        ));
    }

    public SlashCommandBuilder createDefenseCommand() {
        return SlashCommand.with(
                        "defense",
                        "Fetches defense from the current ongoing war of the provided clan tag")
                .setOptions(List.of(
                                SlashCommandOption.create(STRING,
                                        "tag",
                                        "enter a valid clan tag",
                                        true
                                )
                        )
                );

    }

    public SlashCommandBuilder createTeamAllCommand() {
        return SlashCommand.with(
                        "all-teams",
                        "Returns a list of all the teams participating in a given division")
                .setOptions(List.of(
                        new SlashCommandTempImpl().getDivisions()));
    }

    public SlashCommandBuilder createChangeRepCommand() {
        return SlashCommand
                .with("change-rep", "staff only command to change rep of a team")
                .setOptions(List.of(
                        new SlashCommandTempImpl().getDivisions(),
                        new SlashCommandTempImpl().getTeamName(),
                        SlashCommandOption.create(SlashCommandOptionType.USER,
                                "old-rep",
                                "Mention the old representative of the team",
                                true
                        ), SlashCommandOption.create(SlashCommandOptionType.USER,
                                "new-rep",
                                "Mention the new representative of the team",
                                true
                        )
                ));

    }

    public SlashCommandBuilder createChangeClanCommand() {
        return SlashCommand
                .with("change-clan", "staff only command to change clan tag of a team")
                .setOptions(List.of(
                        new SlashCommandTempImpl().getDivisions(),
                        new SlashCommandTempImpl().getTeamName(),
                        SlashCommandOption.create(SlashCommandOptionType.STRING,
                                "clan-tag",
                                "enter the new clan tag for your team",
                                true
                        )
                ));
    }

    public SlashCommandBuilder createAddWeeksCommand() {
        return SlashCommand
                .with("add-weeks", "staff only command to add weeks to a specific division")
                .setOptions(List.of(
                        new SlashCommandTempImpl().getDivisions(), SlashCommandOption.create(SlashCommandOptionType.STRING,
                                "json",
                                "Enter a json array with 3 fields, `start`, `end` and `byeWeek`",
                                true
                        )
                ));
    }

    public SlashCommandBuilder createMatchUpsCommand() {
        return SlashCommand
                .with("create-matchups", "A command to create matchups for a division week. restricted to staff")
                .setOptions(List.of(
                        SlashCommandOption.create(SlashCommandOptionType.STRING,
                                "json",
                                "Enter a json, fields: `divWeekID` and an array `schedule` with two field home and enemy (teams ID),",
                                true
                        )
                ));
    }

    public SlashCommandBuilder createShowDivisionWeekCommand() {
        return SlashCommand
                .with("show-divisionweek", "Commands to view weeks date for a specific division")
                .setOptions(List.of(
                        new SlashCommandTempImpl().getDivisions()
                        ));
    }

    public SlashCommandBuilder createCreateNegotiationChannelsCommand() {
        return SlashCommand
                .with("create-negotiation-channels", "staff only command to create negotiation channels")
                .setOptions(List.of(
                        SlashCommandOption.create(SlashCommandOptionType.LONG,
                                "division-week-id",
                                "The ID of the division week to make channels for",
                                true
                        )
                ));
    }

    public SlashCommandBuilder createPlayerCommand() {
        return SlashCommand
                .with("player", "A command to view a player's profile")
                .setOptions(List.of(
                        SlashCommandOption.create(SlashCommandOptionType.STRING,
                                "player-tag",
                                "Enter the player's tag",
                                true
                        )
                ));
    }

    public SlashCommandBuilder createStatsCommand() {
        return SlashCommand
                .with("stats", "A command to view a current war for a clan")
                .setOptions(List.of(
                        SlashCommandOption.create(SlashCommandOptionType.STRING,
                                "clan-tag",
                                "Enter the clan tag",
                                true
                        )
                ));
    }

    public SlashCommandBuilder createRemoveAllChannelFromACategoryCommand() {
        return SlashCommand.with("remove-channels-from-category", "Staff only command to delete channels from a category with logs!")
                .setOptions(List.of(
                        SlashCommandOption.create(SlashCommandOptionType.STRING,
                                "category-id",
                                "Enter the ID of the category you want to delete all channels from",
                                true),
                        SlashCommandOption.create(SlashCommandOptionType.BOOLEAN,
                                "preserve-logs",
                                "Set this to true to preserve logs of all channels (True by default)",
                                false)
                ));
    }

    public SlashCommandBuilder createTeamInformationCommand() {
        return SlashCommand.with("team-information", "A command get information about a team")
                .setOptions(List.of(
                        new SlashCommandTempImpl().getDivisions(),
                        new SlashCommandTempImpl().getTeamName()
                ));
    }

    public SlashCommandBuilder createCreateAllDivisionCommand() {
        return SlashCommand.with(
                        "create-all-divisions",
                        "Staff only command to create all division channels"
                );
    }

    public SlashCommandBuilder createDeleteATeamCommand() {
        return SlashCommand.with("delete-a-team", "Staff only command to delete a team")
                .setOptions(List.of(
                        new SlashCommandTempImpl().getDivisions(),
                        new SlashCommandTempImpl().getTeamName()
                        ));
    }

    public SlashCommandBuilder createAttackCommand() {
        return SlashCommand.with("attack", "Get attacks for a clan")
                .setOptions(List.of(SlashCommandOption.createWithChoices(SlashCommandOptionType.STRING,
                        "clan-tag",
                        "Enter the clan tag you want to get attacks for",
                        true
                )));
    }

    public SlashCommandBuilder createSnitchCommand() {
        return SlashCommand.with("snitch", "Generates a snitch image.")
                .setOptions(List.of(SlashCommandOption.createWithChoices(USER,
                        "user",
                        "Enter the user you want to get a snitch for",
                        true
                )));
    }

    public SlashCommandBuilder createCheckLineUpCommand() {
        return SlashCommand.with("check-lineup", "Checks the line up for a FTHL war")
                .setOptions(List.of(SlashCommandOption.createWithChoices(SlashCommandOptionType.STRING,
                        "schedule-id",
                        "Enter the schedule ID you want to check, it can be found in your negotiation channel topic",
                        true
                )));
    }

    public SlashCommandBuilder createFPcheckCommand() {
        return SlashCommand.with("fp-check", "Checks the FP for a clan")
                .setOptions(
                        List.of(new SlashCommandTempImpl().getDivisions())
                );
    }

    public SlashCommandBuilder createClanInfoCommand() {
        return SlashCommand.with("clan-info", "Get clan information")
                .setOptions(List.of(
                        SlashCommandOption.create(
                                STRING,
                                "clan-tag",
                                "Enter a clan tag.  ex: #2PP",
                                true
                        )
                ));
    }

    public SlashCommandBuilder createSuggestionCommand() {
        return SlashCommand.with("suggestion", "Suggest a new feature");
    }

    public SlashCommandBuilder createEditTransactionCommand() {
        return SlashCommand.with("edit-transaction", "edit transaction of a team!")
                .addOption(
                        new SlashCommandTempImpl().getDivisions()
                ).addOption(
                        new SlashCommandTempImpl().getTeamName()
                ).addOption(
                        SlashCommandOption.create(
                                LONG,
                                "new-transaction-points",
                                "A new value for the transaction",
                                true
                        )
                );
    }

    public void makeAllCommands() {
        //Make a Method array
        Method[] methods = this.getClass().getDeclaredMethods();
        ArrayList<SlashCommandBuilder> l = new ArrayList<>();
        for (Method method : methods) {
            if (method.getName().startsWith("create")) {
                try {
                    log.info("Creating command: " + method.getName());
                    SlashCommandBuilder invoke = (SlashCommandBuilder) method.invoke(this);
                    l.add(invoke);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        api.bulkOverwriteGlobalApplicationCommands(new HashSet<>(l)).join();
        log.info("Commands should be done!");
    }

    public SlashCommandBuilder createCheckEveryRepJoinedTheServer() {
        return SlashCommand.with("check-rep-joined-the-server", "Checks if every rep has joined the server")
                .addOption(SlashCommandOption.create(LONG, "server-id", "Enter the server ID you want to check, Bot must be present in the server you want to check", true));
    }

    public SlashCommandBuilder createDivisionEditor() {
        return SlashCommand.with("division-editor", "Staff only command to edit a division")
                .setOptions(
                        List.of(
                                new SlashCommandTempImpl().getDivisions(),
                                SlashCommandOption.createWithChoices(STRING,
                                        "to-change",
                                        "select what you want to change",
                                        true,
                                        asList(
                                                SlashCommandOptionChoice.create("name", "name"),
                                                SlashCommandOptionChoice.create("alias", "alias"),
                                                SlashCommandOptionChoice.create("allowed roster change", "allowed roster change"),
                                                SlashCommandOptionChoice.create("roster size", "roster size"),
                                                SlashCommandOptionChoice.create("allowed townhall", "allowed townhall")
                                        )
                                )
                        )
                );
    }


    public SlashCommandBuilder createChangeAliasCommand() {
        return SlashCommand
                .with("change-alias", "staff only command to change alias of a team")
                .setOptions(List.of(
                        new SlashCommandTempImpl().getDivisions(),
                        new SlashCommandTempImpl().getTeamName(),
                        SlashCommandOption.create(STRING,
                                "new-alias",
                                "Enter the new alias for the team",
                                true
                        )
                ));
    }

    public void e() {
        CompletableFuture<Void> voidCompletableFuture = this.getApi().getGlobalSlashCommands()
                .thenApply(x -> x.stream().filter(e -> e.getName().equalsIgnoreCase("change-alias")))
                .thenApply(Stream::findFirst)
                .thenAccept(x -> {
                    if (x.isEmpty()) {
                        System.out.println("No command found");
                        return;
                    }
                    System.out.println("Found command");
                    SlashCommandUpdater s = new SlashCommandUpdater(x.get().getId()).setSlashCommandOptions(
                            List.of(
                                    new SlashCommandTempImpl().getDivisions(),
                                    new SlashCommandTempImpl().getTeamName(),
                                    SlashCommandOption.create(STRING,
                                            "new-alias",
                                            "Enter the new alias for the team",
                                            true
                                    )
                            ));

                    s.updateGlobal(this.api).exceptionally(ExceptionLogger.get()).join();
                });
    }

    public SlashCommandBuilder createInfoCommand() {
        return SlashCommand.with("info", "Shows bots information");
    }

    public SlashCommandBuilder createForcePush() {
        return SlashCommand.with("force-push", "Force pushes accounts to a teams roster")
                .setOptions(List.of(
                        new SlashCommandTempImpl().getDivisions(),
                        new SlashCommandTempImpl().getTeamName(),

                        SlashCommandOption.create(
                                STRING,
                                "tags",
                                "Enter player tags, separated by a space",
                                true
                        )
                ));

    }
    public SlashCommandBuilder createRepBanCommand(){
        return SlashCommand.with("ban-rep", "bans a user from representing a team").setOptions(
                List.of(
                        SlashCommandOption.create(
                                USER,
                                "user",
                                "Mention the user to be banned",
                                false
                        ), SlashCommandOption.create(
                                LONG,
                                "discord-id",
                                "discord id of the user to be banned",
                                false
                        ), SlashCommandOption.create(
                                STRING,
                                "reason",
                                "Reason of ban",
                                false
                        ), SlashCommandOption.create(
                                STRING,
                                "notes",
                                "additional notes",
                                false
                        )
                )
        );
    }
    public SlashCommandBuilder createClanLineupCommand() {
        return SlashCommand.with("clan-lineup", "Shows the lineup of a clan")
                .setOptions(List.of(
                        SlashCommandOption.createWithChoices(SlashCommandOptionType.STRING,
                                "clan-tag",
                                "enter the clan tag of the clan you want to see the lineup of",
                                true
                        )
                ));
    }

    public SlashCommandBuilder createMinionBotStringCommand(){
        return SlashCommand.with("minion-bot-string", "Shows the string to add to your bot")
                .setOptions(List.of(
                        SlashCommandOption.createWithChoices(SlashCommandOptionType.STRING,
                                "div-week-id",
                                "enter the clan tag of the clan you want to see the lineup of",
                                true
                        )
                ));
    }

    public SlashCommandBuilder createAddRegistrationChannelForATeam() {
        return SlashCommand.with( "add-registration-channel", "Adds the registration channel for a team!")
                .setOptions(
                        List.of(
                                new SlashCommandTempImpl().getDivisions(),
                                new SlashCommandTempImpl().getTeamName(),
                                SlashCommandOption.createWithChoices(CHANNEL,
                                        "channel",
                                        "Select a channel",
                                        true)
                        )
                );
    }

    public SlashCommandBuilder createChangeRepWithID() {
        return SlashCommand.with("change-rep-with-id",  "changed Rep of a team with ID")
                .setOptions(
                        List.of(
                                new SlashCommandTempImpl().getDivisions(),
                                new SlashCommandTempImpl().getTeamName(),
                                SlashCommandOption.create(STRING,
                                        "old-rep-id",
                                        "enter the ID of old rep",
                                        true),
                                SlashCommandOption.create(STRING,
                                        "new-rep-id",
                                        "enter the ID of new rep",
                                        true)
                        )
                );
    }

    public SlashCommandBuilder createCreateMatchUpNewCommand() {
        return SlashCommand.with("create-match-ups-new", "A new way to create match ups, without json!")
                .setOptions(
                        List.of(
                                new SlashCommandTempImpl().getDivisions(),
                                SlashCommandOption.createLongOption(
                                        "div-week",
                                        "enter the div week!",
                                        true,
                                        true
                                ),
                                SlashCommandOption.create(
                                        STRING,
                                        "parsable-string",
                                        "TEAM_IDvTEAM_ID...",
                                        true
                                )
                        )
                );
    }
}
