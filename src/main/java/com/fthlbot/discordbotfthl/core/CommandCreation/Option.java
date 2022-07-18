package com.fthlbot.discordbotfthl.core.CommandCreation;

import java.util.ArrayList;
import java.util.List;

public class Option {
    private String name;
    private String description;
    private Boolean required;
    private String type;
    private List<Choice> choices = new ArrayList<>();
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public Boolean getRequired() {
        return required;
    }
    public void setRequired(Boolean required) {
        this.required = required;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public List<Choice> getChoices() {
        return choices;
    }
    public void setChoices(List<Choice> choices) {
        this.choices = choices;
    }
}
