package main.utils;

import com.google.common.collect.ImmutableList;
import io.netty.util.internal.ThreadLocalRandom;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import main.Practice;
import main.utils.Instances.BackHolder;
import main.utils.Instances.DuelHolder;
import main.utils.Instances.LocationHolder;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Initializer {
    public static Map<String, Integer> teams = new Object2ObjectOpenHashMap<>();
    public static Map<String, String> spec = new Object2ObjectOpenHashMap<>();
    public static Map<String, Integer> inMatchmaking = new Object2ObjectOpenHashMap<>();
    public static Map<String, BackHolder> back = new Object2ObjectOpenHashMap<>();
    public static Map<String, String> lastReceived = new Object2ObjectOpenHashMap<>();
    public static Map<Integer, ArmorStand> hologramsCreated = new Object2ObjectOpenHashMap<>();
    public static ObjectArrayList<DuelHolder> inDuel = new ObjectArrayList<>();
    public static ObjectArrayList<String> bannedFromflat = new ObjectArrayList<>();
    public static ObjectArrayList<String> tpa = new ObjectArrayList<>();
    public static ObjectArrayList<String> msg = new ObjectArrayList<>();
    public static ObjectArrayList<Player> inFFA = new ObjectArrayList<>();
    public static ObjectArrayList<DuelHolder> duel = new ObjectArrayList<>();
    public static ObjectArrayList<String> valid = new ObjectArrayList<>();
    public static ObjectArrayList<String> playersRTPing = new ObjectArrayList<>();
    public static ObjectArrayList<LocationHolder> overworldRTP = new ObjectArrayList<>();
    public static ObjectArrayList<LocationHolder> endRTP = new ObjectArrayList<>();
    public static ImmutableList<Color> color = ImmutableList.of(org.bukkit.Color.LIME, org.bukkit.Color.ORANGE, org.bukkit.Color.RED, org.bukkit.Color.BLUE, org.bukkit.Color.OLIVE, org.bukkit.Color.PURPLE, org.bukkit.Color.WHITE, org.bukkit.Color.AQUA, org.bukkit.Color.BLACK, org.bukkit.Color.FUCHSIA, org.bukkit.Color.GRAY, org.bukkit.Color.GREEN, org.bukkit.Color.MAROON, org.bukkit.Color.NAVY, org.bukkit.Color.SILVER, org.bukkit.Color.TEAL, org.bukkit.Color.YELLOW);
    public static Location ffa;
    public static Location flat;
    public static Location spawn;
    public static Practice p;
    public static Chat chat;
    public static ThreadLocalRandom RANDOM = ThreadLocalRandom.current();
    public static ExecutorService THREAD = Executors.newFixedThreadPool(1);
}
