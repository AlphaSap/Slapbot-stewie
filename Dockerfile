FROM openjdk:17

COPY build/libs/DiscordBotFTHL-0.0.1-SNAPSHOT.jar discordbot.jar

ENTRYPOINT ["java", "-jar", "discordbot.jar"]

