package com.fthlbot.discordbotfthl.Handlers;

import org.javacord.api.event.interaction.SlashCommandCreateEvent;

public interface Command {
    void execute(SlashCommandCreateEvent event);
}
