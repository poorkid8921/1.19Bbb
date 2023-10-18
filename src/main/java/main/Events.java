package main;

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import it.unimi.dsi.fastutil.Pair;
import main.expansions.arenas.Arena;
import main.expansions.duels.Matchmaking;
import main.utils.Initializer;
import main.utils.Instances.BackHolder;
import main.utils.Instances.DuelHolder;
import main.utils.Utils;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import org.bukkit.*;
import org.bukkit.block.data.type.Piston;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

import static main.expansions.duels.Utils.*;
import static main.expansions.guis.Utils.*;
import static main.utils.Initializer.*;
import static main.utils.Languages.MAIN_COLOR;
import static main.utils.Languages.SECOND_COLOR;
import static main.utils.RequestManager.getTPArequest;
import static main.utils.RequestManager.tpa;
import static main.utils.Utils.duel_spawnFireworks;
import static main.utils.Utils.translateA;

@SuppressWarnings("deprecation")
public class Events implements Listener {
    String JOIN_PREFIX = translateA("#31ed1c→ ");

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
    String LEAVE_PREFIX = MAIN_COLOR + "← ";

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onAsyncCommandTabComplete(AsyncTabCompleteEvent event) {
        event.setCancelled(Utils.isSuspectedScanPacket(event.getBuffer()));
    }

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
        Player p = e.getPlayer();
        String n = p.getName();
        if (e.getMessage().length() > 98 ||
                Initializer.chatdelay.getOrDefault(n, 0L) > System.currentTimeMillis()) {
            e.setCancelled(true);
            return;
        }

        Initializer.chatdelay.put(n, System.currentTimeMillis() + 500);
        e.setFormat(p.getDisplayName() + SECOND_COLOR + " » §r" + e.getMessage());
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
            Bukkit.getScheduler().scheduleAsyncDelayedTask(Initializer.p, () -> {
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

        e.setQuitMessage(LEAVE_PREFIX + playerName);
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
                    case 10 -> Utils.killeffect((Player) p, -1, null);
                    case 12 -> Utils.killeffect((Player) p, 0, "ᴛʜᴇ ʟɪɢʜᴛɴɪɴɢ ᴋɪʟʟ ᴇꜰꜰᴇᴄᴛ");
                    case 13 -> Utils.killeffect((Player) p, 1, "ᴛʜᴇ ᴇxᴘʟᴏꜱɪᴏɴ ᴋɪʟʟ ᴇꜰꜰᴇᴄᴛ");
                    case 14 -> Utils.killeffect((Player) p, 2, "ᴛʜᴇ ꜰɪʀᴇᴡᴏʀᴋ ᴋɪʟʟ ᴇꜰꜰᴇᴄᴛ");
                }
            } // settings: killeffect
            case 1 -> {
                e.setCancelled(true);
                if (!e.getCurrentItem().getItemMeta().hasLore()) return;

                Utils.report((Player) p, inv.second(), switch (slot) {
                    case 10 -> "Cheating";
                    case 11 -> "Doxxing";
                    case 12 -> "Ban Evading";
                    case 13 -> "Spamming";
                    case 14 -> "Interrupting";
                    case 15 -> "Anchor Spamming";
                    default -> null;
                });
            } // report: report
            case 2 -> {
                e.setCancelled(true);

                switch (inv.second()) {
                    case "-" -> { // duels (main menu)
                        switch (slot) {
                            case 9 -> {
                                ItemStack s = e.getCurrentItem();
                                ItemMeta meta = s.getItemMeta();
                                meta.addEnchant(Enchantment.DURABILITY, 1, false);
                                s.setItemMeta(meta);
                                String pn = p.getName();
                                DuelHolder d = getDUELrequest(pn);
                                if (!duel.contains(d)) {
                                    p.closeInventory();
                                    Matchmaking.start_unranked((Player) p, slot);
                                } else
                                    duel.remove(d);

                                p.getInventory().close();
                            }
                            case 53 -> {
                                openDuelsSpectate((Player) p);
                                updateSpectate();
                                inv.second("0");
                            }
                        }
                    }
                    case "0" -> { // spectate
                        /*switch (slot) {
                            default -> {*/
                        ItemStack item = e.getCurrentItem();
                        if (item.getType() == Material.PLAYER_HEAD) {
                            Initializer.spec.put(p.getName(), item.getItemMeta().getPersistentDataContainer()
                                    .get(spectateHead, PersistentDataType.STRING));
                            p.getInventory().close();
                        }
                        //}
                        //}
                    }
                    case "1" -> { // kits
                    }
                    default -> {
                    }
                }
            } // duels: null\dynamic
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
        if (Initializer.inFFA.contains(p)) Initializer.inFFA.remove(p);
        else e.getDrops().clear();

