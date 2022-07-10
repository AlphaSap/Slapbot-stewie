package com.fthlbot.discordbotfthl.core.CommandCreation;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CommandFromJson {

    @SerializedName("name")
    private String name;
    @SerializedName("description")
    private String description;
    @SerializedName("examples")
    private List<String> examples;

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getExamples() {
        return examples;
    }
}
