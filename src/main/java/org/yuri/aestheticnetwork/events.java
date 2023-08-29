package org.yuri.aestheticnetwork;

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.data.type.Piston;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import org.yuri.aestheticnetwork.commands.duel.DuelRequest;
import org.yuri.aestheticnetwork.utils.Initializer;
import org.yuri.aestheticnetwork.utils.InventoryInstanceReport;
import org.yuri.aestheticnetwork.utils.InventoryInstanceShop;
import org.yuri.aestheticnetwork.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.yuri.aestheticnetwork.utils.Initializer.spawn;
import static org.yuri.aestheticnetwork.utils.Initializer.*;
import static org.yuri.aestheticnetwork.utils.RequestManager.getTPArequest;
import static org.yuri.aestheticnetwork.utils.RequestManager.removeTPArequest;
import static org.yuri.aestheticnetwork.utils.Utils.spawn;
import static org.yuri.aestheticnetwork.utils.Utils.*;
import static org.yuri.aestheticnetwork.utils.duels.DuelManager.*;

public class events implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    private void onAsyncCommandTabComplete(AsyncTabCompleteEvent event) {
        event.setCancelled(isSuspectedScanPacket(event.getBuffer()));
    }

    @EventHandler
    public void onPlayerRegisterChannel(PlayerRegisterChannelEvent e) {
        if (e.getChannel().equals("hcscr:haram")) e.getPlayer().sendPluginMessage(p,
                "hcscr:haram",
                new byte[]{1});
    }

    @EventHandler
    private void ItemConsume(PlayerItemConsumeEvent e) {
        e.setCancelled(e.getItem().getType().equals(Material.ENCHANTED_GOLDEN_APPLE));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(final AsyncPlayerChatEvent e) {
        e.setCancelled(true);

        UUID playerUniqueId = e.getPlayer().getUniqueId();
        if (e.getMessage().length() > 98 ||
                (chatdelay.containsKey(playerUniqueId) &&
                        chatdelay.get(playerUniqueId) > System.currentTimeMillis()))
            return;

        chatdelay.put(playerUniqueId, System.currentTimeMillis() + 500);

        Bukkit.broadcastMessage(translate(e.getPlayer().getDisplayName() + " #d6a7eb» &r") + e.getMessage());
    }

    @EventHandler
    private void onTeleport(final PlayerTeleportEvent e) {
        if (e.getCause() != PlayerTeleportEvent.TeleportCause.PLUGIN) return;

        if (e.getPlayer().hasMetadata("1.19.2")) e.getPlayer().removeMetadata("1.19.2", p);
        ffaconst.remove(e.getPlayer());
    }

    @EventHandler
    private void onPlayerLeave(final PlayerQuitEvent e) {
        Player p = e.getPlayer();
        UUID playerUniqueId = p.getUniqueId();
        String playerName = p.getName();

        // duel related
        if (teams.containsKey(playerUniqueId)) {
            teams.remove(playerUniqueId);
            valid.remove(playerUniqueId);
            DuelRequest tpr = getDUELrequest(e.getPlayer());
            List<Player> plist = new ArrayList<>(e.getPlayer()
                    .getWorld()
                    .getNearbyPlayers(e.getPlayer()
                                    .getLocation(),
                            100));
            Player pw = plist.get(1);
            int red = tpr.getRed();
            int blue = tpr.getBlue();
            int t1 = teams.get(pw.getUniqueId());

            if (t1 == 1) red += 1;
            else blue += 1;
            displayduelresume(pw,
                    e.getPlayer(),
                    false,
                    red,
                    blue,
                    tpr.getStart(),
                    System.currentTimeMillis(),
                    " n ",
                    t1 == 1,
                    tpr.IsLegacy(),
                    translate(t1 == 1 ? "#31ed1cʏᴏᴜ ᴡᴏɴ!" : "#fc282fʏᴏᴜ ʟᴏsᴛ"),
                    translate(t1 == 0 ? "#31ed1cʏᴏᴜ ᴡᴏɴ!" : "#fc282fʏᴏᴜ ʟᴏsᴛ"));
            plist.clear();
            Bukkit.getScheduler().scheduleSyncDelayedTask(Initializer.p, () -> {
                teams.remove(pw.getUniqueId());
                duel.remove(tpr);
                spawn(pw);
            }, 60L);
            e.setQuitMessage(null);
        }

        // requests
        removeDUELrequest(getDUELrequest(p));
        removeTPArequest(getTPArequest(p));
        // misc
        cooldown.remove(playerUniqueId);
        chatdelay.remove(playerUniqueId);
        //teams.remove(playerUniqueId);
        lastReceived.remove(playerUniqueId);
        msg.remove(playerName);
        Initializer.tpa.remove(playerName);
        ffaconst.remove(p);
        e.setQuitMessage(translate("#fc282f← ") + playerName);
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        if (e.getClickedInventory() instanceof PlayerInventory)
            return;

        if (e.getInventory().getHolder() instanceof InventoryInstanceShop holder) {
            e.setCancelled(true);
            holder.whenClicked(e.getCurrentItem(), e.getSlot());
        } else if (e.getInventory().getHolder() instanceof InventoryInstanceReport holder) {
            e.setCancelled(true);
            if (!e.getCurrentItem().getItemMeta().hasLore())
                return;

            holder.whenClicked(e.getCurrentItem(),
                    e.getSlot(),
                    holder.getArg());
        }
    }

    @EventHandler
    private void onPhysics(final BlockPhysicsEvent e) {
        e.setCancelled(e.getBlock().getType().equals(Material.SAND));
    }

    @EventHandler
    private void onPlayerKill(final PlayerDeathEvent e) {
        Player p = e.getPlayer();
        ffaconst.remove(p);
        if (!ffaconst.contains(e.getPlayer())) e.getDrops().clear();

        Player killer = e.getPlayer().getKiller();
        if (teams.containsKey(p.getUniqueId())) {
            e.setCancelled(true);
            DuelRequest tpr = getDUELrequest(p);
            List<Player> plist = new ArrayList<>(p.getWorld().getNearbyPlayers(p.getLocation(), 100));
            Player kp = (killer == p || killer != e.getPlayer()) ? killer : plist.get(1);

            duel_spawnFireworks(p.getLocation());
            //spawnFireworks(kp.getLocation());

            int newrounds = tpr.getRounds() + 1;
            int red = tpr.getRed();
            int blue = tpr.getBlue();
            int t1 = teams.get(kp.getUniqueId());
            Player redp, bluep;

            if (t1 == 1) {
                redp = kp;
                bluep = p;
                red += 1;
            } else {
                redp = p;
                bluep = kp;
                blue += 1;
            }

            if (newrounds == tpr.getMaxrounds()) {
                if (red > blue) {
                    displayduelresume(redp,
                            bluep,
                            true,
                            red,
                            blue,
                            tpr.getStart(),
                            System.currentTimeMillis(),
                            " n ",
                            true,
                            tpr.IsLegacy(),
                            translate("#31ed1cʏᴏᴜ ᴡᴏɴ!"),
                            translate("#fc282fʏᴏᴜ ʟᴏsᴛ"));
                } else if (blue > red) {
                    displayduelresume(bluep,
                            redp,
                            true,
                            red,
                            blue,
                            tpr.getStart(),
                            System.currentTimeMillis(),
                            " n ",
                            false,
                            tpr.IsLegacy(),
                            translate("#fc282fʏᴏᴜ ʟᴏsᴛ"),
                            translate("#31ed1cʏᴏᴜ ᴡᴏɴ!"));
                } else {
                    displayduelresume(redp,
                            bluep,
                            true,
                            red,
                            blue,
                            tpr.getStart(),
                            System.currentTimeMillis(),
                            " y ",
                            false,
                            tpr.IsLegacy(),
                            translateo("&eᴅʀᴀᴡ"),
                            translateo("&eᴅʀᴀᴡ"));
                }

                p.setNoDamageTicks(100);
                p.setFoodLevel(20);
                p.setHealth(20);

                kp.setNoDamageTicks(100);
                kp.setFoodLevel(20);
                kp.setHealth(20);
                Bukkit.getScheduler().runTaskLater(Initializer.p, () -> {
                    teams.remove(kp.getUniqueId());
                    teams.remove(p.getUniqueId());
                    duel.remove(tpr);
                    spawn(kp);
                    spawn(p);
                    plist.clear();
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "arena reset duel_" +
                            tpr.getType() +
                            tpr.getArena() +
                            " veryfast");
                    User up = lp.getUserManager().getUser(p.getUniqueId());
                    up.data().remove(Node.builder("permission:tab.scoreboard.duels").build());
                    lp.getUserManager().saveUser(up);

                    User ukp = lp.getUserManager().getUser(kp.getUniqueId());
                    ukp.data().remove(Node.builder("permission:tab.scoreboard.duels").build());
                    lp.getUserManager().saveUser(ukp);
                }, 60L);
                return;
            }

            String type = tpr.getType();
            int arena = tpr.getArena();
            tpr.setRounds(newrounds);
            tpr.setRed(red);
            tpr.setBlue(blue);
            startduel(kp,
                    p,
                    type,
                    newrounds,
                    tpr.getMaxrounds(),
                    arena);
            plist.clear();
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),
                    "arena reset duel_" +
                            type +
                            arena +
                            (type.equalsIgnoreCase("field") ? " veryfast" : " slow"));
            return;
        }

        if (killer == null) return;

        /*User user = kp.getPlayerAdapter(Player.class).getUser(e.getEntity().getKiller());
        if (!user.getPrimaryGroup().equals("default")) {
            Random rnd = new Random();
            float floati = rnd.nextInt(4);
            Location loc = e.getEntity().getLocation();
            loc.add(new Vector(0, 1, 0));
            if (floati == 0)
                spawnFireworks(e.getEntity().getLocation());
            else if (floati == 1) {
                Vector off = new Vector(3, 1, 3);
                e.getEntity().getWorld().spawnParticle(Particle.TOTEM, loc, 50, off.getX(), off.getY(), off.getZ(), 0.0);
            } else if (floati == 2)
                e.getEntity().getWorld().strikeLightningEffect(e.getEntity().getLocation());
            else
                createHelix(e.getEntity());
        } else
            e.getEntity().getWorld().strikeLightningEffect(e.getEntity().getLocation());*/

        if (e.getPlayer().hasMetadata("1.19.2")) e.getPlayer().removeMetadata("1.19.2", Initializer.p);
        try {
            econ.depositPlayer(killer, 5);
        } catch (RuntimeException en) {
            en.printStackTrace();
        }

        String peffect = Initializer.p.getCustomConfig().getString("r." + killer + ".killeffect");

        if (peffect == null)
            return;

        switch (peffect) {
            case "totem_explosion" -> {
                createHelix(p);
                return;
            }
            case "firework" -> {
                spawnFireworks(p.getLocation());
                return;
            }
            case "lightning" -> {
                p.getWorld().strikeLightningEffect(p.getLocation());
                return;
            }
        }

        String color = String.valueOf(peffect.charAt(0) + peffect.charAt(1));
        int m = peffect.indexOf("_");
        String method = peffect.substring(m + 1);
        switch (method) {
            case "title" -> p.sendTitle(translateo(color + "L"), "", 1, 20, 1);
            case "subtitle" -> p.sendTitle("", translateo(color + "L"), 1, 20, 1);
            case "actionbar" -> p.sendActionBar(translateo(color + "L"));
            default -> p.sendMessage(translate("&7ᴛᴀᴋᴇ ᴛʜᴇ " + color + "ʟ &7ꜰʀᴏᴍ &c" + killer.getDisplayName()));
        }
    }

    public void createHelix(Player player) {
        Location loc = player.getLocation();
        int radius = 2;
        for (double y = 0; y <= 10; y += 0.05) {
            double x = radius * Math.cos(y);
            double z = radius * Math.sin(y);
            Vector off = new Vector(0, 0, 0);
            player.getWorld().spawnParticle(Particle.TOTEM, new Location(player.getWorld(), (float) (loc.getX() + x), (float) (loc.getY() + y), (float) (loc.getZ() + z)), 2, off.getX(), off.getY(), off.getZ(), 1.0);
        }
    }

    @EventHandler
    public void onRedstone(BlockRedstoneEvent e) {
        e.setNewCurrent(e.getBlock() instanceof Piston ? e.getNewCurrent() : 0);
    }

    @EventHandler
    public void onSpawn(EntitySpawnEvent e) {
        e.setCancelled(!entities.contains(e.getEntity().getType()));
    }

    @EventHandler
    public void onVehicleCollide(VehicleEntityCollisionEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (e.getPlayer().getHealth() == 0.0) {
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(p, () -> {
                e.getPlayer().setHealth(20);
                e.getPlayer().kickPlayer("Disconnected");
            }, 2L);
            e.setJoinMessage(null);
            return;
        }

        String name = e.getPlayer().getName();
        UUID uid = e.getPlayer().getUniqueId();

        if (Utils.manager1().get("r." + uid + ".t") == null)
            Initializer.tpa.add(name);

        if (Utils.manager1().get("r." + uid + ".m") == null)
            msg.add(name);
        spawn(e.getPlayer());
        e.setJoinMessage(translate("#31ed1c→ ") + name);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        e.setRespawnLocation(spawn);
    }
}