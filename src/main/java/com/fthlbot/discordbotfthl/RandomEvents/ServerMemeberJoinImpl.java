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

import java.awt.*;
import java.util.List;

public class ServerMemeberJoinImpl implements ServerMemberJoinListener {

    private final BotConfig botConfig;

    public ServerMemeberJoinImpl(BotConfig botConfig, TeamService teamService) {
        this.botConfig = botConfig;
        this.teamService = teamService;
    }

    private final TeamService teamService;

    @Override
    public void onServerMemberJoin(ServerMemberJoinEvent event) {
        Server negoServer = event.getApi().getServerById(botConfig.getNegoServerID()).get();

        if(!event.getServer().equals(negoServer)){ return; }

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
            Role divRole = negoServer.getRolesByName(team.getDivision().getName()).get(0);
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
