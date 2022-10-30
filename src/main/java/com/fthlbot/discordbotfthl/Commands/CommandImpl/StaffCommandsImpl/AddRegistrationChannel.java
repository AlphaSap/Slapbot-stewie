package com.fthlbot.discordbotfthl.Commands.CommandImpl.StaffCommandsImpl;

import com.fthlbot.discordbotfthl.DatabaseModels.Division.Division;
import com.fthlbot.discordbotfthl.DatabaseModels.Division.DivisionService;
import com.fthlbot.discordbotfthl.DatabaseModels.Exception.LeagueException;
import com.fthlbot.discordbotfthl.DatabaseModels.Team.Team;
import com.fthlbot.discordbotfthl.DatabaseModels.Team.TeamService;
import com.fthlbot.discordbotfthl.Util.GeneralService;
import com.fthlbot.discordbotfthl.core.Annotation.CommandType;
import com.fthlbot.discordbotfthl.core.Annotation.Invoker;
import com.fthlbot.discordbotfthl.core.Handlers.Command;
import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Invoker(
        alias = "add-registration-channel",
        type = CommandType.STAFF,
        description = "Adds registration channel for a team!",
        usage = "/add-registration-channel <DIVISION> <TEAM ALIAS> <CHANNEL>"
)
@Component
public class AddRegistrationChannel implements Command {
    private final DivisionService divisionService;
    private final TeamService teamService;

    public AddRegistrationChannel(DivisionService divisionService, TeamService teamService) {
        this.divisionService = divisionService;
        this.teamService = teamService;
    }

    @Override
    public void execute(SlashCommandCreateEvent event) {
        CompletableFuture<InteractionOriginalResponseUpdater> respondLater = event.getSlashCommandInteraction().respondLater();
        List<SlashCommandInteractionOption> arguments = event.getSlashCommandInteraction().getArguments();
        String divAlias = arguments.get(0).getStringValue().get();
        String teamName = arguments.get(1).getStringValue().get();
        ServerChannel serverChannel = arguments.get(2).getChannelValue().get();

        try {
            Division division = divisionService.getDivisionByAlias(divAlias);
            Team team = teamService.getTeamByDivisionAndAlias(teamName, division);

            teamService.registrationChannelID(team, serverChannel.getId());
            respondLater.thenAccept(e -> {
                e.setContent("Registration channel updated").update();
            });

        } catch (LeagueException e) {
            GeneralService.leagueSlashErrorMessage(respondLater, e);
        } catch (Exception e ) {
            GeneralService.leagueSlashErrorMessage(respondLater, e.getMessage());
        }
    }
}
