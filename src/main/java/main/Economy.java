package main;

import com.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import main.commands.BombRTP;
import main.commands.FastCrystals;
import main.commands.economy.Balance;
import main.commands.economy.Baltop;
import main.commands.economy.Kit;
import main.commands.economy.Pay;
import main.commands.essentials.List;
import main.commands.essentials.*;
import main.commands.tpa.*;
import main.commands.warps.*;
import main.utils.*;
import main.utils.arenas.Arena;
import main.utils.arenas.Section;
import main.utils.instances.CommandHolder;
import main.utils.instances.CustomPlayerDataHolder;
import main.utils.npcs.InteractAtNPC;
import main.utils.optimizer.InteractionListeners;
import main.utils.optimizer.LastPacketEvent;
import net.minecraft.world.level.block.Blocks;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.awt.*;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import static main.utils.Initializer.*;
import static main.utils.arenas.Utils.split;
import static org.bukkit.Bukkit.getMessenger;

public class Economy extends JavaPlugin {
    public static final Point spawnDistance = new Point(0, 0);
    private static final Point point = new Point(0, 0);
    public static File dataFolder;
    public static World d;
    public static World d0;
    public static World d1;
    private static boolean flatSwitch = false;

    private Location getRandomLoc(World w, int x, Location[] RTP) {
        Location loc = null;
        while (loc == null) {
            int boundX = Initializer.RANDOM.nextInt(-x, x);
            int boundZ = Initializer.RANDOM.nextInt(-x, x);
            if (spawnDistance.distance(boundX, boundZ) < 128)
                continue;
            for (Location loc1 : RTP) {
                if (loc1 == null)
                    break;
                point.setLocation(loc1.getBlockX(), loc1.getBlockZ());
                if (point.distance(boundX, boundZ) < 50)
                    break;
            }
            Block b = w.getHighestBlockAt(boundX, boundZ);
            if (b.isSolid())
                loc = new Location(w, boundX, b.getY() + 1, boundZ);
        }
        return loc;
    }

    private Location getNetherRandomLoc() {
        Location loc = null;
        while (loc == null) {
            int boundX = Initializer.RANDOM.nextInt(-1500, 1500);
            int boundZ = Initializer.RANDOM.nextInt(-1500, 1500);
            for (int y = 30; y < 128; y++) {
                if (d0.getBlockAt(boundX, y, boundZ).getType() == Material.AIR &&
                        d0.getBlockAt(boundX, y - 1, boundZ).isSolid()) {
                    loc = new Location(d0, boundX, y, boundZ);
                    break;
                }
            }
        }
        return loc;
    }

    private void registerCommands() {
        for (CommandHolder command : new CommandHolder[]{
                new CommandHolder("msg", new Msg()),
                new CommandHolder("reply", new Reply()),
                new CommandHolder("tpa", new Tpa()),
                new CommandHolder("tpaall", new TpaAll()),
                new CommandHolder("tpaccept", new Tpaccept()),
                new CommandHolder("tpahere", new Tpahere()),
                new CommandHolder("tpdeny", new TpDeny()),
                new CommandHolder("report", new Report()),
                new CommandHolder("msglock", new MsgLock()),
                new CommandHolder("tpalock", new TpaLock()),
                new CommandHolder("spawn", new Spawn()),
                new CommandHolder("discord", new Discord()),
                new CommandHolder("warp", new Warp()),
                new CommandHolder("setwarp", new SetWarp()),
                new CommandHolder("playtime", new PlayTime()),
                new CommandHolder("kit", new Kit()),
                new CommandHolder("rbalance", new Balance()),
                new CommandHolder("rsethome", new SetHome()),
                new CommandHolder("rhome", new Home()),
                new CommandHolder("rdelhome", new DelHome()),
                new CommandHolder("ec", new EnderChest()),
                new CommandHolder("setrank", new SetRank()),
                new CommandHolder("rgc", new CreateRegion()),
                new CommandHolder("rpay", new Pay()),
                new CommandHolder("rbaltop", new Baltop()),
                new CommandHolder("grindstone", new GrindStone()),
                new CommandHolder("list", new List()),
                new CommandHolder("broadcast", new Broadcast()),
                new CommandHolder("bombrtp", new BombRTP()),
                new CommandHolder("banip", new Ban()),
                new CommandHolder("suicide", new Suicide()),
                new CommandHolder("fastcrystals", new FastCrystals()),
                new CommandHolder("irename", new ItemRename())
        })
            getCommand(command.getName()).setExecutor(command.getClazz());
        TeleportCompleter tabCompleter = new TeleportCompleter();
        this.getCommand("tpa").setTabCompleter(tabCompleter);
        this.getCommand("tpaccept").setTabCompleter(tabCompleter);
        this.getCommand("tpahere").setTabCompleter(tabCompleter);
        this.getCommand("tpdeny").setTabCompleter(tabCompleter);

        HomeCompleter homeCompleter = new HomeCompleter();
        this.getCommand("rhome").setTabCompleter(homeCompleter);
        this.getCommand("rdelhome").setTabCompleter(homeCompleter);
    }

