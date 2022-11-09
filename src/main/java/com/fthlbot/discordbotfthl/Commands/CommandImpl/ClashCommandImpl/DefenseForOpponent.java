package com.fthlbot.discordbotfthl.Commands.CommandImpl.ClashCommandImpl;


import com.fthlbot.discordbotfthl.Util.Utils;
import com.sahhiill.clashapi.models.war.War;
import com.sahhiill.clashapi.models.war.WarAttack;
import com.sahhiill.clashapi.models.war.WarMember;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.List;
import java.util.*;

public class DefenseForOpponent {
    private static final Logger log = LoggerFactory.getLogger(DefenseForOpponent.class);
    private final static int NAME_MAX_LEN = 20, ID_MAX_LEN = 11, ALIAS_MAX_LEN = 10;



    public EmbedBuilder getDefEmbed(User user, War c) {
        Map<WarMember, List<WarAttack>> defAndAttacks = this.getDefAndAttacks(c);
        StringBuilder stringBuilder = setDefense(defAndAttacks);
        EmbedBuilder em = new EmbedBuilder();
        em = em.setTitle("Defenses for " + c.getOpponent().getName())
                .setDescription(stringBuilder.toString())
                .setColor(Color.cyan)
                .setAuthor(user)
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
                if (x.getAttacks().get(0).getStars() == (3)) {
                    defwonstats += "\uD83D\uDCA5";
                }
            }
            String temp = formatRow(Utils.getTownHallEmote(x.getWarMember().getTownhallLevel()), defwonstats, x.getWarMember().getName() + "`", " ");
            s.append(temp).append("\n");
        }
        return s;
    }

    private Map<WarMember, List<WarAttack>> getDefAndAttacks(War war) {
        List<WarMember> homeWarMembers =  war.getOpponent().getMembers();
        List<WarMember> enemyWarMembers =war.getClan().getMembers();

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
