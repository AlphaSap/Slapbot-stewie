package com.fthlbot.discordbotfthl.Commands.CommandImpl.StaffCommandsImpl;

import com.fthlbot.discordbotfthl.DatabaseModels.Exception.EntityNotFoundException;
import com.fthlbot.discordbotfthl.DatabaseModels.ScheduleWar.DivisonWeek.DivisionWeekService;
import com.fthlbot.discordbotfthl.DatabaseModels.ScheduleWar.DivisonWeek.DivisionWeeks;
import com.fthlbot.discordbotfthl.DatabaseModels.ScheduleWar.Schedule.ScheduleWarService;
import com.fthlbot.discordbotfthl.DatabaseModels.ScheduleWar.Schedule.ScheduledWar;
import com.fthlbot.discordbotfthl.DatabaseModels.Team.Team;
import com.fthlbot.discordbotfthl.DatabaseModels.Team.TeamService;
import com.fthlbot.discordbotfthl.Util.GeneralService;
import com.fthlbot.discordbotfthl.Util.Pagination.ButtonRemoveJobScheduler;
import com.fthlbot.discordbotfthl.Util.Pagination.Pagination;
import com.fthlbot.discordbotfthl.Util.Utils;
import com.fthlbot.discordbotfthl.core.Annotation.CommandType;
import com.fthlbot.discordbotfthl.core.Annotation.Invoker;
import com.fthlbot.discordbotfthl.core.Handlers.Command;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.message.component.ButtonStyle;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;
import org.json.JSONArray;
import org.quartz.SchedulerException;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;


@Invoker(
        alias = "create-match-up-new",
        type = CommandType.STAFF,
                                                    //2022-08-19
        usage = "/create-match-up-new <division> <div-week (Autocomplete)> <parseable string>",
        description = "Will create match ups with the parsable string used in /parse-string command!"
)
public class CreateMatchNew implements Command {
    private final Utils utils;
    private final TeamService teamService;
    private final DivisionWeekService divisionWeekService;
    private final ScheduleWarService scheduleWarService;

    private final String EXPECTED_TEAM_STRING = "2002v2001 3833v8233 843v832";

    public CreateMatchNew(Utils utils, TeamService teamService, DivisionWeekService divisionWeekService, ScheduleWarService scheduleWarService) {
        this.utils = utils;
        this.teamService = teamService;
        this.divisionWeekService = divisionWeekService;
        this.scheduleWarService = scheduleWarService;
    }

    @Override
    public void execute(SlashCommandCreateEvent event) {
        CompletableFuture<InteractionOriginalResponseUpdater> respondLater = event.getSlashCommandInteraction().respondLater();

        List<SlashCommandInteractionOption> arguments = event.getSlashCommandInteraction().getArguments();

        Optional<Long> divWeekID = arguments.get(0).getLongValue();

        DivisionWeeks divWeekByID;
        try {
            divWeekByID = divisionWeekService.getDivWeekByID(Math.toIntExact(divWeekID.get()));
        } catch (EntityNotFoundException e) {
            GeneralService.leagueSlashErrorMessage(respondLater, e.getMessage());
            return;
        }
        Optional<String> matchUpString = arguments.get(1).getStringValue();

        List<Tuple<Integer, Integer>> listOfTuples = parse(matchUpString.get());

        List<ScheduledWar> scheduledWars = new ArrayList<>();
        for (Tuple<Integer, Integer> x : listOfTuples) {
            Team teama = null;
            Team teamb = null;

            try {
                teama = teamService.getTeamByID(x.right);
                teamb = teamService.getTeamByID(x.left);
            } catch (EntityNotFoundException e) {
                GeneralService.leagueSlashErrorMessage(respondLater, e.getMessage());
                return;
            }

            ScheduledWar scheduledWar = new ScheduledWar(
                    divWeekByID,
                    teama,
                    teamb
            );

            scheduledWars.add(scheduledWar);
        }
        if (scheduledWars.isEmpty()) {
            respondLater.thenAccept(res -> {
                res.setContent("War list is empty! expected String as follows: %s".formatted(EXPECTED_TEAM_STRING))
                        .update();
            }).join();
            return;
        }

        List<EmbedBuilder> embedBuilders = scheduledWars.stream().map(x -> {
            return new EmbedBuilder()
                    .setTitle("Match Created!")
                    .setDescription(
                            """
                                    %s  vs  %s
                                    """.formatted(x.getTeamA().getName(), x.getTeamB().getName())
                    ).addField("Start Date", x.getDivisionWeeks().getWeekStartDate().toString())
                    .addField("End Date", x.getDivisionWeeks().getWeekEndDate().toString())
                    .setColor(Color.CYAN)
                    .setAuthor(event.getSlashCommandInteraction().getUser())
                    .setTimestampToNow();
        }).toList();

        new Pagination().buttonPagination(embedBuilders, respondLater, event.getApi());

        CompletableFuture<Message> send = event.getSlashCommandInteraction().createFollowupMessageBuilder()
                .setContent("Match-ups created, Would you like to save them or try again?")
                .addComponents(ActionRow.of(
                        List.of(
                                Button.create("save", ButtonStyle.PRIMARY, "Save"),
                                Button.create("exit", ButtonStyle.DANGER, "Try Again")
                        )
                )).send();

        try {
            new ButtonRemoveJobScheduler().execute(send.join());
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }

        send.join().addButtonClickListener(b -> {
               if (b.getButtonInteraction().getCustomId().equalsIgnoreCase("Save")) {
                   b.getButtonInteraction().acknowledge();
                   scheduleWarService.saveSchedule(scheduledWars);
                   event.getSlashCommandInteraction().createFollowupMessageBuilder()
                           .setContent("Match-ups saved")
                           .send();
               } else {
                   b.getButtonInteraction().acknowledge();
                   event.getSlashCommandInteraction().createFollowupMessageBuilder()
                           .setContent("Match-ups not saved")
                           .send();
               }
               b.getButtonInteraction().getMessage().delete();
               event.getSlashCommandInteraction().createFollowupMessageBuilder()
                       .setContent("Command completed")
                          .send();
            }).removeAfter(9, TimeUnit.MINUTES);
    }


    private List<Tuple<Integer, Integer>> parse(String input) {
        List<Tuple<Integer, Integer>> list = new ArrayList<>();

        String[] teams = input.split("\\s+");
        for (String team : teams) {
            String[] current = team.split("v");
            int teamA = Integer.parseInt(current[0]);
            int teamB = Integer.parseInt(current[1]);

            Tuple<Integer, Integer>  tuple = new Tuple<>(teamA, teamB);
            list.add(tuple);
        }
        return list;
    }

    private void createMatchUps(DivisionWeeks weeks, JSONArray job) {

    }

    class Tuple<R, L> {
        R right;
        L left;
        public Tuple(R right, L left) {
            this.right = right;
            this.left = left;
        }
    }
}
