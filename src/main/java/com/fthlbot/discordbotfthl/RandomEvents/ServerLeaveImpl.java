package com.fthlbot.discordbotfthl.RandomEvents;

import com.fthlbot.discordbotfthl.Util.BotConfig;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.server.ServerLeaveEvent;
import org.javacord.api.listener.server.ServerLeaveListener;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.Optional;

@Component
public class ServerLeaveImpl implements ServerLeaveListener {
    private final BotConfig botConfig;

    public ServerLeaveImpl(BotConfig botConfig) {
        this.botConfig = botConfig;
    }

    @Override
    public void onServerLeave(ServerLeaveEvent event) {
        Server testServer = event.getServer().getApi().getServerById(botConfig.getTestServerID()).get();
        Server server = event.getServer();
        Optional<User> user = testServer.getOwner();

        long id;
        String discriminatedName;

        if (user.isPresent()) {
            id = user.get().getId();
            discriminatedName = user.get().getDiscriminatedName();
        }else {
            id = 000000L;
            discriminatedName = "Unknown";
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Server left :(");
        embedBuilder.addField("Server", server.getName(), false);
        embedBuilder.addField("Server ID", server.getIdAsString(), false);
        embedBuilder.addField("Server owner", discriminatedName, false);
        embedBuilder.addField("Server owner ID", id + "", false);
        embedBuilder.addField("Server Member Count", server.getMemberCount() + "", false);
        embedBuilder.addField("Server Channel Count", server.getChannels().size() + "", false);
        embedBuilder.addField("Server Role Count", server.getRoles().size() + "", false);

        embedBuilder.setColor(Color.cyan);
        testServer.getTextChannelById(botConfig.getErrorLogChannelID()).get().sendMessage(embedBuilder);
    }
}
