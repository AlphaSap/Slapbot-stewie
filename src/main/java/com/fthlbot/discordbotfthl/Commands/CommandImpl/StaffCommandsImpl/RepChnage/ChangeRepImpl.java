package com.fthlbot.discordbotfthl.Commands.CommandImpl.StaffCommandsImpl.RepChnage;

import com.fthlbot.discordbotfthl.Commands.CommandListener.StaffCommandListener.ChangeRepListener;
import com.fthlbot.discordbotfthl.core.Annotation.CommandType;
import com.fthlbot.discordbotfthl.core.Annotation.Invoker;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Invoker(
        alias = "change-rep",
        usage = "/change-rep <DIVISION> <TEAM ALIAS> <@oldRep> <@newRep>",
        description = "Changes representative for a team. Can only be used by staff",
        type = CommandType.STAFF
)
@Component
public class ChangeRepImpl implements ChangeRepListener {

    private final RepChangeUtil repChangeUtil;
    private final Logger log = LoggerFactory.getLogger(ChangeRepImpl.class);

    public ChangeRepImpl(RepChangeUtil repChangeUtil) {
        this.repChangeUtil = repChangeUtil;
    }

    @Override
    public void execute(SlashCommandCreateEvent event) {
        SlashCommandInteraction slashCommandInteraction = event.getSlashCommandInteraction();
        CompletableFuture<InteractionOriginalResponseUpdater> respondLater = slashCommandInteraction.respondLater();
        User user = slashCommandInteraction.getUser();

        String divAlias = slashCommandInteraction.getArguments().get(0).getStringValue().get();
        String teamAlias = slashCommandInteraction.getArguments().get(1).getStringValue().get();
        User oldRep = slashCommandInteraction.getArguments().get(2).getUserValue().get();
        User newRep = slashCommandInteraction.getArguments().get(3).getUserValue().get();

        repChangeUtil.changeRep(event, respondLater, user, divAlias, teamAlias, oldRep, newRep);
    }
}
