package com.fthlbot.discordbotfthl.AutoCompleteSlashcommandOptions;

import com.fthlbot.discordbotfthl.AutoCompleteSlashcommandOptions.Anotation.AutoCompleteMetaData;
import com.fthlbot.discordbotfthl.AutoCompleteSlashcommandOptions.AutoCompleteHandler.AutoCompleter;
import org.javacord.api.event.interaction.AutocompleteCreateEvent;
import org.javacord.api.listener.interaction.AutocompleteCreateListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Component
public class AutoCompleteListener implements AutocompleteCreateListener {
    /**
     * \
     * this casting to autocomplete to meta data annotation might not be the best solution to this problem, but this is a prototype.
     */
    private final HashMap<String, AutoCompleter> autoCompleteMap = new HashMap<>();
    private final Logger log = LoggerFactory.getLogger(AutoCompleteListener.class);

    public AutoCompleteListener(List<AutoCompleter> autoCompleteMap) {
        for (AutoCompleter autoComplete : autoCompleteMap) {
            for (Annotation annotation : autoComplete.getClass().getAnnotations()) {
                if (annotation instanceof AutoCompleteMetaData) {
                    this.autoCompleteMap.put(((AutoCompleteMetaData) annotation).optionName(), (AutoCompleter) annotation);
                }
            }
        }
    }


    @Override
    public void onAutocompleteCreate(AutocompleteCreateEvent event) {
        CompletableFuture.runAsync(() -> {
            Optional<String> stringValue = event.getAutocompleteInteraction().getFocusedOption().getStringValue();
            if (stringValue.isEmpty()) {
                log.warn("An autocomplete listener was fired, but cannot find the focused string value.");
                return;
            }

            log.info(stringValue.get());
            AutoCompleter autoCompleter = autoCompleteMap.get(stringValue.get());
            if (autoCompleter == null) {
                log.warn("An autocomplete listener was fired, with no handler loaded! {}", stringValue.get());
                return;
            }
            if (autoCompleter instanceof AutoCompleteMetaData) {
                //TODO: this part is probably not important as I can just set a specific command to not listen to autocomplete, should be removed after testing.
                if (((AutoCompleteMetaData) autoCompleter).ignoreCommands().length != 0) {
                    for (String s : ((AutoCompleteMetaData) autoCompleter).ignoreCommands()) {
                        if (s.equalsIgnoreCase(event.getAutocompleteInteraction().getCommandName())) return;
                    }
                }
                autoCompleter.execute(event);
            }
        });
    }
}
