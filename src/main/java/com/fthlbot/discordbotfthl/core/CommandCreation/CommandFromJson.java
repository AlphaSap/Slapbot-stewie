package com.fthlbot.discordbotfthl.core.CommandCreation;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class CommandFromJson {
    @SerializedName("commands")
    @Expose
    private List<Command> command = new ArrayList<>();

    public List<Command> getCommand() {
        return command;
    }

    public CommandFromJson setCommand(List<Command> command) {
        this.command = command;
        return this;
    }
}