package com.fthlbot.discordbotfthl.Commands.CommandImpl.ClashCommandImpl;

import com.fthlbot.discordbotfthl.Commands.CommandListener.ClashCommandListener.PlayerListener;
import com.fthlbot.discordbotfthl.DatabaseModels.Roster.RosterService;
import com.fthlbot.discordbotfthl.DatabaseModels.Team.Team;
import com.fthlbot.discordbotfthl.MinionBotAPI.MinionBotClient;
import com.fthlbot.discordbotfthl.MinionBotAPI.MinionBotPlayer;
import com.fthlbot.discordbotfthl.Util.Exception.ClashExceptionHandler;
import com.fthlbot.discordbotfthl.Util.Pagination.Pagination;
import com.fthlbot.discordbotfthl.core.Annotation.CommandType;
import com.fthlbot.discordbotfthl.core.Annotation.Invoker;
import com.sahhiill.clashapi.core.ClashAPI;
import com.sahhiill.clashapi.core.exception.ClashAPIException;
import com.sahhiill.clashapi.models.league.League;
import com.sahhiill.clashapi.models.player.Player;
import com.sahhiill.clashapi.models.player.PlayerClan;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Component
@Invoker(
        alias = "player",
        description = "Get player information",
        usage = "player <player tag>",
        type = CommandType.CLASH
)
public class PlayerImpl implements PlayerListener {
    private final RosterService rosterService;
    private final Logger logger = LoggerFactory.getLogger(PlayerImpl.class);

    public PlayerImpl(RosterService rosterService) {
        this.rosterService = rosterService;
    }

    @Override
    public void execute(SlashCommandCreateEvent event) {
        CompletableFuture<InteractionOriginalResponseUpdater> responder = event.getSlashCommandInteraction().respondLater();
        ClashAPI clash = new ClashAPI();
        String s = event.getSlashCommandInteraction().getArguments().get(0).getStringValue().get();
        try {
            Player player = clash.getPlayer(s);
            List<EmbedBuilder> embed = createEmbed(player);
            Pagination pagination = new Pagination();
            pagination.buttonPagination(embed, responder, event.getApi());
        } catch (ClashAPIException e) {
            logger.error("Error getting player information", e);
            ClashExceptionHandler handler = new ClashExceptionHandler();
            handler.setStatusCode(Integer.valueOf(e.getMessage()));
            handler.setResponder(responder.join());
            handler.respond();
        } catch (IOException e) {
            logger.error("Error getting player information", e);
        } catch (Exception e) {
            logger.error("Error getting player information", e);
            e.printStackTrace();
        }
    }

    private List<EmbedBuilder> createEmbed(Player player) {
        MinionBotClient client = new MinionBotClient();
        MinionBotPlayer[] playerBan = new MinionBotPlayer[0];
        try {
            playerBan = client.getPlayerBan(player.getTag());
        } catch (Exception e) {
            //.. swallow the error
        }
        StringBuilder ban = new StringBuilder();
        if (playerBan.length == 0) {
            //No ban
            ban.append("No ban");
        } else {
            for (MinionBotPlayer m_player : playerBan) {
                ban.append(m_player.getOrgInitials().orElse(m_player.getName().orElse("League Name not found! org id: " + m_player.getOrgID()))).append("\n");
            }
        }
        String teams = rosterService.getTeamsForPlayerTag(player.getTag())
                .stream()
                .map(Team::getName).reduce((a, b) -> a + "\n" + b)
                .orElse("No team found!");

        EmbedBuilder secondPage = new EmbedBuilder()
                .setTitle("Player Information")
                .addInlineField("League bans", ban.toString())
                .addInlineField("Team Registered in Fthl", teams)
                .setTimestampToNow()
                .setColor(Color.GREEN);
        Optional<PlayerClan> clan = Optional.ofNullable(player.getClan());
        String clanName = clan.map(PlayerClan::getName).orElse("No clan found!");
        String clanTag = clan.map(PlayerClan::getTag).orElse("No clan found!");
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle(player.getName())
                .addField("TownHall Level", player.getTownHallLevel() + "")
                .addField("Player Tag", player.getTag())
                .addField("Clan name ", clanName)
                .addField("Clan Tag", clanTag)
                .addField("Clan Role", player.getRole())

                .addField("Experience level", player.getExpLevel() + "<:exp:924431294828531803>")
                .addField("War Stars", player.getWarStars() + "⭐")
                .addField("Trophies", player.getTrophies() + " <:Trophy:888872198754926622>")
                .addField("League", getLeagueEmote(player))
                .addField("Builder hall Trophies", player.getBuilderBaseTrophies() + " <:Icon_Versus_Trophy:888872197832183929>")
                .setTimestampToNow()
                .setColor(Color.GREEN);
        return List.of(embed, secondPage);
    }

    private String getLeagueEmote(Player player) {
        String leagueEmote = "Unranked";
        League league = player.getLeague();

        try {
            leagueEmote = switch (league.getName()) {
                case "Bronze League II", "Bronze League I", "Bronze League III" -> "<:Bronze:888847979556012072>";
                case "Silver League I", "Silver League II", "Silver league III" -> "<:Silver:888847990121463858>";
                case "Gold League II", "Gold League I", "Gold League III" -> "<:gold:888847985146986507>";
                case "Crystal League I", "Crystal League II", "Crystal League III" -> "<:Crystal:888847981384716349>";
                case "Master League I", "Master League II", "Master League III" -> "<:Master:888847990012403802>";
                case "Champion League I", "Champion League II", "Champion League III" ->
                        "<:Champion:888847980122218627>";
                case "Titan League I", "Titan League II", "Titan League III" -> "<:Titan:888847989400035389>";
                case "Legend League" -> "<:Legends:888847986610802688>";
                default -> "<:Unranked:888847990041763881>";
            };
        } catch (NullPointerException e) {
            leagueEmote = "<:Unranked:888847990041763881>";
        }

        return leagueEmote;
    }
}
