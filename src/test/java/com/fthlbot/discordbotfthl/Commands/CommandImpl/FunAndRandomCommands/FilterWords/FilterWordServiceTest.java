package com.fthlbot.discordbotfthl.Commands.CommandImpl.FunAndRandomCommands.FilterWords;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FilterWordServiceTest {

    @Test
    void checkMessage() {
        FilterWordService filterWordService = new FilterWordService();
        assertTrue(filterWordService.checkMessage("nigger    "));
        assertTrue(filterWordService.checkMessage("Nigga"));
        assertTrue(filterWordService.checkMessage("Nig*a"));
        assertTrue(filterWordService.checkMessage("N i g g a"));
        assertTrue(filterWordService.checkMessage("Niqq3r"));
        assertFalse(filterWordService.checkMessage("This is a test with a bad word and a bad word"));
    }
}