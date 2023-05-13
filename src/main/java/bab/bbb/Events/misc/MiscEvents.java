package bab.bbb.Events.misc;

import bab.bbb.Bbb;
import bab.bbb.utils.Utils;
import org.bukkit.*;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;

import static bab.bbb.utils.Utils.*;

@SuppressWarnings("deprecation")
public class MiscEvents implements Listener {
    private final Bbb plugin;

    public MiscEvents(final Bbb plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    private void onPortalUse(EntityPortalEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            event.setCancelled(true);
            sendOpMessage("&7[&4ALERT&7] a " + event.getEntity().getType() + " tried to get into a portal");
        }
    }

    @EventHandler
    private void onPortal(PlayerPortalEvent event) {
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            Player player = event.getPlayer();
            if (player.getLocation().getBlock().getType().equals(Material.NETHER_PORTAL)) {
                player.teleport(event.getFrom());
                player.playSound(player.getLocation(), Sound.BLOCK_PORTAL_TRAVEL, 1.0F, 1.0F);
            }
        }, 200);
    }

    @EventHandler
    private void onTeleport(EntityTeleportEvent event) {
        if (!(event.getEntity() instanceof Player) && !(event.getEntity() instanceof Enderman)) {
            event.setCancelled(true);
            sendOpMessage("&7[&4ALERT&7] a " + event.getEntity().getType() + " tried to teleport");
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        e.joinMessage(null);

        String ip = Objects.requireNonNull(e.getPlayer().getAddress()).getAddress().getHostAddress();
        String uuid = e.getPlayer().getUniqueId().toString();
        String name = e.getPlayer().getName();

        String ac = getString("otherdata." + e.getPlayer().getUniqueId() + ".secure");
        if (ac != null) {
            if (!e.getPlayer().getAddress().getAddress().getHostAddress().equals(ac)) {
                Bukkit.getLogger().log(Level.WARNING, e.getPlayer().getAddress().getAddress().getHostAddress() + " - IS TRYING TO ACCESS " + e.getPlayer().getDisplayName());
                e.getPlayer().kickPlayer(translate("&7The account you're trying to access is &2secured"));
                return;
            }
        }

        String str = getString("otherdata.nicknames");

        if (!str.contains(e.getPlayer().getName()))
            Utils.setData("otherdata.nicknames", "_" + e.getPlayer().getName() + str);

        addUpdateIp(ip, uuid, name);
        saveData();
        String altString = getFormattedAltString(ip, uuid);

        if (Objects.equals(altString, "true") && !name.equals("Gr1f")) {
            Bukkit.getLogger().log(Level.WARNING, e.getPlayer().getAddress().getAddress().getHostAddress() + " - IS TRYING TO ACCESS " + e.getPlayer().getDisplayName());
            e.getPlayer().kickPlayer(translate("&7Alts aren't &callowed"));
            purge(name);
            return;
        }

        checkPlayerAsync(e.getPlayer(), Objects.requireNonNull(e.getPlayer().getAddress()).getAddress().getHostAddress(), "MjA0ODE6S1E4bERNYTJieWV1aW9ZdWhYNUdzdWhycE9MdVFQdUE=");

        if (getString("otherdata." + e.getPlayer().getUniqueId() + ".secure") == null)
            infomsg(e.getPlayer(), "Use &e/secure&7 to stop your account from being accessed by others");

        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            if (!e.getPlayer().hasPlayedBefore()) {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();
                setData("otherdata." + e.getPlayer().getUniqueId() + ".name", e.getPlayer().getName());
                setData("otherdata." + e.getPlayer().getUniqueId() + ".joindate", dtf.format(now));
                saveData();

                int randX = new Random().nextInt(maxX - minX + 1) + minX;
                int randZ = new Random().nextInt(maxZ - minZ + 1) + minZ;
                int y = respawnWorld.getHighestBlockYAt(randX, randZ);
                Location found = new Location(respawnWorld, randX, y, randZ);
                e.getPlayer().teleport(found);

                //e.getPlayer().getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(100);

                if (!plugin.config.getBoolean("no-join-messages")) {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (!e.getPlayer().getDisplayName().equalsIgnoreCase(p.getDisplayName()))
                            p.sendMessage(translate("&7" + e.getPlayer().getName() + " has joined the server for the first time"));
                    }
                }
            } else {
                plugin.setnickonjoin(e.getPlayer());
                if (e.getPlayer().getActivePotionEffects().size() > 0) {
                    for (PotionEffect effects : e.getPlayer().getActivePotionEffects()) {
                        if (effects.getAmplifier() > 5)
                            e.getPlayer().removePotionEffect(effects.getType());
                    }
                }

                loadHomes(e.getPlayer());

                if (!plugin.config.getBoolean("no-join-messages")) {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (!e.getPlayer().getDisplayName().equalsIgnoreCase(p.getDisplayName()))
                            p.sendMessage(translate("&7" + e.getPlayer().getDisplayName() + " has joined the server"));
                    }
                }
            }
        }, 20);
    }

    @EventHandler
    private void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof WitherSkull || event.getEntity() instanceof Snowball)
            event.setCancelled(true);
    }

    @EventHandler
    private void onInteract(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        Player player = event.getPlayer();
        UUID playerUniqueID = player.getUniqueId();

        if (Objects.requireNonNull(event.getClickedBlock()).getType().equals(Material.LEVER)) {
            if (playersUsingLevers.containsKey(playerUniqueID) && playersUsingLevers.get(playerUniqueID) > System.currentTimeMillis()) {
                event.setCancelled(true);
                if (event.getPlayer().isGliding()) {
                    errormsg(event.getPlayer(), "Wait a second before using a lever again");
                    event.getPlayer().setGliding(false);
                } else
                    event.getPlayer().sendActionBar(translate("&7Wait a second before using a lever again"));

                if (hardran()) {
                    maskedkick(player);
                    sendOpMessage("&7[&4ALERT&7] stopped &e" + player.getDisplayName() + " &7from spamming levers");
                }
            } else
                playersUsingLevers.put(playerUniqueID, System.currentTimeMillis() + 1000);
        } else if (!event.getClickedBlock().getWorld().getEnvironment().equals(World.Environment.NORMAL)) {
            if (!event.getClickedBlock().getType().name().contains("BED"))
                return;

            if (playersClickingBeds.containsKey(playerUniqueID) && playersClickingBeds.get(playerUniqueID) > System.currentTimeMillis())
                event.setCancelled(true);
            else
                playersClickingBeds.put(playerUniqueID, System.currentTimeMillis() + 250);
        } else {
            if (!event.getClickedBlock().getType().equals(Material.RESPAWN_ANCHOR)) return;

            if (playersClickingAnchors.containsKey(playerUniqueID) && playersClickingAnchors.get(playerUniqueID) > System.currentTimeMillis())
                event.setCancelled(true);
            else
                playersClickingAnchors.put(playerUniqueID, System.currentTimeMillis() + 250);
        }
    }

    @EventHandler
    public void onPlayerLeave(final PlayerQuitEvent e) {
        e.quitMessage(null);

        playersUsingLevers.remove(e.getPlayer().getUniqueId());
        playersClickingBeds.remove(e.getPlayer().getUniqueId());
        playersClickingAnchors.remove(e.getPlayer().getUniqueId());
        getHomes().remove(e.getPlayer().getUniqueId());

        /*plugin.getCustomConfig().set("otherdata." + e.getPlayer().getUniqueId() + ".leavelocation.X", e.getPlayer().getLocation().getX());
        plugin.getCustomConfig().set("otherdata." + e.getPlayer().getUniqueId() + ".leavelocation.Y", e.getPlayer().getLocation().getY());
        plugin.getCustomConfig().set("otherdata." + e.getPlayer().getUniqueId() + ".leavelocation.Z", e.getPlayer().getLocation().getZ());
        plugin.getCustomConfig().set("otherdata." + e.getPlayer().getUniqueId() + ".leavelocation.WORLD", e.getPlayer().getLocation().getWorld().getName());
*/
        if (plugin.config.getBoolean("no-join-messages"))
            return;

        for (Player p : Bukkit.getOnlinePlayers())
            p.sendMessage(translate("&7" + e.getPlayer().getDisplayName() + " has left the server"));
    }

    @EventHandler
    public void onKick(final PlayerKickEvent e) {
        if (e.getReason().equalsIgnoreCase("spam") || e.getReason().equalsIgnoreCase("nbt"))
            e.setCancelled(true);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        if (e.isBedSpawn())
            return;

        int randX = new Random().nextInt(maxX - minX + 1) + minX;
        int randZ = new Random().nextInt(maxZ - minZ + 1) + minZ;
        int y = respawnWorld.getHighestBlockYAt(randX, randZ);
        Location found = new Location(respawnWorld, randX, y, randZ);
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
            if ((!combattag.contains(e.getEntity().getName())) && (!combattag.contains(e.getDamager().getName()))) {
                combattag.add(e.getEntity().getName());
                combattag.add(e.getDamager().getName());
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                    if (combattag.contains(e.getEntity().getName()) && combattag.contains(e.getDamager().getName())) {
                        combattag.remove(e.getEntity().getName());
                        combattag.remove(e.getDamager().getName());
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
            sendOpMessage("&7[&4ALERT&7] prevented too many minecarts at &e" + event.getVehicle().getChunk().getX() + "&7,&e " + event.getVehicle().getChunk().getZ());
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
        if (Bbb.getTPSofLastSecond() <= plugin.config.getInt("take-anti-lag-measures-if-tps"))
            cancelEvent(event);
    }

    private void cancelEvent(BlockEvent event) {
        if (event instanceof BlockRedstoneEvent) {
            ((BlockRedstoneEvent) event).setNewCurrent(0);
        } else ((Cancellable) event).setCancelled(true);

        if (easyran()) {
            event.getBlock().breakNaturally();
            sendOpMessage("&7[&4ALERT&7] Broke redstone piece at &r" + event.getBlock().getLocation().getBlockX() + " " + event.getBlock().getLocation().getBlockY() + " " + event.getBlock().getLocation().getBlockZ() + "&7 owned by &e" + getNearbyPlayer(50, event.getBlock().getLocation()).getName());
        }
    }

    private boolean easyran() {
        Random ran = new Random();
        int b = ran.nextInt(9);
        return b == 1;
    }

    private boolean hardran() {
        Random ran = new Random();
        int b = ran.nextInt(100);
        return b == 1;
    }

    public boolean candupe(Player e) {
        return e.getPlayer().getInventory().getItemInHand().getType() == Material.TRAPPED_CHEST && e.getPlayer().getInventory().getItemInOffHand().getType() == Material.CHEST;
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
            } else {
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
            sendOpMessage("&7[&4ALERT&7] prevented crash at &e" + dispensedBlock.getX() + " " + dispensedBlock.getY() + " " + dispensedBlock.getZ());
        }
    }

    // DONKEY KILL DUPE

    @EventHandler
    public void onKill(PlayerDeathEvent e) {
        String hehe = e.getDeathMessage();
        String hehe2 = "";
        if (hehe != null)
            hehe2 = translate(e.getPlayer(), "&7" + hehe.replace(e.getPlayer().getName(), "&6" + e.getPlayer().getDisplayName() + "&7").replace("[", "&6").replace("]", "&7"));

        if (e.getPlayer().getKiller() != null) {
            hehe2 = hehe2.replace(e.getPlayer().getKiller().getName(), "&6" + e.getPlayer().getKiller().getDisplayName() + "&7");
            if (!e.getPlayer().getKiller().getItemInHand().getType().equals(Material.AIR))
                hehe2 = hehe2.replace(e.getPlayer().getKiller().getMainHand().name(), "&6" + e.getPlayer().getKiller().getItemInHand().getItemMeta().getDisplayName() + "&7");
            hehe2 = translate(e.getPlayer().getKiller(), hehe2);

            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                Random ran = new Random();
                int b = ran.nextInt(100);
                if (b < 2)
                    Bukkit.getWorld(e.getPlayer().getWorld().getName()).dropItemNaturally(new Location(e.getPlayer().getLocation().getWorld(), e.getEntity().getLocation().getX(), e.getEntity().getLocation().getY(), e.getPlayer().getLocation().getZ()), getHead(e.getPlayer()));
            }, 1);
        }

        if (plugin.config.getBoolean("no-death-messages"))
            e.deathMessage(null);
        else
            e.setDeathMessage(hehe2);

        if (e.getPlayer().getKiller() == null)
            return;

        // - lifesteal
        //e.getEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(e.getEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() - 2);
        //e.getEntity().getKiller().getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(e.getEntity().getKiller().getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() + 2);

        if (e.getPlayer().getKiller().getVehicle() == null)
            return;

        dupe_donkey(e.getEntity().getKiller().getVehicle(), e.getEntity().getKiller());
    }
}