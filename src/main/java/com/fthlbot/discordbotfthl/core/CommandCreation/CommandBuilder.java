package com.fthlbot.discordbotfthl.core.CommandCreation;

import com.google.gson.Gson;
import org.javacord.api.DiscordApi;

import java.io.*;
import java.util.List;

public class CommandBuilder {
    private DiscordApi api;

    public InputStream yaml() {
        return getClass().getResourceAsStream("commands.yml");
    }

    public DiscordApi getApi() {
        return api;
    }

    public void setApi(DiscordApi api) {
        this.api = api;
    }
}