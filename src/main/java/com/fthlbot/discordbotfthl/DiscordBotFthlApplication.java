package com.fthlbot.discordbotfthl;

import Core.JClash;
import com.fthlbot.discordbotfthl.Util.GeneralService;
import com.fthlbot.discordbotfthl.Util.SlashGODClass;
import com.fthlbot.discordbotfthl.core.Bot;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.activity.ActivityType;
import org.javacord.api.entity.intent.Intent;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.interaction.SlashCommandBuilder;
import org.javacord.api.util.logging.ExceptionLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import java.awt.*;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.TimeZone;

@SpringBootApplication
public class DiscordBotFthlApplication {

    private final Environment env;

    private static final Logger log = LoggerFactory.getLogger(DiscordBotFthlApplication.class);

    public static JClash clash;

    private Bot bot;

    private final SlashGODClass slashCommandBuilder;

    public DiscordBotFthlApplication(Environment env, Bot bot, SlashGODClass slashCommandBuilder) {
        this.env = env;
        this.bot = bot;
        this.slashCommandBuilder = slashCommandBuilder;
    }

    public static void main(String[] args) {
        SpringApplication.run(DiscordBotFthlApplication.class, args);
    }

    @Bean
    @ConfigurationProperties(value = "discord-bot")
    public DiscordApi api() throws Exception {

        TimeZone.setDefault(TimeZone.getTimeZone(ZoneOffset.UTC));

        clash = new JClash(env.getProperty("CLASH_EMAIL"), env.getProperty("CLASH_PASS"));
        DiscordApi api = new DiscordApiBuilder()
                .setToken("MTAxMTAxMjE1MTkxOTUyMTg0Mw.Gqq2e_.4hzURLZSkjBUml9Zwc6peh8Sz4busF-Si1Fbfo")
                .setUserCacheEnabled(true)
                .setAllIntentsExcept(
                        Intent.GUILD_WEBHOOKS,
                        Intent.GUILD_INTEGRATIONS
                ).login()
                .join();

        ArrayList<Server> servers = new ArrayList<>(api.getServers());

        log.info("Logged in as {}", api.getYourself().getDiscriminatedName());
        log.info("Watching servers {}", servers.size());

        //Adding commands to the handle
        api.updateActivity(ActivityType.LISTENING, "Slash commands!");

        //Register slash commands!
        api.addMessageCreateListener(e -> {
            log.info("Message");
            if (e.getMessageContent().equalsIgnoreCase("!register") && e.getMessageAuthor().isBotOwner()) {
                slashCommandBuilder.setApi(e.getApi());
                slashCommandBuilder.makeAllCommands();
                e.getChannel().sendMessage(
                        new EmbedBuilder()
                                .setDescription("Registered commands")
                                .setColor(Color.green)
                ).exceptionally(ExceptionLogger.get());
            }
        });

        GeneralService.printMemoryUsage();
        bot.Start(api);
        return api;
    }
}
