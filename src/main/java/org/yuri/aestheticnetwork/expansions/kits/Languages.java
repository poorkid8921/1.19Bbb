package org.yuri.aestheticnetwork.expansions.kits;

import java.util.List;

public class Languages {
    public static String PREFIX;
    public static List<String> MOTD;

    public static void init() {
        PREFIX = "§4▪ §7";
        MOTD = List.of("§7---------------------------------------",
                "§7ᴛʏᴘᴇ §c/kit §7ᴏʀ §c/k §7ᴛᴏ ɢᴇᴛ ꜱᴛᴀʀᴛᴇᴅ",
                "§7---------------------------------------");
    }

    public static String getWhoLoaded(String a) {
        return PREFIX + a + " loaded a kit.";
    }

    public static String getWhoLoadedPublic(String a,
                                            String b) {
        return PREFIX + a + " loaded " + b + "'s kit.";
    }
}
