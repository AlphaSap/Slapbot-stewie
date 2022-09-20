package com.fthlbot.discordbotfthl.Commands.CommandImpl.StaffCommandsImpl;

import Core.JClash;
import com.fthlbot.discordbotfthl.Commands.CommandImpl.LeagueCommandsImpl.RosterAdd.RosterAddUtilClass;
import com.fthlbot.discordbotfthl.DatabaseModels.Division.Division;
import com.fthlbot.discordbotfthl.DatabaseModels.Division.DivisionService;
import com.fthlbot.discordbotfthl.DatabaseModels.Exception.LeagueException;
import com.fthlbot.discordbotfthl.DatabaseModels.Roster.Roster;
import com.fthlbot.discordbotfthl.DatabaseModels.Roster.RosterService;
import com.fthlbot.discordbotfthl.DatabaseModels.Team.Team;
import com.fthlbot.discordbotfthl.DatabaseModels.Team.TeamService;
import com.fthlbot.discordbotfthl.Util.BotConfig;
import com.fthlbot.discordbotfthl.Util.GeneralService;
import com.fthlbot.discordbotfthl.core.Annotation.CommandType;
import com.fthlbot.discordbotfthl.core.Annotation.Invoker;
import com.fthlbot.discordbotfthl.core.Handlers.Command;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;
import org.javacord.api.util.logging.ExceptionLogger;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


@Component
@Invoker(alias = "force-push", description = "Pushes account to a teams master roster - will bypass all checks", usage = "/force-push <division> <team alias>", type = CommandType.STAFF)
public class ForceAddImpl extends RosterAddUtilClass implements Command {
    private DivisionService divisionService;
    private TeamService teamService;
    private RosterService rosterService;

    private BotConfig config;

    @Override
    public void execute(SlashCommandCreateEvent event) {
        SlashCommandInteraction slashCommandInteraction = event.getSlashCommandInteraction();
        CompletableFuture<InteractionOriginalResponseUpdater> res = event.getSlashCommandInteraction().respondLater();
        try {
            List<SlashCommandInteractionOption> arguments = slashCommandInteraction.getArguments();

            String divisionAlias = arguments.get(0).getStringValue().get();

            String teamAlias = arguments.get(1).getStringValue().get();

            String[] tags = arguments.get(2).getStringValue().get().split("\\s+");

            //Conversion to set is necessary to remove duplicates - which would go un notice inside the database when queried at the same time.
            Set<String> collect = Arrays.stream(tags).collect(Collectors.toSet());

            Division division = divisionService.getDivisionByAlias(divisionAlias);
            Team team = teamService.getTeamByDivisionAndAlias(teamAlias, division);
            res.thenAccept(r -> {
                r.setContent("Roster Addition requested!").update();
            }).exceptionally(ExceptionLogger.get());
            addPlayers(event, slashCommandInteraction, collect, team, rosterService, config, false);


        }catch (LeagueException e){
            GeneralService.leagueSlashErrorMessage(res, e);
        }
    }
}
