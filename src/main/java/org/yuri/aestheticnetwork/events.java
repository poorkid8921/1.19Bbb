package org.yuri.aestheticnetwork;

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.*;
import org.bukkit.block.data.type.Piston;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import org.yuri.aestheticnetwork.commands.Shop;
import org.yuri.aestheticnetwork.commands.duel.DuelRequest;
import org.yuri.aestheticnetwork.utils.Initializer;
import org.yuri.aestheticnetwork.utils.Utils;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

import static org.yuri.aestheticnetwork.utils.Initializer.spawn;
import static org.yuri.aestheticnetwork.utils.Initializer.*;
import static org.yuri.aestheticnetwork.utils.RequestManager.getTPArequest;
import static org.yuri.aestheticnetwork.utils.RequestManager.removeTPArequest;
import static org.yuri.aestheticnetwork.utils.Utils.spawn;
import static org.yuri.aestheticnetwork.utils.Utils.*;
import static org.yuri.aestheticnetwork.utils.duels.DuelManager.*;

public class events implements Listener {
    AestheticNetwork plugin = AestheticNetwork.getInstance();
    LuckPerms lp;
    Economy econ;
    AestheticNetwork p;

    /*@EventHandler
    private void antiCW(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player p) || !(e.getEntity() instanceof EnderCrystal))
            return;

        UUID playerUniqueId = p.getUniqueId();
        if (playerscw.containsKey(playerUniqueId) && playerscw.get(playerUniqueId) > System.currentTimeMillis()
        ) {
            Bukkit.getLogger().log(Level.INFO, p.getName() + " - cw");

            for (Player i : Bukkit.getOnlinePlayers()) {
                if (!i.isOp())
                    continue;

                i.sendMessage(translate("&6" + e.getDamager().getName() + " is cwing! &7" + p.getPing() + "ms"));
            }

            e.setCancelled(true);
        } else {
            if (p.getPing() > 300)
                return;

            int ping = p.getPing() + (p.getPing() >= 75 ? 75 : 25);
            playerscw.put(playerUniqueId, System.currentTimeMillis() + ping);
        }
    }*/

    public events(AestheticNetwork pp, LuckPerms lped, Economy eco) {
        lp = lped;
        econ = eco;
        p = pp;
    }

    private static boolean isSuspectedScanPacket(String buffer) {
        return (buffer.split(" ").length == 1 && !buffer.endsWith(" ")) || !buffer.startsWith("/") || buffer.startsWith("/about");
    }

    public static void spawnFireworks(Location loc) {
        loc.add(new Vector(0, 1, 0));
        Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();
        fwm.setPower(2);
        Random random = new Random();
        fwm.addEffect(FireworkEffect.builder().withColor(color.get(random.nextInt(color.size()))).withColor(color.get(random.nextInt(color.size()))).with(FireworkEffect.Type.BALL_LARGE).flicker(true).build());
        fw.setFireworkMeta(fwm);
    }

    public static void duel_spawnFireworks(Location loc) {
        loc.add(new Vector(0, 1, 0));
        Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();
        fwm.setPower(2);
        fwm.addEffect(FireworkEffect.builder().withColor(Color.WHITE).withColor(Color.RED).with(FireworkEffect.Type.BALL_LARGE).flicker(true).build());
        fw.setFireworkMeta(fwm);
        fw.detonate();
    }

    @EventHandler
    private void antiAuto(PlayerSwapHandItemsEvent e) {
        Player ent = e.getPlayer();

        UUID playerUniqueId = ent.getUniqueId();
        if (playerstoteming.containsKey(playerUniqueId) && playerstoteming.get(playerUniqueId) > System.currentTimeMillis()) {
            for (Player i : Bukkit.getOnlinePlayers()) {
                if (!i.hasPermission("has.staff")) continue;

                long ms = playerstoteming.get(playerUniqueId) - System.currentTimeMillis();
                i.sendMessage(translate("&6" + ent.getName() + " totemed in less than " + ms + "ms! &7" + ent.getPing() + "ms"));
            }
            //e.setCancelled(true);
            playerstoteming.remove(playerUniqueId);
        }
    }

