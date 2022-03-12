package com.fthlbot.discordbotfthl.Handlers;

import java.util.HashMap;
import java.util.Map;

public class MessageHolder {
    private Map<String, Command> command = new HashMap<>();

    public Map<String, Command> getCommand() {
        return command;
    }

    public void setCommand(Map<String, Command> command) {
        this.command = command;
    }
}
