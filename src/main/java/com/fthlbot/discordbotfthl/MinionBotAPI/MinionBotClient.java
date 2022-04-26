package com.fthlbot.discordbotfthl.MinionBotAPI;

import com.google.gson.Gson;

public class MinionBotClient {
    public MinionBotPlayer[] getPlayerBan(String tag){
        Http http = new Http();

        String s = http.get("https://minionbot.com/v1/orgvillagebans/village/" + tag.replace("#", "%23"));
        Gson gson = new Gson();
        return gson.fromJson(s,MinionBotPlayer[].class);
    }
}