    private void setupArenas() {
        for (File file : new File(dataFolder, "arenas").listFiles()) {
            Arena arena;
            try {
                byte[] readBytes = Files.readAllBytes(file.toPath());
                int firstSectionSplit = ArrayUtils.indexOf(readBytes, (byte) '\u0002');
                byte[] header = Arrays.copyOfRange(readBytes, 0, firstSectionSplit);
                String headerString = new String(header, StandardCharsets.US_ASCII);
                String name = headerString.split(",")[0];
                int xx1 = Integer.parseInt(headerString.split(",")[1]);
                int yy1 = Integer.parseInt(headerString.split(",")[2]);
                int zz1 = Integer.parseInt(headerString.split(",")[3]);

                World w = Bukkit.getWorld("world");
                Location corner1 = new Location(w, xx1, yy1, zz1);

                int xx2 = Integer.parseInt(headerString.split(",")[4]);
                int yy2 = Integer.parseInt(headerString.split(",")[5]);
                int zz2 = Integer.parseInt(headerString.split(",")[6]);

                Location corner2 = new Location(w, xx2, yy2, zz2);
                int keySectionSplit = ArrayUtils.indexOf(readBytes, (byte) '\u0002', firstSectionSplit + 1);
                byte[] keyBytes = Arrays.copyOfRange(readBytes, firstSectionSplit + 1, keySectionSplit);
                java.util.List<Material> blockDataSet = new ArrayList<>();

                for (byte[] key : split(new byte[]{'\u0003'}, keyBytes)) {
                    String blockData = new String(key, StandardCharsets.US_ASCII);
                    try {
                        blockDataSet.add(Material.valueOf(blockData));
                    } catch (IllegalArgumentException e) {
                        try {
                            blockDataSet.add(Material.valueOf(blockData.split("\\[")[0]));
                        } catch (IllegalArgumentException ignored) {
                            return;
                        }
                    }
                }

                arena = new Arena(name, corner1, corner2);
                arena.setKeys(blockDataSet);
                byte[] blockBytes = Arrays.copyOfRange(readBytes, keySectionSplit + 1, readBytes.length);

                ByteBuffer bb = ByteBuffer.allocate(2);
                bb.put(blockBytes[0]);
                bb.put(blockBytes[1]);
                short sectionCount = bb.getShort(0);
                short currentSection = 0;
                blockBytes = Arrays.copyOfRange(blockBytes, 2, blockBytes.length);

                ByteBuffer buffer = ByteBuffer.allocate(blockBytes.length);
                buffer.put(blockBytes);
                buffer.position(0);

                while (currentSection < sectionCount) {
                    int x1 = buffer.getInt();
                    int y1 = buffer.getInt();
                    int z1 = buffer.getInt();
                    int x2 = buffer.getInt();
                    int y2 = buffer.getInt();
                    int z2 = buffer.getInt();
                    Location start = new Location(corner1.getWorld(), x1, y1, z1);
                    Location end = new Location(corner1.getWorld(), x2, y2, z2);
                    int left = buffer.getInt();
                    int numLeft = left / 2;

                    short[] amounts = new short[numLeft];
                    short[] types = new short[numLeft];

                    for (int i = 0; i < numLeft; i++) {
                        amounts[i] = buffer.getShort();
                        types[i] = buffer.getShort();
                    }

                    arena.getSections().add(new Section(arena, currentSection, start, end, types, amounts));
                    currentSection++;
                }
            } catch (Exception ignored) {
                arena = null;
            }
            if (arena != null) Arena.arenas.put(arena.getName(), arena);
        }
    }

    private void registerPacketListeners() {
        PacketEvents.getAPI().getEventManager().registerListeners(
                new InteractionListeners(),
                new LastPacketEvent(),
                new AutoTotem(),
                new InteractAtNPC());
        PacketEvents.getAPI().init();
    }

