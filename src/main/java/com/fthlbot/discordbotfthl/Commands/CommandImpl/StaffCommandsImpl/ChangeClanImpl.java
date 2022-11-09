package com.fthlbot.discordbotfthl.Commands.CommandImpl.StaffCommandsImpl;

import com.fthlbot.discordbotfthl.Commands.CommandListener.StaffCommandListener.ChangeClanListener;
import com.fthlbot.discordbotfthl.DatabaseModels.Division.Division;
import com.fthlbot.discordbotfthl.DatabaseModels.Division.DivisionService;
import com.fthlbot.discordbotfthl.DatabaseModels.Exception.EntityNotFoundException;
import com.fthlbot.discordbotfthl.DatabaseModels.Team.Team;
import com.fthlbot.discordbotfthl.DatabaseModels.Team.TeamService;
import com.fthlbot.discordbotfthl.Util.GeneralService;
import com.fthlbot.discordbotfthl.core.Annotation.CommandType;
import com.fthlbot.discordbotfthl.core.Annotation.Invoker;
import com.sahhiill.clashapi.core.ClashAPI;
import com.sahhiill.clashapi.core.exception.ClashAPIException;
import com.sahhiill.clashapi.models.clan.Clan;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@Component
@Invoker(
        alias = "change-clan",
        usage = "/change-clan <DIVISION> <TEAM IDENTIFIER> <CLAN TAG>",
        description = "Changes clan tag for a team. Can only be used by staff",
        type = CommandType.STAFF
)
public class ChangeClanImpl implements ChangeClanListener {
    private final DivisionService divisionService;
    private final TeamService teamService;

    public ChangeClanImpl(DivisionService divisionService, TeamService teamService) {
        this.divisionService = divisionService;
        this.teamService = teamService;
    }

    @Override
    public void execute(SlashCommandCreateEvent event) {
        CompletableFuture<InteractionOriginalResponseUpdater> respondLater = event.getSlashCommandInteraction().respondLater();

        String divAlias = event.getSlashCommandInteraction().getArguments().get(0).getStringValue().get();
        String teamAlias = event.getSlashCommandInteraction().getArguments().get(1).getStringValue().get();
        String clanTag = event.getSlashCommandInteraction().getArguments().get(2).getStringValue().get();

        ClashAPI clash = new ClashAPI();

        Division division = null;
        try {
            division = divisionService.getDivisionByAlias(divAlias);
        } catch (EntityNotFoundException e) {
            GeneralService.leagueSlashErrorMessage(respondLater, e);
            return;
        }
        final Team team;
        try {
            team = teamService.getTeamByDivisionAndAlias(teamAlias, division);
        } catch (EntityNotFoundException e) {
            GeneralService.leagueSlashErrorMessage(respondLater, e);
            return;
        }
        Clan clan;
        try {
            clan = clash.getClan(clanTag);
        } catch (ClashAPIException | IOException e) {
            e.printStackTrace();
            return;
        }
        String oldTag = team.getTag();
        Team newTeam = teamService.changeTag(team, clan.getTag(), clan.getName());
        User user = event.getSlashCommandInteraction().getUser();
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("Clan changed successfully <:check:934403622043783228> ")
                .addField("Team Name", team.getName())
                .addInlineField("Old clan tag", oldTag)
                .addInlineField("New clan tag", newTeam.getTag())
                .addInlineField("Changed approved by", user.getDiscriminatedName())
                .setTimestampToNow()
                .setColor(Color.CYAN)
                .setAuthor(user);
        respondLater.thenAccept(res -> {
            res.addEmbed(embedBuilder);
            res.update();
        });
    }
}