    @EventHandler
    public void antiAuto2(EntityResurrectEvent e) {
        ((Player) e.getEntity()).updateInventory();
        if (!playerstoteming.containsKey(e.getEntity().getUniqueId()))
            playerstoteming.put(e.getEntity().getUniqueId(), System.currentTimeMillis() + 500);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onAsyncCommandTabComplete(AsyncTabCompleteEvent event) {
        event.setCancelled(isSuspectedScanPacket(event.getBuffer()));
    }

    @EventHandler
    public void onPlayerRegisterChannel(PlayerRegisterChannelEvent e) {
        if (e.getChannel().equals("hcscr:haram")) e.getPlayer().sendPluginMessage(plugin, "hcscr:haram", new byte[]{1});
    }

    @EventHandler
    private void ItemConsume(PlayerItemConsumeEvent e) {
        e.setCancelled(e.getItem().getType().equals(Material.ENCHANTED_GOLDEN_APPLE));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(final AsyncPlayerChatEvent e) {
        if (e.getMessage().length() > 98) {
            e.setCancelled(true);
            return;
        }

        UUID playerUniqueId = e.getPlayer().getUniqueId();
        if (chatdelay.containsKey(playerUniqueId) &&
                chatdelay.get(playerUniqueId) > System.currentTimeMillis())
            e.setCancelled(true);
        else chatdelay.put(playerUniqueId, System.currentTimeMillis() + 500);
    }

    @EventHandler
    private void onTeleport(final PlayerTeleportEvent e) {
        if (e.getCause() != PlayerTeleportEvent.TeleportCause.PLUGIN) return;

        if (e.getPlayer().hasMetadata("1.19.2")) e.getPlayer().removeMetadata("1.19.2", plugin);
        ffaconst.remove(e.getPlayer());
    }

    @EventHandler
    private void onPlayerLeave(final PlayerQuitEvent e) {
        UUID playerUniqueId = e.getPlayer().getUniqueId();
        String playerName = e.getPlayer().getName();

        // duel related
        if (teams.containsKey(playerUniqueId)) {
            valid.remove(playerUniqueId);
            DuelRequest tpr = getDUELrequest(e.getPlayer());
            ArrayList<Player> plist = new ArrayList<>(e.getPlayer()
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
                    t1 == 1 ? "&aYou won!" : "&cYou lost",
                    t1 == 0 ? "&aYou won!" : "&cYou lost");
            plist.clear();
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                teams.remove(pw.getUniqueId());
                teams.remove(e.getPlayer().getUniqueId());
                duel.remove(tpr);
                spawn(pw);
            }, 60L);
        }

        // requests
        removeDUELrequest(getDUELrequest(e.getPlayer()));
        removeTPArequest(getTPArequest(e.getPlayer()));
        // misc
        cooldown.remove(playerUniqueId);
        chatdelay.remove(playerUniqueId);
        playerstoteming.remove(playerUniqueId);
        //teams.remove(playerUniqueId);
        lastReceived.remove(playerUniqueId);
        msg.remove(playerName);
        Initializer.tpa.remove(playerName);
        ffaconst.remove(e.getPlayer());
    }

    /*@EventHandler(priority = EventPriority.NORMAL)
    public void PlayerDamageReceive(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player damaged) {
            ItemStack i = damaged.getInventory().getItemInMainHand();
            if ((damaged.getHealth() - e.getDamage()) <= 0 && i.getType().equals(Material.TOTEM_OF_UNDYING)) {
                e.setCancelled(true);
                damaged.setHealth(2);
                damaged.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 100, 2));
                damaged.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 800, 2));
                i.setAmount(0);
            }
        }
    }*/

