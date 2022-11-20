package com.fthlbot.discordbotfthl;

import com.fthlbot.discordbotfthl.AutoCompleteSlashcommandOptions.AutoCompleteImpl;
import com.fthlbot.discordbotfthl.Util.GeneralService;
import com.fthlbot.discordbotfthl.Util.SlashGODClass;
import com.fthlbot.discordbotfthl.core.Bot;
import com.sahhiill.clashapi.core.ClashAPI;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.activity.ActivityType;
import org.javacord.api.entity.intent.Intent;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
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
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.CompletableFuture;

@SpringBootApplication
public class DiscordBotFthlApplication {

    private final Environment env;

    private static final Logger log = LoggerFactory.getLogger(DiscordBotFthlApplication.class);

    public static ClashAPI clash;

    private Bot bot;
    //slash command builder class
    private final SlashGODClass slashCommandBuilder;

    private final AutoCompleteImpl autoCompleteListener;

    public DiscordBotFthlApplication(Environment env, Bot bot, SlashGODClass slashCommandBuilder, AutoCompleteImpl autoCompleteListener) {
        this.env = env;
        this.bot = bot;
        this.slashCommandBuilder = slashCommandBuilder;
        this.autoCompleteListener = autoCompleteListener;
    }

    public static void main(String[] args) {
        SpringApplication.run(DiscordBotFthlApplication.class, args);
    }

    @Bean
    @ConfigurationProperties(value = "discord-bot")
    public DiscordApi api() throws Exception {

        TimeZone.setDefault(TimeZone.getTimeZone(ZoneOffset.UTC));

        clash = new ClashAPI(Objects.requireNonNull(env.getProperty("CLASH_EMAIL")), Objects.requireNonNull(env.getProperty("CLASH_PASS")));
        //logging in discord
        DiscordApi api = new DiscordApiBuilder()
                .setToken(env.getProperty("TOKEN_BOT"))
                .setUserCacheEnabled(true) // enabling user cache so users will be valid on startup, reduces API calls but increases memory usage
                .setAllIntentsExcept(
                        Intent.GUILD_WEBHOOKS,
                        Intent.GUILD_INTEGRATIONS
                ).login()
                .join(); //blocking the future

        ArrayList<Server> servers = new ArrayList<>(api.getServers());

        log.info("Logged in as {}", api.getYourself().getDiscriminatedName());
        log.info("Watching servers {}", servers.size());


        //Adding commands to the handle
        api.updateActivity(ActivityType.LISTENING, "Slash commands!");

        //command to add all slash commands
        api.addMessageCreateListener(e -> {

            if (e.getMessageContent().equals("!register") && e.getMessageAuthor().isBotOwner()) {
                CompletableFuture.runAsync(() -> {

                    slashCommandBuilder.setApi(e.getApi());
                    slashCommandBuilder.makeAllCommands();

                    e.getChannel().sendMessage(
                            new EmbedBuilder()
                                    .setDescription("Registered Commands!")
                                    .setColor(Color.green)
                    ).exceptionally(ExceptionLogger.get());
                });
            }
        });

        api.addAutocompleteCreateListener(autoCompleteListener);

        GeneralService.printMemoryUsage();
        //starting the bot
        bot.Start(api);
        return api;
    }
}