        String name = p.getName();
        Player killer = p.getKiller();

        if (Initializer.teams.containsKey(name)) {
            e.setCancelled(true);
            p.setNoDamageTicks(100);
            p.setFoodLevel(20);
            p.setHealth(20);

            DuelHolder tpr = getPlayerDuel(name);
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
                        Duel_Resume(redp, bluep, true, red, blue, tpr.getStart(), System.currentTimeMillis(), " y ", false, "§eᴅʀᴀᴡ", "§eᴅʀᴀᴡ");
                    }

                    Bukkit.getScheduler().runTaskLaterAsynchronously(Initializer.p, () -> {
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

        Bukkit.getLogger().warning(SECOND_COLOR);
        Bukkit.getLogger().warning(SECOND_COLOR + "☠ " + name);
        Bukkit.getLogger().warning(switch (p.getLastDamageCause().getCause()) {
            case ENTITY_EXPLOSION, BLOCK_EXPLOSION -> "blasted themselves";
            case FALL -> "broke their legs";
            case FALLING_BLOCK -> "suffocated";
            case FLY_INTO_WALL -> "thought they're a fly";
            default -> "suicided";
        });
        Bukkit.getLogger().warning(SECOND_COLOR + "☠ " + name + " §7" +
                switch (p.getLastDamageCause().getCause()) {
                    case ENTITY_EXPLOSION, BLOCK_EXPLOSION -> "blasted themselves";
                    case FALL -> "broke their legs";
                    case FALLING_BLOCK -> "suffocated";
                    case FLY_INTO_WALL -> "thought they're a fly";
                    default -> "suicided";
                });

        if (killer == null ||
                killer.getName().equals(name)) {
            e.setDeathMessage(SECOND_COLOR + "☠ " + name + " §7" +
                    switch (p.getLastDamageCause().getCause()) {
                        case ENTITY_EXPLOSION, BLOCK_EXPLOSION -> "blasted themselves";
                        case FALL -> "broke their legs";
                        case FALLING_BLOCK -> "suffocated";
                        case FLY_INTO_WALL -> "thought they're a fly";
                        default -> "suicided";
                    });
            return;
        } else {
            e.setDeathMessage(SECOND_COLOR + "☠ " + name + " §7" +
                    switch (p.getLastDamageCause().getCause()) {
                        case ENTITY_EXPLOSION -> "exploded " + SECOND_COLOR + killer.getName();
                        case BLOCK_EXPLOSION -> "imploded " + SECOND_COLOR + killer.getName();
                        case FALL -> "broke " + SECOND_COLOR + killer.getName() + "§7's legs";
                        case ENTITY_ATTACK, ENTITY_SWEEP_ATTACK -> "sworded " + SECOND_COLOR + killer.getName();
                        case PROJECTILE -> "shot " + SECOND_COLOR + killer.getName() + " §7in the ass";
                        default -> "suicided";
                    });
        }

        Location l = p.getLocation();
        BackHolder back = Initializer.back.getOrDefault(name, null);
        if (back == null) {
            Initializer.back.put(name, new BackHolder(l));
        } else back.setBack(l);

        p.sendMessage("§7Use " + MAIN_COLOR + "/back §7to return to your death location.");

        switch (Practice.config.getInt("r." + killer + ".c", -1)) {
            case 0 -> {
                Location loc = p.getLocation().add(0, 1, 0);
                World w = loc.getWorld();
                for (double y = 0; y <= 10; y += 0.05) {
                    w.spawnParticle(Particle.TOTEM, new Location(w, (float) (loc.getX() + 2 * Math.cos(y)), (float) (loc.getY() + y), (float) (loc.getZ() + 2 * Math.sin(y))), 2, 0, 0, 0, 1.0);
                }
            }
            case 1 -> {
                Firework fw = (Firework) p.getWorld().spawnEntity(p.getLocation().add(0, 1, 0), EntityType.FIREWORK);
                FireworkMeta fwm = fw.getFireworkMeta();
                fwm.setPower(2);
                fwm.addEffect(FireworkEffect.builder().withColor(color.get(Initializer.RANDOM.nextInt(color.size()))).withColor(color.get(RANDOM.nextInt(color.size()))).with(FireworkEffect.Type.BALL_LARGE).flicker(true).build());
                fw.setFireworkMeta(fwm);
            }
            case 2 -> p.getWorld().strikeLightningEffect(p.getLocation().add(0, 1, 0));
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

        if (Practice.config.get("r." + name + ".t") == null) Initializer.tpa.add(name);
        if (Practice.config.get("r." + name + ".m") == null) Initializer.msg.add(name);

        p.teleportAsync(spawn);
        e.setJoinMessage(JOIN_PREFIX + name);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        e.setRespawnLocation(spawn);
    }
}