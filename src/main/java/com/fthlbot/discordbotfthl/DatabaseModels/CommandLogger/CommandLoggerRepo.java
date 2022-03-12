package com.fthlbot.discordbotfthl.DatabaseModels.CommandLogger;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommandLoggerRepo extends JpaRepository<CommandLogger, Long> {
}
