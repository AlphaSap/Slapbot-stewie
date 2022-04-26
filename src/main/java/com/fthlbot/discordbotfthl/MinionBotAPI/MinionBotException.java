package com.fthlbot.discordbotfthl.MinionBotAPI;

public class MinionBotException extends RuntimeException {
    private static final String MESSAGE = "MinionBotException: status code not 200";

    public MinionBotException() {
        super(MESSAGE);
    }
}
