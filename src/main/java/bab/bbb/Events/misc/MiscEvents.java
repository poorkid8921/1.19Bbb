package bab.bbb.Events.misc;

import bab.bbb.Bbb;
import bab.bbb.Events.Dupes.PlayerDupeEvent;
import bab.bbb.utils.DiscordWebhook;
import bab.bbb.utils.Utils;
import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import io.papermc.lib.PaperLib;
import org.apache.commons.math3.util.FastMath;
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
import org.bukkit.event.server.TabCompleteEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

import static bab.bbb.utils.Utils.*;

@SuppressWarnings("deprecation")
public class MiscEvents implements Listener {
    private final HashMap<Location, Long> cooldowns2 = new HashMap<>();
    private final HashMap<Location, Integer> trapdoorActivationByRedstoneCounts = new HashMap<>();

    public MiscEvents()
    {
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            trapdoorActivationByRedstoneCounts.clear();
            cooldowns2.clear();
            cooldowns.clear();
            requests.clear();
        }, 6000L, 6000L);
    }
    private final HashMap<UUID, Long> cooldowns = new HashMap<>();

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getEntity() instanceof Fish)
            event.setCancelled(true);
    }

    @EventHandler
    private void onProjectile(ProjectileLaunchEvent e)
    {
        if (e.getEntity() instanceof Snowball || e.getEntity() instanceof WitherSkull)
            e.setCancelled(true);
    }

    private static boolean isSuspectedScanPacket(String buffer) {
        return (buffer.split(" ").length == 1 && !buffer.endsWith(" ")) || !buffer.startsWith("/") || buffer.startsWith("/about");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onAsyncCommandTabComplete(AsyncTabCompleteEvent event) {
        if (!(event.getSender() instanceof Player)) return;
        if (isSuspectedScanPacket(event.getBuffer()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onDupe(PlayerDupeEvent event) {
        ArrayList<Entity> items = new ArrayList<>();
        for (Entity entity : event.getPlayer().getLocation().getNearbyEntities(16, 16, 16)) {
            if (entity.getType() == EntityType.DROPPED_ITEM)
                items.add(entity);
        }

        if (items.size() > 500) {
            event.setCancelled(true);
            items.clear();
            //items.get(items.size() - 1).remove();
            //items.remove(items.size() -1);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onInteract(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        if (!Objects.requireNonNull(event.getClickedBlock()).getType().equals(Material.LEVER)) return;

        UUID playerUniqueId = event.getPlayer().getUniqueId();
        if (
                cooldowns.containsKey(playerUniqueId)
                        && cooldowns.get(playerUniqueId) > System.currentTimeMillis()
        ) {
            event.setCancelled(true);
        } else {
            cooldowns.put(playerUniqueId, System.currentTimeMillis() + 500);
        }
    }

    /*@EventHandler(priority = EventPriority.NORMAL)
    private void onCommandTabComplete(TabCompleteEvent event) {
        if (!(event.getSender() instanceof Player)) return;
        if (isSuspectedScanPacket(event.getBuffer()))
            event.setCancelled(true);
    }*/

    @EventHandler
    private void onplayerquit(PlayerQuitEvent e) {
        removeRequest(e.getPlayer());
        cooldowns.remove(e.getPlayer().getUniqueId());
        Bbb.kills.remove(e.getPlayer().getUniqueId());
        Bbb.lastReceived.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        e.joinMessage(null);
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            if (!e.getPlayer().hasPlayedBefore()) {
                Location respawn = null;
                while (respawn == null) respawn = calcSpawnLocation();
                PaperLib.teleportAsync(e.getPlayer(), respawn);
            } else
                loadHomes(e.getPlayer());

            if (!Bbb.getInstance().config.getBoolean("no-join-messages")) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (!e.getPlayer().getName().equalsIgnoreCase(p.getName()))
                        p.sendMessage(translate("&7" + e.getPlayer().getName() + " joined the game"));
                }
            }
        });

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            String avturl = "https://mc-heads.net/avatar/hausemaster/100";
            DiscordWebhook webhook = new DiscordWebhook("https://discord.com/api/webhooks/1127000458519658516/oJdlS8_drTx5reJDseTJ17Sk0lzJ-ElKgiEo10-Qy5tm9Jp0iufOE5BEc8Ds-DnLlzCC");
            webhook.setAvatarUrl(avturl);
            webhook.setUsername("mc.aesthetic.red");
            webhook.addEmbed(new DiscordWebhook.EmbedObject()
                    .setTitle(e.getPlayer().getName() + " joined the server")
                    .setColor(java.awt.Color.ORANGE));
            try {
                webhook.execute();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    @EventHandler
    public void onPlayerLeave(final PlayerQuitEvent e) {
        e.quitMessage(null);
        getHomes().remove(e.getPlayer().getUniqueId());
        cooldowns.remove(e.getPlayer().getUniqueId());
        Bbb.lastReceived.remove(e.getPlayer().getUniqueId());

        for (Player p : Bukkit.getOnlinePlayers())
            p.sendMessage(translate("&7" + e.getPlayer().getName() + " left the game"));

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            String avturl = "https://mc-heads.net/avatar/hausemaster/100";
            DiscordWebhook webhook = new DiscordWebhook("https://discord.com/api/webhooks/1127000458519658516/oJdlS8_drTx5reJDseTJ17Sk0lzJ-ElKgiEo10-Qy5tm9Jp0iufOE5BEc8Ds-DnLlzCC");
            webhook.setAvatarUrl(avturl);
            webhook.setUsername("mc.aesthetic.red");
            webhook.addEmbed(new DiscordWebhook.EmbedObject()
                    .setTitle(e.getPlayer().getName() + " left the server")
                    .setColor(java.awt.Color.ORANGE));
            try {
                webhook.execute();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    @EventHandler
    public void onKick(final PlayerKickEvent e) {
        if (e.getReason().contains("spam") || e.getReason().contains("nbt")) {
            e.setCancelled(true);
            return;
        }

        e.setReason(translate("&7Disconnected"));
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        if (e.isBedSpawn())
            return;

        Location respawn = null;
        while (respawn == null) respawn = calcSpawnLocation();
        PaperLib.teleportAsync(e.getPlayer(), respawn);
    }

    @EventHandler
    public void onVehicleCollide(VehicleEntityCollisionEvent event) {
        if (countMinecartInChunk(event.getVehicle().getChunk()) >= 32) {
            removeMinecartInChunk(event.getVehicle().getChunk());
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockRedstoneEvent(BlockRedstoneEvent event) {
        if (Bbb.getTPSofLastSecond() <= 17)
            event.setNewCurrent(0);

        Block trapdoor = event.getBlock();
        if (!trapdoor.getType().name().contains("TRAPDOOR")) return;

        final Location trapdoorLoc = trapdoor.getLocation();
        final long currentTime = System.currentTimeMillis();

        if (!trapdoorActivationByRedstoneCounts.containsKey(trapdoorLoc) || !cooldowns2.containsKey(trapdoorLoc)) {
            trapdoorActivationByRedstoneCounts.put(trapdoorLoc, 1);
            cooldowns2.put(trapdoorLoc, currentTime);
            return;
        }

        int trapdoorOpenByRedstoneCount = trapdoorActivationByRedstoneCounts.get(trapdoorLoc);

        if (trapdoorOpenByRedstoneCount >= 20) {
            if (currentTime - cooldowns2.get(trapdoorLoc) < 3000) {
                trapdoor.breakNaturally();
                return;
            }

            trapdoorOpenByRedstoneCount = 1;
        }

        trapdoorOpenByRedstoneCount++;

        trapdoorActivationByRedstoneCounts.put(trapdoorLoc, trapdoorOpenByRedstoneCount);
        cooldowns2.put(trapdoorLoc, currentTime);
    }

    /*@EventHandler
    private void onDispense(BlockDispenseEvent event) {
        Block dispensedBlock = event.getBlock();
        World world = dispensedBlock.getWorld();
        if (dispensedBlock.getY() <= 1 || dispensedBlock.getY() >= (world.getMaxHeight() - 1))
            event.setCancelled(true);
    }*/

    @EventHandler(priority = EventPriority.LOW)
    public void onKill(PlayerDeathEvent e) {
        Location p = e.getPlayer().getLocation();
        String reason = e.getDeathMessage();
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            double random = FastMath.random();
            if (random <= 0.3)
                Bukkit.getScheduler().runTask(plugin, () -> p.getWorld().dropItemNaturally(p, getHead(e.getPlayer())));

            String avturl = "https://mc-heads.net/avatar/hausemaster/100";
            DiscordWebhook webhook = new DiscordWebhook("https://discord.com/api/webhooks/1127000458519658516/oJdlS8_drTx5reJDseTJ17Sk0lzJ-ElKgiEo10-Qy5tm9Jp0iufOE5BEc8Ds-DnLlzCC");
            webhook.setAvatarUrl(avturl);
            webhook.setUsername("mc.aesthetic.red");
            webhook.addEmbed(new DiscordWebhook.EmbedObject()
                    .setTitle(reason)
                    .setColor(java.awt.Color.RED));
            try {
                webhook.execute();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
    }
}