package com.fthlbot.discordbotfthl.Util;

import org.javacord.api.entity.emoji.KnownCustomEmoji;

import java.util.List;

public class SlapbotEmojis {
    private static List<KnownCustomEmoji> emojis;

    public static List<KnownCustomEmoji> getEmojis() {
        return emojis;
    }

    public static void setEmojis(List<KnownCustomEmoji> emojis) {
        SlapbotEmojis.emojis = emojis;
    }
}
