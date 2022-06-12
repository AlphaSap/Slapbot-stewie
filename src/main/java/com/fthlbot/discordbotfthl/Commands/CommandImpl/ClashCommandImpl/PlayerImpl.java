package com.fthlbot.discordbotfthl.Commands.CommandImpl.ClashCommandImpl;

import Core.Enitiy.clan.ClanModel;
import Core.Enitiy.player.League;
import Core.Enitiy.player.Player;
import Core.JClash;
import Core.exception.ClashAPIException;
import com.fthlbot.discordbotfthl.Annotation.CommandType;
import com.fthlbot.discordbotfthl.Annotation.Invoker;
import com.fthlbot.discordbotfthl.Commands.CommandListener.ClashCommandListener.PlayerListener;
import com.fthlbot.discordbotfthl.DatabaseModels.Roster.RosterService;
import com.fthlbot.discordbotfthl.DatabaseModels.Team.Team;
import com.fthlbot.discordbotfthl.MinionBotAPI.MinionBotClient;
import com.fthlbot.discordbotfthl.MinionBotAPI.MinionBotPlayer;
import com.fthlbot.discordbotfthl.Util.Exception.ClashExceptionHandler;
import com.fthlbot.discordbotfthl.Util.Pagination.Pagination;
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
        JClash clash = new JClash();
        String s = event.getSlashCommandInteraction().getArguments().get(0).getStringValue().get();
        try {
            Player player = clash.getPlayer(s).join();
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
        }catch (Exception e){
            logger.error("Error getting player information", e);
            e.printStackTrace();
        }
    }

    private List<EmbedBuilder> createEmbed(Player player) {
        MinionBotClient client = new MinionBotClient();
        MinionBotPlayer[] playerBan = client.getPlayerBan(player.getTag());
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
        //TODO: add an image to below embed
        Optional<ClanModel> clan = Optional.ofNullable(player.getClan());
        String clanName = clan.map(ClanModel::getName).orElse("No clan found!");
        String clanTag = clan.map(ClanModel::getTag).orElse("No clan found!");
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
                .addField("Builder hall Trophies", player.getVersusTrophies() + " <:Icon_Versus_Trophy:888872197832183929>")
                .setTimestampToNow()
                .setColor(Color.GREEN);
        return List.of(embed, secondPage);
    }

    private String getLeagueEmote(Player player) {
        String leagueEmote = "Unranked";
        League league = player.getLeague();
        try {
            switch (league.getName()) {
                case "Bronze League II":
                case "Bronze League I":
                case "Bronze League III":
                    leagueEmote = "<:Bronze:888847979556012072>";
                    break;

                case "Silver League I":
                case "Silver League II":
                case "Silver league III":
                    leagueEmote = "<:Silver:888847990121463858>";
                    break;

                case "Gold League II":
                case "Gold League I":
                case "Gold League III":
                    leagueEmote = "<:gold:888847985146986507>";
                    break;

                case "Crystal League I":
                case "Crystal League II":
                case "Crystal League III":
                    leagueEmote = "<:Crystal:888847981384716349>";
                    break;

                case "Master League I":
                case "Master League II":
                case "Master League III":
                    leagueEmote = "<:Master:888847990012403802>";
                    break;

                case "Champion League I":
                case "Champion League II":
                case "Champion League III":
                    leagueEmote = "<:Champion:888847980122218627>";
                    break;
                case "Titan League I":
                case "Titan League II":
                case "Titan League III":
                    leagueEmote = "<:Titan:888847989400035389>";
                    break;
                case "Legend League":
                    leagueEmote = "<:Legends:888847986610802688>";

                    break;
                default:
                    leagueEmote = "<:Unranked:888847990041763881>";
                    break;

            }
        } catch (NullPointerException e) {
            leagueEmote = "<:Unranked:888847990041763881>";
        }
        return leagueEmote;
    }
}
