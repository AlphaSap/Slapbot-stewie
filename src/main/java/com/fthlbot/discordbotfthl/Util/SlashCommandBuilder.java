package com.fthlbot.discordbotfthl.Util;

import org.javacord.api.DiscordApi;
import org.javacord.api.interaction.*;
import org.javacord.api.util.logging.ExceptionLogger;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static java.util.Arrays.asList;
import static org.javacord.api.interaction.SlashCommandOptionType.*;

@Component
public class SlashCommandBuilder {
    private DiscordApi api;

    private final BotConfig botConfig;

    public SlashCommandBuilder(BotConfig botConfig) {
        this.botConfig = botConfig;
    }

    public SlashCommandBuilder setApi(DiscordApi api) {
        this.api = api;
        return this;
    }

    public DiscordApi getApi() {
        return api;
    }

//    public SlashCommand createPingCommand() {
//        return SlashCommand.with("ping", "To check bots latency").createGlobal(getApi()).join();
//    }

    public SlashCommand createParseScheduleCommand() {
        return SlashCommand.with("parse-schedule", "parse json for scheduling command").setOptions(
                List.of(
                        SlashCommandOption.create(STRING, "json", "json to parse", true)
                )
        ).createGlobal(getApi()).join();
    }

    public SlashCommand createRegistrationCommand() {
        return SlashCommand
                .with("register", "command to register")
                .setOptions(List.of(
                        SlashCommandOption.create(STRING, "clan-tag", "tag of your main clan!", true),
                        SlashCommandOption.createWithChoices(STRING,
                                "division",
                                "choose from one of the following division",
                                true,
                                asList(
                                        SlashCommandOptionChoice.create("f8", "f8"),
                                        SlashCommandOptionChoice.create("f9", "f9"),
                                        SlashCommandOptionChoice.create("f10", "f10"),
                                        SlashCommandOptionChoice.create("f11", "f11"),
                                        SlashCommandOptionChoice.create("Lite", "Lite"),
                                        SlashCommandOptionChoice.create("Elite", "Elite")
                                )
                        ),
                        SlashCommandOption.create(STRING,
                                "team-alias",
                                "enter your team alias!",
                                true
                        ),
                        SlashCommandOption.create(SlashCommandOptionType.USER,
                                "second-rep",
                                "mention the second representative for your team",
                                false
                        )
                ))
                .createGlobal(getApi()).join();
    }

    public SlashCommand createGiveRoleToRep() {
        return SlashCommand
                .with("give-role-to-rep", "command to give role to rep")
                .createForServer(api.getServerById(botConfig.getFthlServerID()).get()).join();
    }

    public SlashCommand createHelpCommand() {
        return SlashCommand.with("help", "command to get help")
                .setOptions(List.of(
                        SlashCommandOption.create(STRING,
                                "command",
                                "enter the command you want to get help for",
                                false
                        )
                ))
                .createGlobal(getApi()).join();
    }

    public SlashCommand createRosterAddCommand() {
        return SlashCommand.with("roster-add", "command to add accounts to your roster")
                .setOptions(List.of(
                        SlashCommandOption.createWithChoices(STRING,
                                "division",
                                "choose from one of the following division",
                                true,
                                asList(
                                        SlashCommandOptionChoice.create("f8", "f8"),
                                        SlashCommandOptionChoice.create("f9", "f9"),
                                        SlashCommandOptionChoice.create("f10", "f10"),
                                        SlashCommandOptionChoice.create("f11", "f11"),
                                        SlashCommandOptionChoice.create("Lite", "Lite"),
                                        SlashCommandOptionChoice.create("Elite", "Elite")
                                )
                        ),
                        SlashCommandOption.create(
                                STRING,
                                "team-identifier",
                                "Enter your team's alias or name",
                                true
                        ),
                        SlashCommandOption.create(
                                STRING,
                                "tags",
                                "Enter player tags, separated by a space",
                                true
                        )
                )).createGlobal(getApi()).join();

    }

    public SlashCommand createRosterRemoveCommand() {
        return SlashCommand
                .with("roster-remove", "command to remove accounts from your roster")
                .setOptions(List.of(
                        SlashCommandOption.createWithChoices(STRING,
                                "division",
                                "choose from one of the following division",
                                true,
                                asList(
                                        SlashCommandOptionChoice.create("f8", "f8"),
                                        SlashCommandOptionChoice.create("f9", "f9"),
                                        SlashCommandOptionChoice.create("f10", "f10"),
                                        SlashCommandOptionChoice.create("f11", "f11"),
                                        SlashCommandOptionChoice.create("Lite", "Lite"),
                                        SlashCommandOptionChoice.create("Elite", "Elite")
                                )
                        ),
                        SlashCommandOption.create(
                                STRING,
                                "team-identifier",
                                "Enter your team's alias or name",
                                true
                        ),
                        SlashCommandOption.create(
                                STRING,
                                "tags",
                                "Enter player tags, separated by a space",
                                true
                        )
                )).createGlobal(getApi()).join();

    }

