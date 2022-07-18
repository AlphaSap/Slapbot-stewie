package com.fthlbot.discordbotfthl.Commands.CommandImpl.StaffCommandsImpl;

import com.fthlbot.discordbotfthl.DatabaseModels.Division.Division;
import com.fthlbot.discordbotfthl.DatabaseModels.Team.Team;
import com.fthlbot.discordbotfthl.DatabaseModels.Team.TeamService;
import com.fthlbot.discordbotfthl.Util.BotConfig;
import com.fthlbot.discordbotfthl.core.Annotation.AllowedChannel;
import com.fthlbot.discordbotfthl.core.Annotation.CommandType;
import com.fthlbot.discordbotfthl.core.Annotation.Invoker;
import com.fthlbot.discordbotfthl.core.Handlers.Command;
import org.javacord.api.entity.Nameable;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Invoker(
        alias = "give-role-to-rep",
        description = "Gives reps their roles on the main server",
        usage = "/give-role-to-rep",
        type = CommandType.STAFF,
        where = AllowedChannel.MAIN_SERVER

)
@Component
public class GiveRolesImpl implements Command {
    private final TeamService teamService;
    private final BotConfig config;

    private final Logger log = LoggerFactory.getLogger(GiveRolesImpl.class);

    public GiveRolesImpl(TeamService teamService, BotConfig config) {
        this.teamService = teamService;
        this.config = config;
    }

    @Override
    public void execute(SlashCommandCreateEvent event) {
        CompletableFuture<InteractionOriginalResponseUpdater> interactionOriginalResponseUpdaterCompletableFuture = event.getSlashCommandInteraction().respondLater();
        var allTeams = teamService.getAllTeams();

        //Guaranteed to be present.

        var server = event.getApi().getServerById(config.getFthlServerID()).get();
        for (Team team : allTeams) {
            try {
                Optional<User> rep1 = Optional.ofNullable(event.getApi().getUserById(team.getRep1ID()).join());
                Optional<User> rep2 = Optional.ofNullable(event.getApi().getUserById(team.getRep2ID()).join());

                if (rep1.isEmpty() || rep2.isEmpty()) {
                    log.info("One of the reps is null. Skipping team {}", team.getName());

                    log.info("Rep1: {}", rep1.isEmpty() ? "null" : rep1.get().getName());
                    log.info("Rep2: {}", rep2.isEmpty() ? "null" : rep2.get().getName());
                    continue;
                }

                if (!doTheyHaveRole(team.getDivision(), rep1.get().getRoles(server))) {
                    var findRoles = findRoles(server, team.getDivision().getAlias());
                    server.addRoleToUser(rep1.get(), findRoles.getFirst());
                    server.addRoleToUser(rep2.get(), findRoles.getSecond());

                    server.addRoleToUser(rep2.get(), findRoles.getFirst());
                    server.addRoleToUser(rep1.get(), findRoles.getSecond());
                }
            }catch (Exception e) {
                log.error("Error while giving roles to reps for team {}", team.getName(), e);
            }
        }

        interactionOriginalResponseUpdaterCompletableFuture.thenAccept(interactionOriginalResponseUpdater -> {
            interactionOriginalResponseUpdater.setContent("Done!").update();
        });

    }

    private boolean doTheyHaveRole(Division division, List<Role> roles) {
        var map = roles.stream().map(Nameable::getName);

        return map.anyMatch(it -> it.equals(division.getAlias()) && it.equals("Representative"));
    }

    private Pair<Role, Role> findRoles(Server server, String divName) {
        var filter = server.getRoles().stream()
                .filter( it -> it.getName().equalsIgnoreCase(divName));

        var findFirst = filter.findFirst().get();

        var filter2 = server.getRoles().stream()
                .filter( it -> it.getName().equalsIgnoreCase("Representative"));
        var findFirst2 = filter2.findFirst().get();

        return Pair.of(findFirst, findFirst2);
    }
}
