package com.fthlbot.discordbotfthl.Commands.CommandImpl.ClashCommandImpl;

import com.fthlbot.discordbotfthl.Commands.CommandImpl.ClashCommandImpl.Stats.ClanStats;
import com.fthlbot.discordbotfthl.Util.Exception.ClashExceptionHandler;
import com.fthlbot.discordbotfthl.core.Annotation.CommandType;
import com.fthlbot.discordbotfthl.core.Annotation.Invoker;
import com.fthlbot.discordbotfthl.core.Handlers.Command;
import com.sahhiill.clashapi.core.ClashAPI;
import com.sahhiill.clashapi.core.exception.ClashAPIException;
import com.sahhiill.clashapi.models.clan.Clan;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@Component
@Invoker(
        alias = "stats",
        usage = "/stats <clan tag>",
        description = "Returns the stats of the current war of a clan!",
        type = CommandType.CLASH
)
public class StatsImpl implements Command {
    @Override
    public void execute(SlashCommandCreateEvent event) {
        CompletableFuture<InteractionOriginalResponseUpdater> responder = event.getSlashCommandInteraction().respondLater();
        String tag = event.getSlashCommandInteraction().getArguments().get(0).getStringValue().get();

        ClashAPI clash = new ClashAPI();
        try {
            Clan join = clash.getClan(tag);
            if (!join.isWarLogPublic()) {
                responder.thenAccept(res -> {
                    res.setContent("War log is not public").update();
                });
                return;
            }
            String s = new ClanStats(join, clash.getCurrentWar(tag)).clanStats();
            responder.thenAccept(response -> {
                response.setContent(s).update();
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClashAPIException e) {
            e.printStackTrace();
            ClashExceptionHandler handler = new ClashExceptionHandler();
            handler.setStatusCode(Integer.valueOf(e.getMessage()));
            handler.setResponder(responder.join());
            handler.respond();
        }
    }
}
