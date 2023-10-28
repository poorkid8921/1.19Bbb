package main;

import main.commands.*;
import main.commands.chat.Msg;
import main.commands.chat.MsgLock;
import main.commands.chat.Reply;
import main.commands.duel.Duel;
import main.commands.duel.DuelAccept;
import main.commands.duel.DuelDeny;
import main.commands.duel.Event;
import main.commands.ess.List;
import main.commands.ess.*;
import main.commands.tpa.*;
import main.expansions.arenas.Arena;
import main.expansions.arenas.ArenaIO;
import main.expansions.arenas.CreateCommand;
import main.utils.Languages;
import main.utils.TabTPA;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static main.utils.Initializer.*;

@SuppressWarnings("deprecation")
public class Practice extends JavaPlugin implements TabExecutor {
    public static File df;
    public static FileConfiguration config;
    public static Map<File, FileConfiguration> toSave = new HashMap<>();
    public static Map<String, Map<String, Object>> kitMap = new HashMap<>();
    public static Map<Integer, ItemStack[]> kitRoomMap = new HashMap<>();
    public static ArrayList<String> editorChecker = new ArrayList<>();
    public static ArrayList<String> menuChecker = new ArrayList<>();
    public static Map<String, Integer> publicChecker = new HashMap<>();
    public static Map<String, Integer> roomChecker = new HashMap<>();
    public static File kf;
    public static FileConfiguration kfc;
    public static File krf;
    public static FileConfiguration krfc;
    private static File cf;
    int flatstr = 1;
    int ticked = 0;
    private KitClaim kc = new KitClaim();

