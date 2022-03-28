package com.fthlbot.discordbotfthl.Commands.ClashCommandImpl;

import Core.Enitiy.clanwar.Attack;
import Core.Enitiy.clanwar.ClanWarMember;
import Core.Enitiy.clanwar.WarInfo;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.*;
import java.util.List;

public class DefenseForOpponent {
    private static final Logger log = LoggerFactory.getLogger(DefenseForOpponent.class);
    private final static int NAME_MAX_LEN = 20, ID_MAX_LEN = 11, ALIAS_MAX_LEN = 10;


    public EmbedBuilder getDefEmbed(User user, WarInfo c) {
        Map<ClanWarMember, List<Attack>> defAndAttacks = this.getDefAndAttacks(c);
        StringBuilder stringBuilder = setDefense(defAndAttacks);
        EmbedBuilder em = new EmbedBuilder();
        em = em.setTitle("Defenses for " + c.getEnemy().getName())
                .setDescription(stringBuilder.toString())
                .setColor(Color.cyan)
                .setAuthor(user)
                .setTimestampToNow();
        return em;
    }

    private Map<ClanWarMember, List<Attack>> getDefAndAttacks(WarInfo war){
        List<ClanWarMember> homeWarMembers = war.getEnemy().getWarMembers();
        List<ClanWarMember> enemyWarMembers = war.getClan().getWarMembers();

        Map<ClanWarMember, List<Attack>> defence = new HashMap<>();

        enemyWarMembers
                .stream()
                .filter(member -> member.getAttacks() != null)
                .forEach(member -> {
                    member.getAttacks().forEach(attack -> {
                        ClanWarMember homeWarMember = homeWarMembers
                                .stream()
                                .filter(warMember -> warMember.getTag().equalsIgnoreCase(attack.getDefenderTag()))
                                .findFirst().get();

                        if (defence.containsKey(homeWarMember)){
                            List<Attack> attacks = defence.get(homeWarMember);
                            attacks.add(attack);
                            defence.replace(homeWarMember,attacks);
                        }else {
                            List<Attack> newAttacks = new ArrayList<>();
                            newAttacks.add(attack);
                            defence.put(homeWarMember, newAttacks);
                        }
                    });
                });
        return defence;
    }

    private StringBuilder setDefense(Map<ClanWarMember, List<Attack>> defence){
        List<tempWarMember> tempWarMembers = new ArrayList<>();
        defence.forEach((x, y) -> {
            tempWarMembers.add(new tempWarMember(y, x));
        });

        List<tempWarMember> collect = tempWarMembers.stream()
                .sorted(Comparator.comparingInt(x -> x.getClanWarMember().getMapPosition()))
                .toList();
        StringBuilder s = new StringBuilder();
        for (tempWarMember x : collect) {
            if (!x.getAttacks().isEmpty()) {
                final int[] defWon = {0};
                String defwonstats = "";
                for (Attack attack : x.getAttacks()) {
                    if (attack.getStars() <= 0)
                        defWon[0]++;

                    defwonstats = "`  " + defWon[0] + "/" + x.getAttacks().size();
                    if (x.getAttacks().size() == 1)
                        if (x.getAttacks().get(0).getStars().equals(3))
                            defwonstats += "\uD83D\uDCA5";
                }
                String temp = formatRow(getTownHallEmote(x.getClanWarMember().getTownhallLevel()), defwonstats, x.getClanWarMember().getName() + "`", " ");
                s.append(temp).append("\n");
            }
        }
        return s;
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

    }

    private static String formatRow(String name, String tag, String alias, String ext) {
        return String.format("%-" + (ID_MAX_LEN + ext.length()) + "s%-" + (ALIAS_MAX_LEN + ext.length()) +
                "s%-" + NAME_MAX_LEN + "s", name + ext, tag + ext, alias);
    }

    public String getTownHallEmote(int townhallLevel){
        switch (townhallLevel) {
            case 1:
                return "<:th1:947276195945381978>";
            case 2:
                return "<:th2:947276191998570506>";
            case 3:
                return "<:th3:947276192770318368>";
            case 4:
                return  "<:th4:947277976293220362>";
            case 5:
                return  "<:th5:947276195991552011>";
            case 6:
                return "<:th6:947276151418667049>";
            case 7:
                return "<:th7:947276197887352942>";
            case 8:
                return "<:th8:947276734200446976>";
            case 9:
                return "<:th9:947276159681445898>";
            case 10:
                return "<:th10:947276159782113280>";
            case 11:
                return  "<:th11:947276991030243468>";
            case 12:
                return "<:th12:947276159954092088>";
            case 13:
                return  "<:th13:947282074249879572>";
            case 14:
                return  "<:th14:947276161006829590>";
            default:
                return null;
        }
    }


}
