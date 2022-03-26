package com.fthlbot.discordbotfthl.Commands.CommandImpl.RosterAdd;

import Core.Enitiy.player.Player;
import Core.JClash;
import Core.exception.ClashAPIException;
import com.fthlbot.discordbotfthl.DatabaseModels.Exception.LeagueException;
import com.fthlbot.discordbotfthl.DatabaseModels.Roster.Roster;
import com.fthlbot.discordbotfthl.DatabaseModels.Roster.RosterService;
import com.fthlbot.discordbotfthl.DatabaseModels.Team.Team;
import com.fthlbot.discordbotfthl.Util.Exception.ClashExceptionHandler;
import com.fthlbot.discordbotfthl.Util.GeneralService;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.util.logging.ExceptionLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class RosterAddUtilClass {
    private final static Logger log = LoggerFactory.getLogger(RosterAddUtilClass.class);
    public void addPlayers(SlashCommandCreateEvent event, String[] tags, Team team, RosterService service) {
        for (String tag : tags) {
            try {
                JClash clash = new JClash();
                Player player = clash.getPlayer(tag).exceptionally(e -> {
                    e.printStackTrace();
                    ClashExceptionHandler handler = new ClashExceptionHandler();
                    handler.setSlashCommandCreateEvent(event)
                            .setStatusCode(Integer.valueOf(e.getMessage()));
                    handler.respond();
                    return null;
                }).join();
                Roster roster = new Roster(player.getName(), player.getTag(), player.getTownHallLevel(), team);
                service.addToRoster(roster, event.getInteraction().getUser());
                //send a message for each addition
                sendMessage(player.getTag(), team.getName(), event.getSlashCommandInteraction().getChannel().get())
                        .exceptionally(ExceptionLogger.get());
            }catch (LeagueException e){
                EmbedBuilder leagueError = GeneralService.getLeagueError(e, event);
                event.getSlashCommandInteraction().getChannel().get().sendMessage(leagueError);
            } catch (IOException e) {
                event.getSlashCommandInteraction()
                        .respondLater()
                        .thenAccept(res -> {
                            res.setContent("Unhandled exception, this happenes when Clash of clans make a changes without notifying me !")
                                    .update();
                        });
                event.getApi().getChannelById(899282429678878801L).ifPresent(ch -> {
                    String s = "You managed to get the most unexpected error! \nIn rosterAddUtillClass, IException:  "  +  e.getMessage();
                    ch.asServerTextChannel().get().sendMessage(s);
                });
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
