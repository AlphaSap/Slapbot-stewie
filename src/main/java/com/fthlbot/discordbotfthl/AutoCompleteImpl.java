package com.fthlbot.discordbotfthl;

import org.javacord.api.event.interaction.AutocompleteCreateEvent;
import org.javacord.api.interaction.SlashCommandOptionChoice;
import org.javacord.api.listener.interaction.AutocompleteCreateListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AutoCompleteImpl implements AutocompleteCreateListener {
    @Override
    public void onAutocompleteCreate(AutocompleteCreateEvent event) {
        String commandName = event.getAutocompleteInteraction().getCommandName();

        System.out.println("Autocomplete created");

        event.getAutocompleteInteraction().respondWithChoices(List.of(
                SlashCommandOptionChoice.create("!help", "Shows the list of commands")
        ));
    }
}
