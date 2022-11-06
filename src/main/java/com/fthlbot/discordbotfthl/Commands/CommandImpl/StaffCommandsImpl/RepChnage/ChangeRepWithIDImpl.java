package com.fthlbot.discordbotfthl.Commands.CommandImpl.StaffCommandsImpl.RepChnage;

import com.fthlbot.discordbotfthl.core.Annotation.CommandType;
import com.fthlbot.discordbotfthl.core.Annotation.Invoker;
import com.fthlbot.discordbotfthl.core.Handlers.Command;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Component
@Invoker(
        alias = "change-rep-with-id",
        description = "change rep with user ID",
        usage = "/change-rep-with-id <DIV> <TEAM ALIAS> <old rep id> <new rep ID>",
        type = CommandType.STAFF
)
public class ChangeRepWithIDImpl implements Command {

    private final Logger log = LoggerFactory.getLogger(ChangeRepImpl.class);

    private final RepChangeUtil repChangeUtil;

    public ChangeRepWithIDImpl(RepChangeUtil repChangeUtil) {
        this.repChangeUtil = repChangeUtil;
    }

    @Override
    public void execute(SlashCommandCreateEvent event) {

        SlashCommandInteraction slashCommandInteraction = event.getSlashCommandInteraction();
        CompletableFuture<InteractionOriginalResponseUpdater> respondLater = slashCommandInteraction.respondLater();
        User user = slashCommandInteraction.getUser();

        String divAlias = slashCommandInteraction.getArguments().get(0).getStringValue().get();
        String teamAlias = slashCommandInteraction.getArguments().get(1).getStringValue().get();

        Long oldRepID = slashCommandInteraction.getArguments().get(2).getLongValue().get();
        Long newRepID = slashCommandInteraction.getArguments().get(3).getLongValue().get();

        User oldRep;
        try {
            oldRep = event.getApi().getUserById(oldRepID).get();
        } catch (InterruptedException | ExecutionException e) {
            respondLater.thenAccept(res -> res.setContent("Invalid Id: %d".formatted(oldRepID)));
            return;
        }
        User newRep;
        try {
            newRep = event.getApi().getUserById(newRepID).get();
        } catch (InterruptedException | ExecutionException e) {
            respondLater.thenAccept(res -> res.setContent("Invalid Id: %d".formatted(newRepID)));
            return;
        }

        repChangeUtil.changeRep(event, respondLater, user, divAlias, teamAlias, oldRep, newRep);

    }
}