    public static void restoreKitMap() {
        try {
            for (String key : kfc.getConfigurationSection("data").getKeys(false)) {
                kitMap.put(key, new HashMap<>());
                for (String key2 : kfc.getConfigurationSection("data." + key).getKeys(false)) {
                    switch (key2) {
                        case "items" -> {
                            ItemStack[] items = java.util.List.of(kfc.get("data." + key + "." + key2)).toArray(new ItemStack[0]);
                            kitMap.get(key).put(key2, items);
                        }
                        case "name" -> {
                            String name = kfc.get("data." + key + "." + key2).toString();
                            kitMap.get(key).put(key2, name);
                        }
                        case "player" -> {
                            String player = kfc.get("data." + key + "." + key2).toString();
                            kitMap.get(key).put(key2, player);
                        }
                        case "UUID" -> {
                            String uUID = kfc.get("data." + key + "." + key2).toString();
                            kitMap.get(key).put(key2, uUID);
                        }
                        case "public" -> {
                            String isPublic = kfc.get("data." + key + "." + key2).toString();
                            kitMap.get(key).put(key2, isPublic);
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }

    public static void restoreKitRoom() {
        for (Integer i = 1; i <= 5; ++i) {
            try {
                ItemStack[] content = java.util.List.of(krfc.get("data." + i)).toArray(new ItemStack[0]);
                kitRoomMap.put(i, content);
            } catch (Exception e) {
                kitRoomMap.put(i, new ItemStack[45]);
            }
        }
    }

    public void saveCustomConfig() {
        try {
            config.save(cf);
        } catch (IOException ignored) {
        }
    }

    @Override
    public void onDisable() {
        long d = new Date().getTime();
        int x = 0;
        for (File p : new File(Bukkit.getWorld("world")
                .getWorldFolder()
                .getAbsolutePath() + "/stats/").listFiles()) {
            if (d - p.lastModified() > 6.048e+8) {
                x++;
                p.delete();
            }
        }

        for (File p : new File(Bukkit.getWorld("world")
                .getWorldFolder()
                .getAbsolutePath() + "/entities/").listFiles()) {
            p.delete();
        }

        Bukkit.getLogger().warning("Successfully purged " + x + " accounts.");

        kfc.set("data", null);
        if (!kitMap.isEmpty()) {
            for (Map.Entry<String, Map<String, Object>> kit : kitMap.entrySet()) {
                for (Map.Entry<String, Object> data : kit.getValue().entrySet()) {
                    kfc.set("data." + kit.getKey() + "." + data.getKey(), data.getValue());
                }
            }
        }

        try {
            kfc.save(kf);
        } catch (IOException ignored) {

        }
    }

    @Override
    public void onEnable() {
        df = getDataFolder();
        cf = new File(df, "data.yml");
        config = YamlConfiguration.loadConfiguration(cf);

        p = this;
        saveConfig();

        kf = new File(Practice.df, "kits.yml");
        krf = new File(Practice.df, "kitroom.yml");
        if (!kf.exists()) {
            try {
                kf.createNewFile();
                krf.createNewFile();
            } catch (IOException ignored) {
            }
        }
        kfc = YamlConfiguration.loadConfiguration(kf);
        krfc = YamlConfiguration.loadConfiguration(krf);
        restoreKitMap();

        chat = getServer().getServicesManager().getRegistration(Chat.class).getProvider();
        World d = Bukkit.getWorld("world");
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            if (Bukkit.getOnlinePlayers().size() > 0) {
                d.getEntities().stream()
                        .filter(r -> !(r instanceof Player))
                        .forEach(Entity::remove);

                Arena.arenas.get("flat").reset(10000);
                if (ticked++ == 3) {
                    if (flatstr++ == 6)
                        flatstr = 1;

                    //Arena.arenas.get("p_f" + flatstr).reset(30000);
                }

                if (ticked == 6) {
                    ticked = 0;
                    Arena.arenas.get("ffa").reset(30000);
                    Arena.arenas.get("ffaup").reset(30000);
                    inFFA.stream().filter(s -> !s.isGliding()).forEach(player -> {
                        Location location = player.getLocation();
                        location.setY(200);
                        Block b = d.getBlockAt(location);
                        Block b2 = d.getBlockAt(location.add(0, 1, 0));

                        b2.setType(Material.AIR, false);
                        b.setType(Material.AIR, false);
                        location.setY(d.getHighestBlockYAt(location) + 1);
                        player.teleportAsync(location).thenAccept(reason -> {
                            b.setType(Material.BARRIER, false);
                            b2.setType(Material.BARRIER, false);
                        });
                    });

                    for (Map.Entry<File, FileConfiguration> fc : toSave.entrySet()) {
                        File key = fc.getKey();
                        try {
                            fc.getValue().save(key);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        toSave.remove(key);
                    }
                }
            }
        }, 0L, 2400L);

        this.getCommand("report").setExecutor(new Report());
        this.getCommand("killeffect").setExecutor(new Killeffect());

        this.getCommand("discord").setExecutor(new Discord());

        this.getCommand("back").setExecutor(new Back());
        this.getCommand("help").setExecutor(new Help());

        this.getCommand("tpa").setExecutor(new TpaCommand());
        this.getCommand("tpaccept").setExecutor(new TpacceptCommand());
        this.getCommand("tpahere").setExecutor(new TpahereCommand());
        this.getCommand("tpdeny").setExecutor(new TpdenyCommand());

        this.getCommand("msg").setExecutor(new Msg());
        this.getCommand("reply").setExecutor(new Reply());

        this.getCommand("duel").setExecutor(new Duel());
        this.getCommand("duelaccept").setExecutor(new DuelAccept());
        this.getCommand("dueldeny").setExecutor(new DuelDeny());
        this.getCommand("event").setExecutor(new Event());

        this.getCommand("msglock").setExecutor(new MsgLock());
        this.getCommand("tpalock").setExecutor(new TpaLock());

        this.getCommand("rtp").setExecutor(new RTP());
        this.getCommand("irename").setExecutor(new ItemRename());

        this.getCommand("spawn").setExecutor(new Spawn());
        this.getCommand("ffa").setExecutor(new Ffa());
        this.getCommand("flat").setExecutor(new Flat());

        this.getCommand("warp").setExecutor(new Warp());
        this.getCommand("setwarp").setExecutor(new Setwarp());

        this.getCommand("vanish").setExecutor(new Vanish());
        this.getCommand("acreate").setExecutor(new CreateCommand());
        this.getCommand("gmc").setExecutor(new GMc());
        this.getCommand("gms").setExecutor(new GMs());
        this.getCommand("kickall").setExecutor(new Kickall());
        this.getCommand("list").setExecutor(new List());
        this.getCommand("unban").setExecutor(new Unban());
        this.getCommand("ban").setExecutor(new Ban());
        this.getCommand("tp").setExecutor(new Teleport());
        this.getCommand("tphere").setExecutor(new TeleportHere());
        this.getCommand("ss").setExecutor(new Screenshare());

        this.getCommand("kit").setExecutor(new Kit());
        this.getCommand("k1").setExecutor(kc);
        this.getCommand("k2").setExecutor(kc);
        this.getCommand("k3").setExecutor(kc);

        this.getCommand("msg").setTabCompleter(new TabMSG());
        this.getCommand("tpa").setTabCompleter(new TabTPA());
        this.getCommand("tpaccept").setTabCompleter(new TabTPA());
        this.getCommand("tpahere").setTabCompleter(new TabTPA());

        File arenaF = new File(df, "arenas");
        if (!arenaF.exists()) arenaF.mkdirs();

        File wF = new File(df, "warps");
        if (!wF.exists()) wF.mkdirs();

        Arena.arenas.clear();
        Arrays.stream(arenaF.listFiles()).parallel().forEach(r -> {
            try {
                Arena arena = ArenaIO.loadArena(r);
                if (arena == null)
                    return;
                Arena.arenas.put(arena.getName(), arena);
            } catch (Exception ignored) {
            }
        });
        Languages.init();
        main.expansions.guis.Utils.init();
        Bukkit.getPluginManager().registerEvents(new Events(), this);
    }
}