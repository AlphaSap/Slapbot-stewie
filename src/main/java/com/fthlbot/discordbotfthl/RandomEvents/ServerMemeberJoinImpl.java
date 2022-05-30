package com.fthlbot.discordbotfthl.RandomEvents;

import com.fthlbot.discordbotfthl.DatabaseModels.Team.Team;
import com.fthlbot.discordbotfthl.DatabaseModels.Team.TeamService;
import com.fthlbot.discordbotfthl.Util.BotConfig;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.server.member.ServerMemberJoinEvent;
import org.javacord.api.listener.server.member.ServerMemberJoinListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.List;
import java.util.Optional;

@Component
public class ServerMemeberJoinImpl implements ServerMemberJoinListener {

    public ServerMemeberJoinImpl(BotConfig botConfig, TeamService teamService) {
        this.botConfig = botConfig;
        this.teamService = teamService;
    }

    private final BotConfig botConfig;
    private final TeamService teamService;
    private final Logger logger = LoggerFactory.getLogger(ServerMemeberJoinImpl.class);

    @Override
    public void onServerMemberJoin(ServerMemberJoinEvent event) {
        if(event.getServer().getId() != botConfig.getNegoServerID()){ return; };

        Server negoServer;

        try {
            negoServer = event.getApi().getServerById(botConfig.getNegoServerID()).orElseThrow(NullPointerException::new);
        }catch (NullPointerException npe){
            logger.error("Error getting negotiation server {}",npe.getMessage());
            return;
        }

        User user = event.getUser();
        List<Team> teams = teamService.getTeamByRep(user.getId());
        TextChannel sysChannel = event.getServer().getSystemChannel().get();

        if(teams.isEmpty()){
            EmbedBuilder notARep = new EmbedBuilder()
                    .setTitle("New Member Joined!")
                    .addField(user.getDiscriminatedName(), user.getIdAsString(),false)
                    .setThumbnail(user.getAvatar())
                    .setColor(Color.cyan)
                    .setFooter("Not a rep")
                    .setTimestampToNow();

            sysChannel.sendMessage(notARep);
            return;
        }

        Role repRole = negoServer.getRoleById(botConfig.getRepresentativeRoleID()).get();
        user.addRole(repRole);

        for(Team team : teams){
            Role divRole = negoServer.getRolesByName(team.getDivision().getAlias()).get(0);
            user.addRole(divRole);
        }

        EmbedBuilder emb = new EmbedBuilder()
                .setTitle("New Member Joined!")
                .addField(user.getDiscriminatedName(), user.getIdAsString(),false)
                .setThumbnail(user.getAvatar())
                .setTimestampToNow()
                .setColor(Color.green);
        sysChannel.sendMessage(emb);
    }
}
