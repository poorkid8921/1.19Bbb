package main;

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import it.unimi.dsi.fastutil.Pair;
import main.expansions.duels.Matchmaking;
import main.utils.Instances.DuelHolder;
import main.expansions.arenas.Arena;
import main.utils.Initializer;
import main.utils.Instances.BackHolder;
import main.utils.Utils;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import org.bukkit.*;
import org.bukkit.block.data.type.Piston;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

import static main.expansions.duels.Utils.*;
import static main.expansions.guis.Utils.*;
import static main.utils.Initializer.duel;
import static main.utils.Initializer.spawn;
import static main.utils.Languages.MAIN_COLOR;
import static main.utils.RequestManager.getTPArequest;
import static main.utils.RequestManager.tpa;
import static main.utils.Utils.*;

@SuppressWarnings("deprecation")
public class Events implements Listener {
    static String PREFIX = translateA("#d6a7eb☠ ");

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onAsyncCommandTabComplete(AsyncTabCompleteEvent event) {
        event.setCancelled(Utils.isSuspectedScanPacket(event.getBuffer()));
    }

    // Combat Tag
    /*@EventHandler
    private void commandPreprocess(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();
        String pn = player.getName();
        if (!Initializer.inCombat.contains(pn) ||
                Initializer.whitelisted_comms.contains(e.getMessage())) return;

        player.sendMessage(Utils.translateA("#fc282fʏᴏᴜ ᴄᴀɴ'ᴛ ᴜꜱᴇ ᴛʜɪꜱ ᴄᴏᴍᴍᴀɴᴅ ɪɴ ᴄᴏᴍʙᴀᴛ."));
        e.setCancelled(true);
    }

    @EventHandler
    private void onGlide(EntityToggleGlideEvent e) {
        e.setCancelled(Initializer.inCombat.contains(e.getEntity().getName()));
    }
    */

    @EventHandler
    private void ItemConsume(PlayerItemConsumeEvent e) {
        e.setCancelled(e.getItem().getType().equals(Material.ENCHANTED_GOLDEN_APPLE));
    }

    @EventHandler
    private void onDamage(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof EnderCrystal d && e.getDamager() instanceof Player a && a.getPing() > 75) {
            ((CraftPlayer) a).getHandle().b.a(new PacketPlayOutEntityDestroy(d.getEntityId()));
            //return;
        }

