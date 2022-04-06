package com.fthlbot.discordbotfthl.Handlers;

import com.fthlbot.discordbotfthl.Annotation.Invoker;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;

public interface Command {
    void execute(SlashCommandCreateEvent event);
}
