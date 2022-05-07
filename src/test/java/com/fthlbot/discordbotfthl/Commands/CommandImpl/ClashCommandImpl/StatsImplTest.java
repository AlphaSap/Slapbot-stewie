package com.fthlbot.discordbotfthl.Commands.CommandImpl.ClashCommandImpl;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StatsImplTest {

    @Test
    void calculateAverageMinutes() {
        StatsImpl stats = new StatsImpl();

        List<Integer> minutes = List.of(60, 60, 60, 60, 60);
        int s = stats.calculateAverageMinutes(minutes);
        System.out.println(s);
        String s1 = stats.convertSecondsToMinutes(s);
        System.out.println(s1);
    }
}