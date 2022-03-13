package com.fthlbot.discordbotfthl.Commands.CommandImpl.RosterAdd;

import Core.Enitiy.player.Player;
import Core.JClash;
import Core.exception.ClashAPIException;
import com.fthlbot.discordbotfthl.DatabaseModels.Exception.LeagueException;
import com.fthlbot.discordbotfthl.DatabaseModels.Roster.Roster;
import com.fthlbot.discordbotfthl.DatabaseModels.Roster.RosterService;
import com.fthlbot.discordbotfthl.DatabaseModels.Team.Team;
import com.fthlbot.discordbotfthl.Util.GeneralService;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;

import java.awt.*;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class RosterAddUtilClass {
    public void addPlayers(SlashCommandCreateEvent event, String[] tags, Team team, RosterService service) {
        for (String tag : tags) {
            try {
                JClash clash = new JClash();
                Player player = clash.getPlayer(tag);
                Roster roster = new Roster(player.getName(), player.getTag(), player.getTownHallLevel(), team);
                service.addToRoster(roster);
                //send a message for each addition
                sendMessage(player.getTag(), team.getName(), event.getSlashCommandInteraction().getChannel().get());
            }catch (LeagueException e){
                GeneralService.leagueSlashErrorMessage(event, e);
            }
            catch (ClashAPIException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    private CompletableFuture<Message> sendMessage(String tag, String teamName, TextChannel channel){
        return new MessageBuilder()
                .setEmbed(
                        new EmbedBuilder()
                                .setTitle("Roster addition!")
                                .setDescription(String.format(
                                        """
                                        successfully added `%s` to `%s`'s roster
                                        """,
                                        tag, teamName))
                                .setTimestampToNow().setColor(Color.GREEN)
                ).send(channel);
    }
}
