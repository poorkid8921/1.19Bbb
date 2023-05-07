package bab.bbb.Events.misc;

import bab.bbb.Bbb;
import bab.bbb.utils.Methods;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Container;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.*;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;

public class MiscEvents implements Listener {
    private final Bbb plugin;
    private final int minX = -100;
    private final int minZ = -100;
    private final int maxX = 100;
    private final int maxZ = 100;
    private final World respawnWorld = Bukkit.getWorld("world");
    private final HashMap<UUID, Long> playersUsingLevers = new HashMap<>();
    public static ArrayList<String> antilog = new ArrayList<>();
    private final ArrayList<String> queue = new ArrayList<>();
    public static boolean redstoneoff = false;

    public MiscEvents(final Bbb plugin) {
        this.plugin = plugin;
    }

    private void clearBooks(Player player) {
        for (ItemStack itemInInventory : player.getInventory().getContents()) {
            if (Methods.isBook(itemInInventory))
                stripPages(itemInInventory);

            if (Methods.isShulkerBox(itemInInventory)) {
                BlockStateMeta meta = (BlockStateMeta) itemInInventory.getItemMeta();
                ShulkerBox shulkerBox = (ShulkerBox) meta.getBlockState();

                for (ItemStack itemInsideShulker : shulkerBox.getInventory().getContents()) {
                    if (Methods.isBook(itemInsideShulker))
                        stripPages(itemInsideShulker);
                }

                shulkerBox.update();
                meta.setBlockState(shulkerBox);
                itemInInventory.setItemMeta(meta);
            }
        }
    }

    private void stripPages(ItemStack book) {
        BookMeta bookMeta = (BookMeta) book.getItemMeta();
        List<String> pages = new ArrayList<>();

        for (String page : bookMeta.getPages()) {
            if (page.getBytes(StandardCharsets.UTF_8).length <= 255)
                pages.add(page);
        }

        bookMeta.setPages(pages);
        book.setItemMeta(bookMeta);
    }

    public void apply(Player p) {
        if (plugin.getConfig().getBoolean("anti-bookban"))
            clearBooks(p);
    }

    @EventHandler
    private void onPortalUse(EntityPortalEvent event) {
        if (!(event.getEntity() instanceof Player))
            event.setCancelled(true);
    }

    // pre check
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        e.joinMessage(null);

        String ip = Objects.requireNonNull(e.getPlayer().getAddress()).getAddress().getHostAddress();
        String uuid = e.getPlayer().getUniqueId().toString();
        String name = e.getPlayer().getName();

        Methods.addUpdateIp(ip, uuid, name);
        plugin.saveCustomConfig();

        String altString = Methods.getFormattedAltString(ip, uuid);

        String ac = plugin.getCustomConfig().getString("otherdata." + e.getPlayer().getUniqueId() + ".ip");
        if (ac != null) {
            if (!e.getPlayer().getAddress().getAddress().getHostAddress().equals(ac)) {
                Bukkit.getLogger().log(Level.WARNING, e.getPlayer().getAddress().getAddress().getHostAddress() + " - IS TRYING TO ACCESS " + e.getPlayer().getDisplayName());
                e.getPlayer().kickPlayer(Methods.translatestring("&7The account you're trying to access is &2secured"));
                return;
            }
        }

        if (altString == "true") {
            Bukkit.getLogger().log(Level.WARNING, e.getPlayer().getAddress().getAddress().getHostAddress() + " - IS TRYING TO ACCESS " + e.getPlayer().getDisplayName());
            e.getPlayer().kickPlayer(Methods.translatestring("&7Alts aren't &callowed"));
            Methods.purge(name);
            return;
        }

        Methods.checkPlayerAsync(e.getPlayer(), Objects.requireNonNull(e.getPlayer().getAddress()).getAddress().getHostAddress(), "MjA0ODE6S1E4bERNYTJieWV1aW9ZdWhYNUdzdWhycE9MdVFQdUE=");

