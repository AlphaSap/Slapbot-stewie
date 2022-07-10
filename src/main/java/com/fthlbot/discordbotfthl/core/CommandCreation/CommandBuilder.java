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

/**
 * [
 *   {
 *     "name": "testme",
 *     "description": "Ping the bot",
 *     "examples": ["/ping"]
 *   },
 *   {
 *     "name": "register",
 *     "description": "Register a new Team",
 *     "examples": ["/register <clan-tag> <division-alias> <team-alias> <@Second rep>" ,  "/register <clan-tag> <division-alias> <team-alias>]"],
 *     "options" : [
 *       {
 *         "name" : "clan-tag",
 *         "required" : true,
 *         "description" : "The clan tag of the team",
 *         "type" : "STRING"
 *       },
 *       {
 *         "name" : "division-alias",
 *         "required" : true,
 *         "description" : "The division alias of the team",
 *         "choicesBool" : true
 *       },
 *       {
 *         "name" : "team-alias",
 *         "required" : true,
 *         "description" : "The team alias of the team",
 *         "type" : "STRING"
 *       },
 *       {
 *         "name" : "Second rep",
 *         "required" : false,
 *         "description" : "The second rep of the team",
 *         "type" : "USER"
 *       }
 *     ]
 *   }
 * ]
 */
