package com.fthlbot.discordbotfthl.Commands.CommandImpl.LeagueCommandsImpl.RosterAdd;

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
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.util.logging.ExceptionLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.util.Optional;

public class RosterAddUtilClass {
    //TODO: https://stackoverflow.com/questions/22561110/equivalent-of-go-channel-in-java
    private final static Logger log = LoggerFactory.getLogger(RosterAddUtilClass.class);

    public void addPlayers(SlashCommandCreateEvent event, SlashCommandInteraction interaction, String[] tags, Team team, RosterService service) {
        for (String tag : tags) {
            Thread thread = new Thread(() -> {
                try {
                    JClash clash = new JClash();
                    Player player = clash.getPlayer(tag).join();
                    Roster roster = new Roster(player.getName(), player.getTag(), player.getTownHallLevel(), team);
                    service.addToRoster(roster, event.getInteraction().getUser());
                    //send a message for each addition
                    EmbedBuilder embedBuilder = sendMessage(player.getTag(), team.getName(), interaction.getChannel().get(), interaction.getUser());
                    event.getSlashCommandInteraction().createFollowupMessageBuilder().addEmbed(embedBuilder).send();
                } catch (LeagueException e) {
                    EmbedBuilder leagueError = GeneralService.getLeagueError(e, event);
                    interaction.getChannel().get().sendMessage(leagueError);
                } catch (ClashAPIException e) {
                    ClashExceptionHandler clashExceptionHandler = new ClashExceptionHandler();
                    clashExceptionHandler.setStatusCode(Integer.valueOf(e.getMessage()));
                    EmbedBuilder embedBuilder = clashExceptionHandler.createEmbed(tag).getEmbedBuilder();
                    interaction.getChannel().get().sendMessage(embedBuilder).exceptionally(ExceptionLogger.get());
                } catch (IOException e) {
                    event.getApi().getChannelById(899282429678878801L).ifPresent(ch -> {
                        String s = "they managed to get the most unexpected error! \nIn rosterAddUtillClass, IException:  " + e.getMessage();
                        ch.asServerTextChannel().get().sendMessage(s);
                    });
                    Optional<TextChannel> channel = event.getSlashCommandInteraction().getChannel();
                    channel.ifPresent(textChannel -> textChannel.sendMessage("I don't know how you got this error but I'm going to ignore it ||jk xD||"));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            thread.start();
        }
    }

    private EmbedBuilder sendMessage(String tag, String teamName, TextChannel channel, User user) {
        return new EmbedBuilder()
                .setAuthor(user)
                .setTitle("Roster addition!")
                .setDescription("successfully added `%s` to `%s`'s roster".formatted(tag, teamName))
                .setTimestampToNow().setColor(Color.GREEN);
    }
}
