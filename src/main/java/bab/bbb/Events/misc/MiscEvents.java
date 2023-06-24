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
    private void onplayerquit(PlayerQuitEvent e)
    {
        removeRequest(e.getPlayer());
    }

    @EventHandler
    private void onPortalUse(EntityPortalEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            event.setCancelled(true);
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
        if (!(event.getEntity() instanceof Player) && !(event.getEntity() instanceof Enderman))
            event.setCancelled(true);
    }

    @EventHandler
    private void onTeleportEvt(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        vanish(player);
        Bbb.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(Bbb.getInstance(), () -> unVanish(player), 10);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        e.joinMessage(null);

        Bbb.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(Bbb.getInstance(), () -> {
            if (!e.getPlayer().hasPlayedBefore()) {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();

                setData("otherdata." + e.getPlayer().getUniqueId() + ".name", e.getPlayer().getName());
                setData("otherdata." + e.getPlayer().getUniqueId() + ".joindate", dtf.format(now));

                Location respawn = null;
                while (respawn == null) respawn = calcSpawnLocation();
                PaperLib.teleportAsync(e.getPlayer(), respawn);
            } else
                loadHomes(e.getPlayer());

            if (!Bbb.getInstance().config.getBoolean("no-join-messages")) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (!e.getPlayer().getName().equalsIgnoreCase(p.getName()))
                        p.sendMessage(translate("&7" + e.getPlayer().getName() + " joined the server"));
                }
            }
        }, 20);

        saveData();
    }

    @EventHandler
    public void onPlayerLeave(final PlayerQuitEvent e) {
        e.quitMessage(null);

        getHomes().remove(e.getPlayer().getUniqueId());

        if (Bbb.getInstance().config.getBoolean("no-join-messages"))
            return;

        for (Player p : Bukkit.getOnlinePlayers())
            p.sendMessage(translate("&7" + e.getPlayer().getName() + " left the server"));
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

    @EventHandler
    public void onVehicleCollide(VehicleEntityCollisionEvent event) {
        if (Bbb.countMinecartInChunk(event.getVehicle().getChunk()) >= 32) {
            Bbb.removeMinecartInChunk(event.getVehicle().getChunk());
            sendOpMessage("&7[&4ALERT&7] prevented too many minecarts at &e" + event.getVehicle().getChunk().getX() + "&7,&e " + event.getVehicle().getChunk().getZ());
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
        if (Bbb.getTPSofLastSecond() <= 14)
            cancelEvent(event);
    }

    private void cancelEvent(BlockEvent event) {
        if (event instanceof BlockRedstoneEvent) {
            ((BlockRedstoneEvent) event).setNewCurrent(0);
        } else ((Cancellable) event).setCancelled(true);
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

    @EventHandler
    public void onKill(PlayerDeathEvent e) {
        Bbb.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(Bbb.getInstance(), () -> {
            Random ran = new Random();
            int b = ran.nextInt(100);
            if (b < 2)
                Objects.requireNonNull(Bukkit.getWorld(e.getPlayer().getWorld().getName())).dropItemNaturally(new Location(e.getPlayer().getLocation().getWorld(), e.getEntity().getLocation().getX(), e.getEntity().getLocation().getY(), e.getPlayer().getLocation().getZ()), getHead(e.getPlayer()));
        }, 1);
    }
}