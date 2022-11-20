package com.fthlbot.discordbotfthl.AutoCompleteSlashcommandOptions;

import org.javacord.api.event.interaction.AutocompleteCreateEvent;
import org.javacord.api.interaction.SlashCommandOptionChoice;
import org.javacord.api.listener.interaction.AutocompleteCreateListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AutoCompleteImpl implements AutocompleteCreateListener {
    @Override
    public void onAutocompleteCreate(AutocompleteCreateEvent event) {
        System.out.println("Autocomplete created");
    }
}
