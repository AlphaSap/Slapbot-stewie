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
import java.util.concurrent.CompletableFuture;

@Component
public class AutoCompleteListener implements AutocompleteCreateListener {
    /**
     * \
     * this casting to autocomplete to meta data annotation might not be the best solution to this problem, but this is a prototype.
     */
    private final HashMap<String, AutoCompleter> autoCompleteMap = new HashMap<>();
    private final Logger log = LoggerFactory.getLogger(AutoCompleteListener.class);

    public AutoCompleteListener(List<AutoCompleter> autoCompleters) {
        log.info("hi");

        for (AutoCompleter autoCompleter : autoCompleters) {
            Annotation[] annotations = autoCompleter.getClass().getAnnotations();
            log.info("length= {}", annotations.length);
            for (Annotation annotation : annotations) {
                log.info(autoCompleter.getClass().getName());
                log.info(annotation.getClass().getName());
                if (annotation instanceof AutoCompleteMetaData metaData) {
                    log.info("hi again!");
                    this.autoCompleteMap.put(metaData.optionName(), autoCompleter);
                }
            }
        }
    }


    @Override
    public void onAutocompleteCreate(AutocompleteCreateEvent event) {
        CompletableFuture.runAsync(() -> {
           String stringValue = event.getAutocompleteInteraction().getFocusedOption().getName();

            AutoCompleter autoCompleter = autoCompleteMap.get(stringValue);
            if (autoCompleter == null) {
                log.warn("An autocomplete listener was fired, with no handler loaded! {}", stringValue);
                return;
            }
            autoCompleter.execute(event);
        });
    }
}
