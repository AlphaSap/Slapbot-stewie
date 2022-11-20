package com.fthlbot.discordbotfthl.AutoCompleteSlashcommandOptions;

import com.fthlbot.discordbotfthl.DatabaseModels.Division.Division;
import com.fthlbot.discordbotfthl.DatabaseModels.Division.DivisionService;
import com.fthlbot.discordbotfthl.DatabaseModels.Exception.EntityNotFoundException;
import com.fthlbot.discordbotfthl.DatabaseModels.Team.Team;
import com.fthlbot.discordbotfthl.DatabaseModels.Team.TeamService;
import org.javacord.api.event.interaction.AutocompleteCreateEvent;
import org.javacord.api.interaction.SlashCommandOptionChoice;
import org.javacord.api.listener.interaction.AutocompleteCreateListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class AutoCompleteImpl implements AutocompleteCreateListener {
    private final TeamService teamService;
    private final DivisionService divisionService;

    private final Logger log = LoggerFactory.getLogger(AutoCompleteImpl.class);
    public AutoCompleteImpl(TeamService teamService, DivisionService divisionService) {
        this.teamService = teamService;
        this.divisionService = divisionService;
    }

    @Override
    public void onAutocompleteCreate(AutocompleteCreateEvent event) {
       String name = event.getAutocompleteInteraction().getFocusedOption().getName();
       if (!name.equalsIgnoreCase("team-identifier")) {
           return;
       }

       Optional<String> stringValue = event.getAutocompleteInteraction().getFocusedOption().getStringValue();
       if (stringValue.isEmpty()){
           System.out.println("No input yet!");
           return;
       }
       Division division = null;
       var div = event.getAutocompleteInteraction().getArgumentStringValueByName("division");
       if (div.isPresent()) {
           try {
               division = divisionService.getDivisionByAlias(div.get());
           } catch (EntityNotFoundException e) {
                log.error("Cannot get division for auto complete!, must be empty!");
                e.printStackTrace();
           }
       }

       List<String> teams = getTeamNames(stringValue.get(), division);
        System.out.println(teams);
       List<SlashCommandOptionChoice> options = parseOptionFromListOfString(teams);
       event.getAutocompleteInteraction().respondWithChoices(options);
    }

    private List<String> getTeamNames(String query, Division division) {
        List<Team> teams = teamService.searchTeamWithDepth(query, Optional.ofNullable(division));
        return teams.stream()
                .map(Team::getName)
                .limit(20)
                .collect(Collectors.toList());
    }

    private List<SlashCommandOptionChoice> parseOptionFromListOfString(List<String> query) {
        return query.stream()
                .map(x -> SlashCommandOptionChoice.create(x, x))
                .collect(Collectors.toList());
    }
}
