package com.fthlbot.discordbotfthl.DatabaseModels.Exception;

import com.fthlbot.discordbotfthl.DatabaseModels.BannedReps.BannedRep;

public class UserBannedFromReppingTeam extends LeagueException{
    public static final String MESSAGE = """
            This User is banned from misrepresenting for a team!
            
            Reason -> %s
            Date -> %s
            Moderator -> %s
            Notes -> %s
            """;

    private final BannedRep bannedRep;

    public UserBannedFromReppingTeam(BannedRep bannedRep) {
        super(MESSAGE.formatted(
                bannedRep.getReason().orElse("Null"),
                bannedRep.getBannedDate().toString(),
                bannedRep.getDiscordUserID(),
                bannedRep.getNotes().orElse("Null")
                )
        );
        this.bannedRep = bannedRep;
    }
}
