package org.yuri.aestheticnetwork.utils;

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
    public static final List<DuelRequest> duel = new ArrayList<>();
    public static final Map<UUID, Integer> teams = new HashMap<>();
    public static final List<UUID> valid = new ArrayList<>();
    public static final Map<UUID, Long> cooldown = new HashMap<>();
    public static final Map<UUID, UUID> lastReceived = new HashMap<>();
    public static final List<String> tpa = new ArrayList<>();
    public static final List<String> msg = new ArrayList<>();
    public static final List<Player> ffaconst = new ArrayList<>();
    public static Location ffa;
    public static Location flat;
    public static Location lflat;
    public static Location nethpot;
    public static Location spawn;
    public static boolean hasReset = false;
    public static int ffastr = 1, flatstr = 1, nethstr = 1;
    public static List<Color> color = new ArrayList<>(List.of(Color.LIME, Color.ORANGE, Color.RED, Color.BLUE, Color.OLIVE, Color.PURPLE, Color.WHITE, Color.AQUA, Color.BLACK, Color.FUCHSIA, Color.GRAY, Color.GREEN, Color.MAROON, Color.NAVY, Color.SILVER, Color.TEAL, Color.YELLOW));
    public static Map<UUID, Long> playerstoteming = new HashMap<>();
    public static Map<UUID, Long> chatdelay = new HashMap<>();
    public static List<EntityType> entities = new ArrayList<>(List.of(EntityType.THROWN_EXP_BOTTLE,
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
            EntityType.FISHING_HOOK));
    public static Economy econ;
    public static LuckPerms lp;
    public static AestheticNetwork p;
}
