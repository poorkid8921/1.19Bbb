package org.yuri.aestheticnetwork;

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.data.type.Piston;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;
import org.yuri.aestheticnetwork.commands.duel.DuelRequest;
import org.yuri.aestheticnetwork.expansions.kits.Languages;
import org.yuri.aestheticnetwork.utils.Instances.UserData;
import org.yuri.aestheticnetwork.utils.*;
import org.yuri.aestheticnetwork.utils.Instances.InventoryInstanceReport;
import org.yuri.aestheticnetwork.utils.Instances.InventoryInstanceShop;
import org.yuri.aestheticnetwork.utils.Messages.Initializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.yuri.aestheticnetwork.utils.Messages.Initializer.spawn;
import static org.yuri.aestheticnetwork.utils.Messages.Initializer.*;
import static org.yuri.aestheticnetwork.utils.RequestManager.getTPArequest;
import static org.yuri.aestheticnetwork.utils.RequestManager.removeTPArequest;
import static org.yuri.aestheticnetwork.utils.Utils.spawn;
import static org.yuri.aestheticnetwork.utils.Utils.*;
import static org.yuri.aestheticnetwork.utils.duels.DuelManager.*;

@SuppressWarnings("deprecation")
public class Events implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    private void onAsyncCommandTabComplete(AsyncTabCompleteEvent event) {
        event.setCancelled(isSuspectedScanPacket(event.getBuffer()));
    }

    @EventHandler
    private void antiAuto(PlayerSwapHandItemsEvent e) {
        if (!e.getOffHandItem().getType().equals(Material.AIR)) return;
        Player ent = e.getPlayer();

        String playerUniqueId = ent.getName();
        if (playerstoteming.containsKey(playerUniqueId) &&
                playerstoteming.get(playerUniqueId) > System.currentTimeMillis()) {
            long ms = playerstoteming.get(playerUniqueId) - System.currentTimeMillis();
            int ping = ent.getPing();
            String msg = translateo("&6" + playerUniqueId + " totemed in less than " + ms + "ms! &7 " + ping + "ms");
            Bukkit.getOnlinePlayers().stream().filter(r -> r.hasPermission("has.staff")).forEach(r ->
                    r.sendMessage(msg));
            Bukkit.getServer().getScheduler().runTaskAsynchronously(Initializer.p, () -> {
                DiscordWebhook webhook = new DiscordWebhook("https://discord.com/api/webhooks/1154454290233036810/DMkRG4YXKvUcbT5lq03QKNRL-YDYyFC0vFl0Akct_aCvc0-R7XG2C86KjsS7npR-PhjF");
                webhook.setUsername("Flag");
                    webhook.addEmbed(new DiscordWebhook.EmbedObject()
                            .setTitle("Auto Totem")
                            .addField("Suspect", playerUniqueId, true)
                            .addField("Milliseconds", String.valueOf(ms), true)
                            .addField("Ping", String.valueOf(ping), true)
                            .setColor(java.awt.Color.ORANGE));
                try {
                    webhook.execute();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
            //e.setCancelled(true);
            playerstoteming.remove(playerUniqueId);
        }
    }

    @EventHandler
    public void antiAuto2(EntityResurrectEvent e) {
        String n = e.getEntity().getName();
        if (!playerstoteming.containsKey(n))
            playerstoteming.put(n, System.currentTimeMillis() + 500);
    }

    @EventHandler
    private void ItemConsume(PlayerItemConsumeEvent e) {
        e.setCancelled(e.getItem().getType().equals(Material.ENCHANTED_GOLDEN_APPLE));
    }

    // Combat Tag
    @EventHandler
    private void commandPreprocess(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();
        String pn = player.getName();
        if (!Initializer.inCombat.contains(pn))
            return;

        if (Initializer.whitelisted_comms.contains(e.getMessage()))
            return;

        player.sendMessage(translateA("#fc282fʏᴏᴜ ᴄᴀɴ'ᴛ ᴜꜱᴇ ᴛʜɪꜱ ᴄᴏᴍᴍᴀɴᴅ ɪɴ ᴄᴏᴍʙᴀᴛ."));
        e.setCancelled(true);
    }

    @EventHandler
    private void onDamage(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player b))
            return;

        if (inCombat.contains(b.getName()))
            return;

        Entity d = e.getDamager();
        Player t = (Player) (d instanceof Arrow ? ((Arrow) d).getShooter() : d);
        new CombatTag(b, t);
    }

    @EventHandler
    private void onGlide(EntityToggleGlideEvent e) {
        e.setCancelled(inCombat.contains(e.getEntity().getName()));
    }
    //

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(final AsyncPlayerChatEvent e) {
        UserData user = users.get(e.getPlayer().getName());
        if (e.getMessage().length() > 98 ||
                user.getDelay() > System.currentTimeMillis()) {
            e.setCancelled(true);
            return;
        }
        user.setDelay(System.currentTimeMillis() + 500);
    }

    @EventHandler
    private void onTeleport(final PlayerTeleportEvent e) {
        if (e.getCause() != PlayerTeleportEvent.TeleportCause.PLUGIN) return;

        ffaconst.remove(e.getPlayer());
    }

    @EventHandler
    private void onPlayerLeave(final PlayerQuitEvent e) {
        Player p = e.getPlayer();
        String playerName = p.getName();

        // duel related
        if (teams.containsKey(playerName)) {
            DuelRequest tpr = getDUELrequest(playerName);
            List<Player> plist = new ArrayList<>(e.getPlayer()
                    .getWorld()
                    .getNearbyPlayers(e.getPlayer()
                                    .getLocation(),
                            100));
            Player pw = plist.get(1);
            int red = tpr.getRed();
            int blue = tpr.getBlue();
            int t1 = teams.get(pw.getName());

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
                    translate(t1 == 1 ? "#31ed1cʏᴏᴜ ᴡᴏɴ!" : "#fc282fʏᴏᴜ ʟᴏsᴛ"),
                    translate(t1 == 0 ? "#31ed1cʏᴏᴜ ᴡᴏɴ!" : "#fc282fʏᴏᴜ ʟᴏsᴛ"));
            plist.clear();
            Bukkit.getScheduler().scheduleSyncDelayedTask(Initializer.p, () -> {
                teams.remove(playerName);
                teams.remove(pw.getName());
                duel.remove(tpr);
                spawn(pw);
            }, 60L);
        }

        // combat related
        if (inCombat.contains(playerName))
            e.getPlayer().setHealth(0.0D);

        // requests
        removeDUELrequest(getDUELrequest(playerName));
        removeTPArequest(getTPArequest(playerName));
        // misc
        playerstoteming.remove(playerName);
        users.remove(playerName);
        lastReceived.remove(playerName);
        msg.remove(playerName);
        Initializer.tpa.remove(playerName);
        ffaconst.remove(p);
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
        /*else if (e.getInventory().getHolder() instanceof InventoryInstanceDuel holder) {
            e.setCancelled(true);
            holder.whenClicked(e.getCurrentItem(), e.getSlot());
        }*/
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player &&
                event.getEntity() instanceof EnderCrystal &&
                player.getPing() > 150) {
            ((CraftPlayer) player).getHandle().b.a(new PacketPlayOutEntityDestroy(event.getEntity().getEntityId()));
        }
    }

    @EventHandler
    private void onPlayerKill(final PlayerDeathEvent e) {
        Player p = e.getPlayer();
        if (!ffaconst.contains(p)) e.getDrops().clear();
        ffaconst.remove(p);

        String name = p.getName();
        Player killer = p.getKiller();
        if (teams.containsKey(name)) {
            e.setCancelled(true);
            DuelRequest tpr = getDUELrequest(name);
            List<Player> plist = new ArrayList<>(p.getWorld().getNearbyPlayers(p.getLocation(), 100));
            Player kp = (killer == p ||
                    killer == null) ? plist.get(1) : killer;

            duel_spawnFireworks(p.getLocation());
            String kuid = kp.getName();

            Bukkit.getScheduler().scheduleSyncDelayedTask(AestheticNetwork.getInstance(), () -> {
                if (Bukkit.getPlayer(name) == null ||
                        Bukkit.getPlayer(kuid) == null) {
                    return;
                }

                int newrounds = tpr.getRounds() + 1;
                int red = tpr.getRed();
                int blue = tpr.getBlue();
                int t1 = teams.get(kuid);
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
                        teams.remove(kuid);
                        teams.remove(name);
                        duel.remove(tpr);
                        spawn(kp);
                        spawn(p);
                        plist.clear();
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
            }, 60L);
            return;
        }
        if (killer == null) return;

        UserData user = users.get(name);
        user.setBack(Locationfrom(p.getLocation()));
        p.sendMessage(translate("&7Use #fc282f/back &7to return to your death location."));

        try {
            econ.depositPlayer(killer, 5);
        } catch (RuntimeException en) {
            en.printStackTrace();
        }

        int peffect = Initializer.p.getCustomConfig().getInt("r." + killer + ".killeffect", -1);

        Location loc = p.getLocation().add(0, 1, 0);
        switch (peffect) {
            case 0 -> {
                int radius = 2;
                for (double y = 0; y <= 10; y += 0.05) {
                    double x = radius * Math.cos(y);
                    double z = radius * Math.sin(y);
                    Vector off = new Vector(0, 0, 0);
                    loc.getWorld().spawnParticle(Particle.TOTEM, new Location(loc.getWorld(), (float) (loc.getX() + x), (float) (loc.getY() + y), (float) (loc.getZ() + z)), 2, off.getX(), off.getY(), off.getZ(), 1.0);
                }
            }
            case 1 -> spawnFireworks(loc);
            case 2 -> loc.getWorld().strikeLightningEffect(loc);
        }
    }

    @EventHandler
    public void onRedstone(BlockRedstoneEvent e) {
        e.setNewCurrent(e.getBlock() instanceof Piston ? e.getNewCurrent() : 0);
    }

    @EventHandler
    public void onVehicleCollide(VehicleEntityCollisionEvent event) {
        event.getEntity().remove();
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if (p.getHealth() == 0.0) {
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Initializer.p, () -> {
                p.setHealth(20);
                p.kickPlayer("Internal Exception: io.netty.handler.timeout.ReadTimeoutException");
            }, 2L);
            e.setJoinMessage(null);
            return;
        }

        String name = p.getName();

        if (Utils.manager1().get("r." + name + ".t") == null)
            Initializer.tpa.add(name);

        if (Utils.manager1().get("r." + name + ".m") == null)
            msg.add(name);

        users.put(name, new UserData(null, false));
        spawn(p);
        Bukkit.getScheduler().scheduleSyncDelayedTask(Initializer.p, () -> {
                    List<String> m = Languages.MOTD;
                    p.sendMessage(m.get(0), m.get(1), m.get(2));
                },
                5L
        );
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        e.setRespawnLocation(spawn);
    }
}