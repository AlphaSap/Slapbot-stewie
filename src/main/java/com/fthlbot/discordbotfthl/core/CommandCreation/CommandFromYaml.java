package com.fthlbot.discordbotfthl.core.CommandCreation;


import java.util.ArrayList;
import java.util.List;

public class CommandFromYaml {
    private List<Command> command = new ArrayList<>();

    public List<Command> getCommand() {
        return command;
    }

    public void setCommand(List<Command> command) {
        this.command = command;
    }
}