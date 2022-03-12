package com.fthlbot.discordbotfthl.Commands.CommandImpl;

import com.fthlbot.discordbotfthl.Annotation.CommandType;
import com.fthlbot.discordbotfthl.Annotation.Invoker;
import com.fthlbot.discordbotfthl.Commands.CommandListener.HelpListener;
import com.fthlbot.discordbotfthl.Handlers.Command;
import com.fthlbot.discordbotfthl.Util.Pagination;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.event.message.MessageCreateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.List;
import java.util.*;

import static com.fthlbot.discordbotfthl.Annotation.CommandType.*;
import static com.fthlbot.discordbotfthl.Annotation.CommandType.IGNORE;

@Invoker(
        alias = "help",
        description = "A general help command",
        usage = "+help",
        type = IGNORE
)
public class HelpImpl implements HelpListener {
    private final List<Command> commands;
    private final Logger log = LoggerFactory.getLogger(HelpImpl.class);

    public HelpImpl(List<Command> commands) {
        this.commands = commands;
    }

    /*@Override
    public void execute(MessageCreateEvent event) {
        log.info("help command is working!");
        Pagination pagination = new Pagination();
        pagination.buttonPaginate(createHelpEmbeds(), event);
    }*/

    @Override
    public void execute(SlashCommandCreateEvent event) {
        Pagination pagination = new Pagination();
        pagination.buttonPaginate(createHelpEmbeds(), event);
    }

    private List<EmbedBuilder> createHelpEmbeds(){

        System.out.println(commands.size());

        List<EmbedBuilder> embedBuilders = new ArrayList<>();
        Map<CommandType, ArrayList<Object>> em = new LinkedHashMap<>();
        em.put(INFO,                new ArrayList<>());
        em.put(REGISTRATION,        new ArrayList<>());
        em.put(ROSTER_MANAGEMENT,   new ArrayList<>());
        em.put(SCHEDULE,            new ArrayList<>());
        em.put(MISC,                new ArrayList<>());
        em.put(STAFF,               new ArrayList<>());
        em.put(UNSUPPORTED,         new ArrayList<>());

        for(Command MCL : commands){
            Arrays.stream(MCL.getClass().getAnnotations()).forEach(annotation -> {
                //System.out.println(Arrays.toString(annotation.annotationType().getAnnotations()));
                if (annotation instanceof Invoker){
                    //log.info(((Invoker) annotation).type().name());
                    switch (((Invoker) annotation).type()){
                        case INFO -> {
                            em.get(INFO).add(((Invoker) annotation).alias());
                        }case MISC -> {
                            em.get(MISC).add(((Invoker) annotation).alias());
                        }case REGISTRATION -> {
                            em.get(REGISTRATION).add(((Invoker) annotation).alias());
                        }case ROSTER_MANAGEMENT -> {
                            em.get(ROSTER_MANAGEMENT).add(((Invoker) annotation).alias());
                        }case SCHEDULE -> {
                            em.get(SCHEDULE).add(((Invoker) annotation).alias());
                        }case STAFF -> {
                            em.get(STAFF).add(((Invoker) annotation).alias());
                        }default -> {
                            em.get(UNSUPPORTED).add("un");
                        }
                    }
                }
            });
        }

        //If the character count goes up to 4000 chars make a new enum called 2.0 or something
        em.forEach((commandType, strings) -> {
            if (commandType.equals(IGNORE)){
                return;
            }
            StringBuilder str = new StringBuilder();
            strings.forEach(string -> {
                str.append("`").append(string).append("`\n");
            });

            embedBuilders.add(new EmbedBuilder()
                    .setTitle(commandType.name().replace("_", " "))
                    .setDescription(str.toString())
                    .setTimestampToNow()
                    .setFooter("type the name of the command after `>help` to get command specific help")
                    .setColor(Color.DARK_GRAY));
        });
        return embedBuilders;

    }
}
