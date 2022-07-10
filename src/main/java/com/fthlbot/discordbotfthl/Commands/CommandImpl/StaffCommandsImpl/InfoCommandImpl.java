package com.fthlbot.discordbotfthl.Commands.CommandImpl.StaffCommandsImpl;

import com.fthlbot.discordbotfthl.core.Annotation.Invoker;
import com.fthlbot.discordbotfthl.DatabaseModels.Team.TeamService;
import com.fthlbot.discordbotfthl.core.Handlers.Command;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

@Component
@Invoker(
        alias = "info",
        description = "Shows the informatio of the bot",
        usage = "/info"
)
public class InfoCommandImpl implements Command {
    private final TeamService teamService;

    public InfoCommandImpl(TeamService teamService) {
        this.teamService = teamService;
    }

    @Override
    public void execute(SlashCommandCreateEvent event) {
        CompletableFuture<InteractionOriginalResponseUpdater> res = event.getSlashCommandInteraction().respondLater();
        Collection<Server> servers = event.getApi().getServers();
        int members = servers.stream().map(Server::getMemberCount).reduce(0, Integer::sum);
        EmbedBuilder em = new EmbedBuilder()
                .setTitle("Bot Statistics")
                .addInlineField("Logged in as", event.getApi().getYourself().getDiscriminatedName())
                .addInlineField("Server count", servers.size() + "")
                .addInlineField("User count", members + "")
                .addInlineField("Command count", "TODO")
                .addInlineField("Discord Wrapper", "Javacord")
                .addInlineField("Library Version", "3.5.0")
                .addInlineField("Bot Version", "2.0.0")
                .addInlineField("Bot Author", event.getApi().getOwner().join().getDiscriminatedName())
                .addInlineField("Teams Registered in FTHL", teamService.getAllTeams().size()+  "")
                .setColor(Color.GREEN)
                .setFooter("FTHL Bot", event.getApi().getYourself().getAvatar())
                .setTimestampToNow();

        res.thenAccept( r -> {
            r.addEmbed(em).update();
        });


    }
}
