package com.fthlbot.discordbotfthl.Util;

import com.fthlbot.discordbotfthl.Annotation.CommandType;
import com.fthlbot.discordbotfthl.Annotation.Invoker;
import com.fthlbot.discordbotfthl.DatabaseModels.Exception.LeagueException;
import com.fthlbot.discordbotfthl.DiscordBotFthlApplication;
import com.fthlbot.discordbotfthl.Util.Exception.UnsupportedCommandException;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class GeneralService extends DiscordBotFthlApplication {
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
