package com.fthlbot.discordbotfthl.DatabaseModels.BannedReps;

import com.fthlbot.discordbotfthl.DatabaseModels.Exception.EntityAlreadyExistsException;
import com.fthlbot.discordbotfthl.DatabaseModels.Exception.EntityNotFoundException;
import com.fthlbot.discordbotfthl.DatabaseModels.Team.TeamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class BannedRepService {
    private final BannedRepRepository bannedRepRepository;
    private final Logger log = LoggerFactory.getLogger(BannedRepService.class);

    public BannedRepService(BannedRepRepository bannedRepRepository) {
        this.bannedRepRepository = bannedRepRepository;
    }

    public Optional<BannedRep> getBannedRep(long discordID) {
        return bannedRepRepository.findBannedRepByDiscordUserID(discordID);
    }

    public BannedRep banRep(long discordID,
                            String staffName,
                            Optional<String> reason,
                            Optional<String> notes,
                            Date date,
                            TeamService teamService) throws EntityAlreadyExistsException {
        log.info("Banning rep with discordID: " + discordID);

        if (getBannedRep(discordID).isPresent()) {
            log.info("Rep with discordID: " + discordID + " is already banned");
            throw new EntityAlreadyExistsException("This user is already banned from being a rep!");
        }
        log.info("Rep with discordID: " + discordID + " is not banned");

        log.info("getting team for rep with discordID: " + discordID);
        Pair<String, String> repAndDeleteHimFromAllTeam = teamService.findRepAndDeleteFromAllTeam(discordID);

        log.info("making banned rep with discordID: " + discordID);
        BannedRep b = new BannedRep(
                discordID,
                reason.orElse("Null"),
                staffName,
                date,
                repAndDeleteHimFromAllTeam.getFirst(),
                repAndDeleteHimFromAllTeam.getSecond(),
                notes.orElse("Null")
        );
        log.info("saving banned rep with discordID: " + discordID);
        b = bannedRepRepository.save(b);
        return b;
    }

    public void removeBan(long discordID) throws EntityNotFoundException {
        Optional<BannedRep> bannedRep = getBannedRep(discordID);

        if (bannedRep.isEmpty()) {
            throw new EntityNotFoundException("This user is not banned, Cannot unban!");
        }

        bannedRepRepository.delete(bannedRep.get());
    }
}
