package com.fthlbot.discordbotfthl.Util;

import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAttachment;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;
import org.javacord.api.util.DiscordRegexPattern;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Configuration
//This class contains all the utility functions, all the random methods which i need all over the project but don't belong in any other class.
public class Utils {
    public static String getTownHallEmote(int townhallLevel) {
        return switch (townhallLevel) {
            case 1 -> "<:th1:947276195945381978>";
            case 2 -> "<:th2:947276191998570506>";
            case 3 -> "<:th3:947276192770318368>";
            case 4 -> "<:th4:947277976293220362>";
            case 5 -> "<:th5:947276195991552011>";
            case 6 -> "<:th6:947276151418667049>";
            case 7 -> "<:th7:947276197887352942>";
            case 8 -> "<:th8:947276734200446976>";
            case 9 -> "<:th9:947276159681445898>";
            case 10 -> "<:th10:947276159782113280>";
            case 11 -> "<:th11:947276991030243468>";
            case 12 -> "<:th12:947276159954092088>";
            case 13 -> "<:th13:947282074249879572>";
            case 14 -> "<:th14:947276161006829590>";
            default -> null;
        };
    }
    public JSONArray getJsonArray(SlashCommandCreateEvent event, CompletableFuture<InteractionOriginalResponseUpdater> respondLater, String json) {
        JSONArray s;
        Pattern messageLink = DiscordRegexPattern.MESSAGE_LINK;

        Matcher matcher = messageLink.matcher(json);
        if (matcher.matches()) {
            Optional<CompletableFuture<Message>> messageByLink = event.getApi().getMessageByLink(json);
            if (messageByLink.isPresent()) {
                Message message = messageByLink.get().join();
                List<MessageAttachment> attachments = message.getAttachments();

                if (attachments.size() >= 1) {
                    byte[] join = attachments.get(0).downloadAsByteArray().join();
                    String s1 = new String(join, StandardCharsets.UTF_8);
                    s = new JSONArray(s1);
                } else {
                    respondLater.thenAccept(res -> {
                        res.setContent("No file found!").update();
                    });
                    return null;
                }
            } else {
                respondLater.thenAccept(res -> {
                    res.setContent("Unable to find the message, if the bot cannot see the message it cannot access it's content.").update();
                });
                return null;
            }
        }else{
            s = new JSONArray(json);
        }
        return s;
    }

    public JSONObject getJsonObject(SlashCommandCreateEvent event, CompletableFuture<InteractionOriginalResponseUpdater> respondLater, String json) {
        JSONObject s;
        Pattern messageLink = DiscordRegexPattern.MESSAGE_LINK;

        Matcher matcher = messageLink.matcher(json);
        if (matcher.matches()) {
            Optional<CompletableFuture<Message>> messageByLink = event.getApi().getMessageByLink(json);
            if (messageByLink.isPresent()) {
                Message message = messageByLink.get().join();
                List<MessageAttachment> attachments = message.getAttachments();

                if (attachments.size() >= 1) {
                    byte[] join = attachments.get(0).downloadAsByteArray().join();
                    String s1 = new String(join, StandardCharsets.UTF_8);
                    s = new JSONObject(s1);
                } else {
                    respondLater.thenAccept(res -> {
                        res.setContent("No file found!").update();
                    });
                    return null;
                }
            } else {
                respondLater.thenAccept(res -> {
                    res.setContent("Unable to find the message, if the bot cannot see the message it cannot access it's content.").update();
                });
                return null;
            }
        }else{
            s = new JSONObject(json);
        }
        return s;
    }

}
