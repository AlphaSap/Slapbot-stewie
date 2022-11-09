package com.fthlbot.discordbotfthl.Util;

import com.sahhiill.clashapi.models.clan.Clan;
import com.sahhiill.clashapi.models.war.War;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ClashUtils {

    private static String formatTag(String tag) {
        tag = tag.toUpperCase();
        if (!tag.startsWith("#")) {
            tag = "#" + tag;
        }
        return tag;
    }

    private static boolean checkTagWithRegex(String tag) {
        return tag.toUpperCase().matches("^#[PYLQGRJCUV0289]+$");
    }

    public static String getClanLink(Clan clan) {
        return "https://link.clashofclans.com/?action=OpenClanProfile&tag=" + formatTag(clan.getTag());
    }


	public static Date getPreparationStartTimeAsDate(War war) throws ParseException {
		return new SimpleDateFormat("yyyyMMdd'T'HHmmss.SSSX").parse(war.getPreparationStartTime());
	}

	public static Date getStartTimeAsDate(War war) throws ParseException {
		return new SimpleDateFormat("yyyyMMdd'T'HHmmss.SSSX").parse(war.getStartTime());
	}

	public static Date getEndTimeAsDate(War war) throws ParseException{
		return new SimpleDateFormat("yyyyMMdd'T'HHmmss.SSSX").parse(war.getEndTime());
	}

    public static String getStatus(War war){
        var clan = war.getClan();
        var opponent = war.getOpponent();
        var state = war.getState();
        if (state.equals("warEnded")) {
            if (clan.getStars() > opponent.getStars()) {
                return "Winner: " + clan.getName();
            } else if (clan.getStars() < opponent.getStars()) {
                return "Winner: " + opponent.getName();
            } else {
                if (clan.getDestructionPercentage() > opponent.getDestructionPercentage()) {
                    return "Winner: " + clan.getName();
                } else if (clan.getDestructionPercentage() < opponent.getDestructionPercentage()) {
                    return "Winner: " + opponent.getName();
                } else {
                    return "RESULT: DRAW";
                }
            }
        }
        return "war is still active";
    }
}
