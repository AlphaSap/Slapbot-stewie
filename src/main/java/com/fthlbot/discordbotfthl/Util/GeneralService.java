package com.fthlbot.discordbotfthl.Util;

import com.fthlbot.discordbotfthl.Annotation.CommandType;
import com.fthlbot.discordbotfthl.Annotation.Invoker;
import com.fthlbot.discordbotfthl.DatabaseModels.Exception.LeagueException;
import com.fthlbot.discordbotfthl.DiscordBotFthlApplication;
import com.fthlbot.discordbotfthl.Util.Exception.UnsupportedCommandException;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.event.message.MessageCreateEvent;
import org.json.JSONObject;

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

public class GeneralService extends DiscordBotFthlApplication {
    /**
     *
     * @param key of json object
     * @return returns if the today's date is between the noted date define in the json (not inclusive)
     * @throws IOException
     * @throws ParseException - will throw an error if I mess up json formatting
     */
    public boolean isValidDate(String key) throws IOException, ParseException {
        String content = getFileContent("dates.json");

        JSONObject jsonObject = new JSONObject(content);
        String s = jsonObject.getJSONObject(key).getString("startDate");
        String e = jsonObject.getJSONObject(key).getString("endDate");

        DateFormat format = new SimpleDateFormat("yyyy/MM/dd");

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
    private LocalDate convertToLocalDateViaInstant(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    public static void leagueSlashErrorMessage(SlashCommandCreateEvent event, LeagueException e) {
        event.getSlashCommandInteraction().respondLater().thenAccept(res -> {
            res.setContent(e.getMessage());
            res.update();
        });
    }

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

    /*public static boolean messageChecker(MessageCreateEvent event, Class<?> listenerClass) {
        //Create an array of message received
        String[] args = event.getMessageContent().split("\\s+");
        //Annotation object - get Invoker annotation from listenerClass
        Invoker invoker = listenerClass.getAnnotation(Invoker.class);
        //New arraylist
        return Arrays.stream(invoker.alias()).anyMatch(x -> {
            try {
                return isCommand(args[0], prefix + x, event, invoker);
            } catch (UnsupportedCommandException e) {
                e.printStackTrace();
                event.getChannel().sendMessage(getLeagueError(e, event));
                return false;
            }
        });
        *//*ArrayList<String> arrayList = new ArrayList<>();
        //Add all the aliases into the arrayList
        Collections.addAll(arrayList, invoker.alias());
        //Returns true if the args[0] matches any command
        return arrayList.stream().anyMatch(
                str -> {
                    try {
                        return isCommand(args[0], prefix + str, event, invoker);
                    } catch (UnsupportedCommandException e) {
                        e.printStackTrace();
                        event.getChannel().sendMessage(getLeagueError(e, event));
                        return false;
                    }
                }
        );

    }*/
}
