package com.fthlbot.discordbotfthl.DatabaseModels.BannedReps;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BannedRepRepository extends JpaRepository<BannedRep, Long> {
    Optional<BannedRep> findBannedRepByDiscordUserID(long discordUserID);
}
