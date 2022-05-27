package com.fthlbot.discordbotfthl.Commands.CommandImpl.FunAndRandomCommands;

import com.fthlbot.discordbotfthl.Annotation.CommandType;
import com.fthlbot.discordbotfthl.Annotation.Invoker;
import com.fthlbot.discordbotfthl.Handlers.Command;
import com.fthlbot.discordbotfthl.Util.BotConfig;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.TextInput;
import org.javacord.api.entity.message.component.TextInputStyle;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.List;
import java.util.concurrent.TimeUnit;


@Component
@Invoker(
        alias = "suggestion",
        description = "Suggest something to the league",
        usage = "/suggestion (type in the modal form)",
        type = CommandType.MISC
)
public class SuggestionImpl implements Command {
    private final BotConfig config;

    public SuggestionImpl(BotConfig config) {
        this.config = config;
    }

    @Override
    public void execute(SlashCommandCreateEvent event) {
        if (event.getSlashCommandInteraction().getChannel().get().getId() != config.getSuggestionChannelID()){
            event.getSlashCommandInteraction().createImmediateResponder().setFlags(MessageFlag.EPHEMERAL)
                    .setContent("This command is restricted to suggestion channel")
                    .respond();
            return;
        }
        event.getSlashCommandInteraction().respondWithModal(
                "suggestion",
                "Suggestion Form",
                ActionRow.of(
                        TextInput.create(TextInputStyle.PARAGRAPH,
                                "suggestion",
                                "What would you like to suggest?",
                                true
                        )
                )
        );
        User user = event.getSlashCommandInteraction().getUser();

        event.getApi().addModalSubmitListener(mf -> {
            if (!mf.getModalInteraction().getCustomId().equalsIgnoreCase("suggestion")) {
                return;
            }
            if (mf.getModalInteraction().getUser().getId() != user.getId()){
                return;
            }
            List<String> textInputValues = mf.getModalInteraction().getTextInputValues();
            //Convert the string list into a string
            StringBuilder sb = new StringBuilder();
            for (String s : textInputValues) {
                sb.append(s);
            }
            String suggestion = sb.toString();

            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setTitle("Suggestion")
                    .setFooter("Suggestion by " + user.getDiscriminatedName())
                    .setTimestampToNow()
                    .setDescription(suggestion)
                    .setColor(Color.yellow);

            long suggestionChannelID = config.getSuggestionChannelID();
            event.getApi().getTextChannelById(suggestionChannelID).ifPresent(channel -> {
                channel.sendMessage(embedBuilder).thenAccept(
                        message -> {
                            message.addReaction("\uD83D\uDC4D"); //thumbs up
                            message.addReaction("\uD83D\uDC4E"); //thumbs down
                        }
                );
            });
            mf.getModalInteraction().createImmediateResponder()
                    .setFlags(MessageFlag.EPHEMERAL)
                    .setContent("Your suggestion has been submitted!")
                    .respond();

        }).removeAfter(5, TimeUnit.MINUTES);
    }
}
