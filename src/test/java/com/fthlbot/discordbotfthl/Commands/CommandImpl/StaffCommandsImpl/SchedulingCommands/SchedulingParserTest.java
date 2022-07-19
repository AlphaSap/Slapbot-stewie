package com.fthlbot.discordbotfthl.Commands.CommandImpl.StaffCommandsImpl.SchedulingCommands;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SchedulingParserTest {

    static String toParseString = """
              1550 v 1212
              3593 v 4174
              4508 v 5153
              4801 v 1242
              4468 v 1759
              2064 v 3137
              3444 v 1769
              2071  v 594
              3257 v 2871
              3013 v 1764
              2044 v 2348
              4103 v 5662
              1379 v 2557
              3789 v 1364
              3877 v 967
              984 v 3945""";

    @Test
    void testIfTheStringTurnsIntoJson(){

        StringBuilder sb = new SchedulingParser().parseStringToJson(toParseString);
        System.out.println(sb.toString());
        //assertEquals("[{\"home\":\"1838\",\"enemy\":\"1838\"}]", sb.toString());
    }

    @Test
    void checksString(){
        boolean b = new SchedulingParser().checkString(toParseString);
        assertFalse(b);
    }

    @Test
    void tryAgin(){
        String s = new SchedulingParser().parse("1550v1212 3593v4174 4508v5153 4801v1242 4468v1759");
        System.out.println(s);
    }

    /*
     *  @Test
     *     void tryAgin(){
     *         StringBuilder sb = new SchedulingParser().parseStringToJson("1550v1212 3593v4174 4508v5153 4801v1242 4468v1759 2064v3137 3444v1769 2071 v594 3257v2871 3013v1764 2044v2348 4103v5662 1379v2557 3789v1364 3877v967 984v3945");
     *         System.out.println(sb.toString());
     *         }
     *
     */

}