package com.fthlbot.discordbotfthl.Commands.CommandImpl.StaffCommandsImpl;

import com.fthlbot.discordbotfthl.Annotation.CommandType;
import com.fthlbot.discordbotfthl.Annotation.Invoker;
import com.fthlbot.discordbotfthl.Commands.CommandListener.StaffCommandListener.ChangeRepListener;
import com.fthlbot.discordbotfthl.DatabaseModels.Division.Division;
import com.fthlbot.discordbotfthl.DatabaseModels.Division.DivisionService;
import com.fthlbot.discordbotfthl.DatabaseModels.Exception.EntityNotFoundException;
import com.fthlbot.discordbotfthl.DatabaseModels.Exception.NotTheRepException;
import com.fthlbot.discordbotfthl.DatabaseModels.Team.Team;
import com.fthlbot.discordbotfthl.DatabaseModels.Team.TeamService;
import com.fthlbot.discordbotfthl.Util.BotConfig;
import com.fthlbot.discordbotfthl.Util.GeneralService;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Invoker(
        alias = "change-rep",
        usage = "/change-rep <DIVISION> <TEAM ALIAS> <@oldRep> <@newRep>",
        description = "Changes representative for a team. Can only be used by staff",
        type = CommandType.STAFF
)
@Component
public class ChangeRepImpl implements ChangeRepListener {
    private final DivisionService divisionService;
    private final TeamService teamService;
    private final BotConfig config;

    public ChangeRepImpl(DivisionService divisionService, TeamService teamService, BotConfig config) {
        this.divisionService = divisionService;
        this.teamService = teamService;
        this.config = config;
    }

    @Override
    public void execute(SlashCommandCreateEvent event) {
        SlashCommandInteraction slashCommandInteraction = event.getSlashCommandInteraction();
        CompletableFuture<InteractionOriginalResponseUpdater> respondLater = slashCommandInteraction.respondLater();
        User user = slashCommandInteraction.getUser();
        long fthlServerID = config.getFthlServerID();
        long testServerID = config.getTestServerID();

        Server server = event.getApi().getServerById(fthlServerID).orElse(event.getApi().getServerById(testServerID).get());
        boolean b = hasStaffRole(server, user) || user.isBotOwner();
        if (!b){
            respondLater.thenAccept(res -> {
                res.setContent("This command is restriced to staff only!").update();
            });
            return;
        }
        String divAlias = slashCommandInteraction.getArguments().get(0).getStringValue().get();
        String teamAlias = slashCommandInteraction.getArguments().get(1).getStringValue().get();
        User oldRep = slashCommandInteraction.getArguments().get(2).getUserValue().get();
        User newRep = slashCommandInteraction.getArguments().get(3).getUserValue().get();
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
        try {
            team = teamService.changeRep(newRep, oldRep, team);
        } catch (NotTheRepException e) {
            e.printStackTrace();
        }

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("Rep change successfully executed!<:check:934403622043783228>")
                .addInlineField("Clan name", team.getName())
                .addInlineField("Clan tag", team.getTag())
                .addInlineField("Old rep", oldRep.getDiscriminatedName())
                .addInlineField("New Rep", newRep.getDiscriminatedName())
                .addInlineField("Change approved by", user.getDiscriminatedName())
                .setTimestampToNow()
                .setColor(Color.CYAN)
                .setAuthor(user);
        respondLater.thenAccept(res -> {
            res.addEmbed(embedBuilder).update();
        });
    }

    private boolean hasStaffRole(Server server, User user){
        List<Role> roles = user.getRoles(server);
        return roles.stream().anyMatch(x -> x.getId() == config.getStaffRoleID());
    }
}
