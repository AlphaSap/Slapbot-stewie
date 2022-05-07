package com.fthlbot.discordbotfthl.Commands.CommandImpl.ClashCommandImpl;

import Core.Enitiy.clanwar.Attack;
import Core.Enitiy.clanwar.ClanWarMember;
import Core.Enitiy.clanwar.ClanWarModel;
import Core.Enitiy.clanwar.WarInfo;
import Core.Enitiy.player.Player;
import Core.JClash;
import Core.exception.ClashAPIException;
import com.fthlbot.discordbotfthl.Annotation.CommandType;
import com.fthlbot.discordbotfthl.Annotation.Invoker;
import com.fthlbot.discordbotfthl.Commands.CommandListener.ClashCommandListener.AttackListener;
import com.fthlbot.discordbotfthl.Util.Exception.ClashExceptionHandler;
import com.fthlbot.discordbotfthl.Util.Pagination.Pagination;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@Invoker(
        alias = "attack",
        description = "Get attacks for a clan",
        usage = "/attack <Clan tag>",
        type = CommandType.CLASH
)
public class AttackImpl implements AttackListener {
    private static final Logger log = LoggerFactory.getLogger(AttackImpl.class);

    @Override
    public void execute(SlashCommandCreateEvent event) {
        CompletableFuture<InteractionOriginalResponseUpdater> respond = event.getSlashCommandInteraction().respondLater();
        String tag = event.getSlashCommandInteraction().getArguments().get(0).getStringValue().get();

        JClash clash = new JClash();
        WarInfo war;
        try {
            war = clash.getCurrentWar(tag).join();
        } catch (IOException e) {
            //TODO: Handle IOException
            log.error("IOException", e);
            return;
        } catch (ClashAPIException e) {
            //TODO: Handle exception
            log.error("ClashAPIException", e);
            ClashExceptionHandler handler = new ClashExceptionHandler();
            handler.setStatusCode(Integer.valueOf(e.getMessage()));
            handler.setResponder(respond.join());
            handler.respond();
            return;
        }
        List<EmbedBuilder> embeds;
        try {
            embeds = getAllAttacks(war.getClan().getWarMembers(), war.getClan());
        } catch (IOException e) {
            String unexpectedError = "Unexpected error occurred. Please report this to the developers.";
            log.error("IOException", e);
            respond.thenAccept(updater -> updater.setContent(unexpectedError).update());
            return;
        }
        new Pagination().buttonPagination(embeds, respond, event.getApi());
    }

    //Get all attacks for a clan
    private List<EmbedBuilder> getAllAttacks(List<ClanWarMember> clanWarMembers, ClanWarModel clan) throws IOException {
        List<EmbedBuilder> attacks = new ArrayList<>();
        for (ClanWarMember clanWarMember : clanWarMembers) {
            if (clanWarMember.getAttacks() != null) {
                for (Attack attack : clanWarMember.getAttacks()) {
                    attacks.add(makeEmbed(clanWarMember, attack, clan));
                }
            }
        }
        int i = 0;
        for (EmbedBuilder e : attacks) {
            e.setFooter("Attack #" + (i + 1) + " / " + attacks.size());
            i++;
        }
        return attacks;
    }

    private EmbedBuilder makeEmbed(ClanWarMember clanWarMember, Attack attack, ClanWarModel clan) throws IOException {
        return new JClash().getPlayer(attack.getDefenderTag()).thenApply(player -> {
            String s = """
                    ```Stars:           %-3s
                    Destruction:     %-4s%%
                    Attack Duration: %-4s```
                    """;
            return new EmbedBuilder()
                    .setTitle("Attack for:- " + clan.getName())
                    .addField(clanWarMember.getName() + " <a:BlueArrows:972242920310710343> " + player.getName(), String.format(s, "⭐".repeat(attack.getStars()), attack.getDestructionPercentage(), convertSecondsToMinutes(attack.getDuration())), false)
                    .setColor(Color.BLUE);
        }).join();
    }

    private static final int SECONDS_PER_MINUTE = 60;
    //convert seconds into minutes and seconds
    public String convertSecondsToMinutes(int seconds) {
        int minutes = seconds / SECONDS_PER_MINUTE;
        int secondsLeft = seconds % SECONDS_PER_MINUTE;

        return String.format("%d:%02d", minutes, secondsLeft);
    }



}
