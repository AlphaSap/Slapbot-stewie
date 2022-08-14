package com.fthlbot.discordbotfthl.core.CommandCreation;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class CommandBuilderTest {

    @Test
    public void testCommandBuilder() throws IOException {
        CommandBuilder commandBuilder = new CommandBuilder();
        commandBuilder.createCommand();
        assertNotNull(commandBuilder);
    }
}