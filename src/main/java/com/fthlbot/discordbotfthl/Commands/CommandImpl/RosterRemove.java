package com.fthlbot.discordbotfthl.Commands.CommandImpl;

import Core.Enitiy.player.Player;
import Core.JClash;
import Core.exception.ClashAPIException;
import com.fthlbot.discordbotfthl.Annotation.Invoker;
import com.fthlbot.discordbotfthl.DatabaseModels.Division.Division;
import com.fthlbot.discordbotfthl.DatabaseModels.Division.DivisionService;
import com.fthlbot.discordbotfthl.DatabaseModels.Exception.EntityNotFoundException;
import com.fthlbot.discordbotfthl.DatabaseModels.Exception.LeagueException;
import com.fthlbot.discordbotfthl.DatabaseModels.Roster.RosterService;
import com.fthlbot.discordbotfthl.DatabaseModels.Team.Team;
import com.fthlbot.discordbotfthl.DatabaseModels.Team.TeamService;
import com.fthlbot.discordbotfthl.Handlers.Command;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.callback.InteractionCallbackDataFlag;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import static com.fthlbot.discordbotfthl.Util.GeneralService.leagueSlashErrorMessage;

@Invoker(
        alias = "roster-remove",
        description = "A command to remove accounts from your master roster. Add multiple tags seperated by tags",
        usage = "/roster-remove <DIVISION ALIAS> <TEAM ALIAS> <TAGs ...>"
)
@Component
public class RosterRemove implements Command {
    @Autowired
    private TeamService teamService;
    @Autowired
    private DivisionService divisionService;
    @Autowired
    private RosterService rosterService;

    @Override
    public void execute(SlashCommandCreateEvent event) {
        SlashCommandInteraction interaction = event.getSlashCommandInteraction();
        CompletableFuture<InteractionOriginalResponseUpdater> re = interaction.respondLater(true);
        try {
            String divAlias = interaction.getArguments().get(0).getStringValue().get();
            String teamAlias = interaction.getArguments().get(1).getStringValue().get();
            String[] tags = interaction.getArguments().get(2).getStringValue().get().split("\\s+");

            Division divisionByAlias = divisionService.getDivisionByAlias(divAlias);
            Team teamByDivisionAndAlias = teamService.getTeamByDivisionAndAlias(teamAlias, divisionByAlias);

            for (String tag : tags) {
                try {
                    JClash clash = new JClash();
                    Player player = clash.getPlayer(tag);
                    removeAcc(teamByDivisionAndAlias, player, event);
                    //success message
                    sendMessage(player.getTag(), interaction.getChannel().get());
                } catch (ClashAPIException | IOException e) {
                    e.printStackTrace();
                }
            }
            re.thenAccept(res -> res.setContent("task Complete!")
                            .setFlags(InteractionCallbackDataFlag.EPHEMERAL)
                            .update()
                    );
        } catch (LeagueException e) {
            leagueSlashErrorMessage(event, e);
        }
    }

    private void removeAcc(Team teamByDivisionAndAlias, Player player, SlashCommandCreateEvent event) {
        try {
            rosterService.removeFromRoster(teamByDivisionAndAlias, player.getTag());
        } catch (EntityNotFoundException e) {
            leagueSlashErrorMessage(event, e);
        }
    }

    private CompletableFuture<Message> sendMessage(String tag, TextChannel textChannel){
        return new MessageBuilder()
                .setEmbed(
                        new EmbedBuilder()
                                .setDescription(String.format("Successfully removed `%s` from your roster", tag)
                ).setColor(Color.GREEN)
                                .setTimestampToNow()).send(textChannel);
    }
}
