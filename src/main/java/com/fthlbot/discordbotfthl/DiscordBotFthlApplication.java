package com.fthlbot.discordbotfthl;

import Core.JClash;
import Core.exception.ClashAPIException;
import com.fthlbot.discordbotfthl.Commands.CommandImpl.HelpImpl;
import com.fthlbot.discordbotfthl.Commands.CommandImpl.PingImpl;
import com.fthlbot.discordbotfthl.Commands.CommandImpl.RegistrationImpl;
import com.fthlbot.discordbotfthl.Commands.CommandImpl.RosterAdditionImpl;
import com.fthlbot.discordbotfthl.Handlers.Command;
import com.fthlbot.discordbotfthl.Handlers.MessageHandlers;
import com.fthlbot.discordbotfthl.Handlers.MessageHolder;
import com.fthlbot.discordbotfthl.Handlers.MessageListener;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.intent.Intent;
import org.javacord.api.entity.server.Server;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.fthlbot.discordbotfthl.Util.GeneralService.*;

@SpringBootApplication
public class            DiscordBotFthlApplication {

    @Autowired
    private Environment env;

    @Autowired
    private PingImpl pingImpl;

    @Autowired
    private RegistrationImpl registration;

    @Autowired
    private RosterAdditionImpl rosterAddition;

    public static final String prefix = "+";

    private static final Logger log = LoggerFactory.getLogger(DiscordBotFthlApplication.class);
    public static JClash clash;


    public static void main(String[] args) {
        SpringApplication.run(DiscordBotFthlApplication.class, args);
    }

    @Bean
    @ConfigurationProperties(value = "discord-bot")
    public DiscordApi api() throws ClashAPIException, IOException {

        String content = getFileContent("Servers.json");

        JSONObject jsonObject = new JSONObject(content);
        long testID = jsonObject.getLong("test");


        clash = new JClash(env.getProperty("CLASH_EMAIL"), env.getProperty("CLASH_PASS"));

        DiscordApi api = new DiscordApiBuilder()
                .setToken(env.getProperty("TOKEN_TEST_BOT"))
                .setAllIntentsExcept(Intent.GUILD_WEBHOOKS,
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


        List<Command> commandList = new ArrayList<>(List.of(
                this.pingImpl,
                this.registration,
                this.rosterAddition
        ));

        HelpImpl help = new HelpImpl(commandList);
        commandList.add(help);

        MessageHandlers messageHandlers = new MessageHandlers(commandList);

        MessageHolder messageHolder = messageHandlers.setCommands();

        MessageListener messageListener = new MessageListener(messageHolder);

        api.addListener(messageListener);

        return api;
    }


}
