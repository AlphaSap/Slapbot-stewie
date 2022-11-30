package com.fthlbot.discordbotfthl.AutoCompleteSlashcommandOptions.AutoCompleteHandler;

import org.javacord.api.event.interaction.AutocompleteCreateEvent;


public interface AutoCompleter {
    void execute(AutocompleteCreateEvent event);
}
