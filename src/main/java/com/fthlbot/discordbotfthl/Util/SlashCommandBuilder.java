package com.fthlbot.discordbotfthl.Util;

import org.javacord.api.DiscordApi;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.SlashCommandOptionChoice;
import org.javacord.api.interaction.SlashCommandOptionType;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static java.util.Arrays.asList;
import static org.javacord.api.interaction.SlashCommandOptionType.*;

@Component
public class SlashCommandBuilder {
    private DiscordApi api;

    public DiscordApi setApi(DiscordApi api) {
        this.api = api;
        return api;
    }

    public DiscordApi getApi() {
        return api;
    }

    public void createPingCommand() {
        SlashCommand.with("ping", "To check bots latency").createGlobal(getApi()).join();
    }

    public void createRegistrationCommand() {
        SlashCommand
                .with("register", "command to register")
                .setOptions(List.of(
                        SlashCommandOption.create(STRING, "clan-tag", "tag of your main clan!", true),
                        SlashCommandOption.createWithChoices(STRING,
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

    public void createHelpCommand() {
        SlashCommand.with("help", "command to get help")
                .setOptions(List.of(
                        SlashCommandOption.create(STRING,
                                "command",
                                "enter the command you want to get help for",
                                false
                        )
                ))
                .createGlobal(getApi()).join();
    }

    public void createRosterAddCommand() {
        SlashCommand.with("roster-add", "command to add accounts to your roster")
                .setOptions(List.of(
                        SlashCommandOption.createWithChoices(STRING,
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

    public void createRosterRemoveCommand() {
        SlashCommand command = SlashCommand
                .with("roster-remove", "command to remove accounts from your roster")
                .setOptions(List.of(
                        SlashCommandOption.createWithChoices(STRING,
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

    public void createTeamRosterCommand(){
        SlashCommand command = SlashCommand
                .with("team-roster", "A command to view the roster for a team!")
                .setOptions(List.of(
                        SlashCommandOption.createWithChoices(STRING,
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
                        ),
                        SlashCommandOption.create(
                                STRING,
                                "team-identifier",
                                "Enter your team's alias or name",
                                true
                        )
                )).createGlobal(getApi()).join();
    }

    public void createDefenseCommand(){
        SlashCommand command = SlashCommand.with(
                        "defense",
                        "Fetches defense from the current ongoing war of the provided clan tag")
                .setOptions(List.of(
                                SlashCommandOption.create(STRING,
                                        "tag",
                                        "enter a valid clan tag",
                                        false)
                        )
                ).createGlobal(getApi()).join();

    }

    public void createTeamAllCommand(){
        SlashCommand command = SlashCommand.with(
                        "all-teams",
                        "Returns a list of all the teams participating in a given division")
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
                        )))
                .createGlobal(getApi()).join();
    }

    public void createChangeRepCommand(){
        SlashCommand command = SlashCommand
                .with("change-rep", "staff only command to change rep of a team")
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

    public void createChangeClanCommand(){
        SlashCommand command = SlashCommand
                .with("change-clan", "staff only command to change clan tag of a team")
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
                                "clan-tag",
                                "enter the new clan tag for your team",
                                true
                        )
                ))
                .createGlobal(getApi()).join();
    }

    public void createAddWeeksCommand(){
        SlashCommand command = SlashCommand
                .with("add-weeks", "staff only command to add weeks to a specific division")
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
                                "json",
                                "Enter a json array with 3 fields, `start`, `end` and `byeWeek`",
                                true
                        )
                ))
                .createGlobal(getApi()).join();
    }

    public void createMatchUpsCommand(){
        SlashCommand command = SlashCommand
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

    public void createShowDivisionWeekCommand(){
        SlashCommand command = SlashCommand
                .with("show-divisionweek", "Commands to view weeks date for a specific division")
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
                        )
                ))
                .createGlobal(getApi()).join();
    }

    public void createCreateNegotiationChannelsCommand(){
        SlashCommand command = SlashCommand
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

    public void createPlayerCommand(){
        SlashCommand command = SlashCommand
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

    public void createRemoveAllChannelFromACategoryCommand(){
        SlashCommand command = SlashCommand.with("remove-channels-from-category", "Staff only command to delete channels from a category with logs!")
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

    public void createTeamInformationCommand(){
        SlashCommand command = SlashCommand.with("team-information", "A command get information about a team")
                .setOptions(List.of(SlashCommandOption.createWithChoices(STRING,
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
                        ),
                        SlashCommandOption.create(SlashCommandOptionType.STRING,
                                "team-identifier",
                                "Enter the name/alias of the team you want to get information about",
                                true)
                )).createGlobal(getApi()).join();
    }

    public void createCreateAllDivisionCommand(){
        SlashCommand command = SlashCommand.with(
                "create-all-divisions",
                        "Staff only command to create all division channels"
                )
                .createGlobal(getApi()).join();
    }

    public void createDeleteATeamCommand(){
        SlashCommand command = SlashCommand.with("delete-a-team", "Staff only command to delete a team")
                .setOptions(List.of(SlashCommandOption.createWithChoices(SlashCommandOptionType.STRING,
                                "division",
                                "choose from one of the following division",
                                true,
                                asList(
                                        SlashCommandOptionChoice.create("F8", "F8"),
                                        SlashCommandOptionChoice.create("F5", "F5"),
                                        SlashCommandOptionChoice.create("F9", "F9"),
                                        SlashCommandOptionChoice.create("F11", "F11"),
                                        SlashCommandOptionChoice.create("F10", "F10"),
                                        SlashCommandOptionChoice.create("Fmix", "Fmix")
                                )
                        ),
                        SlashCommandOption.create(SlashCommandOptionType.STRING,
                                "team-identifier",
                                "Enter the name/alias of the team you want to delete",
                                true)
                )).createGlobal(getApi()).join();
    }
    public void makeAllCommands(){
        //Make a Method array
        Method[] methods = this.getClass().getDeclaredMethods();
        for(Method method : methods){
            if(method.getName().startsWith("create")){
                try {
                    System.out.println("Creating command: " + method.getName());
                    method.invoke(this);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
