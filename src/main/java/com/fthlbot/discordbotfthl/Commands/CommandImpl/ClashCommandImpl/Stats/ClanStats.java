package com.fthlbot.discordbotfthl.Commands.CommandImpl.ClashCommandImpl.Stats;

import com.fthlbot.discordbotfthl.Util.ClashUtils;
import com.sahhiill.clashapi.models.clan.Clan;
import com.sahhiill.clashapi.models.war.War;
import com.sahhiill.clashapi.models.war.WarClan;
import com.sahhiill.clashapi.models.war.WarMember;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ClanStats {
    private Clan clan;
    private War war;

    public ClanStats(Clan clan, War war) {
        this.clan = clan;
        this.war = war;
    }

    public String clanStats() {
        if (!this.clan.isWarLogPublic()) {
            return "War log is not public";
        }
        if (this.war.getState().equalsIgnoreCase("notInWar")) {
            return "No war is currently ongoing";
        }

        StringBuilder sb = new StringBuilder();

        WarClan enemy = this.war.getOpponent();

        AtomicInteger homeThree = new AtomicInteger();
        AtomicInteger homeTwo = new AtomicInteger();
        AtomicInteger opponentThree = new AtomicInteger();
        AtomicInteger opponentTwo = new AtomicInteger();

        List<Integer> homeTime = new ArrayList<>();
        List<Integer> opponentTime = new ArrayList<>();

        enemy.getMembers().stream().filter(x -> x.getBestOpponentAttack() != null).map(WarMember::getBestOpponentAttack).forEach(x -> {
            if (x.getStars() == 3) {
                homeThree.incrementAndGet();
            } else if (x.getStars() == 2) {
                homeTwo.incrementAndGet();
            }
            homeTime.add(x.getDuration());
        });
        this.war.getClan().getMembers().stream().filter(x -> x.getBestOpponentAttack() != null).map(WarMember::getBestOpponentAttack).forEach(x -> {
            if (x.getStars() == 3) {
                opponentThree.incrementAndGet();
            } else if (x.getStars() == 2) {
                opponentTwo.incrementAndGet();
            }
            opponentTime.add(x.getDuration());
        });
        int homeTotal = this.war.getClan().getAttacks();
        int opponentTotal = this.war.getOpponent().getAttacks();

        //Calculating Average Time
        //Home
        int homeTimeAsInt = calculateAverageMinutes(homeTime);
        //Opponent
        int opponentTimeAsInt = calculateAverageMinutes(opponentTime);

        int avgHR = (int) ((float) homeThree.get() * 100 / (float) homeTotal);
        int avgHR_opponent = (int) ((float) opponentThree.get() * 100 / (float) opponentTotal);

        Date BattleTime_end = null;
        Date BattleTime_start;
        String to_join = "";
        try {
            BattleTime_end = ClashUtils.getEndTimeAsDate(this.war);
            BattleTime_start = ClashUtils.getStartTimeAsDate(this.war);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        if (this.war.getState().equalsIgnoreCase("preparation")) {

            Date now = new Date();
            long difference_In_prep = BattleTime_start.getTime() - now.getTime();

            long difference_In_Minute = (difference_In_prep / (1000 * 60)) % 60;

            long difference_In_Hours = (difference_In_prep / (1000 * 60 * 60)) % 24;

            if (difference_In_Minute < 0) {
                to_join = "Preparation time ending soon";
            } else
                to_join = "War starts in " + difference_In_Hours + " hours " + difference_In_Minute + " minutes";
        } else if (this.war.getState().equalsIgnoreCase("inWar")) {
            Date now = new Date();
            long difference_in_battle = BattleTime_end.getTime() - now.getTime();
            long difference_In_Minute = (difference_in_battle / (1000 * 60)) % 60;

            long difference_In_Hours = (difference_in_battle / (1000 * 60 * 60)) % 24;

            if (difference_In_Minute < 0) {
                to_join = "War is ending!";
            } else
                to_join = "War ends in " + difference_In_Hours + " hours " + difference_In_Minute + " minutes";
        } else if (this.war.getState().equalsIgnoreCase("warEnded")) {
            to_join = ClashUtils.getStatus(this.war);
        }
        String s = """
                %-5s     VS     %5s
                %-5s         Stars           %5s
                %-5.2f%%       Percentage      %5.2f%%
                %-5s         Attacks         %5s
                %-5d         ***             %5d
                %-5d         **              %5d
                %-5s         Average Time    %5s
                                
                %d/%d -  %d%%    HR     %d%% -  %d/%d
                """.formatted(
                this.war.getClan().getName(),
                this.war.getOpponent().getName(),
                this.war.getClan().getStars(),
                this.war.getOpponent().getStars(),
                this.war.getClan().getDestructionPercentage(),
                this.war.getOpponent().getDestructionPercentage(),
                this.war.getClan().getAttacks(),
                this.war.getOpponent().getAttacks(),
                homeThree.get(), opponentThree.get(), homeTwo.get(), opponentTwo.get(),
                convertSecondsToMinutes(homeTimeAsInt), convertSecondsToMinutes(opponentTimeAsInt),
                homeThree.get(), homeTotal, avgHR, avgHR_opponent, opponentThree.get(), opponentTotal
        );


        return "```" + sb.append(s).append("\n").append(to_join) + "```";
    }

    //calculate average minutes from a list of seconds
    public int calculateAverageMinutes(List<Integer> seconds) {
        double sum = 0;
        for (Integer second : seconds) {
            sum += second;
        }
        return (int) (sum / seconds.size());
    }

    private static final int SECONDS_PER_MINUTE = 60;

    //convert seconds into minutes and seconds
    public String convertSecondsToMinutes(int seconds) {
        int minutes = seconds / SECONDS_PER_MINUTE;
        int secondsLeft = seconds % SECONDS_PER_MINUTE;

        return String.format("%d:%02d", minutes, secondsLeft);
    }
}
