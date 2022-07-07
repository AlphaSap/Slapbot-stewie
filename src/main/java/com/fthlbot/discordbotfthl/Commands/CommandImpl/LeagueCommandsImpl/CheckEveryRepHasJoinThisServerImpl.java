package com.fthlbot.discordbotfthl.Commands.CommandImpl.LeagueCommandsImpl;

import com.fthlbot.discordbotfthl.Annotation.CommandType;
import com.fthlbot.discordbotfthl.Annotation.Invoker;
import com.fthlbot.discordbotfthl.DatabaseModels.Team.TeamService;
import com.fthlbot.discordbotfthl.Handlers.Command;
import com.fthlbot.discordbotfthl.Util.BotConfig;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

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

    public CheckEveryRepHasJoinThisServerImpl(TeamService teamService, BotConfig config) {
        this.teamService = teamService;
        this.config = config;
    }

    @Override
    public void execute(SlashCommandCreateEvent event) {
        CompletableFuture<InteractionOriginalResponseUpdater> response = event.getSlashCommandInteraction().respondLater();

        response.thenAccept(updater -> {
            updater.setContent("Checking...");
        });

        teamService.getAllTeams().stream().map(x -> {
            User rep1 = event.getSlashCommandInteraction().getApi().getUserById(x.getRep1ID()).join();
            User rep2 = event.getSlashCommandInteraction().getApi().getUserById(x.getRep2ID()).join();
            return new UserHolder(rep1, rep2);
        }).forEach(x -> {
            boolean b = x.getRep1().getMutualServers().stream().allMatch(a -> a.getId() == config.getNegoServerID());
            if (!b) {
                event.getSlashCommandInteraction().createFollowupMessageBuilder()
                        .setContent(x.getRep1().getName() + " has not joined the server yet")
                        .send();
            }
            boolean b2 = x.getRep2().getMutualServers().stream().allMatch(a -> a.getId() == config.getNegoServerID());
            if (!b2) {
                event.getSlashCommandInteraction().createFollowupMessageBuilder()
                        .setContent(x.getRep2().getName() + " has not joined the server yet")
                        .send();
            }
        });

    }

    class UserHolder {
        private final User rep1;
        private final User rep2;

        public User getRep1() {
            return rep1;
        }

        public User getRep2() {
            return rep2;
        }

        public UserHolder(User rep1, User rep2) {
            this.rep1 = rep1;
            this.rep2 = rep2;
        }
    }
}
