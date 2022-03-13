package com.fthlbot.discordbotfthl.Util;

import com.sun.istack.NotNull;
import org.javacord.api.entity.emoji.CustomEmojiBuilder;
import org.javacord.api.entity.emoji.Emoji;
import org.javacord.api.entity.emoji.KnownCustomEmoji;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.message.component.HighLevelComponent;
import org.javacord.api.entity.message.component.LowLevelComponent;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.interaction.callback.InteractionCallbackDataFlag;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;
import org.javacord.api.util.logging.ExceptionLogger;
import org.springframework.scheduling.annotation.Scheduled;

import javax.swing.*;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Pagination {
    public void reactionPaginate(List<EmbedBuilder> em, MessageCreateEvent event) {
        event.getChannel().sendMessage(em.get(0)).thenAccept(message -> {
            message.addReactions("⏪", "◀", "▶", "⏩");
            AtomicInteger i = new AtomicInteger();
            message.addReactionAddListener(reaction -> {
                if (reaction.getUser().get().isYourself() || reaction.getUser().get().isBot()) {
                    return;
                }
                try {
                    if (reaction.getEmoji().equalsEmoji("⏪")) {
                        //Move to the first index 0
                        message.edit(em.get(0));
                        i.set(0);
                            reaction.removeReaction();
                    } else if (reaction.getEmoji().equalsEmoji("◀")) {
                        //Move to the previous embed
                        message.edit(em.get(i.get() - 1));
                        i.decrementAndGet();
                        reaction.removeReaction();

                    } else if (reaction.getEmoji().equalsEmoji("▶")) {
                        //Move to the next embed

                        message.edit(em.get(i.get() + 1));
                        i.getAndIncrement();
                        reaction.removeReaction();

                    } else if (reaction.getEmoji().equalsEmoji("⏩")) {
                        //Move to the last embed
                        message.edit(em.get(em.size() - 1));
                        i.set(em.size() - 1);
                        reaction.removeReaction();
                    }
                } catch (IndexOutOfBoundsException e) {
                    reaction.removeReaction();
                }
                event.getApi().getThreadPool().getScheduler().schedule(() -> {
                    message.removeAllReactions();
                }, 10, TimeUnit.MINUTES);
            }).removeAfter(11, TimeUnit.MINUTES);

        }).exceptionally(ExceptionLogger.get());
    }

    public void buttonPaginate(List<EmbedBuilder> em, MessageCreateEvent event) {
        LowLevelComponent[] lowLevelComponents = {
                Button.secondary("first", "⏪"),
                Button.secondary("previous", "◀️"),
                Button.secondary("next", "▶️"),
                Button.secondary("last", "⏩")
        };
        CompletableFuture<Message> send = new MessageBuilder()
                .setEmbed(em.get(0))
                .addComponents(
                        ActionRow.of(
                                lowLevelComponents
                        )
                ).send(event.getChannel());

        send.thenAccept(message -> {
            AtomicInteger i = new AtomicInteger();

            event.getApi().addButtonClickListener(button -> {
                if (button.getButtonInteraction().getUser().getId() != event.getMessageAuthor().getId()) {
                    button.getButtonInteraction()
                            .createImmediateResponder()
                            .setFlags(InteractionCallbackDataFlag.EPHEMERAL)
                            .setContent("You cannot interact with a message which was not triggered by your command")
                            .respond();
                    return;
                }
                String customId = button.getButtonInteraction().getCustomId();

                try {
                    switch (customId) {
                        case "first" -> {
                            button.getButtonInteraction().acknowledge().thenAccept(a -> {
                                message.edit(em.get(0));
                                i.set(0);
                            });
                        }
                        case "last" -> {
                            button.getButtonInteraction().acknowledge().thenAccept(a -> {
                                message.edit(em.get(em.size() - 1));
                                i.set(em.size() - 1);
                            });
                        }
                        case "next" -> {
                            button.getButtonInteraction().acknowledge().thenAccept(a -> {
                                message.edit(em.get(i.get() + 1));
                                i.incrementAndGet();
                            });
                        }
                        case "previous" -> {
                            button.getButtonInteraction().acknowledge().thenAccept(a -> {
                                message.edit(em.get(i.get() - 1));
                                i.decrementAndGet();
                            });
                        }
                    }
                } catch (IndexOutOfBoundsException e) {
                    //
                }
                //Delete the buttons after some time!

            }).addRemoveHandler(() -> message.toMessageBuilder().removeAllComponents());
        });

            event.getApi().getThreadPool().getScheduler().schedule(() -> {
                send.join().toMessageBuilder().removeAllComponents();
            }, 10, TimeUnit.SECONDS);
    }

    public void buttonPaginate(List<EmbedBuilder> em, SlashCommandCreateEvent event) {
        LowLevelComponent[] lowLevelComponents = {
                Button.secondary("first", "⏪"),
                Button.secondary("previous", "◀️"),
                Button.secondary("next", "▶️"),
                Button.secondary("last", "⏩")
        };

        CompletableFuture<InteractionOriginalResponseUpdater> send = event.getSlashCommandInteraction().createImmediateResponder()
                .addEmbeds(em.get(0)).respond();
        send.thenAccept(res -> {
            res.addComponents(ActionRow.of(lowLevelComponents));
        });


        send.thenAccept(message -> {
            AtomicInteger i = new AtomicInteger();
            User user = event.getSlashCommandInteraction().getUser();

            event.getApi().addButtonClickListener(button -> {
                if (button.getButtonInteraction().getUser().getId() != user.getId()) {
                    button.getButtonInteraction()
                            .createImmediateResponder()
                            .setFlags(InteractionCallbackDataFlag.EPHEMERAL)
                            .setContent("You cannot interact with a message which was not triggered by your command")
                            .respond();
                    return;
                }
                String customId = button.getButtonInteraction().getCustomId();

                try {
                    switch (customId) {
                        case "first" -> {
                            button.getButtonInteraction().acknowledge().thenAccept(a -> {
                                message.update().thenAccept(m -> m.edit(em.get(0)));
                                i.set(0);
                            });
                        }
                        case "last" -> {
                            button.getButtonInteraction().acknowledge().thenAccept(a -> {
                                message.update().thenAccept(m -> m.edit(em.get(em.size() - 1)));
                                i.set(em.size() - 1);
                            });
                        }
                        case "next" -> {
                            button.getButtonInteraction().acknowledge().thenAccept(a -> {
                                message.update().thenAccept(m -> m.edit(em.get(i.get() + 1)));
                                i.incrementAndGet();
                            });
                        }
                        case "previous" -> {
                            button.getButtonInteraction().acknowledge().thenAccept(a -> {
                                message.update().thenAccept(m -> m.edit(em.get(i.get() - 1)));
                                i.decrementAndGet();
                            });
                        }
                    }
                } catch (IndexOutOfBoundsException e) {
                    //
                }
                //Delete the buttons after some time!

            }).removeAfter(11, TimeUnit.MINUTES);
        });

    }
}
