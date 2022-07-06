package com.fthlbot.discordbotfthl.Commands.CommandImpl.StaffCommandsImpl;

import com.fthlbot.discordbotfthl.Annotation.CommandType;
import com.fthlbot.discordbotfthl.Annotation.Invoker;
import com.fthlbot.discordbotfthl.DatabaseModels.Division.Division;
import com.fthlbot.discordbotfthl.DatabaseModels.Division.DivisionService;
import com.fthlbot.discordbotfthl.DatabaseModels.Exception.EntityNotFoundException;
import com.fthlbot.discordbotfthl.DatabaseModels.Roster.Roster;
import com.fthlbot.discordbotfthl.DatabaseModels.Roster.RosterService;
import com.fthlbot.discordbotfthl.DatabaseModels.Team.Team;
import com.fthlbot.discordbotfthl.DatabaseModels.Team.TeamService;
import com.fthlbot.discordbotfthl.Handlers.Command;
import com.fthlbot.discordbotfthl.MinionBotAPI.MinionBotClient;
import com.fthlbot.discordbotfthl.MinionBotAPI.MinionBotPlayer;
import com.fthlbot.discordbotfthl.Util.GeneralService;
import com.fthlbot.discordbotfthl.Util.Pagination.ButtonRemoveJobScheduler;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Component
@Invoker(
        alias = "fp-check",
        usage = "/fp-check <Division>",
        description = "Checks all teams in a division for fair play violations, returns a list of teams with violations and players.",
        type = CommandType.STAFF
)
public class FairPlayCheckOnAllTeamImpl implements Command {
    private final DivisionService divisionService;
    private final TeamService teamService;
    private final RosterService rosterService;
    public FairPlayCheckOnAllTeamImpl(DivisionService divisionService, TeamService teamService, RosterService rosterService) {
        this.divisionService = divisionService;
        this.teamService = teamService;
        this.rosterService = rosterService;
    }

    @Override
    public void execute(SlashCommandCreateEvent event) {
        CompletableFuture<InteractionOriginalResponseUpdater> respond = event.getSlashCommandInteraction().respondLater();
        String diviAlias = event.getSlashCommandInteraction().getArguments().get(0).getStringValue().get();
        Division division;
        List<Team> team;
        try {
            division = divisionService.getDivisionByAlias(diviAlias);
            team = teamService.getAllTeamsByDivision(division);
        } catch (EntityNotFoundException e) {
            GeneralService.leagueSlashErrorMessage(respond, e);
            return;
        }
        respond.thenAccept(updater -> {
            updater.setContent("This task may take a while, please wait...").update();
        });
        int count = 0;
        MinionBotClient client = new MinionBotClient();
        for (Team t : team) {
            count++;
            List<Roster> rosterForATeam;
            try {
                rosterForATeam = rosterService.getRosterForATeam(t);
            } catch (EntityNotFoundException e) {
                EmbedBuilder embedBuilder = new EmbedBuilder()
                        .setTitle("Error!")
                        .setDescription("Something went wrong, please try again later, Team: %s not found ".formatted(t.getName()))
                        .setColor(Color.RED);
                event.getSlashCommandInteraction().createFollowupMessageBuilder().addEmbed(embedBuilder).send();
                return;
            }

            CompletableFuture.runAsync(() -> {
                run(event, client, t, rosterForATeam, respond.join());
            });

        }
        respond.join().setContent("Finished checking %s teams".formatted(count)).update();
    }

    private void run(SlashCommandCreateEvent event, MinionBotClient client, Team t, List<Roster> rosterForATeam, InteractionOriginalResponseUpdater updater) {
        for (Roster roster : rosterForATeam) {
            MinionBotPlayer[] playerBan = client.getPlayerBan(roster.getPlayerTag());
            if (playerBan.length == 0) {
                continue;
            }
            for (MinionBotPlayer minionBotPlayer : playerBan) {
                if (minionBotPlayer.getOrgInitials().isEmpty()) {
                    continue;
                }
//                if (!minionBotPlayer.getOrgInitials().get().equalsIgnoreCase("FTHL")) {
//                    continue;
//                }
                EmbedBuilder embed = new EmbedBuilder()
                        .setTitle("Fair Play Violation")
                        .setColor(Color.RED)
                        .setFooter("Click the button to remove the player form the roster!")
                        .addField("League", minionBotPlayer.getOrgInitials().get())
                        .addField("Team", t.getName(), false)
                        .addField("Player", "Name: %s\nTag: %s".formatted(roster.getPlayerName(), roster.getPlayerTag()), false);
                ActionRow actionRow = ActionRow.of(
                        Button.primary("remove", "Remove Player")

                );
                var d = event.getSlashCommandInteraction()
                        .createFollowupMessageBuilder()
                        .addComponents(actionRow)
                        .addEmbed(embed).send();
                d.thenAccept(message -> {
                    try {
                        new ButtonRemoveJobScheduler().execute(message);
                    } catch (SchedulerException e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }
                    message.addButtonClickListener(buttonClickEvent -> {
                        if (!buttonClickEvent.getButtonInteraction().getCustomId().equals("remove")) {
                            return;
                        }
                        if (buttonClickEvent.getButtonInteraction().getUser().getId() == event.getSlashCommandInteraction().getUser().getId() || buttonClickEvent.getButtonInteraction().getUser().isBotOwner()) {
                            //remove player from roster
                            rosterService.removeRoster(roster);
                            buttonClickEvent.getButtonInteraction().createImmediateResponder().setContent("Removed player from roster").respond();
                        }
                    }).removeAfter(11, TimeUnit.MINUTES);
                });
            }
        }
    }
}
