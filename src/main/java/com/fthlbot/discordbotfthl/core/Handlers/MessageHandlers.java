package com.fthlbot.discordbotfthl.core.Handlers;


import com.fthlbot.discordbotfthl.core.Annotation.Invoker;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class MessageHandlers {
    private final List<Command> commands;
    public MessageHandlers(List<Command> commands) {
        this.commands = commands;
    }

    public MessageHolder setCommands(){
        MessageHolder messageHolder = new MessageHolder();
        Map<String, Command> commandMap = new HashMap<>();
        for (Command command : commands) {
            Annotation[] annotations = command.getClass().getAnnotations();
            //add a forloop for all the commands above line
            for (Annotation annotation : annotations) {
                if (annotation instanceof Invoker invoker) {
                    commandMap.put(invoker.alias(), command);
                }
            }
        }
        messageHolder.setCommand(commandMap);
        return messageHolder;
    }
}
