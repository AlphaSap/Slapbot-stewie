package com.fthlbot.discordbotfthl.Util;

import org.springframework.context.annotation.Configuration;

@Configuration
public class ClashUtils {
    public static String getTownHallEmote(int townhallLevel) {
        return switch (townhallLevel) {
            case 1 -> "<:th1:947276195945381978>";
            case 2 -> "<:th2:947276191998570506>";
            case 3 -> "<:th3:947276192770318368>";
            case 4 -> "<:th4:947277976293220362>";
            case 5 -> "<:th5:947276195991552011>";
            case 6 -> "<:th6:947276151418667049>";
            case 7 -> "<:th7:947276197887352942>";
            case 8 -> "<:th8:947276734200446976>";
            case 9 -> "<:th9:947276159681445898>";
            case 10 -> "<:th10:947276159782113280>";
            case 11 -> "<:th11:947276991030243468>";
            case 12 -> "<:th12:947276159954092088>";
            case 13 -> "<:th13:947282074249879572>";
            case 14 -> "<:th14:947276161006829590>";
            default -> null;
        };
    }
}
