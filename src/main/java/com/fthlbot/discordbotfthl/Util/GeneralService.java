package com.fthlbot.discordbotfthl.Util;

import com.fthlbot.discordbotfthl.core.Annotation.CommandType;
import com.fthlbot.discordbotfthl.core.Annotation.Invoker;
import com.fthlbot.discordbotfthl.DatabaseModels.Exception.LeagueException;
import com.fthlbot.discordbotfthl.DiscordBotFthlApplication;
import com.fthlbot.discordbotfthl.Util.Exception.UnsupportedCommandException;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;
import org.javacord.api.util.logging.ExceptionLogger;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.CompletableFuture;


public class GeneralService {
    private static final Logger log = LoggerFactory.getLogger(GeneralService.class);
    private static final long UNIX_CONVERTER = 1000L;


    /**
     *
     * @param key of json object
     * @return returns if the today's date is between the noted date define in the json (not inclusive)
     * @throws IOException throw when something goes wrong lmao
     * @throws ParseException - will throw an error if I mess up json formatting
     */
    public static boolean isValidDate(String key) throws IOException, ParseException {
        String content = getFileContent("dates.json");

        JSONObject jsonObject = new JSONObject(content);
        String s = jsonObject.getJSONObject(key).getString("startDate");
        String e = jsonObject.getJSONObject(key).getString("endDate");

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        LocalDate startDate = convertToLocalDateViaInstant(format.parse(s));
        LocalDate endDate = convertToLocalDateViaInstant(format.parse(e));

        LocalDate date = LocalDate.now();
        return startDate.compareTo(date) * date.compareTo(endDate) > 0;
    }

