package com.fthlbot.discordbotfthl.DatabaseModels.CommandLogger;

import org.springframework.stereotype.Service;

@Service
public class CommandLoggerService {
    private final CommandLoggerRepo repo;

    public CommandLoggerService(CommandLoggerRepo repo) {
        this.repo = repo;
    }

    public CommandLogger logCommand(CommandLogger commandLogger){
        commandLogger = repo.save(commandLogger);
        return commandLogger;
    }

}
