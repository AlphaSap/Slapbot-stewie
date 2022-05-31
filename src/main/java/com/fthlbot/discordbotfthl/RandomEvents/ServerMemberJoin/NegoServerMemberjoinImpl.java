package com.fthlbot.discordbotfthl.RandomEvents.ServerMemberJoin;

import com.fthlbot.discordbotfthl.DatabaseModels.Team.Team;
import com.fthlbot.discordbotfthl.DatabaseModels.Team.TeamService;
import com.fthlbot.discordbotfthl.Util.BotConfig;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.server.member.ServerMemberJoinEvent;
import org.javacord.api.listener.server.member.ServerMemberJoinListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NegoServerMemberjoinImpl implements ServerMemberJoinListener {

    public NegoServerMemberjoinImpl(BotConfig botConfig, TeamService teamService, ServerMemberJoinService serverMemberJoinService) {
        this.botConfig = botConfig;
        this.teamService = teamService;
        this.serverMemberJoinService = serverMemberJoinService;
    }

    private final BotConfig botConfig;
    private final TeamService teamService;

    private final ServerMemberJoinService serverMemberJoinService;
    private final Logger logger = LoggerFactory.getLogger(NegoServerMemberjoinImpl.class);

    @Override
    public void onServerMemberJoin(ServerMemberJoinEvent event) {
        Server server = event.getServer();
        if(server.getId() != botConfig.getNegoServerID()){ return; };

        List<Team> teamByRep = teamService.getTeamByRep(event.getUser().getId());

        serverMemberJoinService
                .setUser(event.getUser())
                .setTeams(teamByRep);

        EmbedBuilder embedBuilder = serverMemberJoinService.getEmbed();

        TextChannel sysChannel = server.getSystemChannel().orElse(
                server.getTextChannels().get(0)
        ); // will return the first channel in case the system channel is not set

        sysChannel.sendMessage(embedBuilder);

        serverMemberJoinService.giveRoles(server, "Representative");
    }
}
