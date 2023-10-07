package main.utils;

import commands.TpaRequest;
import main.Economy;
import net.luckperms.api.LuckPerms;
import org.bukkit.Color;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;

public class Initializer {
    public static ArrayList<TpaRequest> requests = new ArrayList<>();
    public static List<Color> color = List.of(Color.LIME,
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
    public static Map<String, Long> cooldowns = new WeakHashMap<>();
    public static Map<String, String> lastReceived = new WeakHashMap<>();
    public static ArrayList<String> tpa = new ArrayList<>();
    public static ArrayList<String> msg = new ArrayList<>();
    public static Economy p;
    public static LuckPerms lp;
    public static boolean chatlock = false;
    public static Location spawn;
    public static net.milkbowl.vault.economy.Economy economy;
    //public static ScheduledExecutorService EXECUTOR = Executors.newScheduledThreadPool(1);
    public static ThreadLocalRandom RANDOM = ThreadLocalRandom.current();
}
