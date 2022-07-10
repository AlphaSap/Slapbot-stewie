package com.fthlbot.discordbotfthl.Events;

import com.fthlbot.discordbotfthl.Util.BotConfig;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.server.ServerLeaveEvent;
import org.javacord.api.listener.server.ServerLeaveListener;
import org.springframework.stereotype.Component;

import java.awt.*;

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
        User user = server.requestOwner().join();

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Server left :(");
        embedBuilder.addField("Server", server.getName(), false);
        embedBuilder.addField("Server ID", server.getIdAsString(), false);
        embedBuilder.addField("Server owner", user.getDiscriminatedName(), false);
        embedBuilder.addField("Server owner ID", user.getId() + "", false);
        embedBuilder.addField("Server Member Count", server.getMemberCount() + "", false);
        embedBuilder.addField("Server Channel Count", server.getChannels().size() + "", false);
        embedBuilder.addField("Server Role Count", server.getRoles().size() + "", false);

        embedBuilder.setColor(Color.cyan);
        testServer.getTextChannelById(botConfig.getErrorLogChannelID()).get().sendMessage(embedBuilder);
    }
}
