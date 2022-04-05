package com.fthlbot.discordbotfthl.Util;

import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Optional;

//TODO
public class JavacordLogger {

    private Class<?> clazz;
    private ServerTextChannel channel;
    private User user;
    private Logger log;

    /**
     * https://stackoverflow.com/questions/5762491/how-to-print-color-in-console-using-system-out-println
     *
     * these are colour values taken directly from fucking stackoverflow
     */
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLACK = "\u001B[30m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_WHITE = "\u001B[37m";

    public JavacordLogger() {
    }


    public JavacordLogger setLogger(Class<?> clazz) {
        this.clazz = clazz;
        this.log = LoggerFactory.getLogger(clazz);
        return this;
    }

    public JavacordLogger setChannel(ServerTextChannel channel) {
        this.channel = channel;
        return this;
    }

    public Optional<User> getUser() {
        return Optional.ofNullable(user);
    }


    public void info(String message, User user, Server server){
        String s = "%10s [INFO] ```\n%s``` \n**User**:%s, %d\n**Server**:%s";
        Optional<User> user1 = Optional.ofNullable(user);
        String userName;
        long id;
        if (user1.isPresent()) {
            userName = user1.get().getDiscriminatedName();
            id = user1.get().getId();
        }else {
            userName = "Not a user!";
            id = 0L;
        }
        s = String.format(s, LocalDateTime.now().toString(), message, userName, id, server.getName());
        log.info(s);
        channel.sendMessage(this.clazz.getName() + "\n" + s);
    }

    public void error(String message, User user, Server server){
        String s = "%10s <@584118780746268716> [ERROR] ```\n%s``` \n**User**:%s, %d\n**Server**:%s";
        Optional<User> user1 = Optional.ofNullable(user);
        String userName;
        long id;
        if (user1.isPresent()) {
            userName = user1.get().getDiscriminatedName();
            id = user1.get().getId();
        }else {
            userName = "Not a user!";
            id = 0L;
        }
        s = String.format(s, LocalDateTime.now().toString(), message, userName, id, server.getName());
        log.error(s);
        channel.sendMessage(this.clazz.getName() + "\n" + s);
    }
}
