package org.yuri.aestheticnetwork.utils;

import net.intelie.tinymap.TinyList;
import net.intelie.tinymap.TinyListBuilder;
import net.intelie.tinymap.TinyMap;
import net.intelie.tinymap.TinyMapBuilder;
import net.luckperms.api.LuckPerms;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.yuri.aestheticnetwork.AestheticNetwork;
import org.yuri.aestheticnetwork.commands.duel.DuelRequest;

import java.util.UUID;

public class Initializer {
    public static Location ffa;
    public static Location flat;
    public static Location lflat;
    public static Location nethpot;
    public static Location spawn;
    public static boolean hasReset = false;
    public static int ffastr = 1, flatstr = 1, nethstr = 1;
    public static Economy econ;
    public static LuckPerms lp;
    public static AestheticNetwork p;

    // TinyMap wrapper
    static TinyMapBuilder<UUID, Long> long_builder = TinyMap.builder();
    public static final TinyMap<UUID, Long> cooldown = long_builder.build();
    public static final TinyMap<UUID, Long> playerstoteming = long_builder.build();
    public static final TinyMap<UUID, Long> chatdelay = long_builder.build();
    static TinyMapBuilder<UUID, Integer> int_builder = TinyMap.builder();
    public static final TinyMap<UUID, Integer> teams = int_builder.build();
    static TinyMapBuilder<UUID, UUID> uuid_builder = TinyMap.builder();
    public static final TinyMap<UUID, UUID> lastReceived = uuid_builder.build();

    // TinyList wrapper
    static TinyListBuilder<Color> color_list_builder = TinyList.builder();
    public static TinyList<Color> color = color_list_builder.build();

    static TinyListBuilder<EntityType> entity_list_builder = TinyList.builder();
    public static TinyList<EntityType> entities = entity_list_builder.build();

    static TinyListBuilder<DuelRequest> duel_list_builder = TinyList.builder();
    public static TinyList<DuelRequest> duel = duel_list_builder.build();

    static TinyListBuilder<UUID> uuid_list_builder = TinyList.builder();
    public static TinyList<UUID> valid = uuid_list_builder.build();

    static TinyListBuilder<String> string_list_builder = TinyList.builder();
    public static TinyList<String> tpa = string_list_builder.build();
    public static TinyList<String> msg = string_list_builder.build();
    static TinyListBuilder<Player> player_list_builder = TinyList.builder();
    public static TinyList<Player> ffaconst = player_list_builder.build();
}
