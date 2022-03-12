package com.fthlbot.discordbotfthl.Util.Exception;

public class UnsupportedCommandException extends Exception{
    private static final String MESSAGE = "`%s` command is no longer supported, please find an alternate command to use! ";
    public UnsupportedCommandException(String commandName) {
        super(String.format(MESSAGE, commandName));
    }
}