    public SlashCommand createTeamRosterCommand() {
        return SlashCommand
                .with("team-roster", "A command to view the roster for a team!")
                .setOptions(List.of(
                        SlashCommandOption.createWithChoices(STRING,
                                "division",
                                "choose from one of the following division",
                                true,
                                asList(
                                        SlashCommandOptionChoice.create("f8", "f8"),
                                        SlashCommandOptionChoice.create("f9", "f9"),
                                        SlashCommandOptionChoice.create("f10", "f10"),
                                        SlashCommandOptionChoice.create("f11", "f11"),
                                        SlashCommandOptionChoice.create("Lite", "Lite"),
                                        SlashCommandOptionChoice.create("Elite", "Elite")
                                )
                        ),
                        SlashCommandOption.create(
                                STRING,
                                "team-identifier",
                                "Enter your team's alias or name",
                                true
                        )
                )).createGlobal(getApi()).join();
    }

    public SlashCommand createDefenseCommand() {
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
                ).createGlobal(getApi()).join();

    }

    public SlashCommand createTeamAllCommand() {
        return SlashCommand.with(
                        "all-teams",
                        "Returns a list of all the teams participating in a given division")
                .setOptions(List.of(
                        SlashCommandOption.createWithChoices(SlashCommandOptionType.STRING,
                                "division",
                                "choose from one of the following division",
                                true,
                                asList(
                                        SlashCommandOptionChoice.create("f8", "f8"),
                                        SlashCommandOptionChoice.create("f9", "f9"),
                                        SlashCommandOptionChoice.create("f10", "f10"),
                                        SlashCommandOptionChoice.create("f11", "f11"),
                                        SlashCommandOptionChoice.create("Lite", "Lite"),
                                        SlashCommandOptionChoice.create("Elite", "Elite")
                                )
                        )))
                .createGlobal(getApi()).join();
    }

    public SlashCommand createChangeRepCommand() {
        return SlashCommand
                .with("change-rep", "staff only command to change rep of a team")
                .setOptions(List.of(
                        SlashCommandOption.createWithChoices(SlashCommandOptionType.STRING,
                                "division",
                                "choose from one of the following division",
                                true,
                                asList(
                                        SlashCommandOptionChoice.create("f8", "f8"),
                                        SlashCommandOptionChoice.create("f9", "f9"),
                                        SlashCommandOptionChoice.create("f10", "f10"),
                                        SlashCommandOptionChoice.create("f11", "f11"),
                                        SlashCommandOptionChoice.create("Lite", "Lite"),
                                        SlashCommandOptionChoice.create("Elite", "Elite")
                                )
                        ), SlashCommandOption.create(SlashCommandOptionType.STRING,
                                "team-identifier",
                                "Enter the name of your team or its alias",
                                true
                        ),
                        SlashCommandOption.create(SlashCommandOptionType.USER,
                                "old-rep",
                                "Mention the old representative of the team",
                                true
                        ), SlashCommandOption.create(SlashCommandOptionType.USER,
                                "new-rep",
                                "Mention the new representative of the team",
                                true
                        )
                ))
                .createGlobal(getApi()).join();

    }

    public SlashCommand createChangeClanCommand() {
        return SlashCommand
                .with("change-clan", "staff only command to change clan tag of a team")
                .setOptions(List.of(
                        SlashCommandOption.createWithChoices(SlashCommandOptionType.STRING,
                                "division",
                                "choose from one of the following division",
                                true,
                                asList(
                                        SlashCommandOptionChoice.create("f8", "f8"),
                                        SlashCommandOptionChoice.create("f9", "f9"),
                                        SlashCommandOptionChoice.create("f10", "f10"),
                                        SlashCommandOptionChoice.create("f11", "f11"),
                                        SlashCommandOptionChoice.create("Lite", "Lite"),
                                        SlashCommandOptionChoice.create("Elite", "Elite")
                                )
                        ), SlashCommandOption.create(SlashCommandOptionType.STRING,
                                "team-identifier",
                                "Enter the name of your team or its alias",
                                true
                        ),
                        SlashCommandOption.create(SlashCommandOptionType.STRING,
                                "clan-tag",
                                "enter the new clan tag for your team",
                                true
                        )
                ))
                .createGlobal(getApi()).join();
    }

    public SlashCommand createAddWeeksCommand() {
        return SlashCommand
                .with("add-weeks", "staff only command to add weeks to a specific division")
                .setOptions(List.of(
                        SlashCommandOption.createWithChoices(SlashCommandOptionType.STRING,
                                "division",
                                "choose from one of the following division",
                                true,
                                asList(
                                        SlashCommandOptionChoice.create("f8", "f8"),
                                        SlashCommandOptionChoice.create("f9", "f9"),
                                        SlashCommandOptionChoice.create("f10", "f10"),
                                        SlashCommandOptionChoice.create("f11", "f11"),
                                        SlashCommandOptionChoice.create("Lite", "Lite"),
                                        SlashCommandOptionChoice.create("Elite", "Elite")
                                )
                        ), SlashCommandOption.create(SlashCommandOptionType.STRING,
                                "json",
                                "Enter a json array with 3 fields, `start`, `end` and `byeWeek`",
                                true
                        )
                ))
                .createGlobal(getApi()).join();
    }

    public SlashCommand createMatchUpsCommand() {
        return SlashCommand
                .with("create-matchups", "A command to create matchups for a division week. restricted to staff")
                .setOptions(List.of(
                        SlashCommandOption.create(SlashCommandOptionType.STRING,
                                "json",
                                "Enter a json, fields: `divWeekID` and an array `schedule` with two field home and enemy (teams ID),",
                                true
                        )
                ))
                .createGlobal(getApi()).join();
    }

    public SlashCommand createShowDivisionWeekCommand() {
        return SlashCommand
                .with("show-divisionweek", "Commands to view weeks date for a specific division")
                .setOptions(List.of(
                        SlashCommandOption.createWithChoices(SlashCommandOptionType.STRING,
                                "division",
                                "choose from one of the following division",
                                true,
                                asList(
                                        SlashCommandOptionChoice.create("f8", "f8"),
                                        SlashCommandOptionChoice.create("f9", "f9"),
                                        SlashCommandOptionChoice.create("f10", "f10"),
                                        SlashCommandOptionChoice.create("f11", "f11"),
                                        SlashCommandOptionChoice.create("Lite", "Lite"),
                                        SlashCommandOptionChoice.create("Elite", "Elite"))
                        )
                ))
                .createGlobal(getApi()).join();
    }

    public SlashCommand createCreateNegotiationChannelsCommand() {
        return SlashCommand
                .with("create-negotiation-channels", "staff only command to create negotiation channels")
                .setOptions(List.of(
                        SlashCommandOption.create(SlashCommandOptionType.LONG,
                                "division-week-id",
                                "The ID of the division week to make channels for",
                                true
                        )
                ))
                .createGlobal(getApi()).join();
    }

    public SlashCommand createPlayerCommand() {
        return SlashCommand
                .with("player", "A command to view a player's profile")
                .setOptions(List.of(
                        SlashCommandOption.create(SlashCommandOptionType.STRING,
                                "player-tag",
                                "Enter the player's tag",
                                true
                        )
                ))
                .createGlobal(getApi()).join();
    }

    public SlashCommand createStatsCommand() {
        return SlashCommand
                .with("stats", "A command to view a current war for a clan")
                .setOptions(List.of(
                        SlashCommandOption.create(SlashCommandOptionType.STRING,
                                "clan-tag",
                                "Enter the clan tag",
                                true
                        )
                ))
                .createGlobal(getApi()).join();
    }

    public SlashCommand createRemoveAllChannelFromACategoryCommand() {
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
                )).createGlobal(getApi()).join();
    }

    public SlashCommand createTeamInformationCommand() {
        return SlashCommand.with("team-information", "A command get information about a team")
                .setOptions(List.of(SlashCommandOption.createWithChoices(STRING,
                                "division",
                                "choose from one of the following division",
                                true,
                                asList(
                                        SlashCommandOptionChoice.create("f8", "f8"),
                                        SlashCommandOptionChoice.create("f9", "f9"),
                                        SlashCommandOptionChoice.create("f10", "f10"),
                                        SlashCommandOptionChoice.create("f11", "f11"),
                                        SlashCommandOptionChoice.create("Lite", "Lite"),
                                        SlashCommandOptionChoice.create("Elite", "Elite")
                                )
                        ),
                        SlashCommandOption.create(SlashCommandOptionType.STRING,
                                "team-identifier",
                                "Enter the name/alias of the team you want to get information about",
                                true)
                )).createGlobal(getApi()).join();
    }

    public SlashCommand createCreateAllDivisionCommand() {
        return SlashCommand.with(
                        "create-all-divisions",
                        "Staff only command to create all division channels"
                )
                .createGlobal(getApi()).join();
    }

    public SlashCommand createDeleteATeamCommand() {
        return SlashCommand.with("delete-a-team", "Staff only command to delete a team")
                .setOptions(List.of(SlashCommandOption.createWithChoices(SlashCommandOptionType.STRING,
                                "division",
                                "choose from one of the following division",
                                true,
                                asList(
                                        SlashCommandOptionChoice.create("f8", "f8"),
                                        SlashCommandOptionChoice.create("f9", "f9"),
                                        SlashCommandOptionChoice.create("f10", "f10"),
                                        SlashCommandOptionChoice.create("f11", "f11"),
                                        SlashCommandOptionChoice.create("Lite", "Lite"),
                                        SlashCommandOptionChoice.create("Elite", "Elite")
                                )
                        ),
                        SlashCommandOption.create(SlashCommandOptionType.STRING,
                                "team-identifier",
                                "Enter the name/alias of the team you want to delete",
                                true)
                )).createGlobal(getApi()).join();
    }

    public SlashCommand createAttackCommand() {
        return SlashCommand.with("attack", "Get attacks for a clan")
                .setOptions(List.of(SlashCommandOption.createWithChoices(SlashCommandOptionType.STRING,
                        "clan-tag",
                        "Enter the clan tag you want to get attacks for",
                        true
                ))).createGlobal(getApi()).join();
    }

    public SlashCommand createSnitchCommand() {
        return SlashCommand.with("snitch", "Generates a snitch image.")
                .setOptions(List.of(SlashCommandOption.createWithChoices(USER,
                        "user",
                        "Enter the user you want to get a snitch for",
                        true
                ))).createGlobal(getApi()).join();
    }

    public SlashCommand createCheckLineUpCommand() {
        return SlashCommand.with("check-lineup", "Checks the line up for a FTHL war")
                .setOptions(List.of(SlashCommandOption.createWithChoices(SlashCommandOptionType.STRING,
                        "schedule-id",
                        "Enter the schedule ID you want to check, it can be found in your negotiation channel topic",
                        true
                ))).createGlobal(getApi()).join();
    }

    public SlashCommand createFPcheckCommand() {
        return SlashCommand.with("fp-check", "Checks the FP for a clan")
                .setOptions(List.of(SlashCommandOption.createWithChoices(
                        SlashCommandOptionType.STRING,
                        "division",
                        "Enter the division you want to check, it can be found in your negotiation channel topic",
                        true,
                        asList(
                                SlashCommandOptionChoice.create("f8", "f8"),
                                SlashCommandOptionChoice.create("f9", "f9"),
                                SlashCommandOptionChoice.create("f10", "f10"),
                                SlashCommandOptionChoice.create("f11", "f11"),
                                SlashCommandOptionChoice.create("Lite", "Lite"),
                                SlashCommandOptionChoice.create("Elite", "Elite")
                        )
                ))).createGlobal(getApi()).join();
    }

    public SlashCommand createClanInfoCommand() {
        return SlashCommand.with("clan-info", "Get clan information")
                .setOptions(List.of(
                        SlashCommandOption.create(
                                STRING,
                                "clan-tag",
                                "Enter a clan tag.  ex: #2PP",
                                true
                        )
                )).createGlobal(getApi()).join();
    }

    public SlashCommand createSuggestionCommand() {
        return SlashCommand.with("suggestion", "Suggest a new feature").createGlobal(getApi()).join();
    }

    public void makeAllCommands() {
        //Make a Method array
        Method[] methods = this.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (method.getName().startsWith("create")) {
                try {
                    System.out.println("Creating command: " + method.getName());
                    method.invoke(this);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //delete all command
    public void deleteAllCommand() {
        this.getApi().getGlobalSlashCommands().thenAccept(globalSlashCommands -> {
            globalSlashCommands.forEach(e -> {
                System.out.println("Deleting command: " + e.getName());
                e.deleteGlobal();
            });
        }).join();
    }

    public SlashCommand createCheckEveryRepJoinedTheServer() {
        return SlashCommand.with("check-rep-joined-the-server", "Checks if every rep has joined the server")
                .addOption(SlashCommandOption.create(LONG, "server-id", "Enter the server ID you want to check, Bot must be present in the server you want to check", true))
                .createGlobal(getApi()).join();
    }

    public SlashCommand createDivisionEditor() {
        return SlashCommand.with("division-editor", "Staff only command to edit a division")
                .setOptions(
                        List.of(
                                SlashCommandOption.createWithChoices(STRING,
                                        "division",
                                        "choose from one of the following division",
                                        true,
                                        asList(
                                                SlashCommandOptionChoice.create("f8", "f8"),
                                                SlashCommandOptionChoice.create("f9", "f9"),
                                                SlashCommandOptionChoice.create("f10", "f10"),
                                                SlashCommandOptionChoice.create("f11", "f11"),
                                                SlashCommandOptionChoice.create("Lite", "Lite"),
                                                SlashCommandOptionChoice.create("Elite", "Elite")
                                        )
                                ),
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
                )
                .createGlobal(getApi())
                .exceptionally(ExceptionLogger.get())
                .join();
    }

    public void deleteACommand(String name, long serverId) {
        this.getApi().getServerSlashCommands(this.getApi().getServerById(serverId).get()).thenAccept(x -> {
            x.stream().filter(e -> e.getName().equals(name)).forEach(e -> {
                System.out.println("Deleting command: " + e.getName());
                e.deleteForServer(serverId);
            });
        });
    }

    public void deleteACommand(String name) throws Exception {
        List<SlashCommand> x = this.getApi().getGlobalSlashCommands().join();
        Optional<SlashCommand> first = x.stream().filter(e -> e.getName().equalsIgnoreCase(name)).findFirst();
        if (first.isEmpty()) {
            throw new Exception("Cannot find the command to delete");
        }
        first.get().deleteGlobal();
    }

    public SlashCommand createChangeAliasCommand() {
        return SlashCommand
                .with("change-alias", "staff only command to change alias of a team")
                .setOptions(List.of(
                        SlashCommandOption.createWithChoices(SlashCommandOptionType.STRING,
                                "division",
                                "choose from one of the following division",
                                true,
                                asList(
                                        SlashCommandOptionChoice.create("f8", "f8"),
                                        SlashCommandOptionChoice.create("f9", "f9"),
                                        SlashCommandOptionChoice.create("f10", "f10"),
                                        SlashCommandOptionChoice.create("f11", "f11"),
                                        SlashCommandOptionChoice.create("Lite", "Lite"),
                                        SlashCommandOptionChoice.create("Elite", "Elite")
                                )
                        ), SlashCommandOption.create(SlashCommandOptionType.STRING,
                                "team-identifier",
                                "Enter the name of your team or its CURRENT alias",
                                true
                        ),
                        SlashCommandOption.create(STRING,
                                "new-alias",
                                "Enter the new alias for the team",
                                true
                        )
                ))
                .createGlobal(getApi()).join();
    }

    public void e() {
        CompletableFuture<Void> voidCompletableFuture = this.getApi().getGlobalSlashCommands()
                .thenApply(x -> x.stream().filter(e -> e.getName().equalsIgnoreCase("change-alias")))
                .thenApply(x -> x.findFirst())
                .thenAccept(x -> {
                    if (x.isEmpty()) {
                        System.out.println("No command found");
                        return;
                    }
                    System.out.println("Found command");
                    SlashCommandUpdater s = new SlashCommandUpdater(x.get().getId()).setSlashCommandOptions(
                            List.of(
                                    SlashCommandOption.createWithChoices(SlashCommandOptionType.STRING,
                                            "division",
                                            "choose from one of the following division",
                                            true,
                                            asList(
                                                    SlashCommandOptionChoice.create("f8", "f8"),
                                                    SlashCommandOptionChoice.create("f9", "f9"),
                                                    SlashCommandOptionChoice.create("f10", "f10"),
                                                    SlashCommandOptionChoice.create("f11", "f11"),
                                                    SlashCommandOptionChoice.create("Lite", "Lite"),
                                                    SlashCommandOptionChoice.create("Elite", "Elite")
                                            )
                                    ), SlashCommandOption.create(SlashCommandOptionType.STRING,
                                            "team-identifier",
                                            "Enter the name of your team or its CURRENT alias",
                                            true
                                    ),
                                    SlashCommandOption.create(STRING,
                                            "new-alias",
                                            "Enter the new alias for the team",
                                            true
                                    )
                            ));

                    s.updateGlobal(this.api).exceptionally(ExceptionLogger.get()).join();
                });
    }

    public void createInfoCommand() {
        SlashCommand.with("info", "Shows bots information").createGlobal(getApi()).join();
    }

    public void createClanLineupCommand() {
        SlashCommand.with("clan-lineup", "Shows the lineup of a clan")
                .setOptions(List.of(
                        SlashCommandOption.createWithChoices(SlashCommandOptionType.STRING,
                                "clan-tag",
                                "enter the clan tag of the clan you want to see the lineup of",
                                true
                        )
                ))
                .createGlobal(getApi()).join();
    }
}
