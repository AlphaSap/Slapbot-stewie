package com.fthlbot.discordbotfthl.RandomEvents;

import com.fthlbot.discordbotfthl.Util.BotConfig;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.server.ServerJoinEvent;
import org.javacord.api.listener.server.ServerJoinListener;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.Optional;

@Component
public class ServerJoinImpl implements ServerJoinListener {

    private final BotConfig botConfig;

    public ServerJoinImpl(BotConfig botConfig) {
        this.botConfig = botConfig;
    }

    @Override
    public void onServerJoin(ServerJoinEvent event) {
        Server server = event.getServer().getApi().getServerById(botConfig.getTestServerID()).get();
        Optional<User> user = server.getOwner();

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
        embedBuilder.setTitle("Server joined");
        embedBuilder.addField("Server", server.getName(), false);
        embedBuilder.addField("Server ID", server.getIdAsString(), false);
        embedBuilder.addField("Server owner", discriminatedName, false);
        embedBuilder.addField("Server owner ID", id + "", false);
        embedBuilder.addField("Server Member Count", server.getMemberCount() + "", false);
        embedBuilder.addField("Server Channel Count", server.getChannels().size() + "", false);
        embedBuilder.addField("Server Role Count", server.getRoles().size() + "", false);

        embedBuilder.setColor(Color.cyan);
        server.getTextChannelById(botConfig.getErrorLogChannelID()).get().sendMessage(embedBuilder);
    }
}
