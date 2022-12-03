package com.fthlbot.discordbotfthl.AutoCompleteSlashcommandOptions.autoCompleteSuggestions;

import com.fthlbot.discordbotfthl.AutoCompleteSlashcommandOptions.Anotation.AutoCompleteMetaData;
import com.fthlbot.discordbotfthl.AutoCompleteSlashcommandOptions.AutoCompleteHandler.AutoCompleter;
import com.fthlbot.discordbotfthl.DatabaseModels.Division.Division;
import com.fthlbot.discordbotfthl.DatabaseModels.Division.DivisionService;
import com.fthlbot.discordbotfthl.DatabaseModels.Exception.EntityNotFoundException;
import com.fthlbot.discordbotfthl.DatabaseModels.ScheduleWar.DivisonWeek.DivisionWeekService;
import com.fthlbot.discordbotfthl.DatabaseModels.ScheduleWar.DivisonWeek.DivisionWeeks;
import org.javacord.api.event.interaction.AutocompleteCreateEvent;
import org.javacord.api.interaction.SlashCommandOptionChoice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@AutoCompleteMetaData(
        optionName = "div-week"
)
@Component
//sample output is: 2022-12-09
public class AutoCompleteDivisionWeek implements AutoCompleter {

    private final DivisionWeekService divisionWeekService;
    private final DivisionService divisionService;
    private Logger log = LoggerFactory.getLogger(AutoCompleteDivisionWeek.class);
    public AutoCompleteDivisionWeek(DivisionWeekService divisionWeekService, DivisionService divisionService) {
        this.divisionWeekService = divisionWeekService;
        this.divisionService = divisionService;
    }

    @Override
    public void execute(AutocompleteCreateEvent event) {
        var div = event.getAutocompleteInteraction().getOptionByName("division");
        if (div.isEmpty()) return;
        var opDivAlias = div.get().getStringValue();

        Optional<Division> opDiv =   Optional.empty();
        if (opDivAlias.isPresent()) {
            try {
                opDiv = Optional.ofNullable(divisionService.getDivisionByAlias(opDivAlias.get()));
            } catch (EntityNotFoundException e) {
                log.error(e.getMessage());
            }
        }
        var query = event.getAutocompleteInteraction().getFocusedOption().getStringValue();

        List<DivisionWeeks> divisionWeeks = divisionWeekService.searchQuery(query, opDiv);
        List<SlashCommandOptionChoice> optionChoices = pairDivWeekString(divisionWeeks).stream()
                .map(x -> SlashCommandOptionChoice.create(x.getFirst(), x.getSecond().getID()))
                .limit(25)
                .toList();
        event.getAutocompleteInteraction().respondWithChoices(optionChoices);

    }

    private List<Pair<String, DivisionWeeks>> pairDivWeekString(List<DivisionWeeks> divisionWeeks) {
        return divisionWeeks.stream()
                .map(x -> {
                    LocalDate ld = LocalDate.from(x.getWeekEndDate().toInstant());
                    int year = ld.getYear();
                    int day = ld.getDayOfMonth();
                    int month = ld.getMonthValue();

                    return Pair.of(year + "-" + month + "-" + day, x);
                }).toList();
    }
}
