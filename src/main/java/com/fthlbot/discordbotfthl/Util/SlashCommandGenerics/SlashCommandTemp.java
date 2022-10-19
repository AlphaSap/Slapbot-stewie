package com.fthlbot.discordbotfthl.Util.SlashCommandGenerics;

import org.javacord.api.interaction.SlashCommandOption;

public interface SlashCommandTemp {
    SlashCommandOption getDivisions();

    SlashCommandOption getTeamName();

    SlashCommandOption getUser();
}
