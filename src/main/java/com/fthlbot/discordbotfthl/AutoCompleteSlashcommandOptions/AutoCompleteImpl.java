package com.fthlbot.discordbotfthl.AutoCompleteSlashcommandOptions;

import com.fthlbot.discordbotfthl.DatabaseModels.Division.Division;
import com.fthlbot.discordbotfthl.DatabaseModels.Team.Team;
import com.fthlbot.discordbotfthl.DatabaseModels.Team.TeamService;
import org.javacord.api.event.interaction.AutocompleteCreateEvent;
import org.javacord.api.interaction.SlashCommandOptionChoice;
import org.javacord.api.listener.interaction.AutocompleteCreateListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class AutoCompleteImpl implements AutocompleteCreateListener {
    private final TeamService teamService;

    public AutoCompleteImpl(TeamService teamService) {
        this.teamService = teamService;
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

       List<String> teams = getTeamNames(stringValue.get(), null);
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
