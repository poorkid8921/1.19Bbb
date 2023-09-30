package main.utils.Messages;

import io.netty.util.internal.ThreadLocalRandom;
import it.unimi.dsi.fastutil.Pair;
import main.AestheticNetwork;
import main.utils.Instances.BackHolder;
import main.utils.Instances.TotemHolder;
import net.luckperms.api.LuckPerms;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Initializer {
    public static Map<String, Long> chatdelay = new WeakHashMap<>();
    public static Map<String, BackHolder> back = new WeakHashMap<>();
    public static Map<String, String> lastReceived = new WeakHashMap<>();
    public static Location ffa;
    public static Location flat;
    public static Location lflat;
    public static Location spawn;
    public static Economy econ;
    public static LuckPerms lp;
    public static AestheticNetwork p;

    public static ThreadLocalRandom random = ThreadLocalRandom.current();

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

    public static ArrayList<String> tpa = new ArrayList<>();
    public static ArrayList<String> msg = new ArrayList<>();
    public static ArrayList<Player> ffaconst = new ArrayList<>();
    public static ArrayList<String> inCombat = new ArrayList<>();
    public static Map<String, TotemHolder> playerstoteming = new WeakHashMap<>();
    public static ScheduledExecutorService EXECUTOR = Executors.newScheduledThreadPool(1);
    public static Server s = Bukkit.getServer();
}
