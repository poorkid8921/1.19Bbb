package bab.bbb.Events.misc;

import bab.bbb.Bbb;
import bab.bbb.utils.Utils;
import io.papermc.lib.PaperLib;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
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
    public MiscEvents() {
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
        Bbb.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(Bbb.getInstance(), () -> {
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
    private void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        vanish(player);
        Bbb.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(Bbb.getInstance(), () -> unVanish(player), 10);
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

        String altString = getFormattedAltString(ip, uuid);

        if (Objects.equals(altString, "true") && !name.equals("Gr1f")) {
            Bukkit.getLogger().log(Level.WARNING, e.getPlayer().getAddress().getAddress().getHostAddress() + " - IS TRYING TO ACCESS " + e.getPlayer().getDisplayName());
            e.getPlayer().kickPlayer(translate("&7Alts aren't &callowed"));
            purge(name);
            return;
        }

        checkPlayerAsync(e.getPlayer(), Objects.requireNonNull(e.getPlayer().getAddress()).getAddress().getHostAddress(), "32402b-e47483-b093j0-872921");

        if (getString("otherdata." + e.getPlayer().getUniqueId() + ".secure") == null)
            infomsg(e.getPlayer(), "Use &e/secure&7 to stop your account from being accessed by others");

        String str = getString("otherdata.nicknames");
        String stnr = getString("otherdata.realnames");

        addUpdateIp(ip, uuid, name);

        Bbb.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(Bbb.getInstance(), () -> {
            if (!e.getPlayer().hasPlayedBefore()) {
                if (!str.contains(e.getPlayer().getName()))
                    setData("otherdata.nicknames", ":_:" + e.getPlayer().getName() + str);

                if (!stnr.contains(e.getPlayer().getName()))
                    setData("otherdata.realnames", ":_:" + e.getPlayer().getName() + stnr);

                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();

                setData("otherdata." + e.getPlayer().getUniqueId() + ".name", e.getPlayer().getName());
                setData("otherdata." + e.getPlayer().getUniqueId() + ".joindate", dtf.format(now));

                Location respawn = null;
                while (respawn == null) respawn = calcSpawnLocation();
                PaperLib.teleportAsync(e.getPlayer(), respawn);

                //e.getPlayer().getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(100);

                if (!Bbb.getInstance().config.getBoolean("no-join-messages")) {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (!e.getPlayer().getDisplayName().equalsIgnoreCase(p.getDisplayName()))
                            p.sendMessage(translate("&7" + e.getPlayer().getName() + " has joined the server for the first time"));
                    }
                }
            } else {
                Bbb.getInstance().setnickonjoin(e.getPlayer());
                if (e.getPlayer().getActivePotionEffects().size() > 0) {
                    for (PotionEffect effects : e.getPlayer().getActivePotionEffects()) {
                        if (effects.getAmplifier() > 5)
                            e.getPlayer().removePotionEffect(effects.getType());
                    }
                }

                loadHomes(e.getPlayer());

                if (!Bbb.getInstance().config.getBoolean("no-join-messages")) {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (!e.getPlayer().getDisplayName().equalsIgnoreCase(p.getDisplayName()))
                            p.sendMessage(translate("&7" + e.getPlayer().getDisplayName() + " has joined the server"));
                    }
                }
            }
        }, 20);

        saveData();
    }

    @EventHandler
    private void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof WitherSkull || event.getEntity() instanceof Snowball)
            event.setCancelled(true);
    }

    @EventHandler
    private void onInteract(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
            return;

        if (Objects.requireNonNull(event.getClickedBlock()).getType().equals(Material.RESPAWN_ANCHOR)) {
            event.setCancelled(true);
            return;
        }

        Player player = event.getPlayer();
        UUID playerUniqueID = player.getUniqueId();

        if (Objects.requireNonNull(event.getClickedBlock()).getType().equals(Material.LEVER)) {
            if (playersUsingLevers.containsKey(playerUniqueID) && playersUsingLevers.get(playerUniqueID) > System.currentTimeMillis()) {
                event.setCancelled(true);
                if (event.getPlayer().isGliding()) {
                    errormsgs(event.getPlayer(), 26, "");
                    event.getPlayer().setGliding(false);
                } else
                    event.getPlayer().sendActionBar(translate("&7Wait a second before using a lever again"));

                if (hardran()) {
                    playersUsingLevers.remove(playerUniqueID);
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
        }
    }

    @EventHandler
    public void onPlayerLeave(final PlayerQuitEvent e) {
        e.quitMessage(null);

        playersUsingLevers.remove(e.getPlayer().getUniqueId());
        playersClickingBeds.remove(e.getPlayer().getUniqueId());
        playersClickingAnchors.remove(e.getPlayer().getUniqueId());
        combattag.remove(e.getPlayer().getUniqueId());
        getHomes().remove(e.getPlayer().getUniqueId());

        if (Bbb.getInstance().config.getBoolean("no-join-messages"))
            return;

        for (Player p : Bukkit.getOnlinePlayers())
            p.sendMessage(translate("&7" + e.getPlayer().getDisplayName() + " has left the server"));
    }

    @EventHandler
    public void onKick(final PlayerKickEvent e) {
        if (e.getReason().contains("spam") || e.getReason().contains("nbt"))
            e.setCancelled(true);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        vanish(e.getPlayer());
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Bbb.getInstance(), () -> unVanish(e.getPlayer()), 10);

        if (e.isBedSpawn())
            return;

        Location respawn = null;
        while (respawn == null) respawn = calcSpawnLocation();
        PaperLib.teleportAsync(e.getPlayer(), respawn);
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
                players.hidePlayer(Bbb.getInstance(), p);
                players.showPlayer(Bbb.getInstance(), p);
            }
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
            Player damager = ((Player) e.getDamager()).getPlayer();
            Player damaged = ((Player) e.getEntity()).getPlayer();

            if ((!combattag.containsKey(e.getEntity().getUniqueId())) && (!combattag.containsKey(e.getDamager().getUniqueId()))) {
                combattag.put(e.getEntity().getUniqueId(), System.currentTimeMillis() + 10000);
                combattag.put(e.getDamager().getUniqueId(), System.currentTimeMillis() + 10000);
                infomsg(damager, "You are now in combat with &e" + damaged.getDisplayName() + "&7!");
                infomsg(damaged, "You are now in combat with &e" + damager.getDisplayName() + "&7!");
                if (damaged.isGliding()) {
                    damaged.playSound(damaged.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
                    int y = damaged.getWorld().getHighestBlockYAt((int) damaged.getLocation().getX(), (int) damaged.getLocation().getZ()) + 2;
                    damaged.teleport(new Location(damaged.getWorld(), damaged.getLocation().getX(), y, damaged.getLocation().getX()));
                    Utils.errormsgs(damaged, 28, "");
                }

                if (damager.isGliding()) {
                    damager.playSound(damager.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
                    int y = damager.getWorld().getHighestBlockYAt((int) damager.getLocation().getX(), (int) damager.getLocation().getZ()) + 2;
                    damager.teleport(new Location(damager.getWorld(), damager.getLocation().getX(), y, damager.getLocation().getX()));
                    Utils.errormsgs(damager, 28, "");
                }

                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Bbb.getInstance(), () -> {
                    infomsg(damager, "You are no longer in combat");
                    infomsg(damaged, "You are no longer in combat");
                }, 200L);
            }
        } else if (e.getEntity() instanceof EnderCrystal) {
            if (e.getEntity().getTicksLived() < 4)
                e.setCancelled(true);
        }
    }

    @EventHandler
    private void onLiquidSpread(BlockFromToEvent event) {
        if (Bbb.getTPSofLastSecond() <= Bbb.getInstance().tps)
            event.setCancelled(true);
    }

    @EventHandler
    private void onNoteblockGetsPlayed(NotePlayEvent event) {
        if (Bbb.getTPSofLastSecond() <= Bbb.getInstance().tps)
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
        if (Bbb.getTPSofLastSecond() <= Bbb.getInstance().tps) {
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
        if (Bbb.getTPSofLastSecond() <= Bbb.getInstance().tps)
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
            }/* else {
                event.setCancelled(true);
                Bbb.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(Bbb.getInstance(), () -> {
                    if (((ChestedHorse) event.getRightClicked()).isCarryingChest())
                        ((ChestedHorse) event.getRightClicked()).setCarryingChest(false);
                    event.getRightClicked().eject();
                }, 4L);
            }*/
        }
    }

    @EventHandler
    private void onEntityExplode(EntityExplodeEvent event) {
        if (Bbb.getTPSofLastSecond() <= Bbb.getInstance().tps) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onExplodePrime(ExplosionPrimeEvent event) {
        if (Bbb.getTPSofLastSecond() <= Bbb.getInstance().tps) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onBlockExplode(BlockExplodeEvent event) {
        if (Bbb.getTPSofLastSecond() <= Bbb.getInstance().tps) {
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

            Bbb.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(Bbb.getInstance(), () -> {
                Random ran = new Random();
                int b = ran.nextInt(100);
                if (b < 2)
                    Bukkit.getWorld(e.getPlayer().getWorld().getName()).dropItemNaturally(new Location(e.getPlayer().getLocation().getWorld(), e.getEntity().getLocation().getX(), e.getEntity().getLocation().getY(), e.getPlayer().getLocation().getZ()), getHead(e.getPlayer()));
            }, 1);
            if (combattag.containsKey(e.getPlayer().getUniqueId()) && combattag.containsKey(e.getPlayer().getKiller().getUniqueId())) {
                combattag.remove(e.getPlayer().getUniqueId());
                combattag.remove(e.getPlayer().getUniqueId());
                infomsg(e.getPlayer().getKiller(), "You are no longer in combat");
            }
        }

        if (Bbb.getInstance().config.getBoolean("no-death-messages"))
            e.deathMessage(null);
        else
            e.setDeathMessage(hehe2);

        if (e.getPlayer().getKiller() == null)
            return;

        // - lifesteal
        /*double health = e.getEntity().getKiller().getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
        double healthE = e.getEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
        if (health <= 40) {
            e.getEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(healthE - 2);
            e.getEntity().getKiller().getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health + 2);
        }*/

        if (e.getPlayer().getKiller().getVehicle() == null)
            return;

        dupe_donkey(e.getEntity().getKiller().getVehicle(), e.getEntity().getKiller());
    }
}