package com.fthlbot.discordbotfthl.Commands.CommandImpl.StaffCommandsImpl;

import com.fthlbot.discordbotfthl.DatabaseModels.Team.Team;
import com.fthlbot.discordbotfthl.core.Annotation.CommandType;
import com.fthlbot.discordbotfthl.core.Annotation.Invoker;
import com.fthlbot.discordbotfthl.DatabaseModels.Team.TeamService;
import com.fthlbot.discordbotfthl.core.Handlers.Command;
import com.fthlbot.discordbotfthl.Util.BotConfig;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.callback.InteractionFollowupMessageBuilder;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

@Invoker(
        alias = "check-rep-joined-the-server",
        description = "Checks if every representative has joined this server",
        usage = "/checkEveryRepHasJoinThisServer",
        type = CommandType.STAFF
)
@Component
public class CheckEveryRepHasJoinThisServerImpl implements Command {
    private final TeamService teamService;
    private final BotConfig config;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public CheckEveryRepHasJoinThisServerImpl(TeamService teamService, BotConfig config) {
        this.teamService = teamService;
        this.config = config;
    }

    @Override
    public void execute(SlashCommandCreateEvent event) {
        CompletableFuture<InteractionOriginalResponseUpdater> response = event.getSlashCommandInteraction().respondLater();

        response.thenAccept(updater -> {
            updater.setContent("Checking...").update();
        });

        List<UserHolder> userHolderStream = teamService.getAllTeams().stream().map(x -> {
            User rep1 = event.getSlashCommandInteraction().getApi().getUserById(x.getRep1ID()).join();
            User rep2 = event.getSlashCommandInteraction().getApi().getUserById(x.getRep2ID()).join();
            return new UserHolder(rep1, rep2, x);
        }).toList();

        InteractionFollowupMessageBuilder updater = event.getSlashCommandInteraction().createFollowupMessageBuilder();
        userHolderStream.forEach(x -> {
            Collection<User> members = event.getApi().getServerById(config.getNegoServerID()).get().getMembers();
            boolean b = !members.contains(x.getRep1());
            boolean b2 = !members.contains(x.getRep2());
            if (b) {
                updater.setContent("Reps not joined: " + x.getRep1().getName() + "\nTeam Name: " + x.getTeam().getName()).send();
            }
            if (b2) {
                updater.setContent("Reps not joined: " + x.getRep2().getName() + "\nTeam Name: " + x.getTeam().getName()).send();
            }
        });



    }

    class UserHolder {
        private final User rep1;
        private final User rep2;

        private final Team team;

        public User getRep1() {
            return rep1;
        }

        public User getRep2() {
            return rep2;
        }

        public UserHolder(User rep1, User rep2, Team team) {
            this.rep1 = rep1;
            this.rep2 = rep2;
            this.team = team;
        }

        public Team getTeam() {
            return team;
        }
    }
}