    public void killeffect(Player p, String toset, String fancy, int cost) {
        p.closeInventory();
        double bal = econ.getBalance(p);
        if (bal < cost) {
            p.sendMessage(translate("#d6a7ebꜱʜᴏᴘ » &cʏᴏᴜ ᴅᴏɴ'ᴛ ʜᴀᴠᴇ ᴇɴᴏᴜɢʜ ᴍᴏɴᴇʏ."));
            return;
        }

        EconomyResponse ar = econ.withdrawPlayer(p, cost);

        if (ar.transactionSuccess()) {
            plugin.getCustomConfig().set("r." + p.getUniqueId() + ".killeffect", toset);
            plugin.saveCustomConfig();
            p.sendMessage(translate("#d6a7ebꜱʜᴏᴘ » &fʏᴏᴜ ʜᴀᴠᴇ ꜱᴜᴄᴄᴇꜱꜱꜰᴜʟʟʏ ᴘᴜʀᴄʜᴀꜱᴇ ᴛʜᴇ #d6a7eb" + fancy + " &fꜰᴏʀ #d6a7eb$" + cost));
        }
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        final ItemStack clickedItem = e.getCurrentItem();
        if (clickedItem == null) return;

        ItemMeta meta = clickedItem.getItemMeta();
        if (meta == null) return;

        NamespacedKey key = new NamespacedKey(p, "reported");
        final Player p = (Player) e.getWhoClicked();
        String report = meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
        if (report != null) {

            switch (e.getRawSlot()) {
                case 10 -> report(p, report, "Exploiting");
                case 11 -> report(p, report, "Interrupting");
                case 12 -> report(p, report, "Doxxing");
                case 13 -> report(p, report, "Ban Evading");
                case 14 -> report(p, report, "Spam");
                case 15 -> report(p, report, "Advertise");
                case 16 -> report(p, report, "Anchor Spam");
            }
        }

        if (!e.getInventory().equals(Shop.inv)) return;
        e.setCancelled(true);

        if (e.getRawSlot() == 10) {
            killeffect(p, "lightning", "ʟɪɢʜᴛɴɪɴɢ ᴋɪʟʟ ᴇꜰꜰᴇᴄᴛ", 50);
        } else if (e.getRawSlot() == 11) {
            killeffect(p, "totem_explosion", "ᴇxᴘʟᴏꜱɪᴏɴ ᴋɪʟʟ ᴇꜰꜰᴇᴄᴛ", 125);
        } else if (e.getRawSlot() == 12) {
            killeffect(p, "firework", "ꜰɪʀᴇᴡᴏʀᴋ ᴋɪʟʟ ᴇꜰꜰᴇᴄᴛ", 200);
        } //else if (e.getRawSlot() == 13)
        //killeffect(p, "explosion", "ᴇxᴩʟᴏꜱɪᴏɴ ᴋɪʟʟ ᴇꜰꜰᴇᴄᴛ", 300);
    }

    @EventHandler
    private void onPhysics(final BlockPhysicsEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    private void onPlayerKill(final PlayerDeathEvent e) {
        Player p = e.getPlayer();
        ffaconst.remove(p);
        boolean a = teams.containsKey(p.getUniqueId());
        if (!ffaconst.contains(e.getPlayer()) || a) e.getDrops().clear();

        Player killer = e.getPlayer().getKiller();
        if (a) {
            e.setCancelled(true);
            DuelRequest tpr = getDUELrequest(p);
            ArrayList<Player> plist = new ArrayList<>(p.getWorld().getNearbyPlayers(p.getLocation(), 100));
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
                            translate("&aYou won!"),
                            translate("&cYou lost"));
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
                            translate("&cYou lost"),
                            translate("&aYou won!"));
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
                            translate("&eDraw"),
                            translate("&eDraw"));
                }

                p.setNoDamageTicks(100);
                p.setFoodLevel(20);
                p.setHealth(20);

                kp.setNoDamageTicks(100);
                kp.setFoodLevel(20);
                kp.setHealth(20);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    teams.remove(kp.getUniqueId());
                    teams.remove(p.getUniqueId());
                    duel.remove(tpr);
                    spawn(kp);
                    spawn(p);
                    plist.clear();
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "arena reset duel_" + tpr.getType() + tpr.getArena() + " veryfast");
                    User up = api.getUserManager().getUser(p.getUniqueId());
                    up.data().remove(Node.builder("permission:tab.scoreboard.duels").build());
                    api.getUserManager().saveUser(up);

                    User ukp = api.getUserManager().getUser(kp.getUniqueId());
                    ukp.data().remove(Node.builder("permission:tab.scoreboard.duels").build());
                    api.getUserManager().saveUser(ukp);
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

        if (e.getPlayer().hasMetadata("1.19.2")) e.getPlayer().removeMetadata("1.19.2", plugin);
        try {
            econ.depositPlayer(killer, 5);
        } catch (RuntimeException en) {
            en.printStackTrace();
        }

        String peffect = plugin.getCustomConfig().getString("r." + killer + ".killeffect");

        if (Objects.equals(peffect, "totem_explosion")) createHelix(p);
        else if (Objects.equals(peffect, "firework")) spawnFireworks(p.getLocation());
        else if (Objects.equals(peffect, "lightning")) p.getWorld().strikeLightningEffect(p.getLocation());
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
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (e.getPlayer().getHealth() == 0.0) {
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                e.getPlayer().setHealth(20);
                e.getPlayer().kickPlayer("Disconnected");
            }, 2L);
            return;
        }

        if (Utils.manager1().get("r." + e.getPlayer().getUniqueId() + ".t") == null)
            Initializer.tpa.add(e.getPlayer().getName());

        if (Utils.manager1().get("r." + e.getPlayer().getUniqueId() + ".m") == null)
            msg.add(e.getPlayer().getName());
        spawn(e.getPlayer());
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        e.setRespawnLocation(spawn);
    }
}