package com.fthlbot.discordbotfthl.Commands.CommandImpl.FunAndRandomCommands;

import com.fthlbot.discordbotfthl.core.Annotation.CommandType;
import com.fthlbot.discordbotfthl.core.Annotation.Invoker;
import com.fthlbot.discordbotfthl.Commands.CommandListener.PingListener;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.springframework.stereotype.Component;

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
