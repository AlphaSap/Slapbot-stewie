package com.fthlbot.discordbotfthl.Commands.CommandImpl.ClashCommandImpl;

import Core.Enitiy.clan.ClanModel;
import Core.Enitiy.clanwar.ClanWarMember;
import Core.Enitiy.clanwar.WarInfo;
import Core.JClash;
import Core.exception.ClashAPIException;
import com.fthlbot.discordbotfthl.Annotation.CommandType;
import com.fthlbot.discordbotfthl.Annotation.Invoker;
import com.fthlbot.discordbotfthl.Commands.CommandImpl.ClashCommandImpl.Stats.ClanStats;
import com.fthlbot.discordbotfthl.Handlers.Command;
import com.fthlbot.discordbotfthl.Util.Exception.ClashExceptionHandler;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
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

        JClash clash = new JClash();
        try {
            ClanModel join = clash.getClan(tag).join();
            if (!join.isWarLogPublic()) {
                responder.thenAccept(res -> {
                    res.setContent("War log is not public").update();
                });
                return;
            }
            String s = new ClanStats(join, clash.getCurrentWar(tag).join()).clanStats();
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
