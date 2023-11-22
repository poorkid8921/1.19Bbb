package main;

import commands.Discord;
import commands.Report;
import commands.Spawn;
import commands.deprecated.Stats;
import commands.chat.Msg;
import commands.chat.MsgLock;
import commands.chat.Reply;
import commands.chat.TpaLock;
import commands.tpa.*;
import main.expansions.arenas.Arena;
import main.expansions.arenas.ArenaIO;
import main.expansions.arenas.commands.CreateCommand;
import main.expansions.arenas.Section;
import main.utils.Initializer;
import main.utils.Languages;
import main.utils.Utils;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
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
import java.util.Collection;

import static main.utils.Initializer.economy;
import static main.utils.Utils.lootDrop;

public class Economy extends JavaPlugin implements CommandExecutor, TabExecutor {
    public static FileConfiguration cc;
    public static File cf;
    public static File df;

    public void saveCustomConfig() {
        try {
            cc.save(cf);
        } catch (IOException ignored) {
        }
    }

    @Override
    public void onEnable() {
        df = getDataFolder();
        cf = new File(df, "data.yml");
        cc = YamlConfiguration.loadConfiguration(cf);
        Initializer.p = this;
        saveConfig();

        if (!cf.exists()) this.saveCustomConfig();

        Initializer.lp = Bukkit.getServicesManager().getRegistration(LuckPerms.class).getProvider();
        economy = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class).getProvider();
        Bukkit.getPluginManager().registerEvents(new Events(), this);

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            Utils.d.getEntities().stream()
                    .filter(r -> r instanceof EnderCrystal)
                    .forEach(Entity::remove);

            Arena ffa = Arena.arenas.get("ffa");
            Arena.ResetLoopinData data = new Arena.ResetLoopinData();
            data.speed = 50000;
            for (Section s : ffa.getSections()) {
                int sectionAmount = (int) ((double) 50000 / (double) (ffa.getc2().getBlockX() - ffa.getc1().getBlockX() + 1) * (ffa.getc2().getBlockY() - ffa.getc1().getBlockY() + 1) * (ffa.getc2().getBlockZ() - ffa.getc1().getBlockZ() + 1) * (double) s.getTotalBlocks());
                if (sectionAmount <= 0) sectionAmount = 1;
                data.sections.put(s.getID(), sectionAmount);
                data.sectionIDs.add(s.getID());
            }

            boolean ffaresetted;
            boolean ffaupresetted;
            do {
                ffaresetted = true;

                ffa = Arena.arenas.get("ffa" + Initializer.RANDOM.nextInt(2) + 1);
                data = new Arena.ResetLoopinData();
                data.speed = 50000;
                for (Section s : ffa.getSections()) {
                    int sectionAmount = (int) ((double) 50000 / (double) (ffa.getc2().getBlockX() - ffa.getc1().getBlockX() + 1) * (ffa.getc2().getBlockY() - ffa.getc1().getBlockY() + 1) * (ffa.getc2().getBlockZ() - ffa.getc1().getBlockZ() + 1) * (double) s.getTotalBlocks());
                    if (sectionAmount <= 0) sectionAmount = 1;
                    data.sections.put(s.getID(), sectionAmount);
                    data.sectionIDs.add(s.getID());
                }

                do {
                    ffaupresetted = true;
                    Collection<? extends Player> p = Bukkit.getOnlinePlayers();
                    for (Player a : p) {
                        if (a.isGliding())
                            continue;

                        Location c = a.getLocation();
                        c.setY(319);
                        if (Utils.d.getBlockAt(c).getType() != Material.BARRIER)
                            continue;

                        c.setY(135);
                        a.teleportAsync(c);
                    }

                    int size = p.size();
                    if (p.size() > 10) {
                        int divided = size / 10;
                        if (divided == 1) {
                            lootDrop();
                        } else
                            lootDrop(divided);
                    }
                } while (!ffa.loopyReset(data) && !ffaupresetted);
            } while (!ffa.loopyReset(data) && !ffaresetted);
        }, 0L, 21000L);
        // CHAT
        this.getCommand("msg").setExecutor(new Msg());
        this.getCommand("reply").setExecutor(new Reply());

        // TPA
        this.getCommand("tpa").setExecutor(new Tpa());
        this.getCommand("tpaall").setExecutor(new TpaAll());
        this.getCommand("tpaccept").setExecutor(new Tpaccept());
        this.getCommand("tpahere").setExecutor(new Tpahere());
        this.getCommand("tpdeny").setExecutor(new TpDeny());

        this.getCommand("tpa").setTabCompleter(new TabTPA());
        this.getCommand("tpaccept").setTabCompleter(new TabTPA());
        this.getCommand("tpahere").setTabCompleter(new TabTPA());

        // OTHER
        this.getCommand("report").setExecutor(new Report());

        this.getCommand("msglock").setExecutor(new MsgLock());
        this.getCommand("tpalock").setExecutor(new TpaLock());

        this.getCommand("stats").setExecutor(new Stats());
        this.getCommand("acreate").setExecutor(new CreateCommand());

        this.getCommand("spawn").setExecutor(new Spawn());
        this.getCommand("discord").setExecutor(new Discord());

        File folder = new File(getDataFolder(), "Arenas");
        if (!folder.exists()) folder.mkdirs();

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

        Languages.init();
    }
}