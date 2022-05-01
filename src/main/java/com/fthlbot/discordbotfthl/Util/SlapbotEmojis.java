package com.fthlbot.discordbotfthl.Util;

import org.javacord.api.entity.emoji.KnownCustomEmoji;

import java.util.List;
import java.util.Optional;

public class SlapbotEmojis {
    private static List<KnownCustomEmoji> emojis;

    public static List<KnownCustomEmoji> getEmojis() {
        return emojis;
    }

    public static void setEmojis(List<KnownCustomEmoji> emojis) {
        SlapbotEmojis.emojis = emojis;
    }

    //find an emoji by name, but wrap it in an Optional
    public static Optional<KnownCustomEmoji> getEmojiOptional(String name) {
        for (KnownCustomEmoji emoji : emojis) {
            if (emoji.getName().equals(name)) {
                return Optional.of(emoji);
            }
        }
        return Optional.empty();
    }
}
