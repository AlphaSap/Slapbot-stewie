package com.fthlbot.discordbotfthl.DatabaseModels.Roster;

import com.fthlbot.discordbotfthl.DatabaseModels.Division.Division;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.*;

class RosterServiceTest {

    @Mock
    RosterService rosterService;
    @Test
    void canDecrement() throws ParseException {
        Division div = new Division(
                1,
                "Hello world",
                "Hw",
                20,
                20,
                null
        );
        boolean b = rosterService.canDecrement(div);
        assertEquals(b, false);
    }
}