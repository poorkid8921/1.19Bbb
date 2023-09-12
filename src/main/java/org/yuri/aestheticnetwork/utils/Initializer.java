package org.yuri.aestheticnetwork.utils;

import io.netty.util.concurrent.FastThreadLocalThread;
import io.netty.util.internal.ThreadLocalRandom;
import net.luckperms.api.LuckPerms;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.yuri.aestheticnetwork.AestheticNetwork;
import org.yuri.aestheticnetwork.commands.duel.DuelRequest;
import org.yuri.aestheticnetwork.json.UserData;

import java.util.*;

public class Initializer {
    public static final Map<String, UserData> users = new HashMap<>();
    public static final Map<String, Integer> teams = new HashMap<>();
    public static final Map<String, String> lastReceived = new HashMap<>();
    // Duels
    public static final Map<String, String> spec = new HashMap<>();
    public static final Map<String, String> inMatchmaking = new HashMap<>();
    public static Location ffa;
    public static Location flat;
    public static Location lflat;
    public static Location spawn;
    public static Economy econ;
    public static LuckPerms lp;
    public static AestheticNetwork p;
    //public static ItemStack[] duelInventory;

    static ThreadLocalRandom random = ThreadLocalRandom.current();

    public static List<Color> color = List.of(org.bukkit.Color.LIME,
            org.bukkit.Color.ORANGE,
            org.bukkit.Color.RED,
            org.bukkit.Color.BLUE,
            org.bukkit.Color.OLIVE,
            org.bukkit.Color.PURPLE,
            org.bukkit.Color.WHITE,
            org.bukkit.Color.AQUA,
            org.bukkit.Color.BLACK,
            org.bukkit.Color.FUCHSIA,
            org.bukkit.Color.GRAY,
            org.bukkit.Color.GREEN,
            org.bukkit.Color.MAROON,
            org.bukkit.Color.NAVY,
            org.bukkit.Color.SILVER,
            org.bukkit.Color.TEAL,
            org.bukkit.Color.YELLOW);

    public static ArrayList<DuelRequest> duel = new ArrayList<>();
    public static ArrayList<String> valid = new ArrayList<>();
    public static ArrayList<String> tpa = new ArrayList<>();
    public static ArrayList<String> msg = new ArrayList<>();
    public static ArrayList<Player> ffaconst = new ArrayList<>();
    public static ArrayList<String> inCombat = new ArrayList<>();
}
