package bab.bbb.Events.misc;

import bab.bbb.Bbb;
import bab.bbb.utils.Methods;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.logging.Level;

import static bab.bbb.Bbb.checkArmorContents;
import static bab.bbb.Bbb.checkInventory;

public class MiscEvents implements Listener {
    private final Bbb plugin;
    private final int minX = -100;
    private final int minZ = -100;
    private final int maxX = 100;
    private final int maxZ = 100;
    private final World respawnWorld = Bukkit.getWorld("world");
    private final HashMap<UUID, Long> playersUsingLevers = new HashMap<>();
    public static ArrayList<String> antilog = new ArrayList<>();
    public static boolean redstoneoff = false;

    public MiscEvents(final Bbb plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    private void onPortalUse(EntityPortalEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            event.setCancelled(true);
            Methods.sendOpMessage("&7[&4ALERT&7] a " + event.getEntity().getType() + " tried to get into a portal");
        }
    }

    @EventHandler
    private void onTeleport(EntityTeleportEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            event.setCancelled(true);
            Methods.sendOpMessage("&7[&4ALERT&7] a " + event.getEntity().getType() + " tried to teleport");
        }
    }

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
                    int randX = new Random().nextInt(maxX - minX + 1) + minX;
                    int randZ = new Random().nextInt(maxZ - minZ + 1) + minZ;
                    int y = respawnWorld.getHighestBlockYAt(randX, randZ);
                    Location found = new Location(respawnWorld, randX, y, randZ);

                    if (!found.getChunk().isLoaded())
                        found.getChunk().load();
                    e.getPlayer().teleport(found);

                    if (plugin.config.getBoolean("no-join-messages"))
                        return;

                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (!e.getPlayer().getDisplayName().equalsIgnoreCase(p.getDisplayName()))
                            p.sendMessage(Methods.translatestring("&7" + e.getPlayer().getName() + " has joined the server for the first time"));
                    }
                } else {
                    plugin.setnickonjoin(e.getPlayer());
                    if (e.getPlayer().getActivePotionEffects().size() > 0) {
                        for (PotionEffect effects : e.getPlayer().getActivePotionEffects()) {
                            if (effects.getAmplifier() > 5)
                                e.getPlayer().removePotionEffect(effects.getType());
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
            Methods.sendOpMessage("&7[&4ALERT&7] stopped &e" + player.getDisplayName() + " &7from spamming levers");
            if (easyran())
                Methods.maskedkick(player);
        } else
            playersUsingLevers.put(playerUniqueID, System.currentTimeMillis() + 1000);
    }

    @EventHandler
    public void onPlayerLeave(final PlayerQuitEvent e) {
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
        if (e.getReason().equalsIgnoreCase("spam") || e.getReason().equalsIgnoreCase("nbt"))
            e.setCancelled(true);
        checkInventory(e.getPlayer().getInventory(), e.getPlayer().getLocation(), true);
        checkArmorContents(e.getPlayer().getInventory(), e.getPlayer().getLocation(), true);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        if (e.isBedSpawn())
            return;

        if (respawnWorld == null)
            return;

        int randX = new Random().nextInt(maxX - minX + 1) + minX;
        int randZ = new Random().nextInt(maxZ - minZ + 1) + minZ;
        int y = respawnWorld.getHighestBlockYAt(randX, randZ);
        Location found = new Location(respawnWorld, randX, y, randZ);
        if (!found.getChunk().isLoaded())
            found.getChunk().load();

        e.getPlayer().teleport(found);
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
        } else if (e.getEntity() instanceof EnderCrystal) {
            if (e.getEntity().getTicksLived() < 4)
                e.setCancelled(true);
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
        if (Bbb.countMinecartInChunk(event.getVehicle().getChunk()) >= 32) {
            Bbb.removeMinecartInChunk(event.getVehicle().getChunk());
            Methods.sendOpMessage("&7[&4ALERT&7] prevented too many minecarts at &e" + event.getVehicle().getChunk().getX() + "&7,&e " + event.getVehicle().getChunk().getZ());
            event.setCancelled(true);
        }
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

    @EventHandler
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
                Methods.sendOpMessage("&7[&4ALERT&7] Broke lag machine at &r" + event.getBlock().getLocation().getBlockX() + " " + event.getBlock().getLocation().getBlockY() + " " + event.getBlock().getLocation().getBlockZ() + " owned by " + Methods.getNearbyPlayer(50, event.getBlock().getLocation()).getName());
            }, 600);
        } else if (easyran())
            event.getBlock().breakNaturally();
    }

    private boolean easyran() {
        Random ran = new Random();
        int b = ran.nextInt(9);
        return b == 1;
    }

    public boolean candupe(Player e)
    {
        if (e.getPlayer().getInventory().getItemInHand().getType() == Material.TRAPPED_CHEST && e.getPlayer().getInventory().getItemInOffHand().getType() == Material.CHEST)
            return true;

        return false;
    }

    @EventHandler
    public void onVehicleEnter(PlayerInteractAtEntityEvent event) {
        if (event.getRightClicked() instanceof ChestedHorse) {
            if (candupe(event.getPlayer())) {
                ChestedHorse entity = (ChestedHorse) event.getRightClicked();
                PlayerDupeEvent playerDupeEvent = new PlayerDupeEvent(event.getPlayer(), entity.getLocation().getChunk());
                Bukkit.getServer().getPluginManager().callEvent(playerDupeEvent);
                if (!playerDupeEvent.isCancelled()) {
                    if (entity.getPassenger() == null)
                        entity.setPassenger(event.getPlayer());
                    event.setCancelled(true);
                    for (ItemStack item : entity.getInventory().getContents()) {
                        if (item != null)
                            entity.getWorld().dropItemNaturally(entity.getLocation(), item);
                    }
                    entity.setCarryingChest(false);
                }
            }
            else
            {
                event.setCancelled(true);
                Bbb.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(Bbb.getInstance(), () -> {
                    if (((ChestedHorse) event.getRightClicked()).isCarryingChest())
                        ((ChestedHorse) event.getRightClicked()).setCarryingChest(false);
                    event.getRightClicked().eject();
                }, 4L);
            }
        }
    }
    @EventHandler
    private void onEntityExplode(EntityExplodeEvent event) {
        if (Bbb.getTPSofLastSecond() <= plugin.config.getInt("take-anti-lag-measures-if-tps")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onExplodePrime(ExplosionPrimeEvent event) {
        if (Bbb.getTPSofLastSecond() <= plugin.config.getInt("take-anti-lag-measures-if-tps")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onBlockExplode(BlockExplodeEvent event) {
        if (Bbb.getTPSofLastSecond() <= plugin.config.getInt("take-anti-lag-measures-if-tps")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onDispense(BlockDispenseEvent event) {
        Block dispensedBlock = event.getBlock();
        World world = dispensedBlock.getWorld();
        if (dispensedBlock.getY() <= 1 || dispensedBlock.getY() >= (world.getMaxHeight() - 1)) {
            event.setCancelled(true);
            Methods.sendOpMessage("&7[&4ALERT&7] prevented crash at &e" + dispensedBlock.getX() + " " + dispensedBlock.getY() + " " + dispensedBlock.getZ());
        }
    }

    // DONKEY KILL DUPE

    @EventHandler
    public void onKill(PlayerDeathEvent e) {
        String hehe = e.getDeathMessage();
        String hehe2 = "";
        if (hehe != null)
            hehe2 = Methods.parseText("&7" + hehe.replace(e.getPlayer().getName(), "&6" + e.getPlayer().getDisplayName() + "&7").replace("[", "&6").replace("]", "&7").replace(e.getPlayer().getKiller().getMainHand().toString(),"&6" + e.getPlayer().getKiller().getMainHand() + "&7"));

        if (plugin.config.getBoolean("no-death-messages"))
            e.deathMessage(null);
        else
            e.setDeathMessage(hehe2);

        if (e.getEntity().getKiller() == null)
            return;

        if (e.getEntity().getKiller().getVehicle() == null)
            return;

        dupe_donkey(e.getEntity().getKiller().getVehicle(), e.getEntity().getKiller());
    }
}