    @Override
    public void onLoad() {
        p = this;
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().getSettings()
                .checkForUpdates(false)
                .reEncodeByDefault(false);
        PacketEvents.getAPI().load();
    }

    @Override
    public void onEnable() {
        dataFolder = getDataFolder();
        economyHandler = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class).getProvider();
        Bukkit.getScheduler().runTaskLater(this, () -> {
            d = Bukkit.getWorld("world");
            d0 = Bukkit.getWorld("world_nether");
            d1 = Bukkit.getWorld("world_the_end");
            Utils.nmsOverworld = ((CraftWorld) d).getHandle();
            WorldBorder wb = d0.getWorldBorder();
            wb.setCenter(0D, 0D);
            wb.setSize(12500D);
            wb = d1.getWorldBorder();
            wb.setCenter(0D, 0D);
            wb.setSize(50000D);
            spawn = new Location(d, -0.5D, 140.0D, 0.5D, 90F, 0F);
            for (short i = 0; i < 101; i++) {
                if (i == 100) {
                    getCommand("rtp").setExecutor(new RTP());
                    Utils.setCuboid(
                            0, 0, 0,
                            300, 300, 300,
                            Blocks.OBSIDIAN,
                            Blocks.OBSIDIAN.defaultBlockState(),
                            false
                    );
                    Bukkit.getScheduler().scheduleSyncRepeatingTask(p, () -> {
                        for (Entity ent : d.getEntities()) {
                            if (ent instanceof EnderCrystal || ent instanceof Arrow)
                                ent.remove();
                        }
                        // flat
                        /*BlockUtil.setSectionCuboidAsynchronously(
                                new Location(d, 44, 134, 45),
                                new Location(d, -46, 136 ,24),
                                new ItemStack(Material.AIR)
                        ).thenAccept(r ->
                                BlockUtil.setSectionCuboidAsynchronously(
                                        new Location(d, 23, 134, -45),
                                        new Location(d, 44,136,23),
                                        new ItemStack(Material.AIR)
                                ).thenAccept(r1 ->
                                        BlockUtil.setSectionCuboidAsynchronously(
                                                new Location(d, -46, 134, -24),
                                                new Location(d, 22, 136, -45),
                                                new ItemStack(Material.AIR)
                                        ).thenAccept(r2 ->
                                                BlockUtil.setSectionCuboidAsynchronously(
                                                        new Location(d, -46, 136, -231),
                                                        new Location(d, -25, 134, 23),
                                                        new ItemStack(Material.AIR)
                                                ))));*/
                        if (!flatSwitch) {
                            String key;
                            for (Map.Entry<String, CustomPlayerDataHolder> D0 : playerData.entrySet()) {
                                key = D0.getKey();
                                if (Bukkit.getPlayer(key) != null)
                                    playerData.remove(key);
                            }
                            Arena.arenas.get("ffa2").reset(10000000);
                            Location loc;
                            for (Player p : Bukkit.getOnlinePlayers()) {
                                if (p.isGliding())
                                    continue;
                                loc = p.getLocation();
                                loc.setY(200);
                                if (Economy.d.getBlockAt(loc).getType() != Material.BARRIER)
                                    continue;
                                loc.setY(135);
                                p.teleport(loc);
                            }
                            // arena down
                            /*BlockUtil.setSectionCuboidAsynchronously(
                                    new Location(d, -47, 128, -111),
                                    new Location(d, -112, 3, 111),
                                    new ItemStack(Material.STONE)
                            ).thenAccept(r ->
                                    BlockUtil.setSectionCuboidAsynchronously(
                                            new Location(d, -46, 128, -111),
                                            new Location(d, 110, 3, -46),
                                            new ItemStack(Material.STONE)
                                    ).thenAccept(r1 ->
                                            BlockUtil.setSectionCuboidAsynchronously(
                                                    new Location(d, 45, 128, 111),
                                                    new Location(d, 110, 3, -45),
                                                    new ItemStack(Material.STONE)
                                            ).thenAccept(r2 ->
                                                    BlockUtil.setSectionCuboidAsynchronously(
                                                            new Location(d, -46, 128, 111),
                                                            new Location(d, 44, 3, 46),
                                                            new ItemStack(Material.STONE)
                                                    ).thenAccept(r3 -> // dirt layer
                                                            BlockUtil.setSectionCuboidAsynchronously(
                                                                    new Location(d, -47, 132, -111),
                                                                    new Location(d, -112, 129, 111),
                                                                    new ItemStack(Material.DIRT)
                                                            ).thenAccept(a ->
                                                                    BlockUtil.setSectionCuboidAsynchronously(
                                                                            new Location(d, -46, 132, -111),
                                                                            new Location(d, 110, 129, -46),
                                                                            new ItemStack(Material.DIRT)
                                                                    ).thenAccept(a1 ->
                                                                            BlockUtil.setSectionCuboidAsynchronously(
                                                                                    new Location(d, 45, 132, 111),
                                                                                    new Location(d, 110, 129, -45),
                                                                                    new ItemStack(Material.DIRT)
                                                                            ).thenAccept(a2 ->
                                                                                    BlockUtil.setSectionCuboidAsynchronously(
                                                                                            new Location(d, -46, 132, 111),
                                                                                            new Location(d, 44, 129, 46),
                                                                                            new ItemStack(Material.DIRT)
                                                                                    ).thenAccept(a3 ->
                            BlockUtil.setSectionCuboidAsynchronously(
                                    new Location(d, -47, 133, -111),
                                    new Location(d, -112, 133, 111),
                                    new ItemStack(Material.GRASS_BLOCK)
                            ).thenAccept(a4 ->
                                    BlockUtil.setSectionCuboidAsynchronously(
                                            new Location(d, -46, 133, -111),
                                            new Location(d, 110, 133, -46),
                                            new ItemStack(Material.GRASS_BLOCK)
                                    ).thenAccept(a5 ->
                                            BlockUtil.setSectionCuboidAsynchronously(
                                                    new Location(d, 45, 133, 111),
                                                    new Location(d, 110, 133, -45),
                                                    new ItemStack(Material.GRASS_BLOCK)
                                            ).thenAccept(a6 ->
                                                    BlockUtil.setSectionCuboidAsynchronously(
                                                            new Location(d, -46, 133, 111),
                                                            new Location(d, 44, 133, 46),
                                                            new ItemStack(Material.GRASS_BLOCK)
                                                    ).thenAccept(a7 ->
                            BlockUtil.setSectionCuboidAsynchronously(
                                    new Location(d, -47, 134, -111),
                                    new Location(d, -112, 136, 111),
                                    new ItemStack(Material.AIR)
                            ).thenAccept(a8 ->
                                    BlockUtil.setSectionCuboidAsynchronously(
                                            new Location(d, -46, 134, -111),
                                            new Location(d, 110, 136, -46),
                                            new ItemStack(Material.AIR)
                                    ).thenAccept(a9 ->
                                            BlockUtil.setSectionCuboidAsynchronously(
                                                    new Location(d, 45, 134, 111),
                                                    new Location(d, 110, 136, -45),
                                                    new ItemStack(Material.AIR)
                                            ).thenAccept(a10 ->
                                                    BlockUtil.setSectionCuboidAsynchronously(
                                                            new Location(d, -46, 134, 111),
                                                            new Location(d, 44, 136, 46),
                                                            new ItemStack(Material.AIR)
                                                    ).thenAccept(a11 -> {
                                                        for (Player p : Bukkit.getOnlinePlayers()) {
                                                            if (p.isGliding())
                                                                continue;
                                                            Location loc = p.getLocation();
                                                            loc.setY(200);
                                                            if (Economy.d.getBlockAt(loc).getType() != Material.BARRIER)
                                                                continue;
                                                            loc.setY(135);
                                                            p.teleport(loc);
                                                        }
                                                    }))))))))))))))));*/
                        }
                        flatSwitch = !flatSwitch;
                    }, 0L, 12000L);
                    Bukkit.getLogger().warning("Finished RTP population.");
                    return;
                }
                short finalI = i;
                Bukkit.getScheduler().runTaskLater(this, () -> {
                    overworldRTP[finalI] = getRandomLoc(d, 5000, overworldRTP);
                    netherRTP[finalI] = getNetherRandomLoc();
                    endRTP[finalI] = getRandomLoc(d1, 1500, endRTP);
                }, i);
            }
        }, 100L);
        registerCommands();
        setupArenas();
        Gui.init();
        registerPacketListeners();
        Initializer.init();
    }

    @Override
    public void onDisable() {
        PacketEvents.getAPI().terminate();
    }
}