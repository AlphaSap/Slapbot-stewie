package com.fthlbot.discordbotfthl.Commands.CommandImpl.ClashCommandImpl;

import Core.Enitiy.clan.ClanModel;
import Core.Enitiy.clanwar.Attack;
import Core.Enitiy.clanwar.ClanWarMember;
import Core.Enitiy.clanwar.WarInfo;
import Core.JClash;
import Core.exception.ClashAPIException;
import com.fthlbot.discordbotfthl.Annotation.CommandType;
import com.fthlbot.discordbotfthl.Annotation.Invoker;
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
            if (!join.isWarLogPublic()){
                responder.thenAccept(res -> {
                    res.setContent("War log is not public").update();
                });
                return;
            }
            String s = stats(join, clash.getCurrentWar(tag).join());
            responder.thenAccept(response -> {
                response.setContent(s).update();
            });
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        } catch (ClashAPIException e) {
            e.printStackTrace();
            ClashExceptionHandler handler = new ClashExceptionHandler();
            handler.setStatusCode(Integer.valueOf(e.getMessage()));
            handler.setResponder(responder.join());
            handler.respond();
        }
    }

    public String stats(ClanModel clan, WarInfo war) throws ClashAPIException, ParseException {
        if (clan.isWarLogPublic()) {
            if (!war.getState().equalsIgnoreCase("notInWar")) {
                int Home_2_left = 0;
                int Opponent_2_left = 0;
                int Home_3_left = 0;
                int Opponent_3_left = 0;

                int Home3 = 0;
                int opponent3 = 0;

                int Home2 = 0;
                int opponent2 = 0;

                Date BattleTime_end = war.getEndTimeAsDate();

                Date BattleTime_start = war.getStartTimeAsDate();

                String to_join = " ";
                TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

                if (war.getState().equalsIgnoreCase("preparation")) {

                    Date now = new Date();
                    long difference_In_prep = BattleTime_start.getTime() - now.getTime();

                    long difference_In_Minute = (difference_In_prep / (1000 * 60)) % 60;

                    long difference_In_Hours = (difference_In_prep / (1000 * 60 * 60)) % 24;

                    if (difference_In_Minute < 0) {
                        to_join = "Preparation time ending soon";
                    } else
                        to_join = "War starts in " + difference_In_Hours + " hours " + difference_In_Minute + " minutes";
                } else if (war.getState().equalsIgnoreCase("inWar")) {
                    Date now = new Date();
                    long difference_in_battle = BattleTime_end.getTime() - now.getTime();
                    long difference_In_Minute = (difference_in_battle / (1000 * 60)) % 60;

                    long difference_In_Hours = (difference_in_battle / (1000 * 60 * 60)) % 24;

                    if (difference_In_Minute < 0) {
                        to_join = "War is ending!";
                    } else
                        to_join = "War ends in " + difference_In_Hours + " hours " + difference_In_Minute + " minutes";
                } else if (war.getState().equalsIgnoreCase("warEnded")) {
                    to_join = war.getStatus();
                }

                List<ClanWarMember> clanWarMember = war.getClan().getWarMembers();
                List<ClanWarMember> clanWarMember_Opponent = war.getEnemy().getWarMembers();

                List<Integer> homeAvgTime = new ArrayList<>();
                List<Integer> opponentAvgTime = new ArrayList<>();

                for (int i = 0; i < clanWarMember.toArray().length; i++) {

                    // Counting Home 2 and 3
                    if (clanWarMember.get(i).getAttacks() != null) {
                        for (int j = 0; j < clanWarMember.get(i).getAttacks().toArray().length; j++) {
                            var temp = clanWarMember.get(i).getAttacks().get(j).getStars();
                            if (temp == 3) {
                                Home3++;
                            } else if (temp == 2) {
                                Home2++;
                            }
                            Integer duration = clanWarMember.get(i).getAttacks().get(j).getDuration();
                            homeAvgTime.add(duration);
                        }
                    }

                    if (clanWarMember.get(i).getBestOpponentAttack() != null) {
                        var temp = clanWarMember.get(i).getBestOpponentAttack().getStars();

                        if (temp == 3) {
                            Opponent_3_left++;
                        } else if (temp == 2) {
                            Opponent_2_left++;
                        }
                    }

                    if (clanWarMember_Opponent.get(i).getAttacks() != null) {
                        for (int j = 0; j < clanWarMember_Opponent.get(i).getAttacks().size(); j++) {

                            var temp = clanWarMember_Opponent.get(i).getAttacks().get(j).getStars();

                            if (temp == 3) {
                                opponent3++;

                            } else if (temp == 2) {
                                opponent2++;
                            }
                            Integer duration = clanWarMember_Opponent.get(i).getAttacks().get(j).getDuration();
                            opponentAvgTime.add(duration);
                        }
                    }

                    if (clanWarMember_Opponent.get(i).getBestOpponentAttack() != null) {
                        var temp = clanWarMember_Opponent.get(i).getBestOpponentAttack().getStars();

                        if (temp == 3) {
                            Home_3_left++;
                        } else if (temp == 2) {
                            Home_2_left++;
                        }
                    }
                }


                String hr = Home3 + "/" + war.getClan().getAttacks() + "   " + Math.round((double) Home3 * 100 / war.getClan().getAttacks()) + "%       HR       " + Math.round((double) opponent3 * 100 / war.getEnemy().getAttacks()) + "%   " + opponent3 + "/" + war.getEnemy().getAttacks();

                //Calculating Average Time
                //Home
                int homeTime = calculateAverageMinutes(homeAvgTime);
                //Opponent
                int opponentTime = calculateAverageMinutes(opponentAvgTime);

                String Mes = String.format(
                        """
                                %s   vs   %s
                                %-5d     Stars         %5d
                                %-5.2f     Percentage    %5.2f
                                %-5d     Attacks       %5d
                                %-5d     ***           %5d
                                %-5d     **            %5d
                                %-5s     Average Time  %5s""",

                        clan.getName(), war.getEnemy().getName(), war.getClan().getStars(), war.getEnemy().getStars(),
                        war.getClan().getDestructionPercentage(), war.getEnemy().getDestructionPercentage(),
                        war.getClan().getAttacks(), war.getEnemy().getAttacks(),
                        Home_3_left, Opponent_3_left, Home_2_left, Opponent_2_left,
                        convertSecondsToMinutes(homeTime), convertSecondsToMinutes(opponentTime)
                );

                String stats = Mes + "\n \n" + hr + "\n" + to_join;
                return "```" + stats + "```";

            } else
                return "Clan not in war";
        } else
            return "Unable to fetch the current war! Reason: 'War Log not public'";
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
