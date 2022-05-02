package com.fthlbot.discordbotfthl.Commands.CommandImpl.ClashCommandImpl;

import Core.Enitiy.clan.ClanModel;
import Core.Enitiy.clanwar.Attack;
import Core.Enitiy.clanwar.ClanWarMember;
import Core.Enitiy.clanwar.WarInfo;
import Core.JClash;
import Core.exception.ClashAPIException;
import com.fthlbot.discordbotfthl.Annotation.CommandType;
import com.fthlbot.discordbotfthl.Annotation.Invoker;
import com.fthlbot.discordbotfthl.Commands.CommandListener.ClashCommandListener.AttackListener;
import com.fthlbot.discordbotfthl.Util.Utils;
import com.fthlbot.discordbotfthl.Util.Exception.ClashExceptionHandler;
import com.fthlbot.discordbotfthl.Util.JavacordLogger;
import org.javacord.api.entity.channel.ServerTextChannel;
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
import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@Invoker(
        alias = "defense",
        description = "Fetches defense from the current ongoing war of the provided clan tag",
        usage = "/defense <TAG>",
        type = CommandType.CLASH
)
//TODO revist this shit ass code and fix the 2hit glitch, this thing mad annoying not gonna continue this again.
public class DefenseImpl implements AttackListener {
    private static final Logger log = LoggerFactory.getLogger(DefenseImpl.class);
    private final static int NAME_MAX_LEN = 20, ID_MAX_LEN = 11, ALIAS_MAX_LEN = 15;
    @Override
    public void execute(SlashCommandCreateEvent event) {
        SlashCommandInteraction interaction = event.getSlashCommandInteraction();
        CompletableFuture<InteractionOriginalResponseUpdater> respondLater = interaction.respondLater();
        String tag = interaction.getArguments().get(0).getStringValue().get();

        JClash clash = new JClash();

        try {
            ClanModel join = clash.getClan(tag).join();
            if (!join.isWarLogPublic()){
                respondLater.thenAccept(res -> {
                   res.setContent("War log is not public").update();
                });
                return;
            }
            clash.getCurrentWar(tag).thenAccept(c -> {
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

            });
        } catch (ClashAPIException | IOException e) {
            new ClashExceptionHandler()
                    .setResponder(respondLater.join())
                    .setStatusCode(Integer.valueOf(e.getMessage()))
                    .respond();
        }
        new JavacordLogger()
                .setLogger(DefenseImpl.class)
                .setChannel((ServerTextChannel) event.getApi().getTextChannelById(777902179771613184L).get())
                .info("User def command",
                        event.getInteraction().getUser(),
                        event.getSlashCommandInteraction().getServer().get());
    }

    private EmbedBuilder getDefEmbed(SlashCommandInteraction interaction, WarInfo c) {
        Map<ClanWarMember, List<Attack>> defAndAttacks = this.getDefAndAttacks(c);
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
        private final List<Attack> attacks;
        private final ClanWarMember clanWarMember;

        public tempWarMember(List<Attack> attacks, ClanWarMember clanWarMember) {
            this.attacks = attacks;
            this.clanWarMember = clanWarMember;
        }

        public ClanWarMember getClanWarMember() {
            return clanWarMember;
        }

        public List<Attack> getAttacks() {
            return attacks;
        }

        @Override
        public String toString() {
            return "tempWarMember{" +
                    "attacks=" + attacks +
                    ", clanWarMember=" + clanWarMember +
                    '}';
        }
    }

    private static String formatRow(String name, String tag, String alias, String ext) {
        return String.format("%-" + (ID_MAX_LEN + ext.length()) + "s%-" + (ALIAS_MAX_LEN + ext.length()) +
                "s%-" + NAME_MAX_LEN + "s", name + ext, tag + ext, alias);
    }

    //perfect  example for setting fresh hits just add a spark after the attack lenght is 1 and is a 3 star
    private StringBuilder setDefense(Map<ClanWarMember, List<Attack>> defence) {
        List<tempWarMember> tempWarMembers = new ArrayList<>();
        defence.forEach((x, y) -> {
            tempWarMember e = new tempWarMember(y, x);
            tempWarMembers.add(e);
        });

        List<tempWarMember> collect = tempWarMembers.stream()
                .sorted(Comparator.comparingInt(x -> x.getClanWarMember().getMapPosition()))
                .toList();
        StringBuilder s = new StringBuilder();
        for (tempWarMember x : collect) {
            if (x.getAttacks().isEmpty()) {
                continue;
            }
            final int[] defWon = {0};
            for (Attack attack : x.getAttacks()) {
                if (attack.getStars() <= 0)
                    defWon[0]++;
            }
            String defwonstats = "`  " + defWon[0] + "/" + x.getAttacks().size();
            x.attacks.sort(Comparator.comparingInt(Attack::getStars));//.stream().anyMatch(a -> a.getStars().equals(3));
            defwonstats += "⭐".repeat(x.attacks.get(x.attacks.size() - 1).getStars());


            if (x.getAttacks().size() == 1) {
                if (x.getAttacks().get(0).getStars().equals(3)) {
                    defwonstats += "\uD83D\uDCA5";
                }
            }
            String temp = formatRow(Utils.getTownHallEmote(x.getClanWarMember().getTownhallLevel()), defwonstats, x.getClanWarMember().getName() + "`", " ");
            s.append(temp).append("\n");
        }
        return s;
    }

    private Map<ClanWarMember, List<Attack>> getDefAndAttacks(WarInfo war) {
        List<ClanWarMember> homeWarMembers = war.getClan().getWarMembers();
        List<ClanWarMember> enemyWarMembers = war.getEnemy().getWarMembers();

        Map<ClanWarMember, List<Attack>> defence = new HashMap<>();

        enemyWarMembers.stream()
                .filter(member -> member.getAttacks() != null)
                .forEach(member -> {
                    member.getAttacks().forEach(attack -> {
                        ClanWarMember homeWarMember = null;
                        String defenderTag = attack.getDefenderTag();
                        for (ClanWarMember warMember : homeWarMembers) {
                            if (warMember.getTag().equalsIgnoreCase(defenderTag)) {
                                homeWarMember = warMember;
                                break;
                            }
                        }

                        if (defence.containsKey(homeWarMember)) {
                            List<Attack> attacks = defence.get(homeWarMember);
                            attacks.add(attack);
                            defence.replace(homeWarMember, attacks);
                        } else {
                            List<Attack> newAttacks = new ArrayList<>();
                            newAttacks.add(attack);
                            defence.put(homeWarMember, newAttacks);
                        }
                    });
                });
        return defence;
    }

}