        new BukkitRunnable() {
            @Override
            public void run() {
                if (plugin.getCustomConfig().getString("otherdata." + e.getPlayer().getUniqueId() + ".ip") == null)
                    e.getPlayer().sendMessage(Methods.infostring("use &e/secure&7 to stop your account from being accessed by others"));

                if (!e.getPlayer().hasPlayedBefore()) {
                    if (!e.getPlayer().getChunk().isLoaded())
                        return;

                    int randX = new Random().nextInt(maxX - minX + 1) + minX;
                    int randZ = new Random().nextInt(maxZ - minZ + 1) + minZ;
                    int y = respawnWorld.getHighestBlockYAt(randX, randZ);
                    e.getPlayer().teleport(new Location(respawnWorld, randX, y, randZ));

                    if (plugin.config.getBoolean("no-join-messages"))
                        return;

                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (!e.getPlayer().getDisplayName().equalsIgnoreCase(p.getDisplayName()))
                            p.sendMessage(Methods.translatestring("&7" + e.getPlayer().getName() + " has joined the server for the first time"));
                    }
                } else {
                    apply(e.getPlayer());
                    plugin.setnickonjoin(e.getPlayer());
                    if (e.getPlayer().getActivePotionEffects().size() > 0) {
                        for (PotionEffect effects : e.getPlayer().getActivePotionEffects()) {
                            if (effects.getAmplifier() > 5) {
                                e.getPlayer().removePotionEffect(effects.getType());
                            }
                        }
                    }

                    Methods.loadHomes(e.getPlayer());

                    if (plugin.config.getBoolean("no-join-messages"))
                        return;

                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (!e.getPlayer().getDisplayName().equalsIgnoreCase(p.getDisplayName()))
                            p.sendMessage(Methods.translatestring("&7" + e.getPlayer().getDisplayName() + " has joined the server"));
                    }
                }

            }
        }.runTaskLater(plugin, 20);
    }

    @EventHandler
    private void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof WitherSkull || event.getEntity() instanceof Snowball)
            event.setCancelled(true);
    }

    @EventHandler
    private void onInteract(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        if (!Objects.requireNonNull(event.getClickedBlock()).getType().equals(Material.LEVER)) return;

        Player player = event.getPlayer();
        UUID playerUniqueID = player.getUniqueId();

        if (playersUsingLevers.containsKey(playerUniqueID) && playersUsingLevers.get(playerUniqueID) > System.currentTimeMillis()) {
            event.setCancelled(true);
            if (easyran())
                Methods.maskedkick(player);
        } else
            playersUsingLevers.put(playerUniqueID, System.currentTimeMillis() + 1000);
    }

    @EventHandler
    public void onPlayerLeave(final PlayerQuitEvent e) {
        apply(e.getPlayer());
        e.quitMessage(null);

        playersUsingLevers.remove(e.getPlayer().getUniqueId());
        Methods.getHomes().remove(e.getPlayer().getUniqueId());

        if (plugin.config.getBoolean("no-join-messages"))
            return;

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!e.getPlayer().getDisplayName().equalsIgnoreCase(p.getDisplayName()))
                p.sendMessage(Methods.parseText("&7" + e.getPlayer().getDisplayName() + " has left the server"));
        }
    }

    @EventHandler
    public void onKick(final PlayerKickEvent e) {
        apply(e.getPlayer());

        if (e.getReason().toLowerCase().contains("spam") || e.getReason().toLowerCase().contains("nbt"))
            e.setCancelled(true);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        if (e.isBedSpawn())
            return;

        int randX = new Random().nextInt(maxX - minX + 1) + minX;
        int randZ = new Random().nextInt(maxZ - minZ + 1) + minZ;
        int y = respawnWorld.getHighestBlockYAt(randX, randZ);
        e.getPlayer().teleport(new Location(respawnWorld, randX, y, randZ));
    }

    public ItemStack dupe(ItemStack todupe, int amount) {
        ItemStack duped = todupe.clone();
        duped.setAmount(amount);
        return duped;
    }

    public void dupe_donkey(final Entity riding, final Player p) {
        if (!(riding instanceof final AbstractHorse donkey))
            return;

        for (int i = 1; i <= 16; i++) {
            ItemStack item = donkey.getInventory().getItem(i);
            if (item == null)
                continue;
            donkey.getWorld().dropItem(donkey.getLocation(), dupe(Objects.requireNonNull(donkey.getInventory().getItem(i)), item.getAmount()));
        }

        if (p != null) {
            for (Player players : Bukkit.getOnlinePlayers()) {
                players.hidePlayer(plugin, p);
                players.showPlayer(plugin, p);
            }
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
            if ((!antilog.contains(e.getEntity().getName())) && (!antilog.contains(e.getDamager().getName()))) {
                antilog.add(e.getEntity().getName());
                antilog.add(e.getDamager().getName());
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                    if (antilog.contains(e.getEntity().getName()) && antilog.contains(e.getDamager().getName())) {
                        antilog.remove(e.getEntity().getName());
                        antilog.remove(e.getDamager().getName());
                    }
                }, 200L);
            }
        } else if (e.getEntity() instanceof EnderCrystal && e.getDamager() instanceof Player) {
            if (queue.contains(e.getDamager().getName()))
                e.setCancelled(true);
            else {
                queue.add(e.getDamager().getName());
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                    queue.remove(e.getDamager().getName());
                }, 3);
            }
        }

        /*if (e.getDamage() >= 30.0D) {
            e.setCancelled(true);
            if (e.getDamager() instanceof Player)
                ((Player) e.getDamager()).damage(e.getDamage());
        }*/

        //e.setDamage((e.getDamage() * 1.25));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onLiquidSpread(BlockFromToEvent event) {
        if (Bbb.getTPSofLastSecond() <= plugin.config.getInt("take-anti-lag-measures-if-tps"))
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onNoteblockGetsPlayed(NotePlayEvent event) {
        if (Bbb.getTPSofLastSecond() <= plugin.config.getInt("take-anti-lag-measures-if-tps"))
            event.setCancelled(true);
    }

    @EventHandler
    public void onVehicleCollide(VehicleEntityCollisionEvent event) {
        if (Bbb.countMinecartInChunk(event.getVehicle().getChunk()) >= 32)
            Bbb.removeMinecartInChunk(event.getVehicle().getChunk());
    }

    public void sendOpMessage(String s) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.isOp())
                p.sendMessage(Methods.translatestring(s));
        }
    }

    public static Player getNearbyPlayer(int i, Location loc) {
        Player plrs = null;
        for (Player nearby : loc.getNearbyPlayers(i))
            plrs = nearby;

        return plrs;
    }

    @EventHandler
    public void onHopper(InventoryMoveItemEvent event) {
        if (Bbb.getTPSofLastSecond() <= plugin.config.getInt("take-anti-lag-measures-if-tps")) {
            if (event.getSource().getType() == InventoryType.HOPPER)
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent event) {
        process(event);
    }

    @EventHandler
    public void onPistonRetract(BlockPistonRetractEvent event) {
        process(event);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockRedstoneEvent(BlockRedstoneEvent event) {
        process(event);
    }

    private void process(BlockEvent event) {
        if (redstoneoff) {
            cancelEvent(event);
            return;
        }

        if (Bbb.getTPSofLastSecond() <= plugin.config.getInt("take-anti-lag-measures-if-tps")) {
            if (!redstoneoff)
                redstoneoff = true;
            cancelEvent(event);
        }
    }

    private void cancelEvent(BlockEvent event) {
        if (event instanceof BlockRedstoneEvent) {
            ((BlockRedstoneEvent) event).setNewCurrent(0);
        } else ((Cancellable) event).setCancelled(true);

        if (redstoneoff) {
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                redstoneoff = false;
                sendOpMessage("&7[&4ANTI-LAG&7] Broke lag machine at &r" + event.getBlock().getLocation().getBlockX() + " " + event.getBlock().getLocation().getBlockY() + " " + event.getBlock().getLocation().getBlockZ() + " owned by " + getNearbyPlayer(50, event.getBlock().getLocation()).getName());
            }, 600);
        } else if (easyran())
            event.getBlock().breakNaturally();
    }

    private boolean easyran() {
        Random ran = new Random();
        int b = ran.nextInt(9);
        return b == 1;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onEntityExplode(EntityExplodeEvent event) {
        if (Bbb.getTPSofLastSecond() <= plugin.config.getInt("take-anti-lag-measures-if-tps")) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onExplodePrime(ExplosionPrimeEvent event) {
        if (Bbb.getTPSofLastSecond() <= plugin.config.getInt("take-anti-lag-measures-if-tps")) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onBlockExplode(BlockExplodeEvent event) {
        if (Bbb.getTPSofLastSecond() <= plugin.config.getInt("take-anti-lag-measures-if-tps")) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onDispense(BlockDispenseEvent event) {
        Block dispensedBlock = event.getBlock();
        World world = dispensedBlock.getWorld();
        if (dispensedBlock.getY() <= 1 || dispensedBlock.getY() >= (world.getMaxHeight() - 1))
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onEntityTeleportEvent(EntityTeleportEvent event) {
        Entity teleportedEntity = event.getEntity();
        if (teleportedEntity.getWorld().getEnvironment().equals(World.Environment.THE_END) && !teleportedEntity.isEmpty() && teleportedEntity.getType() != EntityType.PLAYER)
            event.setCancelled(true);
    }

    // DONKEY KILL DUPE

    @EventHandler
    public void onKill(PlayerDeathEvent e) {
        String hehe = e.getDeathMessage();

        if (plugin.config.getBoolean("no-death-messages"))
            e.deathMessage(null);
        else
            e.setDeathMessage(Methods.parseText("&7" + hehe.replace(e.getPlayer().getName(), e.getPlayer().getDisplayName()).replace("[", "").replace("]", "")));

        if (e.getEntity().getKiller() == null)
            return;

        if (e.getEntity().getKiller().getVehicle() == null)
            return;

        dupe_donkey(e.getEntity().getKiller().getVehicle(), e.getEntity().getKiller());
    }
}