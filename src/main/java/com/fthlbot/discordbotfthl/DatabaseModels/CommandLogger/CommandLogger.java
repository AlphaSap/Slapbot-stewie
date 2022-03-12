package com.fthlbot.discordbotfthl.DatabaseModels.CommandLogger;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table
public class CommandLogger {
    @Id
    @GeneratedValue( strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Long commandID;

    private Long userID;

    private Long channelID;

    private String commandName;

    private String fullCommandString;

    private LocalDateTime usageDateAndTime;

    private Long ServerID;

    public CommandLogger(Long commandID, Long userID, Long channelID, String commandName, String fullCommandString, LocalDateTime usageDateAndTime, Long serverID) {
        this.commandID = commandID;
        this.userID = userID;
        this.channelID = channelID;
        this.commandName = commandName;
        this.fullCommandString = fullCommandString;
        this.usageDateAndTime = usageDateAndTime;
        ServerID = serverID;
    }
    public CommandLogger(){}

    public Long getCommandID() {
        return commandID;
    }

    public Long getUserID() {
        return userID;
    }

    public Long getChannelID() {
        return channelID;
    }

    public String getCommandName() {
        return commandName;
    }

    public String getFullCommandString() {
        return fullCommandString;
    }

    public LocalDateTime getUsageDateAndTime() {
        return usageDateAndTime;
    }

    public Long getServerID() {
        return ServerID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommandLogger that = (CommandLogger) o;
        return commandID.equals(that.commandID) && userID.equals(that.userID) && Objects.equals(channelID, that.channelID) && commandName.equals(that.commandName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(commandID, userID, channelID, commandName);
    }
}
