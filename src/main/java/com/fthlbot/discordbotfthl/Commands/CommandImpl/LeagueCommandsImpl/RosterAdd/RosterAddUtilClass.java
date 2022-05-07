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
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;
import org.javacord.api.util.logging.ExceptionLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class RosterAddUtilClass {
    private final static Logger log = LoggerFactory.getLogger(RosterAddUtilClass.class);
    public int addPlayers(SlashCommandCreateEvent event, SlashCommandInteraction interaction, String[] tags, Team team, RosterService service) {
        int success = 0;
        for (String tag : tags) {
            try {
                JClash clash = new JClash();
                Player player = clash.getPlayer(tag).join();
                Roster roster = new Roster(player.getName(), player.getTag(), player.getTownHallLevel(), team);
                service.addToRoster(roster, event.getInteraction().getUser());
                //send a message for each addition
                sendMessage(player.getTag(), team.getName(), interaction.getChannel().get(), interaction.getUser())
                        .exceptionally(ExceptionLogger.get());
                success++;
            }catch (LeagueException e){
                EmbedBuilder leagueError = GeneralService.getLeagueError(e, event);
                interaction.getChannel().get().sendMessage(leagueError);
            } catch (ClashAPIException e){
                e.printStackTrace();
                ClashExceptionHandler clashExceptionHandler = new ClashExceptionHandler();
                clashExceptionHandler.setStatusCode(Integer.valueOf(e.getMessage()));
                EmbedBuilder embedBuilder = clashExceptionHandler.createEmbed(tag).getEmbedBuilder();
                interaction.getChannel().get().sendMessage(embedBuilder).exceptionally(ExceptionLogger.get());
            }catch (IOException e) {
                event.getApi().getChannelById(899282429678878801L).ifPresent(ch -> {

                    String s = "You managed to get the most unexpected error! \nIn rosterAddUtillClass, IException:  "  +  e.getMessage();
                    ch.asServerTextChannel().get().sendMessage(s);
                });
                Optional<TextChannel> channel = event.getSlashCommandInteraction().getChannel();
                channel.ifPresent(textChannel -> textChannel.sendMessage("I don't know how you got this error but I'm going to ignore it ||jk xD||"));

            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return success;
    }

    private CompletableFuture<Message> sendMessage(String tag, String teamName, TextChannel channel, User user){
        return new MessageBuilder()
                .setEmbed(
                        new EmbedBuilder()
                                .setAuthor(user)
                                .setTitle("Roster addition!")
                                .setDescription("successfully added `%s` to `%s`'s roster".formatted(tag, teamName))
                                .setTimestampToNow().setColor(Color.GREEN)
                ).send(channel);
    }
}
