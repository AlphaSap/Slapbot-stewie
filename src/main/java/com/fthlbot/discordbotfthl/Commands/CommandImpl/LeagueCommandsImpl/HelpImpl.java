package com.fthlbot.discordbotfthl.Commands.CommandImpl.LeagueCommandsImpl;

import com.fthlbot.discordbotfthl.Annotation.CommandType;
import com.fthlbot.discordbotfthl.Annotation.Invoker;
import com.fthlbot.discordbotfthl.Commands.CommandListener.HelpListener;
import com.fthlbot.discordbotfthl.Handlers.Command;
import com.fthlbot.discordbotfthl.Util.Pagination.Pagination;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static com.fthlbot.discordbotfthl.Annotation.CommandType.*;
import static com.fthlbot.discordbotfthl.Annotation.CommandType.IGNORE;

@Invoker(
        alias = "help",
        description = "You need help with \"help\" for `help` command",
        usage = "/help",
        type = UNSUPPORTED
)
public class HelpImpl implements HelpListener {
    private final List<Command> commands;
    private final Logger log = LoggerFactory.getLogger(HelpImpl.class);

    public HelpImpl(List<Command> commands) {
        this.commands = commands;
    }

    @Override
    public void execute(SlashCommandCreateEvent event) {
        CompletableFuture<InteractionOriginalResponseUpdater> response = event.getSlashCommandInteraction().respondLater();
        List<SlashCommandInteractionOption> arguments = event.getSlashCommandInteraction().getArguments();
        if  (arguments.size() >= 1){
            boolean present = arguments.get(0).getStringValue().isPresent();
            if (present){
                String s = arguments.get(0).getStringValue().get();
                EmbedBuilder embedBuilder = findCommand(s, event.getSlashCommandInteraction().getUser());
                event.getSlashCommandInteraction().createImmediateResponder().addEmbeds(embedBuilder).respond();
            }
            return;
        }
        Pagination pagination = new Pagination();
        List<EmbedBuilder> helpEmbeds = createHelpEmbeds();

        pagination.buttonPagination(helpEmbeds, response, event.getApi());
    }

    /**
     *
     * @param name: takes the name of the command
     * @param user: Take the user who invoked the command
     * @return EmbedBuilder: returns an embedBuilder which will be sent to the user once they find the command they were looking for!
     */
    private EmbedBuilder findCommand(String name, User user) {
        for (Command command : commands) {
            Annotation[] annotations = command.getClass().getAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation instanceof Invoker invoker){
                    if (invoker.alias().equalsIgnoreCase(name)) {
                        EmbedBuilder em = new EmbedBuilder()
                                .setTitle(invoker.alias())
                                .setDescription(invoker.description())
                                .setTimestampToNow()
                                .setColor(Color.cyan)
                                .setAuthor(user);
                        if (invoker.type() == UNSUPPORTED) {
                            return new EmbedBuilder()
                                    .setTitle(invoker.alias())
                                    .setDescription(invoker.description())
                                    .setTimestampToNow()
                                    .setAuthor(user)
                                    .setColor(Color.red)
                                    .setFooter("Note - this command is deprecated, find an alternative command!");
                        }
                        return em;
                    }
                }
            }
        }
        return new EmbedBuilder()
                .setTitle("Command Not found!")
                .setDescription("Use `/help` command to view a list of all the available commands!")
                .setColor(Color.RED)
                .setTimestampToNow();
    }

    private List<EmbedBuilder> createHelpEmbeds(){

        System.out.println(commands.size());

        List<EmbedBuilder> embedBuilders = new ArrayList<>();
        Map<CommandType, ArrayList<String>> em = new LinkedHashMap<>();
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
                        }case TEAM -> {
                            em.get(TEAM).add(((Invoker) annotation).alias());
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
                        }case UNSUPPORTED -> {
                            em.get(UNSUPPORTED).add(((Invoker) annotation).alias());
                        }default -> {
                            // do nothing...
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
                    .setFooter("type the name of the command after `/help` to get command specific help")
                    .setColor(Color.DARK_GRAY));
        });
        return embedBuilders;

    }
}
