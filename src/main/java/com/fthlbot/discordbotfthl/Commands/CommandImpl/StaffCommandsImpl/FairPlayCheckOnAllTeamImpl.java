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
import org.javacord.api.entity.message.embed.Embed;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;

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

        for (Team t : team) {
            try {
                List<Roster> rosterForATeam = rosterService.getRosterForATeam(t);
                MinionBotClient client = new MinionBotClient();

                for (Roster roster : rosterForATeam) {
                    MinionBotPlayer[] playerBan = client.getPlayerBan(roster.getPlayerTag());
                    if (playerBan.length == 0) {
                        continue;
                    }
                    for (MinionBotPlayer minionBotPlayer : playerBan) {
                        if (minionBotPlayer.getOrgID() == 55L){
                            event.getSlashCommandInteraction()
                                    .createFollowupMessageBuilder()
                                    .addEmbed(
                                            new EmbedBuilder()
                                                    .setTitle("Fair Play Violation")
                                                    .setFooter("Report to the League Admin")
                                                    .addField("Team", t.getName(), false)
                                                    .addField("Player", "Name: %s\nTag: %s".formatted(roster.getPlayerName(), roster.getPlayerTag()), false)
                                    ).send();
                        }
                    }
                }

            } catch (EntityNotFoundException e) {
                //TODO handle this
                EmbedBuilder embedBuilder = new EmbedBuilder()
                        .setTitle("Error!")
                        .setDescription("Something went wrong, please try again later, Team: %s not found ".formatted(t.getName()))
                        .setColor(Color.RED);
                event.getSlashCommandInteraction().createFollowupMessageBuilder().addEmbed(embedBuilder).send();
            }
        }
    }
}
