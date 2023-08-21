package org.yuri.aestheticnetwork.utils;

import net.luckperms.api.LuckPerms;
import org.bukkit.Color;
import org.yuri.aestheticnetwork.AestheticNetwork;
import org.yuri.aestheticnetwork.commands.tpa.TpaRequest;

import java.util.*;

public class Initializer {
    public static final List<TpaRequest> requests = new ArrayList<>();
    public static final List<Color> color = new ArrayList<>(List.of(Color.LIME,
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
            Color.YELLOW));
    public static final Map<UUID, Long> cooldowns = new HashMap<>();
    public static final Map<UUID, Long> playerstoteming = new HashMap<>();
    public static AestheticNetwork p;
    public static LuckPerms lp;
    public static Map<UUID, UUID> lastReceived = new HashMap<>();
    public static boolean chatlock = false;
    public static List<String> tpa = new ArrayList<>();
    public static List<String> msg = new ArrayList<>();
}
