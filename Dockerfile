FROM openjdk:17

COPY build/distributions/DiscordBotFTHL-0.0.1-SNAPSHOT DiscordBotFTHL

WORKDIR /DiscordBotFTHL/bin

ENTRYPOINT ["./DiscordBotFTHL"]