        /*Player b = (Player) e.getEntity();
        if (Initializer.inCombat.contains(b.getName())) return;

        Entity d = e.getDamager();
        if (!(d instanceof Player p))
            return;

        new CombatTag(b, p);*/
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent e) {
        String n = e.getPlayer().getName();
        if (e.getMessage().length() > 98 || Initializer.chatdelay.getOrDefault(n, 0L) > System.currentTimeMillis()) {
            e.setCancelled(true);
            return;
        }

        Initializer.chatdelay.put(n, System.currentTimeMillis() + 500);
    }

    @EventHandler
    private void onTeleport(PlayerTeleportEvent e) {
        if (e.getCause() != PlayerTeleportEvent.TeleportCause.PLUGIN) return;
        Initializer.inFFA.remove(e.getPlayer());
    }

    @EventHandler
    private void onPlayerLeave(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        String playerName = p.getName();

        if (Initializer.teams.containsKey(playerName)) {
            DuelHolder tpr = getPlayerDuel(playerName);
            List<Player> plist = new ArrayList<>(p.getWorld().getNearbyPlayers(p.getLocation(), 100));
            Player pw = plist.get(1);
            int red = tpr.getRed();
            int blue = tpr.getBlue();
            int t1 = Initializer.teams.get(pw.getName());

            if (t1 == 1) red += 1;
            else blue += 1;
            Duel_Resume(pw,
                    p,
                    false,
                    red,
                    blue,
                    tpr.getStart(),
                    System.currentTimeMillis(),
                    " n ",
                    t1 == 1,
                    MAIN_COLOR + (t1 == 1 ? "ʏᴏᴜ ᴡᴏɴ!" : "ʏᴏᴜ ʟᴏsᴛ"),
                    MAIN_COLOR + (t1 == 0 ? "ʏᴏᴜ ᴡᴏɴ!" : "ʏᴏᴜ ʟᴏsᴛ"));
            plist.clear();
            Bukkit.getScheduler().scheduleSyncDelayedTask(Initializer.p, () -> {
                Initializer.teams.remove(playerName);
                Initializer.teams.remove(pw.getName());
                Initializer.duel.remove(tpr);
                pw.teleportAsync(Initializer.spawn);
            }, 60L);
        }

        tpa.remove(getTPArequest(playerName));
        duel.remove(getDUELrequest(playerName));

        Initializer.back.remove(playerName);
        Initializer.lastReceived.remove(playerName);
        Initializer.msg.remove(playerName);
        Initializer.tpa.remove(playerName);
        Initializer.inFFA.remove(p);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getClickedInventory() instanceof PlayerInventory) return;

        HumanEntity p = e.getWhoClicked();
        Pair<Integer, String> inv = inInventory.getOrDefault(p.getName(), null);
        if (inv == null)
            return;

        int slot = e.getSlot();
        switch (inv.first()) {
            case 0 -> {
                e.setCancelled(true);
                if (!e.getCurrentItem().getItemMeta().hasLore()) return;

                switch (slot) {
                    case 10 -> Utils.killeffect((Player) p, 0, "ʟɪɢʜᴛɴɪɴɢ ᴋɪʟʟ ᴇꜰꜰᴇᴄᴛ", 100);
                    case 11 -> Utils.killeffect((Player) p, 1, "ᴇxᴘʟᴏꜱɪᴏɴ ᴋɪʟʟ ᴇꜰꜰᴇᴄᴛ", 200);
                    case 12 -> Utils.killeffect((Player) p, 2, "ꜰɪʀᴇᴡᴏʀᴋ ᴋɪʟʟ ᴇꜰꜰᴇᴄᴛ", 250);
                }
                return;
            }
            case 1 -> {
                e.setCancelled(true);
                if (!e.getCurrentItem().getItemMeta().hasLore()) return;

                Utils.report((Player) p, inv.second(), slot == 10 ? "Cheating" : slot == 11 ? "Doxxing" : slot == 12 ? "Ban Evading" : slot == 13 ? "Spamming" : slot == 14 ? "Interrupting" : slot == 15 ? "Anchor Spam" : null);
                return;
            }
        }

        switch (inv.second()) {
            case "0"  -> {
                switch (slot) {
                    default -> {
                        ItemStack item = e.getCurrentItem();
                        if (item.getType() == Material.PLAYER_HEAD) {
                            Initializer.spec.put(p.getName(), item.getItemMeta().getPersistentDataContainer()
                                    .get(spectateHead, PersistentDataType.STRING));
                            p.getInventory().close();
                            return;
                        }
                    }
                }
            }
            case "1" -> {
            }
            default -> {
                switch (slot) {
                    case 9 -> {
                        ItemStack s = e.getCurrentItem();
                        ItemMeta meta = s.getItemMeta();
                        meta.addEnchant(Enchantment.DURABILITY, 1, false);
                        s.setItemMeta(meta);
                        String pn = p.getName();
                        DuelHolder d = getDUELrequest(pn);
                        if (!Initializer.duel.contains(d)) {
                            p.closeInventory();
                            Matchmaking.start_unranked((Player) p, slot);
                        }
                        else
                            Initializer.duel.remove(d);

                        p.getInventory().close();
                    }
                    case 43 -> {
                        updateSpectate();
                        inv.second("0");
                    }
                }
            }
        }
    }

    @EventHandler
    public void onGUIClose(InventoryCloseEvent e) {
        if (e.getInventory() instanceof PlayerInventory) return;

        inInventory.remove(e.getPlayer().getName());
    }

    @EventHandler
    private void onPlayerKill(PlayerDeathEvent e) {
        Player p = e.getPlayer();
        if (!Initializer.inFFA.contains(p)) e.getDrops().clear();
        else Initializer.inFFA.remove(p);

        String name = p.getName();
        Player killer = p.getKiller();

        if (Initializer.teams.containsKey(name)) {
            e.setCancelled(true);
            p.setNoDamageTicks(100);
            p.setFoodLevel(20);
            p.setHealth(20);

            DuelHolder tpr = getDUELrequest(name);
            List<Player> plist = new ArrayList<>(p.getWorld().getNearbyPlayers(p.getLocation(), 100));
            Player kp = (killer == p || killer == null) ? plist.get(1) : killer;

            kp.setNoDamageTicks(100);
            kp.setFoodLevel(20);
            kp.setHealth(20);

            duel_spawnFireworks(p.getLocation());
            String kuid = kp.getName();

            Bukkit.getScheduler().scheduleSyncDelayedTask(Initializer.p, () -> {
                int newrounds = tpr.getRounds() + 1;
                int red = tpr.getRed();
                int blue = tpr.getBlue();
                int t1 = Initializer.teams.get(kuid);
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

                int arena = tpr.getArena();
                int type = tpr.getType();
                Arena.arenas.get("d_" + type + arena).reset(1000000);

                if (Bukkit.getPlayer(name) == null ||
                        Bukkit.getPlayer(kuid) == null) {
                    return;
                }

                if (newrounds == tpr.getMaxrounds()) {
                    if (red > blue) {
                        Duel_Resume(redp, bluep, true, red, blue, tpr.getStart(), System.currentTimeMillis(), " n ", true, MAIN_COLOR + "ʏᴏᴜ ʟᴏsᴛ", MAIN_COLOR + "ʏᴏᴜ ᴡᴏɴ!");
                    } else if (blue > red) {
                        Duel_Resume(bluep, redp, true, red, blue, tpr.getStart(), System.currentTimeMillis(), " n ", false, MAIN_COLOR + "ʏᴏᴜ ᴡᴏɴ!", MAIN_COLOR + "ʏᴏᴜ ʟᴏsᴛ");
                    } else {
                        Duel_Resume(redp, bluep, true, red, blue, tpr.getStart(), System.currentTimeMillis(), " y ", false, Utils.translateo("&eᴅʀᴀᴡ"), Utils.translateo("&eᴅʀᴀᴡ"));
                    }

                    Bukkit.getScheduler().runTaskLater(Initializer.p, () -> {
                        Initializer.teams.remove(kuid);
                        Initializer.teams.remove(name);
                        Initializer.inDuel.remove(tpr);
                        kp.teleportAsync(Initializer.spawn);
                        p.teleportAsync(Initializer.spawn);
                        plist.clear();

                        updateDuels();
                        updateSpectate();
                    }, 60L);
                    return;
                }

                tpr.setRounds(newrounds);
                tpr.setRed(red);
                tpr.setBlue(blue);
                Duel_Start(kp, p, type, newrounds, tpr.getMaxrounds(), arena);
                plist.clear();
            }, 60L);
            Initializer.back.remove(name);
            return;
        }

        EntityDamageEvent.DamageCause b = p.getLastDamageCause().getCause();
        if (killer == null) {
            e.setDeathMessage(
                    PREFIX + name +
                            ((b.equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) ||
                                    b.equals(EntityDamageEvent.DamageCause.BLOCK_EXPLOSION)) ?
                                    Utils.translateA(" §7blasted themselves") :
                                    b.equals(EntityDamageEvent.DamageCause.FALL) ?
                                            Utils.translateA(" §7broke their legs") :
                                            b.equals(EntityDamageEvent.DamageCause.FALLING_BLOCK) ?
                                                    Utils.translateA(" §7suffocated in a wall") :
                                                    b.equals(EntityDamageEvent.DamageCause.FLY_INTO_WALL) ?
                                                            " §7thought they can fly" :
                                                            " §7suicided"));
            return;
        } else {
            e.setDeathMessage(
                    PREFIX + killer.getName() +
                            (b.equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) ?
                                    Utils.translateA(" §7exploded #d6a7eb" + name) :
                                    b.equals(EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) ?
                                            Utils.translateA(" §7imploded #d6a7eb" + name) :
                                            b.equals(EntityDamageEvent.DamageCause.FALL) ?
                                                    Utils.translateA(" §7broke #d6a7eb" + name + "§7's legs") :
                                                    b.equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK) ?
                                                            Utils.translateA(" §7sworded #d6a7eb" + name) :
                                                            b.equals(EntityDamageEvent.DamageCause.PROJECTILE) ?
                                                                    " §7shot #d6a7eb" + name + " §7in the ass" :
                                                                    b.equals(EntityDamageEvent.DamageCause.FALLING_BLOCK) ?
                                                                            Utils.translateA(name + " §7suffocated in a wall whilst fighting #d6a7eb" + killer.getName()) :
                                                                            " §7suicided"));
        }

        Location l = p.getLocation();
        BackHolder back = Initializer.back.getOrDefault(name, null);
        if (back == null) {
            Initializer.back.put(name, new BackHolder(Utils.Locationfrom(l)));
        } else back.setBack(Utils.Locationfrom(l));

        p.sendMessage("§7Use " + MAIN_COLOR + "/back §7to return to your death location.");

        try {
            Initializer.econ.depositPlayer(killer, 5);
        } catch (RuntimeException en) {
            en.printStackTrace();
        }

        int peffect = Practice.cc.getInt("r." + killer + ".killeffect", -1);

        Location loc = p.getLocation().add(0, 1, 0);
        switch (peffect) {
            case 0 -> {
                World w = loc.getWorld();
                for (double y = 0; y <= 10; y += 0.05) {
                    w.spawnParticle(Particle.TOTEM, new Location(w, (float) (loc.getX() + 2 * Math.cos(y)), (float) (loc.getY() + y), (float) (loc.getZ() + 2 * Math.sin(y))), 2, 0, 0, 0, 1.0);
                }
            }
            case 1 -> Utils.spawnFireworks(loc);
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
            Bukkit.getScheduler().scheduleSyncDelayedTask(Initializer.p, () -> {
                p.setHealth(20);
                p.kickPlayer("Internal Exception: io.netty.handler.timeout.ReadTimeoutException");
            }, 2L);
            e.setJoinMessage(null);
            return;
        }

        String name = p.getName();

        if (Practice.cc1.get("r." + name + ".t") == null) Initializer.tpa.add(name);
        if (Practice.cc1.get("r." + name + ".m") == null) Initializer.msg.add(name);

        p.teleportAsync(spawn);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        e.setRespawnLocation(spawn);
    }
}