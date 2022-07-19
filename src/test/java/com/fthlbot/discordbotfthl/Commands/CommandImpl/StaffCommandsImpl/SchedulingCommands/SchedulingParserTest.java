package com.fthlbot.discordbotfthl.Commands.CommandImpl.StaffCommandsImpl.SchedulingCommands;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SchedulingParserTest {

    @Test
    void testIfTheStringTurnsIntoJson(){
      String toParseString = "1838 v 1838";

        StringBuilder sb = new SchedulingParser().parseStringToJson(toParseString);
        assertEquals("[{\"home\":\"1838\",\"enemy\":\"1838\"}]", sb.toString());
    }

    @Test
    void checksString(){
        String s =  "1838 v 1838 Lefbeowb ke fkefk ekjwf";
        boolean b = new SchedulingParser().checkString(s);
        assertFalse(b);
    }

}