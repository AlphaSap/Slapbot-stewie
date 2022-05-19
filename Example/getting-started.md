# Getting started with Slapbot-stewie

Firstly we need to look at how the bot works and where it gets started! <br />
The entry point for our bot is `src/main/java/com/fthlbot/discordbotfthl/DiscordBotFthlApplication.java`, since we are using spring we do not have to start at the main function,
and we can ignore it, instead we can create a spring boot bean which will be executed as soon as the project starts, for that reason we can "say" the entry point for our bot is actually bean named "api" <br />

```java
public DiscordApi api(){
    return new DiscordApiBuilder()
            .setToken(token)
            .login()
            .join();
    }
}
```

## How to make a command?

If you look at the same folder as our 'Main' class file, theres a folder named 'Handlers' which handles all the commands we make,
The handler has four files, `Command.java`, `CommandListener.java`, `MessageHandlers.java`, `MessageHolder.java`, additionally there's a 
separate folder called `Annotation` in the root which has the annotation we use to make the command, `@Invoker` <br />
### Command.java
This java file is an interface which is implemented by all the commands we make. the default type for `type` 
is `CommandType.MISC`and for `where` is `AllowedChannel.ANYWHERE` <br />


```java
@Component
@Invoker(name = "ban", description = "Bans a user", usage = "/ban <@user>", type = CommandType.STAFF, where = AllowedChannel.USER)
public class BanCommand implements Command {
    @Override
    public void execute(SlashCommandCreateEvent event) {
        // do something
    }
}
```
- We also implement this class with `@Component` annotation, which makes this class a spring component.
- go to `src/main/java/com/fthlbot/discordbotfthl/Annotation/CommandType.java` to find more about the `CommandType` enum.

This method will only work if `ban` command already exists, but how do we make a new command?
I have not made an abstraction for that yet, I don't know if its worth making one. But anyway, the `Utils` package has a class called 
`SlashCommandBuilder` which is what I use to make a new command, this class is strict about the naming convention, each commadn should have a 
new method the name like this `create<commandName>Command` for out ban command it would look like this:

```java     
public void createBanCommand(){
    SlashCommand command = SlashCommand.with("ban", "Bans a user")
                .setOptions(List.of(SlashCommandOption.createWithChoices(USER,
                        "user",
                        "Enter the user you want to ban",
                        true
                ))).createGlobal(getApi()).join();
}
```
The naming convention is very important, as when we want to make all the command at once, it would quite time consuming to call each method individually,
instead we can use the `createAllCommand` method, which you probably saw the main file which was commented out, this method will run every method in the 
`SlashCommandBuilder` class with the name that starts with `create`. <br />



