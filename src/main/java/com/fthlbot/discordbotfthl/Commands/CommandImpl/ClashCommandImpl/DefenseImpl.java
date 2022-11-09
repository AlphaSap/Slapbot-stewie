package com.fthlbot.discordbotfthl.Commands.CommandImpl.ClashCommandImpl;


import com.fthlbot.discordbotfthl.Commands.CommandListener.ClashCommandListener.DefenseListener;
import com.fthlbot.discordbotfthl.Util.Exception.ClashExceptionHandler;
import com.fthlbot.discordbotfthl.Util.Utils;
import com.fthlbot.discordbotfthl.core.Annotation.CommandType;
import com.fthlbot.discordbotfthl.core.Annotation.Invoker;
import com.sahhiill.clashapi.core.ClashAPI;
import com.sahhiill.clashapi.core.exception.ClashAPIException;
import com.sahhiill.clashapi.models.clan.Clan;
import com.sahhiill.clashapi.models.war.War;
import com.sahhiill.clashapi.models.war.WarAttack;
import com.sahhiill.clashapi.models.war.WarMember;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Component
@Invoker(
        alias = "defense",
        description = "Fetches defense from the current ongoing war of the provided clan tag",
        usage = "/defense <TAG>",
        type = CommandType.CLASH
)
public class DefenseImpl implements DefenseListener {
    private static final Logger log = LoggerFactory.getLogger(DefenseImpl.class);
    private final static int NAME_MAX_LEN = 20, ID_MAX_LEN = 11, ALIAS_MAX_LEN = 15;
    @Override
    public void execute(SlashCommandCreateEvent event) {
        SlashCommandInteraction interaction = event.getSlashCommandInteraction();
        CompletableFuture<InteractionOriginalResponseUpdater> respondLater = interaction.respondLater();
        String tag = interaction.getArguments().get(0).getStringValue().get();

        ClashAPI clash = new ClashAPI();

        try {
            Clan join = clash.getClan(tag);
            if (!join.isWarLogPublic()){
                respondLater.thenAccept(res -> {
                   res.setContent("War log is not public").update();
                });
                return;
            }
            War c = clash.getCurrentWar(tag);
                EmbedBuilder em = getDefEmbed(interaction, c);
                //EmbedBuilder finalEm = em;

                respondLater.thenAccept(res -> {
                    CompletableFuture<Message> message = res.addEmbed(em).update();

                    message.thenAccept(msg -> {
                        msg.addReaction("\uD83D\uDD01");
                        msg.addReactionAddListener(react -> {
                            if (react.getUser().get().getId() != react.getApi().getYourself().getId()) {
                                EmbedBuilder defEmbed = new DefenseForOpponent().getDefEmbed(interaction.getUser(), c);
                                event.getInteraction().getChannel().get().sendMessage(defEmbed);
                            }
                        });
                    });

                });

        } catch (ClashAPIException | IOException e) {
            new ClashExceptionHandler()
                    .setResponder(respondLater.join())
                    .setStatusCode(Integer.valueOf(e.getMessage()))
                    .respond();
        }
    }

    private EmbedBuilder getDefEmbed(SlashCommandInteraction interaction, War c) {
        Map<WarMember, List<WarAttack>> defAndAttacks = this.getDefAndAttacks(c);
        StringBuilder stringBuilder = setDefense(defAndAttacks);
        EmbedBuilder em = new EmbedBuilder();
        em = em.setTitle("Defenses for " + c.getClan().getName())
                .setDescription(stringBuilder.toString())
                .setColor(Color.cyan)
                .setAuthor(interaction.getUser())
                .setTimestampToNow();
        return em;
    }

    class tempWarMember {
        private final List<WarAttack> attacks;
        private final WarMember WarMember;

        public tempWarMember(List<WarAttack> attacks, WarMember WarMember) {
            this.attacks = attacks;
            this.WarMember = WarMember;
        }

        public WarMember getWarMember() {
            return WarMember;
        }

        public List<WarAttack> getAttacks() {
            return attacks;
        }

        @Override
        public String toString() {
            return "tempWarMember{" +
                    "attacks=" + attacks +
                    ", WarMember=" + WarMember +
                    '}';
        }
    }

    private static String formatRow(String name, String tag, String alias, String ext) {
        return String.format("%-" + (ID_MAX_LEN + ext.length()) + "s%-" + (ALIAS_MAX_LEN + ext.length()) +
                "s%-" + NAME_MAX_LEN + "s", name + ext, tag + ext, alias);
    }

    //perfect  example for setting fresh hits just add a spark after the attack lenght is 1 and is a 3 star
    private StringBuilder setDefense(Map<WarMember, List<WarAttack>> defence) {
        List<tempWarMember> tempWarMembers = new ArrayList<>();
        defence.forEach((x, y) -> {
            tempWarMember e = new tempWarMember(y, x);
            tempWarMembers.add(e);
        });

        List<tempWarMember> collect = tempWarMembers.stream()
                .sorted(Comparator.comparingInt(x -> x.getWarMember().getMapPosition()))
                .toList();
        StringBuilder s = new StringBuilder();
        for (tempWarMember x : collect) {
            if (x.getAttacks().isEmpty()) {
                continue;
            }
            final int[] defWon = {0};
            for (WarAttack attack : x.getAttacks()) {
                if (attack.getStars() <= 0)
                    defWon[0]++;
            }
            String defwonstats = "`  " + defWon[0] + "/" + x.getAttacks().size();
            x.attacks.sort(Comparator.comparingInt(WarAttack::getStars));//.stream().anyMatch(a -> a.getStars().equals(3));
            defwonstats += "⭐".repeat(x.attacks.get(x.attacks.size() - 1).getStars());


            if (x.getAttacks().size() == 1) {
                if (x.getAttacks().get(0).getStars() == 3) {
                    defwonstats += "\uD83D\uDCA5";
                }
            }
            String temp = formatRow(Utils.getTownHallEmote(x.getWarMember().getTownhallLevel()), defwonstats, x.getWarMember().getName() + "`", " ");
            s.append(temp).append("\n");
        }
        return s;
    }

    private Map<WarMember, List<WarAttack>> getDefAndAttacks(War war) {
        List<WarMember> homeWarMembers = war.getClan().getMembers();
        List<WarMember> enemyWarMembers = war.getOpponent().getMembers();

        Map<WarMember, List<WarAttack>> defence = new HashMap<>();

        enemyWarMembers.stream()
                .filter(member -> member.getAttacks() != null)
                .forEach(member -> {
                    member.getAttacks().forEach(attack -> {
                        WarMember homeWarMember = null;
                        String defenderTag = attack.getDefenderTag();
                        for (WarMember warMember : homeWarMembers) {
                            if (warMember.getTag().equalsIgnoreCase(defenderTag)) {
                                homeWarMember = warMember;
                                break;
                            }
                        }

                        if (defence.containsKey(homeWarMember)) {
                            List<WarAttack> attacks = defence.get(homeWarMember);
                            attacks.add(attack);
                            defence.replace(homeWarMember, attacks);
                        } else {
                            List<WarAttack> newAttacks = new ArrayList<>();
                            newAttacks.add(attack);
                            defence.put(homeWarMember, newAttacks);
                        }
                    });
                });
        return defence;
    }

}
