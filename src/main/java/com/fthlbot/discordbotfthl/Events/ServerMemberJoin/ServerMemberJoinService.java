package com.fthlbot.discordbotfthl.Events.ServerMemberJoin;

import com.fthlbot.discordbotfthl.DatabaseModels.Team.Team;
import com.fthlbot.discordbotfthl.Util.BotConfig;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.List;

@Service
public class ServerMemberJoinService {
    private User user;
    private List<Team> teams;

    private final BotConfig botConfig;
    private Logger log = LoggerFactory.getLogger(ServerMemberJoinService.class);

    public ServerMemberJoinService(BotConfig botConfig) {
        this.botConfig = botConfig;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public ServerMemberJoinService setTeams(List<Team> teams) {
        this.teams = teams;
        return this;
    }

    public ServerMemberJoinService setUser(User user) {
        this.user = user;
        return this;
    }

    public User getUser() {
        return user;
    }

    public EmbedBuilder getEmbed() {
        if (getTeams().isEmpty()) {
            return new EmbedBuilder()
                    .setTitle("New Member Joined!")
                    .addField(this.getUser().getDiscriminatedName(), this.getUser().getIdAsString(), false)
                    .setThumbnail(this.getUser().getAvatar())
                    .setColor(Color.cyan)
                    .setFooter("Not a rep")
                    .setTimestampToNow();
        } else {
            return new EmbedBuilder()
                    .setTitle("New Member Joined!")
                    .addField(user.getDiscriminatedName(), user.getIdAsString())
                    .setThumbnail(user.getAvatar())
                    .setTimestampToNow()
                    .addInlineField("Teams", String.join("\n", teams.stream().map(Team::getName).toArray(String[]::new)))
                    .setColor(Color.green);
        }
    }

    public void giveRoles(Server server, long roleID, String roleName) {
        if (getTeams().isEmpty()) {
            log.warn("User {} is not a rep", user.getIdAsString());
            return;
        }
        try {
            for (Team team : getTeams()) {
                server.addRoleToUser(getUser(), server.getRolesByName(team.getDivision().getAlias()).get(0));
            }
            Role repRole = server.getRoleById(roleID).orElse(
                    server.getRolesByName(roleName).get(0)
            );
            server.addRoleToUser(getUser(), repRole);
        } catch (Exception e) {
            log.error("Error adding roles to user {}", getUser().getIdAsString());
        }
    }
}
