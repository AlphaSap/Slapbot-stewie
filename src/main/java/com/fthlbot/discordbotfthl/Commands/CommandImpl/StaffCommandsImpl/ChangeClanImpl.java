package com.fthlbot.discordbotfthl.Commands.CommandImpl.StaffCommandsImpl;

import Core.Enitiy.clan.ClanModel;
import Core.JClash;
import Core.exception.ClashAPIException;
import com.fthlbot.discordbotfthl.Annotation.CommandType;
import com.fthlbot.discordbotfthl.Annotation.Invoker;
import com.fthlbot.discordbotfthl.Commands.CommandListener.StaffCommandListener.ChangeClanListener;
import com.fthlbot.discordbotfthl.DatabaseModels.Division.Division;
import com.fthlbot.discordbotfthl.DatabaseModels.Division.DivisionService;
import com.fthlbot.discordbotfthl.DatabaseModels.Exception.EntityNotFoundException;
import com.fthlbot.discordbotfthl.DatabaseModels.Team.Team;
import com.fthlbot.discordbotfthl.DatabaseModels.Team.TeamService;
import com.fthlbot.discordbotfthl.DiscordBotFthlApplication;
import com.fthlbot.discordbotfthl.Util.GeneralService;
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

        JClash clash = DiscordBotFthlApplication.clash;
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
        ClanModel clan;
        try {
            clan = clash.getClan(clanTag).join();
        } catch (ClashAPIException | IOException e) {
            e.printStackTrace();
            return;
        }
        String oldTag = team.getTag();
        Team newTeam = teamService.changeTag(team, clan.getTag(), clan.getName());
        User user = event.getSlashCommandInteraction().getUser();
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("Clan changed successfully <:check:934403622043783228> ")
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
