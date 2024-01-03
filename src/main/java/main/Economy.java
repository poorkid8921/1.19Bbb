package main;

import com.github.retrooper.packetevents.PacketEvents;
import main.commands.*;
import main.expansions.Anticheat;
import main.expansions.arenas.Arena;
import main.expansions.arenas.ArenaIO;
import main.expansions.arenas.Section;
import main.expansions.arenas.commands.CreateCommand;
import main.expansions.optimizer.AnimationEvent;
import main.expansions.optimizer.InteractionEvent;
import main.expansions.optimizer.LastPacketEvent;
import main.utils.Constants;
import main.utils.Languages;
import main.utils.instances.CustomPlayerDataHolder;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import static main.utils.Constants.economy;
import static main.utils.Constants.playerData;

public class Economy extends JavaPlugin implements CommandExecutor, TabExecutor {
    public static FileConfiguration config;
    public static World d;
    public File cf = new File(getDataFolder(), "data.yml");

    @Override
    public void onEnable() {
        config = YamlConfiguration.loadConfiguration(cf);
        Constants.p = this;
        saveConfig();
        Constants.lp = Bukkit.getServicesManager().getRegistration(LuckPerms.class).getProvider();
        economy = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class).getProvider();

        Bukkit.getScheduler().runTaskLater(this, () -> {
            File folder = new File(getDataFolder(), "Arenas");
            if (!folder.exists()) folder.mkdirs();

            d = Bukkit.getWorld("world");
            Arena.arenas.clear();
            Arrays.stream(folder.listFiles()).parallel().forEach(r -> {
                try {
                    Arena arena = ArenaIO.loadArena(r);
                    if (arena == null) return;
                    Arena.arenas.put(arena.getName(), arena);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            Constants.spawn = new Location(Economy.d,
                    -0.5, 142.06250, 0.5);

            Arena ffa = Arena.arenas.get("ffa");
            Arena.ResetLoopinData ffa_data = new Arena.ResetLoopinData();
            ffa_data.speed = 100000;
            for (Section s : ffa.getSections()) {
                int sectionAmount = (int) ((double) 100000 / (double) (ffa.getc2().getBlockX() - ffa.getc1().getBlockX() + 1) * (ffa.getc2().getBlockY() - ffa.getc1().getBlockY() + 1) * (ffa.getc2().getBlockZ() - ffa.getc1().getBlockZ() + 1) * (double) s.getTotalBlocks());
                if (sectionAmount <= 0) sectionAmount = 1;
                ffa_data.sections.put(s.getID(), sectionAmount);
                ffa_data.sectionIDs.add(s.getID());
            }

            Arena ffaup1 = Arena.arenas.get("ffa1");
            Arena.ResetLoopinData ffaup_data1 = new Arena.ResetLoopinData();
            ffaup_data1.speed = 20000;
            for (Section s : ffaup1.getSections()) {
                int sectionAmount = (int) ((double) 20000 / (double) (ffaup1.getc2().getBlockX() - ffaup1.getc1().getBlockX() + 1) * (ffaup1.getc2().getBlockY() - ffaup1.getc1().getBlockY() + 1) * (ffaup1.getc2().getBlockZ() - ffaup1.getc1().getBlockZ() + 1) * (double) s.getTotalBlocks());
                if (sectionAmount <= 0) sectionAmount = 1;
                ffaup_data1.sections.put(s.getID(), sectionAmount);
                ffaup_data1.sectionIDs.add(s.getID());
            }

            Arena ffaup2 = Arena.arenas.get("ffa2");
            Arena.ResetLoopinData ffaup_data2 = new Arena.ResetLoopinData();
            ffaup_data2.speed = 20000;
            for (Section s : ffaup2.getSections()) {
                int sectionAmount = (int) ((double) 20000 / (double) (ffaup2.getc2().getBlockX() - ffaup2.getc1().getBlockX() + 1) * (ffaup2.getc2().getBlockY() - ffaup2.getc1().getBlockY() + 1) * (ffaup2.getc2().getBlockZ() - ffaup2.getc1().getBlockZ() + 1) * (double) s.getTotalBlocks());
                if (sectionAmount <= 0) sectionAmount = 1;
                ffaup_data2.sections.put(s.getID(), sectionAmount);
                ffaup_data2.sectionIDs.add(s.getID());
            }

            Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
                Economy.d.getEntities().stream()
                        .filter(r -> r instanceof EnderCrystal)
                        .forEach(Entity::remove);

                boolean ffaresetted;
                boolean ffaupresetted;
                do {
                    ffaresetted = true;

                    boolean i = Constants.RANDOM.nextInt() == 1;
                    Arena.ResetLoopinData ffaup_data = i ? ffaup_data1 : ffaup_data2;
                    Arena ffaup = Arena.arenas.get(i ? "ffa1" : "ffa2");
                    do {
                        ffaupresetted = true;
                        for (Player a : Bukkit.getOnlinePlayers()) {
                            if (a.isGliding())
                                continue;

                            Location c = a.getLocation();
                            c.setY(319);
                            if (Economy.d.getBlockAt(c).getType() != Material.BARRIER)
                                continue;

                            c.setY(135);
                            a.teleportAsync(c);
                        }
                    } while (!ffaup.loopyReset(ffaup_data) && !ffaupresetted);
                } while (!ffa.loopyReset(ffa_data) && !ffaresetted);
            }, 0L, 24000L);
        }, 240L);
        this.getCommand("msg").setExecutor(new Msg());
        this.getCommand("reply").setExecutor(new Reply());
        this.getCommand("tpa").setExecutor(new Tpa());
        this.getCommand("tpaall").setExecutor(new TpaAll());
        this.getCommand("tpaccept").setExecutor(new Tpaccept());
        this.getCommand("tpahere").setExecutor(new Tpahere());
        this.getCommand("tpdeny").setExecutor(new TpDeny());
        this.getCommand("report").setExecutor(new Report());
        this.getCommand("msglock").setExecutor(new MsgLock());
        this.getCommand("tpalock").setExecutor(new TpaLock());
        this.getCommand("stats").setExecutor(new Stats());
        this.getCommand("acreate").setExecutor(new CreateCommand());
        this.getCommand("spawn").setExecutor(new Spawn());
        this.getCommand("discord").setExecutor(new Discord());
        this.getCommand("tpa").setTabCompleter(new TabTPA());
        this.getCommand("tpaccept").setTabCompleter(new TabTPA());
        this.getCommand("tpahere").setTabCompleter(new TabTPA());
        PacketEvents.getAPI().getEventManager().registerListener(new AnimationEvent());
        PacketEvents.getAPI().getEventManager().registerListener(new InteractionEvent());
        PacketEvents.getAPI().getEventManager().registerListener(new LastPacketEvent());
        PacketEvents.getAPI().getEventManager().registerListener(new Anticheat());
        PacketEvents.getAPI().init();
        Bukkit.getPluginManager().registerEvents(new Events(), this);
        Languages.init();
        Constants.init();

        if (config.contains("r")) {
            int dataLoaded = 0;
            for (String key : config.getConfigurationSection("r").getKeys(false)) {
                for (String key2 : config.getConfigurationSection("r." + key).getKeys(false)) {
                    int m = 0;
                    int t = 0;
                    switch (key2) {
                        case "m" -> m = config.getInt("r." + key + "." + key2);
                        case "t" -> t = config.getInt("r." + key + "." + key2);
                    }

                    playerData.put(key, new CustomPlayerDataHolder(m, t));
                }
            }
            Bukkit.getLogger().warning("Successfully loaded " + dataLoaded + " accounts!");
        }
    }

    @Override
    public void onDisable() {
        PacketEvents.getAPI().terminate();
        config.set("r", null);
        if (!playerData.isEmpty()) {
            for (Map.Entry<String, CustomPlayerDataHolder> entry : playerData.entrySet()) {
                CustomPlayerDataHolder value = entry.getValue();

                if (value.getM() == 0 && value.getT() == 0)
                    continue;

                String key = entry.getKey();
                config.set("r." + key + ".m", value.getM());
                config.set("r." + key + ".t", value.getT());
            }
        }
        try {
            config.save(cf);
        } catch (IOException ignored) {
        }
    }
}