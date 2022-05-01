package com.fthlbot.discordbotfthl.Commands.CommandImpl.StaffCommandsImpl;

import com.fthlbot.discordbotfthl.Annotation.CommandType;
import com.fthlbot.discordbotfthl.Annotation.Invoker;
import com.fthlbot.discordbotfthl.Commands.CommandListener.StaffCommandListener.DeleteATeamListener;
import com.fthlbot.discordbotfthl.DatabaseModels.Division.Division;
import com.fthlbot.discordbotfthl.DatabaseModels.Division.DivisionService;
import com.fthlbot.discordbotfthl.DatabaseModels.Exception.LeagueException;
import com.fthlbot.discordbotfthl.DatabaseModels.Team.Team;
import com.fthlbot.discordbotfthl.DatabaseModels.Team.TeamService;
import com.fthlbot.discordbotfthl.Util.GeneralService;
import com.fthlbot.discordbotfthl.Util.SlapbotEmojis;
import org.javacord.api.entity.emoji.KnownCustomEmoji;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.message.component.LowLevelComponent;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.concurrent.CompletableFuture;

@Component
@Invoker(
        alias = "delete-a-team",
        description = "Delete a team from the database.",
        usage = "delete-team <division> <team-identifier>",
        type = CommandType.STAFF
)
public class DeleteATeamImpl implements DeleteATeamListener {
    private final TeamService teamService;
    private final DivisionService divisionService;
    private final Logger log = LoggerFactory.getLogger(DeleteATeamImpl.class);

    public DeleteATeamImpl(TeamService teamService, DivisionService divisionService) {
        this.teamService = teamService;
        this.divisionService = divisionService;
    }

    @Override
    public void execute(SlashCommandCreateEvent event) {
        CompletableFuture<InteractionOriginalResponseUpdater> respond =event.getSlashCommandInteraction().respondLater();

        String divisionAlias = event.getSlashCommandInteraction().getArguments().get(0).getStringValue().get();
        String teamAlias = event.getSlashCommandInteraction().getArguments().get(1).getStringValue().get();
        Team team;
        Division division;
        try{
            division = divisionService.getDivisionByAlias(divisionAlias);
            team = teamService.getTeamByDivisionAndAlias(teamAlias, division);
        }catch (LeagueException e){
            GeneralService.leagueSlashErrorMessage(respond, e);
            return;
        }

        KnownCustomEmoji emoji = SlapbotEmojis.getEmojiOptional("check").get();
        KnownCustomEmoji emoji1 = SlapbotEmojis.getEmojiOptional("deny").get();
        respond.thenAccept(response -> {
            LowLevelComponent[] lowLevelComponents = {
                    Button.primary("Accept", emoji),
                    Button.primary("Cancel", emoji1)
            };
            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setDescription("Are you sure you want to delete the team " + team.getName() + "?")
                    .setFooter("Press the ✅ to confirm, or ❌ to cancel.").setColor(Color.RED);
            response.addEmbed(embedBuilder).addComponents(ActionRow.of(lowLevelComponents)).update().thenAccept(update -> {
               update.addButtonClickListener(button -> {
                   if (button.getButtonInteraction().getCustomId().equals("Accept")) {
                       teamService.deleteTeam(team);
                       EmbedBuilder embedBuilder1 = new EmbedBuilder()
                               .setDescription("Team " + team.getName() + " has been deleted from the division " + division.getName() + ".")
                                       .setColor(Color.GREEN);
                       event.getSlashCommandInteraction().createFollowupMessageBuilder()
                               .addEmbed(embedBuilder1).send();
                        log.info("Team " + team.getName() + " has been deleted from the division " + division.getName() + ".");
                   }else if (button.getButtonInteraction().getCustomId().equals("Cancel")){
                       EmbedBuilder embedBuilder1 = new EmbedBuilder()
                               .setDescription("Team deletion cancelled.")
                               .setColor(Color.RED);
                       event.getSlashCommandInteraction().createFollowupMessageBuilder()
                               .addEmbed(embedBuilder1).send();
                   }else{
                       EmbedBuilder embedBuilder1 = new EmbedBuilder()
                               .setDescription("Something went wrong.")
                               .setColor(Color.RED);
                       event.getSlashCommandInteraction().createFollowupMessageBuilder()
                               .addEmbed(embedBuilder1).send();
                       log.info("Something went wrong with the button click listener.");
                   }
                   response.removeAllComponents().update();
               });
            });

        });
    }
}
