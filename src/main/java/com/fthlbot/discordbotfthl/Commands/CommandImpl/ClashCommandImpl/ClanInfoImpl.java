package com.fthlbot.discordbotfthl.Commands.CommandImpl.ClashCommandImpl;

import Core.Enitiy.clan.ClanModel;
import Core.JClash;
import Core.exception.ClashAPIException;
import com.fthlbot.discordbotfthl.core.Annotation.CommandType;
import com.fthlbot.discordbotfthl.core.Annotation.Invoker;
import com.fthlbot.discordbotfthl.Commands.CommandListener.ClashCommandListener.ClanInfoListener;
import com.fthlbot.discordbotfthl.Util.Exception.ClashExceptionHandler;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@Component
@Invoker(
        alias = "clan-info",
        description = "Get clan information",
        usage = "/clan-info <clan tag>",
        type = CommandType.CLASH
)
public class ClanInfoImpl implements ClanInfoListener {

    private final Logger logger = LoggerFactory.getLogger(ClanInfoImpl.class);

    @Override
    public void execute(SlashCommandCreateEvent event) {
        SlashCommandInteraction slash = event.getSlashCommandInteraction();
        CompletableFuture<InteractionOriginalResponseUpdater> responder = slash.respondLater();
        String tag = slash.getArguments().get(0).getStringValue().get();

        try {
            JClash clash = new JClash();
            ClanModel clan = clash.getClan(tag).join();
            sendCLanInfo(clan, slash, responder.join());
        } catch (ClashAPIException ce){
            logger.error("Error getting clan info",ce);
            ClashExceptionHandler errorHandler = new ClashExceptionHandler();
            errorHandler.setStatusCode(Integer.valueOf(ce.getMessage()));
            errorHandler.setResponder(responder.join());
            errorHandler.respond();
        } catch (IOException ioe) {
            logger.error("Error getting clan info {}",ioe.getMessage());
        } catch (Exception e){
            logger.error("Unexpected error in ClanInfoImpl {}",e.getMessage());
        }
    }

    private void sendCLanInfo(
            ClanModel clan,
            SlashCommandInteraction slashInter,
            InteractionOriginalResponseUpdater responseUpdater){

        User invokedUser = slashInter.getUser();

        EmbedBuilder emb = new EmbedBuilder()
                .setAuthor(invokedUser.getDiscriminatedName(),null,invokedUser.getAvatar())
                .setTitle(clan.getName())
                .setDescription(clan.getTag())
                .setThumbnail(clan.getBadgeUrls().getMedium())
                .addField("Description",clan.getDescription(),false)
                .addField("Level",clan.getClanLevel().toString(),true)
                .addField("Members",String.format("%d/50",clan.getMembers()),true)
                .addField("Public War Log",clan.isWarLogPublic().toString(),false)
                .addField("War League",clan.getWarLeague().getName(),false)
                .addField("Location",clan.getLocation().getName(),false)
                .setTimestampToNow()
                .setColor(Color.ORANGE);

        responseUpdater
                .addEmbed(emb)
                .addComponents(
                        ActionRow.of(Button.link(clan.getLink(),"Clan Link"))
                )
                .update();
    }
}
