package com.fthlbot.discordbotfthl.RandomEvents.ServerMemberJoin;

import com.fthlbot.discordbotfthl.DatabaseModels.Team.Team;
import com.fthlbot.discordbotfthl.DatabaseModels.Team.TeamService;
import com.fthlbot.discordbotfthl.Util.BotConfig;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.server.member.ServerMemberJoinEvent;
import org.javacord.api.listener.server.member.ServerMemberJoinListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ApplicantServerJoinImpl implements ServerMemberJoinListener {
    private final ServerMemberJoinService serverMemberJoinService;
    private final BotConfig botConfig;

    private final TeamService teamService;

    public ApplicantServerJoinImpl(ServerMemberJoinService serverMemberJoinService, BotConfig botConfig, TeamService teamService) {
        this.serverMemberJoinService = serverMemberJoinService;
        this.botConfig = botConfig;
        this.teamService = teamService;
    }

    @Override
    public void onServerMemberJoin(ServerMemberJoinEvent event) {
        if (event.getServer().getId() != botConfig.getApplicantServerID()) {
            return;
        }
        List<Team> teamByRep = teamService.getTeamByRep(event.getUser().getId());

        serverMemberJoinService
                .setUser(event.getUser())
                .setTeams(teamByRep);

        EmbedBuilder embedBuilder = serverMemberJoinService.getEmbed();
        event.getServer().getTextChannels().get(0).sendMessage(embedBuilder);

        TextChannel sysChannel = event.getServer().getSystemChannel().orElse(
                event.getServer().getTextChannels().get(0)
        ); // will return the first channel in case the system channel is not set

        sysChannel.sendMessage(embedBuilder);

        serverMemberJoinService.giveRoles(event.getServer(), "Applicant");
    }
}
