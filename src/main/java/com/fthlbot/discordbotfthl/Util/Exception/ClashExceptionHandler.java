package com.fthlbot.discordbotfthl.Util.Exception;

import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.util.logging.ExceptionLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.time.LocalDateTime;

public class ClashExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(ClashExceptionHandler.class);

    private EmbedBuilder embedBuilder;
    private Integer statusCode;
    private SlashCommandInteraction interaction;
    public ClashExceptionHandler setSlashCommandInteraction(SlashCommandInteraction interaction) {
        this.interaction = interaction;
        return this;
    }

    private ClashExceptionHandler setEmbedBuilder(EmbedBuilder embedBuilder) {
        this.embedBuilder = embedBuilder;
        return this;
    }

    public ClashExceptionHandler setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public EmbedBuilder getEmbedBuilder() {
        return embedBuilder;
    }

    private Integer getStatusCode() {
        return statusCode;
    }

    private SlashCommandInteraction getSlashCommandCreateEvent() {
        return interaction;
    }

    private ClashExceptionHandler createEmbed() {
        switch (getStatusCode()) {
            case 400 -> {
                return this.setEmbedBuilder(
                        new EmbedBuilder()
                                .setDescription("Invalid request! `Connection Timeout`")
                                .setColor(Color.RED)
                                .setAuthor(this.getSlashCommandCreateEvent().getUser())
                );
            }
            case 403 -> {
                //TODO add a method where this would be logged
                log.error("Clash API key was revoked! {}", LocalDateTime.now());
                return this.setEmbedBuilder(
                        new EmbedBuilder()
                                .setDescription("Authorization Error!\n this error occurred, because clash of clans API has revoked my rights :( please contact Sahil to report this issue ")
                                .setColor(Color.RED)
                                .setAuthor(this.getSlashCommandCreateEvent().getUser())
                );
            }
            case 404 -> {
                log.warn("404");
                return this.setEmbedBuilder(
                        new EmbedBuilder()
                                .setDescription("`Invalid Tag`")
                                .setColor(Color.RED)
                );
            }
            case 429 -> {
                return this.setEmbedBuilder(
                        new EmbedBuilder()
                                .setDescription("Hey I got rate limited by clash of clans! I would need some time to recover from this devastating blow")
                                .setColor(Color.RED)
                                .setAuthor(this.getSlashCommandCreateEvent().getUser())
                );
            }
            case 503 -> {
                return this.setEmbedBuilder(
                        new EmbedBuilder()
                                .setDescription("Clash of clans is currently experiencing Maintenance, please try again once the game is up!")
                                .setColor(Color.RED)
                                .setAuthor(this.getSlashCommandCreateEvent().getUser())
                );
            }
            default -> {
                return this.setEmbedBuilder(
                        new EmbedBuilder()
                                .setDescription("Clash of clans did not respond to this request!\nReason: Api is currently overloaded!\nOr it's dead 💀")
                                .setColor(Color.RED)
                                .setFooter("No commands related to clash of clans will work, this may include some league commands!")
                );
            }
        }
    }

    public void respond() {
        SlashCommandInteraction i = this.createEmbed().getSlashCommandCreateEvent();

        i.respondLater().thenAccept(res -> {
            res.addEmbed(this.getEmbedBuilder()).update();
        }).exceptionally(ExceptionLogger.get());
    }
}