    /**
     *
     * @param dateToConvert takes the date to be converted
     * @return returns the date as local date
     */
    private static LocalDate convertToLocalDateViaInstant(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    public static String padLeftZeros(String inputString, int length) {
        if (inputString.length() >= length) {
            return inputString;
        }
        StringBuilder sb = new StringBuilder();
        while (sb.length() < length - inputString.length()) {
            sb.append('0');
        }
        sb.append(inputString);

        return sb.toString();
    }

    public static void leagueSlashErrorMessage(CompletableFuture<InteractionOriginalResponseUpdater> responder, LeagueException e) {
        responder.thenAccept(res -> {
            EmbedBuilder em = new EmbedBuilder()
                    .setTitle("<:deny:934405749881315380>Error! ")
                    .setDescription(e.getMessage())
                    .setColor(Color.red)
                    .setTimestampToNow();
            res.addEmbed(em);
            res.update();
        }).exceptionally(ExceptionLogger.get());
    }
    public static void leagueSlashErrorMessage(CompletableFuture<InteractionOriginalResponseUpdater> responder, String e) {
        responder.thenAccept(res -> {
            res.setContent(e);
            res.update();
        }).exceptionally(ExceptionLogger.get());
    }
    public static void leagueSlashErrorMessage(SlashCommandCreateEvent event, LeagueException e){
        EmbedBuilder em = new EmbedBuilder()
                .setTitle("<:deny:934405749881315380>Error! ")
                .setDescription(e.getMessage())
                .setColor(Color.red)
                .setTimestampToNow();
        event.getSlashCommandInteraction().createImmediateResponder().addEmbeds(em).respond();
    }
    public static void leagueSlashErrorMessage(SlashCommandCreateEvent event, Exception e){
        EmbedBuilder em = new EmbedBuilder()
                .setTitle("<:deny:934405749881315380>Error! ")
                .setDescription(e.getMessage())
                .setColor(Color.red)
                .setTimestampToNow();
        event.getSlashCommandInteraction().createImmediateResponder().addEmbeds(em).respond();
    }

    public static void leagueSlashErrorMessage(SlashCommandCreateEvent event, String e){
        EmbedBuilder em = new EmbedBuilder()
                .setTitle("<:deny:934405749881315380>Error! ")
                .setDescription(e)
                .setColor(Color.red)
                .setTimestampToNow();
        event.getSlashCommandInteraction().createImmediateResponder().addEmbeds(em).respond();
    }



    @Deprecated
    private static boolean isCommand(String args, String command, MessageCreateEvent event, Invoker invoker ) throws UnsupportedCommandException {
        if (!args.equalsIgnoreCase(command)){
            return false;
        }
        if (invoker.type().equals(CommandType.UNSUPPORTED)){
            throw new UnsupportedCommandException(command);
        }
        return true;
    }

    private static InputStream getFileAsIOStream(final String fileName) {
        InputStream ioStream = DiscordBotFthlApplication.class
                .getClassLoader()
                .getResourceAsStream(fileName);

        if (ioStream == null) {
            throw new IllegalArgumentException(fileName + " is not found");
        }
        return ioStream;
    }

    public static String getFileContent(String fileName) throws IOException {
        InputStream is = getFileAsIOStream(fileName);
        StringBuilder str = new StringBuilder();
        try (InputStreamReader isr = new InputStreamReader(is);
             BufferedReader br = new BufferedReader(isr);)
        {
            String line;
            while ((line = br.readLine()) != null) {
                str.append(line);
            }
            is.close();
        }
        return str.toString();
    }

    @Deprecated
    public static EmbedBuilder notEnoughArgument(){
        return new EmbedBuilder()
                .setTitle("<:deny:934405749881315380>Error!")
                .setDescription("One or more argument is missing!\nLearn how to use my commands via the `>help` command")
                .setColor(Color.red)
                .setTimestampToNow();
    }
    public static EmbedBuilder getLeagueError(Exception message, MessageCreateEvent event) {
        return new EmbedBuilder()
                .setTitle("<:deny:934405749881315380>Error! ")
                .setDescription(message.getMessage())
                .setAuthor(event.getMessageAuthor())
                .setColor(Color.red)
                .setTimestampToNow();
    }
    public static EmbedBuilder getLeagueError(Exception message, SlashCommandCreateEvent event) {
        return new EmbedBuilder()
                .setTitle("<:deny:934405749881315380>Error! ")
                .setDescription(message.getMessage())
                .setAuthor(event.getSlashCommandInteraction().getUser())
                .setColor(Color.red)
                .setTimestampToNow();
    }


    //create a method that sends a message to the channel when a fatal error occurs
    public static void sendFatalError(CompletableFuture<InteractionOriginalResponseUpdater> responder, Exception e) {
        responder.thenAccept(response -> {
           //Create an embed with error as title and description to say contact the developer
            EmbedBuilder embed = new EmbedBuilder().setTitle("<:deny:934405749881315380>Error!")
                    .setDescription("An error occurred!\nContact the developer to fix this issue")
                    .addField("Error", e.getMessage(), false)
                    .setColor(Color.red)
                    .setTimestampToNow();
            //add the embed in response and send it, via the update method
            response.addEmbed(embed).update();
        });
    }

    public static EmbedBuilder getEmbedBuilder(Color red, String s) {
        return new EmbedBuilder()
                .setTitle("<:deny:934405749881315380>Warning!")
                .setDescription(s)
                .setColor(red)
                .setTimestampToNow();
    }

    private static long dateToUnix(Date date) {
        return date.getTime() / UNIX_CONVERTER;
    }

    /**
     * Converts a unix timestamp to a date
     * @param date the unix timestamp
     * @return Date
     */
    public static String dateToStringInDiscordFormat(Date date) {
        StringBuilder sb = new StringBuilder();
        return sb.append("<t:").append(dateToUnix(date)).append(":f>").toString();
    }

    public static void printMemoryUsage(){
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long allocatedMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        log.info("Max Memory: " + maxMemory);
        log.info("Allocated Memory: " + allocatedMemory);
        log.info("Free Memory: " + freeMemory);
    }
}
