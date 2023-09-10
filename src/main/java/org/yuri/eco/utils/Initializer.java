package org.yuri.eco.utils;

import common.commands.tpa.TpaRequest;
import net.luckperms.api.LuckPerms;
import org.bukkit.Color;
import org.yuri.eco.AestheticNetwork;

import java.util.*;

public class Initializer {
    public static final Map<String, Long> playerstoteming = new HashMap<>();
    public static final ArrayList<TpaRequest> requests = new ArrayList<>();
    public static final List<Color> color = List.of(Color.LIME,
            Color.ORANGE,
            Color.RED,
            Color.BLUE,
            Color.OLIVE,
            Color.PURPLE,
            Color.WHITE,
            Color.AQUA,
            Color.BLACK,
            Color.FUCHSIA,
            Color.GRAY,
            Color.GREEN,
            Color.MAROON,
            Color.NAVY,
            Color.SILVER,
            Color.TEAL,
            Color.YELLOW);
    public static final Map<String, Long> cooldowns = new HashMap<>();
    public static final Map<String, String> lastReceived = new HashMap<>();
    public static final ArrayList<String> tpa = new ArrayList<>();
    public static final ArrayList<String> msg = new ArrayList<>();
    public static AestheticNetwork p;
    public static LuckPerms lp;
    public static boolean chatlock = false;
}
