package com.fthlbot.discordbotfthl.Util;

import com.fthlbot.discordbotfthl.Util.Exception.SlashCommandArgsNotFound;
import org.javacord.api.entity.Attachment;
import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.SlashCommandInteractionOption;

import java.util.List;
import java.util.Optional;

public class SlashCommandArgsParser {
    private final List<SlashCommandInteractionOption> args;

    public SlashCommandArgsParser(List<SlashCommandInteractionOption> args) {
        this.args = args;
    }


    public String getStringValue(int index) throws SlashCommandArgsNotFound {
        Optional<String> stringValue = args.get(index).getStringValue();
        if (stringValue.isPresent()){
            return stringValue.get();
        }
        //a sends an error directly to discord!
        throw new SlashCommandArgsNotFound("Missing args value");
    }
    public User getUserValue(int index) throws SlashCommandArgsNotFound {
        Optional<User> userValue = args.get(index).getUserValue();
        if (userValue.isPresent()){
            return userValue.get();
        }
        //a sends an error directly to discord!
        throw new SlashCommandArgsNotFound("Missing args User value");
    }
    public Long getLongValue(int index) throws SlashCommandArgsNotFound {
        Optional<Long> longValue = args.get(index).getLongValue();
        if (longValue.isPresent()){
            return longValue.get();
        }
        //a sends an error directly to discord!
        throw new SlashCommandArgsNotFound("Missing args Long value");
    }
    public ServerChannel getChannelValue(int index) throws SlashCommandArgsNotFound {
        Optional<ServerChannel> channelValue = args.get(index).getChannelValue();
        if (channelValue.isPresent()){
            return channelValue.get();
        }
        //a sends an error directly to discord!
        throw new SlashCommandArgsNotFound("Missing args Channel value");
    }
    public Boolean getBoolValue(int index) throws SlashCommandArgsNotFound {
        Optional<Boolean> booleanValue = args.get(index).getBooleanValue();
        if (booleanValue.isPresent()){
            return booleanValue.get();
        }
        //a sends an error directly to discord!
        throw new SlashCommandArgsNotFound("Missing args boolean value");
    }
     public Attachment getAttachmentValue(int index) throws SlashCommandArgsNotFound {
         Optional<Attachment> attachmentValue = args.get(index).getAttachmentValue();
        if (attachmentValue.isPresent()){
            return attachmentValue.get();
        }
        //a sends an error directly to discord!
        throw new SlashCommandArgsNotFound("Missing args attachment value");
    }


}
