package com.fthlbot.discordbotfthl.Util.Exception;

import Core.JClash;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;
import org.javacord.api.util.logging.ExceptionLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.time.LocalDateTime;

public class ClashExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(ClashExceptionHandler.class);

    private EmbedBuilder embedBuilder;
    private Integer statusCode;
    private SlashCommandInteraction interaction;

    private InteractionOriginalResponseUpdater responder;

    @Deprecated(forRemoval = true)
    public ClashExceptionHandler setSlashCommandInteraction(SlashCommandInteraction interaction) {
        this.interaction = interaction;
        return this;
    }

    public InteractionOriginalResponseUpdater getResponder() {
        return responder;
    }

    public ClashExceptionHandler setResponder(InteractionOriginalResponseUpdater responder) {
        this.responder = responder;
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

    public ClashExceptionHandler createEmbed() {
        switch (getStatusCode()) {
            case 400 -> {
                return this.setEmbedBuilder(
                        new EmbedBuilder()
                                .setDescription("400 Bad Request. Please contact the developer.")
                                .setColor(Color.RED)
                );
            }
            case 403 -> {
                log.error("Clash API key was revoked! {}", LocalDateTime.now());
                try {
                    new JClash(System.getenv("CLASH_EMAIL"), System.getenv("CLASH_PASS"));
                } catch (IOException e) {
                    log.error("Error Making API key {}", e.getMessage());
                }
                return this.setEmbedBuilder(
                        new EmbedBuilder()
                                .setDescription("Invalid request! `API Key was revoked` Please contact the developer.")
                                .setColor(Color.RED)
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
                                .setDescription("`Too Many Requests`")
                                .setColor(Color.RED)
                );
            }
            case 503 -> {
                return this.setEmbedBuilder(
                        new EmbedBuilder()
                                .setDescription("Clash of clans is down for maintenance! I will be back soon!")
                                .setColor(Color.RED)
                );
            }
            default -> {
                return this.setEmbedBuilder(
                        new EmbedBuilder()
                                .setDescription("`Unknown Error`")
                                .setColor(Color.RED)
                                .setFooter("No commands related to clash of clans will work, this may include some league commands!")
                );
            }
        }
    }

    public ClashExceptionHandler createEmbed(String tag) {
        switch (getStatusCode()) {
            case 400 -> {
                return this.setEmbedBuilder(
                        new EmbedBuilder()
                                .setDescription("400 Bad Request. Please contact the developer.")
                                .setColor(Color.RED)
                );
            }
            case 403 -> {
                //TODO add a method where this would be logged
                log.error("Clash API key was revoked! {}", LocalDateTime.now());
                return this.setEmbedBuilder(
                        new EmbedBuilder()
                                .setDescription("Invalid request! `API Key was revoked` Please contact the developer.")
                                .setColor(Color.RED)
                );
            }
            case 404 -> {
                log.warn("404");
                return this.setEmbedBuilder(
                        new EmbedBuilder()
                                .setDescription("`Invalid Tag` " + tag)
                                .setColor(Color.RED)
                );
            }
            case 429 -> {
                return this.setEmbedBuilder(
                        new EmbedBuilder()
                                .setDescription("`Too Many Requests`")
                                .setColor(Color.RED)
                );
            }
            case 503 -> {
                return this.setEmbedBuilder(
                        new EmbedBuilder()
                                .setDescription("Clash of clans is down for maintenance! I will be back soon!")
                                .setColor(Color.RED)
                );
            }
            default -> {
                return this.setEmbedBuilder(
                        new EmbedBuilder()
                                .setDescription("`Unknown Error`")
                                .setColor(Color.RED)
                                .setFooter("No commands related to clash of clans will work, this may include some league commands!")
                );
            }
        }
    }

    public void respond() {
        //SlashCommandInteraction i = this.createEmbed().getSlashCommandCreateEvent();

        InteractionOriginalResponseUpdater responseUpdater = getResponder() == null ? getSlashCommandCreateEvent().respondLater().join() : getResponder();
        responseUpdater.addEmbed(this.createEmbed().getEmbedBuilder()).update();
    }
}
