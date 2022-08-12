package com.fthlbot.discordbotfthl.Commands.CommandImpl.StaffCommandsImpl;

import com.fthlbot.discordbotfthl.DatabaseModels.BannedReps.BannedRepService;
import com.fthlbot.discordbotfthl.DatabaseModels.Exception.EntityAlreadyExistsException;
import com.fthlbot.discordbotfthl.DatabaseModels.Team.TeamService;
import com.fthlbot.discordbotfthl.Util.GeneralService;
import com.fthlbot.discordbotfthl.core.Annotation.CommandType;
import com.fthlbot.discordbotfthl.core.Annotation.Invoker;
import com.fthlbot.discordbotfthl.core.Handlers.Command;
import org.javacord.api.entity.DiscordEntity;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;
import org.javacord.api.util.logging.ExceptionLogger;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Component
@Invoker(
        alias = "ban-rep",
        description = "bans a user form representing a team",
        usage = "/ban-rep <@User OR discord-ID> <reason> <notes>",
        type = CommandType.STAFF
)
public class BanRepImpl implements Command {

    private final BannedRepService bannedRepService;
    private final TeamService teamService;

    public BanRepImpl(BannedRepService bannedRepService, TeamService teamService) {
        this.bannedRepService = bannedRepService;
        this.teamService = teamService;
    }

    @Override
    public void execute(SlashCommandCreateEvent event) {
        List<SlashCommandInteractionOption> arguments = event.getSlashCommandInteraction().getArguments();

        Optional<User> userValue = arguments.get(0).getUserValue();
        Optional<Long> longValue = arguments.get(1).getLongValue();

        if (userValue.isEmpty() && longValue.isEmpty()){
            event.getSlashCommandInteraction().createImmediateResponder().setContent("Please provide a user or discord-ID").respond();
            return;
        }
        CompletableFuture<InteractionOriginalResponseUpdater> respondLater = event.getSlashCommandInteraction().respondLater();
        long userID;
        userID = userValue.map(DiscordEntity::getId).orElseGet(longValue::get);

        Optional<String> reason = arguments.get(2).getStringValue();
        Optional<String> notes = arguments.get(3).getStringValue();

        try {
            bannedRepService.banRep(userID,
                    event.getSlashCommandInteraction().getUser().getName(),
                    reason,
                    notes,
                    new Date(),
                    teamService);
        } catch (EntityAlreadyExistsException e) {
            GeneralService.leagueSlashErrorMessage(respondLater, e);
            return;
        }
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Rep Ban");
        builder.setDescription("User has been banned from representing a team");
        builder.addField("User", userValue.map(User::getName).orElseGet(() -> userID + ""), false);
        builder.addField("Reason", reason.orElse("None"), false);
        builder.addField("Notes", notes.orElse("None"), false);
        builder.setFooter("Banned by " + event.getSlashCommandInteraction().getUser().getName());
        builder.setTimestampToNow();
        builder.setColor(Color.CYAN);

        respondLater.thenAccept(updater -> updater.addEmbed(builder).update().exceptionally(ExceptionLogger.get()));
    }
}
