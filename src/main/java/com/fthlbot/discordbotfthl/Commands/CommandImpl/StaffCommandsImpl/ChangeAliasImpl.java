package com.fthlbot.discordbotfthl.Commands.CommandImpl.StaffCommandsImpl;

import com.fthlbot.discordbotfthl.Annotation.CommandType;
import com.fthlbot.discordbotfthl.Annotation.Invoker;
import com.fthlbot.discordbotfthl.Commands.CommandListener.StaffCommandListener.ChangeClanListener;
import com.fthlbot.discordbotfthl.DatabaseModels.Division.Division;
import com.fthlbot.discordbotfthl.DatabaseModels.Division.DivisionService;
import com.fthlbot.discordbotfthl.DatabaseModels.Exception.EntityNotFoundException;
import com.fthlbot.discordbotfthl.DatabaseModels.Team.Team;
import com.fthlbot.discordbotfthl.DatabaseModels.Team.TeamService;
import com.fthlbot.discordbotfthl.Util.GeneralService;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@Invoker(
        alias = "change-alias",
        usage = "/change-alias <DIVISION> <TEAM IDENTIFIER> <NEW ALIAS>",
        description = "Changes alias for a team. Can only be used by staff",
        type = CommandType.STAFF
)
public class ChangeAliasImpl implements ChangeClanListener {
    private final DivisionService divisionService;
    private final TeamService teamService;

    public ChangeAliasImpl(DivisionService divisionService, TeamService teamService) {
        this.divisionService = divisionService;
        this.teamService = teamService;
    }

    @Override
    public void execute(SlashCommandCreateEvent event) {
        CompletableFuture<InteractionOriginalResponseUpdater> respondLater = event.getSlashCommandInteraction().respondLater();
        List<SlashCommandInteractionOption> arguments = event.getSlashCommandInteraction().getArguments();
        String divAlias = arguments.get(0).getStringValue().get();
        String teamAlias = arguments.get(1).getStringValue().get();
        String newAlias = arguments.get(2).getStringValue().get();

        if (newAlias.length() > 5){
            respondLater.thenAccept(res -> {
               res.setContent("Team alias cannot be longer than 5 letters!").update();
            });
            return;
        }

        Division division = null;
        try {
            division = divisionService.getDivisionByAlias(divAlias);
        } catch (EntityNotFoundException e) {
            GeneralService.leagueSlashErrorMessage(respondLater, e);
            return;
        }
        Team team = null;
        try {
            team = teamService.getTeamByDivisionAndAlias(teamAlias, division);
        } catch (EntityNotFoundException e) {
            GeneralService.leagueSlashErrorMessage(respondLater, e);
            return;
        }
        String oldAlias = team.getAlias();
        team = teamService.changeAlias(team, newAlias);

        User user = event.getSlashCommandInteraction().getUser();
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("Alias changed successfully <:check:934403622043783228> ")
                .addInlineField("Team name", team.getName())
                .addInlineField("Old alias", oldAlias)
                .addInlineField("New alias", team.getAlias())
                .addInlineField("Change approved by:", user.getDiscriminatedName())
                .setTimestampToNow()
                .setColor(Color.CYAN)
                .setAuthor(user);
        respondLater.thenAccept(res -> {
            res.addEmbed(embedBuilder).update();
        });
    }
}
