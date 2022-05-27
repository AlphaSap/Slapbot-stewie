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
        type = IGNORE
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
        if (arguments.size() >= 1) {
            String commandName = arguments.get(0).getStringValue().get();
            Optional<Invoker> commandFromInvoker = findCommandFromInvoker(commandName);
            if (commandFromInvoker.isEmpty()) {
                response.thenAccept(
                        res -> {
                            res.addEmbed(
                                    new EmbedBuilder()
                                            .setDescription("Command not found")
                                            .setColor(Color.RED)
                            ).update();
                        }
                );
                return;
            }
            Invoker command = commandFromInvoker.get();
            EmbedBuilder embedBuilder = makeEmbed(command);
            response.thenAccept(
                    res -> {
                        res.addEmbed(embedBuilder).update();
                    }
            );
            return;
        }

        Pagination pagination = new Pagination();
        List<EmbedBuilder> helpEmbeds = getHelpEmbeds();

        pagination.buttonPagination(helpEmbeds, response, event.getApi());
    }

    private Optional<Invoker> findCommandFromInvoker(String commandName) {
        for (Command command : commands) {
            for (Annotation annotation : command.getClass().getAnnotations()) {
                if (annotation instanceof Invoker invoker) {
                    if (invoker.alias().equalsIgnoreCase(commandName)) {
                        return Optional.of(invoker);
                    }
                }
            }
        }
        return Optional.empty();
    }

    private EmbedBuilder makeEmbed(Invoker invoker) {
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle(invoker.alias())
                .setDescription(invoker.description())
                .setTimestampToNow()
                .setColor(Color.GREEN)
                .addField("Usage", invoker.usage(), false)
                .addField("Type", invoker.type().toString(), false);

        if (invoker.type() == UNSUPPORTED) {
            embedBuilder.setFooter("Note: This command is not supported");
        }
        return embedBuilder;
    }
    private List<EmbedBuilder> getHelpEmbeds() {
        Map<CommandType, ArrayList<Invoker>> em = new HashMap<>();
        em.put(INFO, new ArrayList<>());
        em.put(REGISTRATION, new ArrayList<>());
        em.put(ROSTER_MANAGEMENT, new ArrayList<>());
        em.put(SCHEDULE, new ArrayList<>());
        em.put(MISC, new ArrayList<>());
        em.put(STAFF, new ArrayList<>());

        for (Command command : commands) {
            Annotation[] annotations = command.getClass().getAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation instanceof Invoker invoker) {
                    if (!em.containsKey(invoker.type())) {
                        continue;
                    }
                    em.get(invoker.type()).add(invoker);
                }
            }
        }
        List<EmbedBuilder> embedBuilders = new ArrayList<>();
        em.forEach((key, value) -> {
            StringBuilder sb = new StringBuilder();
            for (Invoker invoker : value) {
                sb.append("`").append(invoker.alias()).append("`").append("\n");
            }
            embedBuilders.add(
                    new EmbedBuilder()
                            .setTitle(key.toString())
                            .setDescription(sb.toString())
                            .setColor(Color.GREEN)
            );
        });
        return embedBuilders;
    }


}
