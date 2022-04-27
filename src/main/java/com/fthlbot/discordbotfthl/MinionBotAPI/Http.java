package com.fthlbot.discordbotfthl.MinionBotAPI;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class Http {
    protected String get(String url) {
        HttpClient.Builder client = HttpClient.newBuilder();

        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .header("accept", "application/json")
                .timeout(Duration.of(5, ChronoUnit.SECONDS))
                .GET()
                .build();
        return client.build().sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(e -> {
                    int i = e.statusCode();
                    if (i != 200) {
                        throw new MinionBotException();
                    }
                    return e.body();
                })
                .join();
    }
}
