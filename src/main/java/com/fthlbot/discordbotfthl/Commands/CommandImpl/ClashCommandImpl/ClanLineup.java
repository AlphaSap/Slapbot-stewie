package com.fthlbot.discordbotfthl.Commands.CommandImpl.ClashCommandImpl;

import com.fthlbot.discordbotfthl.Util.Exception.ClashExceptionHandler;
import com.fthlbot.discordbotfthl.Util.Pagination.Pagination;
import com.fthlbot.discordbotfthl.core.Annotation.CommandType;
import com.fthlbot.discordbotfthl.core.Annotation.Invoker;
import com.fthlbot.discordbotfthl.core.Handlers.Command;
import com.sahhiill.clashapi.core.ClashAPI;
import com.sahhiill.clashapi.core.exception.ClashAPIException;
import com.sahhiill.clashapi.models.war.War;
import com.sahhiill.clashapi.models.war.WarMember;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@Invoker(
        alias = "clan-lineup",
        description = "Shows lineup of clan",
        usage = "/clan-lineup <Clan tag>",
        type = CommandType.CLASH
)
public class ClanLineup implements Command {
    @Override
    public void execute(SlashCommandCreateEvent event) {
        ClashAPI clashAPI = new ClashAPI();
        String clanTag = event.getSlashCommandInteraction().getArguments().get(0).getStringValue().get();
        CompletableFuture<InteractionOriginalResponseUpdater> res = event.getSlashCommandInteraction().respondLater();
        try {
            boolean isWarLogPublic = clashAPI.getClan(clanTag).isWarLogPublic();

            if (!isWarLogPublic) {
                res.thenAccept(r -> r.setContent("Clan war log is not public!").update());
                return;
            }
            War join = clashAPI.getCurrentWar(clanTag);
            List<WarMember> clanLineup = join.getClan().getMembers();
            List<WarMember> warMembers = join.getOpponent().getMembers();

            List<String> strings = formatLineup(clanLineup);
            List<String> strings1 = formatLineup(warMembers);

            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setDescription(String.join("\n", strings))
                    .setTitle("Clan Lineup for " + join.getClan().getName())
                    .setColor(Color.GREEN)
                    .setTimestampToNow();

            EmbedBuilder embedBuilder1 = new EmbedBuilder()
                    .setDescription(String.join("\n", strings1))
                    .setTitle("Clan Lineup for " + join.getOpponent().getName())
                    .setColor(Color.RED)
                    .setTimestampToNow();

            new Pagination().buttonPagination(List.of(embedBuilder, embedBuilder1), res, event.getApi());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }catch (ClashAPIException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ClashExceptionHandler handler = new ClashExceptionHandler();
            handler.setStatusCode(Integer.valueOf(e.getMessage()));
            handler.setResponder(res.join());
            handler.respond();
        }

    }

    private List<String> formatLineup(List<WarMember> clanLineup) {
        return clanLineup.stream()
                .sorted(Comparator.comparingInt(WarMember::getMapPosition))
                .map(x -> "%-8s  %d  %s".formatted(x.getTag(), x.getTownhallLevel(), x.getName()))
                .toList();
    }
}
