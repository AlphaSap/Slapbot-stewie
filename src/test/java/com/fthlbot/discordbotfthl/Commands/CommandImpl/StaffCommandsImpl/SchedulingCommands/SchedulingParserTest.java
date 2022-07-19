package com.fthlbot.discordbotfthl.Commands.CommandImpl.StaffCommandsImpl.SchedulingCommands;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SchedulingParserTest {

    static String toParseString = """
              1550v1212
              3593v4174
              4508v5153
              4801v1242
              4468v1759
              2064v3137
              3444v1769
              2071v594
              3257v2871
              3013v1764
              2044v2348
              4103v5662
              1379v2557
              3789v1364
              3877v967
              984v3945""";


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