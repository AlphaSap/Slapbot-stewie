package com.fthlbot.discordbotfthl;

import Core.JClash;
import com.fthlbot.discordbotfthl.Util.GeneralService;
import com.fthlbot.discordbotfthl.Util.SlashCommandBuilder;
import com.fthlbot.discordbotfthl.core.Bot;
import com.fthlbot.discordbotfthl.core.CommandCreation.CommandBuilder;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.activity.ActivityType;
import org.javacord.api.entity.intent.Intent;
import org.javacord.api.entity.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.TimeZone;

@SpringBootApplication
public class DiscordBotFthlApplication {

    private final Environment env;

    private static final Logger log = LoggerFactory.getLogger(DiscordBotFthlApplication.class);

    public static JClash clash;

    private Bot bot;

    private final SlashCommandBuilder slashCommandBuilder;

    public DiscordBotFthlApplication(Environment env, Bot bot, SlashCommandBuilder slashCommandBuilder) {
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
                .setToken(env.getProperty("TOKEN_TEST"))
                .setUserCacheEnabled(true)
                .setAllIntentsExcept(
                        Intent.GUILD_WEBHOOKS,
                        Intent.GUILD_INTEGRATIONS,
                        Intent.DIRECT_MESSAGE_TYPING,
                        Intent.DIRECT_MESSAGE_REACTIONS,
                        Intent.DIRECT_MESSAGES,
                        Intent.DIRECT_MESSAGE_TYPING,
                        Intent.GUILD_MESSAGE_TYPING
                ).login()
                .join();

        ArrayList<Server> servers = new ArrayList<>(api.getServers());

        log.info("Logged in as {}", api.getYourself().getDiscriminatedName());
        log.info("Watching servers {}", servers.size());

        //Adding commands to the handle
        api.updateActivity(ActivityType.LISTENING, "Slash commands!");

        bot.Start(api);

        slashCommandBuilder.setApi(api);
        slashCommandBuilder.createRepBanCommand();

        CommandBuilder commandBuilder = new CommandBuilder();
        commandBuilder.setApi(api);
        commandBuilder.init();

        GeneralService.printMemoryUsage();
        return api;
    }
}
