package com.fthlbot.discordbotfthl.Commands.CommandImpl.LeagueCommandsImpl.RosterAdd;

import com.fthlbot.discordbotfthl.core.Annotation.CommandType;
import com.fthlbot.discordbotfthl.core.Annotation.Invoker;
import com.fthlbot.discordbotfthl.Commands.CommandListener.RosterAddListener;
import com.fthlbot.discordbotfthl.DatabaseModels.Division.Division;
import com.fthlbot.discordbotfthl.DatabaseModels.Division.DivisionService;
import com.fthlbot.discordbotfthl.DatabaseModels.Exception.LeagueException;
import com.fthlbot.discordbotfthl.DatabaseModels.Roster.RosterService;
import com.fthlbot.discordbotfthl.DatabaseModels.Team.Team;
import com.fthlbot.discordbotfthl.DatabaseModels.Team.TeamService;
import com.fthlbot.discordbotfthl.Util.BotConfig;
import com.fthlbot.discordbotfthl.Util.GeneralService;
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

@Invoker(
        alias = "roster-add",
        description = "This command allows you to add accounts to your roster!",
        usage = "/roster-add <Division alias> <Team alias> <tags...>",
        type = CommandType.ROSTER_MANAGEMENT

)
@Component
public class RosterAdditionImpl extends RosterAddUtilClass implements RosterAddListener {
    private final TeamService teamService;
    private final RosterService rosterService;
    private final DivisionService divisionService;

    private final BotConfig config;

    public RosterAdditionImpl(TeamService teamService, RosterService rosterService, DivisionService divisionService, BotConfig config) {
        this.teamService = teamService;
        this.rosterService = rosterService;
        this.divisionService = divisionService;
        this.config = config;
    }

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
            Set<String> collect = Arrays.stream(tags).map(x -> x.trim().toLowerCase()).collect(Collectors.toSet());

            Division division = divisionService.getDivisionByAlias(divisionAlias);
            Team team = teamService.getTeamByDivisionAndAlias(teamAlias, division);
            res.thenAccept(r -> {
                r.setContent("Roster Addition requested!").update();
            }).exceptionally(ExceptionLogger.get());
            addPlayers(event, slashCommandInteraction, collect, team, rosterService, config, true);


        }catch (LeagueException e){
            GeneralService.leagueSlashErrorMessage(res, e);
        }
    }

}
