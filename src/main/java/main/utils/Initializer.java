package main.utils;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import main.Economy;
import main.utils.instances.CustomPlayerDataHolder;
import net.luckperms.api.LuckPerms;
import org.bukkit.Color;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

public class Initializer {
    public static ImmutableList<Color> color = ImmutableList.of(Color.LIME,
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
    public static Map<String, Integer> bukkitTasks = new Object2ObjectOpenHashMap<>();
    public static Map<String, Long> cooldowns = new Object2ObjectOpenHashMap<>();
    public static Map<String, String> lastReceived = new Object2ObjectOpenHashMap<>();
    public static Map<Integer, Location> crystalsToBeOptimized = new Object2ObjectOpenHashMap<>();
    public static Map<String, CustomPlayerDataHolder> playerData = new Object2ObjectOpenHashMap<>();
    public static ObjectArrayList<TpaRequest> requests = new ObjectArrayList<>();
    public static ObjectArrayList<String> tpa = new ObjectArrayList<>();
    public static ObjectArrayList<String> msg = new ObjectArrayList<>();
    public static Economy p;
    public static LuckPerms lp;
    public static Location spawn;
    public static Location nethpot;
    public static net.milkbowl.vault.economy.Economy economy;
    public static ThreadLocalRandom RANDOM = ThreadLocalRandom.current();
}
