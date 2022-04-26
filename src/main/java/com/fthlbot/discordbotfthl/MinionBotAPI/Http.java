package com.fthlbot.discordbotfthl.MinionBotAPI;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Http {
    protected String get(String url) {
        HttpClient.Builder client = HttpClient.newBuilder();

        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .header("accept", "application/json")
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
