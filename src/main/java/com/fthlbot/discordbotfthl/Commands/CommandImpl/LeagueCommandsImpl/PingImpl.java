package com.fthlbot.discordbotfthl.Commands.CommandImpl.LeagueCommandsImpl;

import com.fthlbot.discordbotfthl.Annotation.CommandType;
import com.fthlbot.discordbotfthl.Annotation.Invoker;
import com.fthlbot.discordbotfthl.Commands.CommandListener.PingListener;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.event.message.MessageCreateEvent;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

@Invoker(
        alias = "ping",
        description = "Check bots latency",
        usage = "/ping",
        type = CommandType.MISC
)
@Component
public class PingImpl implements PingListener {
    /*public void onMessageCreate(MessageCreateEvent event) {
        if(messageChecker(event, PingImpl.class)){
        }
    }*/
   /* @Override
    public void execute(MessageCreateEvent event) {
        event.getChannel().sendMessage(String.format( " `%.2f seconds`", event.getApi().getLatestGatewayLatency().getNano() / 1_000_000_000.0  ));
    }*/

    @Override
    public void execute(SlashCommandCreateEvent event) {
        event.getSlashCommandInteraction().createImmediateResponder()
                .setContent(String.valueOf(event.getApi().getLatestGatewayLatency().getNano() / 1_000_000_000.0))
                .respond();
    }
}
