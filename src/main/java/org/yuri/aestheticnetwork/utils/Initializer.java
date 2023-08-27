package org.yuri.aestheticnetwork.utils;

import com.google.common.collect.ImmutableMap;
import net.luckperms.api.LuckPerms;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.yuri.aestheticnetwork.AestheticNetwork;
import org.yuri.aestheticnetwork.commands.duel.DuelRequest;

import java.util.*;

public class Initializer {
    public static Location ffa;
    public static Location flat;
    public static Location lflat;
    public static Location spawn;
    public static Economy econ;
    public static LuckPerms lp;
    public static AestheticNetwork p;

    public static final Map<UUID, Long> cooldown = new HashMap<>();
    public static final Map<UUID, Long> chatdelay = new HashMap<>();
    public static final Map<UUID, Integer> teams = new HashMap<>();
    public static final Map<UUID, UUID> lastReceived = new HashMap<>();

    // TinyList wrapper
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
    public static List<EntityType> entities = List.of(EntityType.THROWN_EXP_BOTTLE,
            EntityType.PLAYER,
            EntityType.SPLASH_POTION,
            EntityType.LIGHTNING,
            EntityType.ARROW,
            EntityType.DROPPED_ITEM,
            EntityType.ENDER_CRYSTAL,
            EntityType.FALLING_BLOCK,
            EntityType.EXPERIENCE_ORB,
            EntityType.ARMOR_STAND,
            EntityType.ENDER_PEARL,
            EntityType.FIREWORK,
            EntityType.FISHING_HOOK);
    public static ArrayList<DuelRequest> duel = new ArrayList<>();

    public static ArrayList<UUID> valid = new ArrayList<>();
    public static ArrayList<String> tpa = new ArrayList<>();
    public static ArrayList<String> msg = new ArrayList<>();
    public static ArrayList<Player> ffaconst = new ArrayList<>();
}
