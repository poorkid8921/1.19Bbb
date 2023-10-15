package main.utils;

import io.netty.util.internal.ThreadLocalRandom;
import main.Practice;
import main.utils.Instances.BackHolder;
import main.utils.Instances.DuelHolder;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Initializer {
    public static Map<String, Long> chatdelay = new HashMap<>();
    public static Map<String, Integer> teams = new HashMap<>();
    public static Map<String, String> spec = new HashMap<>();
    public static Map<String, Integer> inMatchmaking = new HashMap<>();
    public static ArrayList<DuelHolder> inDuel = new ArrayList<>();
    public static Map<String, BackHolder> back = new HashMap<>();
    public static Map<String, String> lastReceived = new HashMap<>();
    public static Location ffa;
    public static Location flat;
    public static Location spawn;
    public static Practice p;

    public static ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    public static List<Color> color = List.of(org.bukkit.Color.LIME, org.bukkit.Color.ORANGE, org.bukkit.Color.RED, org.bukkit.Color.BLUE, org.bukkit.Color.OLIVE, org.bukkit.Color.PURPLE, org.bukkit.Color.WHITE, org.bukkit.Color.AQUA, org.bukkit.Color.BLACK, org.bukkit.Color.FUCHSIA, org.bukkit.Color.GRAY, org.bukkit.Color.GREEN, org.bukkit.Color.MAROON, org.bukkit.Color.NAVY, org.bukkit.Color.SILVER, org.bukkit.Color.TEAL, org.bukkit.Color.YELLOW);

    public static ArrayList<String> tpa = new ArrayList<>();
    public static ArrayList<String> msg = new ArrayList<>();
    public static ArrayList<Player> inFFA = new ArrayList<>();

    public static ArrayList<DuelHolder> duel = new ArrayList<>();
    public static ArrayList<String> valid = new ArrayList<>();
}
