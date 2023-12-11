package main;

import it.unimi.dsi.fastutil.Pair;
import main.expansions.duels.Matchmaking;
import main.utils.*;
import main.utils.Instances.BackHolder;
import main.utils.Instances.DuelHolder;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

import static main.expansions.guis.Utils.*;
import static main.utils.DuelUtils.*;
import static main.utils.Initializer.*;
import static main.utils.Languages.MAIN_COLOR;
import static main.utils.Languages.SECOND_COLOR;
import static main.utils.RequestManager.*;

@SuppressWarnings("deprecation")
public class Events implements Listener {
    String JOIN_PREFIX = Utils.translateA("#31ed1c→ ");

    @EventHandler
    public void onArmorStandClick(PlayerArmorStandManipulateEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        e.setCancelled(teams.containsKey(e.getPlayer().getName()));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        e.setFormat(chat.getPlayerPrefix("world", p).replace("&", "§") + p.getName() + SECOND_COLOR + " » §r" + e.getMessage());
    }

    @EventHandler
    private void onTeleport(PlayerTeleportEvent e) {
        if (e.getCause() == PlayerTeleportEvent.TeleportCause.PLUGIN)
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
            String pwn = pw.getName();
            int t1 = Initializer.teams.get(pwn);

            if (t1 == 1) red += 1;
            else blue += 1;
            resume(pw, p, false, red, blue, tpr.getStart(), System.currentTimeMillis(), " n ", t1 == 1, MAIN_COLOR + (t1 == 1 ? "ʏᴏᴜ ᴡᴏɴ!" : "ʏᴏᴜ ʟᴏsᴛ"), MAIN_COLOR + (t1 == 0 ? "ʏᴏᴜ ᴡᴏɴ!" : "ʏᴏᴜ ʟᴏsᴛ"));
            plist.clear();
            Bukkit.getScheduler().scheduleAsyncDelayedTask(Initializer.p, () -> {
                Initializer.teams.remove(playerName);
                Initializer.teams.remove(pwn);
                Initializer.duel.remove(tpr);
                pw.teleportAsync(Initializer.spawn);
            }, 60L);
        }

        Initializer.THREAD.submit(() -> {
            RequestManager.tpa.remove(getTPArequest(playerName));
            duel.remove(getDUELrequest(playerName));

            Initializer.back.remove(playerName);
            Initializer.lastReceived.remove(playerName);
            Initializer.msg.remove(playerName);
            Initializer.tpa.remove(playerName);
            Initializer.inFFA.remove(p);
        });
        e.setQuitMessage(MAIN_COLOR + "← " + playerName);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Inventory c = e.getClickedInventory();
        if (c instanceof PlayerInventory)
            return;

        Player p = (Player) e.getWhoClicked();
        String pn = p.getName();

        Pair<Integer, String> inv = inInventory.getOrDefault(pn, null);
        if (inv == null) return;

        int slot = e.getSlot();
        switch (inv.first()) {
            case 0 -> {
                e.setCancelled(true);
                if (!e.getCurrentItem().getItemMeta().hasLore()) return;

                switch (slot) {
                    case 10 -> Utils.killeffect(p, -1, null);
                    case 12 -> Utils.killeffect(p, 0, "ᴛʜᴇ ʟɪɢʜᴛɴɪɴɢ ᴋɪʟʟ ᴇꜰꜰᴇᴄᴛ");
                    case 13 -> Utils.killeffect(p, 1, "ᴛʜᴇ ᴇxᴘʟᴏꜱɪᴏɴ ᴋɪʟʟ ᴇꜰꜰᴇᴄᴛ");
                    case 14 -> Utils.killeffect(p, 2, "ᴛʜᴇ ꜰɪʀᴇᴡᴏʀᴋ ᴋɪʟʟ ᴇꜰꜰᴇᴄᴛ");
                }
            } // settings: killeffect
            case 1 -> {
                e.setCancelled(true);
                if (!e.getCurrentItem().getItemMeta().hasLore()) return;

                Utils.report(p, inv.second(), switch (slot) {
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
                                DuelHolder d = getDUELrequest(pn);
                                if (!duel.contains(d)) {
                                    p.closeInventory();
                                    Matchmaking.start_unranked(p, slot);
                                } else duel.remove(d);

                                p.getInventory().close();
                            }
                            case 53 -> {
                                openDuelsSpectate(p);
                                updateSpectate();
                                inv.second("0");
                            }
                        }
                    }
                    case "0" -> { // spectate
                        ItemStack item = e.getCurrentItem();
                        if (item.getType() == Material.PLAYER_HEAD) {
                            Initializer.spec.put(p.getName(), item.getItemMeta().getPersistentDataContainer().get(spectateHead, PersistentDataType.STRING));
                            p.getInventory().close();
                        }
                    }
                    case "1" -> { // kit override

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

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerKill(PlayerDeathEvent e) {
        Player p = e.getPlayer();
        String name = p.getName();
        Location l = p.getLocation();
        if (Initializer.inFFA.contains(p)) {
            Initializer.inFFA.remove(p);
        } else e.getDrops().clear();

        Player killer = p.getKiller();

        World w = p.getWorld();
        if (Initializer.teams.containsKey(name)) {
            e.setCancelled(true);
            p.setNoDamageTicks(100);
            p.setFoodLevel(20);
            p.setHealth(20);

            DuelHolder tpr = getPlayerDuel(name);
            ;
            Player kp = (killer == p || killer == null) ? w.getNearbyPlayers(l, 100).stream().toList().get(1) : killer;

            kp.setNoDamageTicks(100);
            kp.setFoodLevel(20);
            kp.setHealth(20);

            DuelUtils.spawnFireworks(l, w);
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
                /*Arena ffa = Arena.arenas.get("d_" + type + arena);
                Arena.ResetLoopinData data = new Arena.ResetLoopinData();
                data.speed = 10000;
                for (Section s : ffa.getSections()) {
                    int sectionAmount = (int) ((double) 10000 / (double) (ffa.getc2().getBlockX() - ffa.getc1().getBlockX() + 1) * (ffa.getc2().getBlockY() - ffa.getc1().getBlockY() + 1) * (ffa.getc2().getBlockZ() - ffa.getc1().getBlockZ() + 1) * (double) s.getTotalBlocks());
                    if (sectionAmount <= 0) sectionAmount = 1;
                    data.sections.put(s.getID(), sectionAmount);
                    data.sectionIDs.add(s.getID());
                }*/

                if (Bukkit.getPlayer(name) == null || Bukkit.getPlayer(kuid) == null) {
                    if (red > blue) {
                        resume(redp, bluep, true, red, blue, tpr.getStart(), System.currentTimeMillis(), " n ", true, MAIN_COLOR + "ʏᴏᴜ ʟᴏsᴛ", MAIN_COLOR + "ʏᴏᴜ ᴡᴏɴ!", e);
                    } else if (blue > red) {
                        resume(bluep, redp, true, red, blue, tpr.getStart(), System.currentTimeMillis(), " n ", false, MAIN_COLOR + "ʏᴏᴜ ᴡᴏɴ!", MAIN_COLOR + "ʏᴏᴜ ʟᴏsᴛ", e);
                    } else {
                        resume(redp, bluep, true, red, blue, tpr.getStart(), System.currentTimeMillis(), " y ", false, "§eᴅʀᴀᴡ", "§eᴅʀᴀᴡ", e);
                    }

                    Bukkit.getScheduler().runTaskLater(Initializer.p, () -> {
                        Initializer.teams.remove(kuid);
                        Initializer.teams.remove(name);
                        kp.teleportAsync(Initializer.spawn);
                        p.teleportAsync(Initializer.spawn);
                    }, 60L);
                    //boolean resetted;
                    //do {
                    //resetted = true;

                    Initializer.inDuel.remove(tpr);
                    updateDuels();
                    updateSpectate();
                    //} while (!ffa.loopyReset(data) && !resetted);
                    return;
                }

                if (newrounds == tpr.getMaxrounds()) {
                    if (red > blue) {
                        resume(redp, bluep, true, red, blue, tpr.getStart(), System.currentTimeMillis(), " n ", true, MAIN_COLOR + "ʏᴏᴜ ʟᴏsᴛ", MAIN_COLOR + "ʏᴏᴜ ᴡᴏɴ!", e);
                    } else if (blue > red) {
                        resume(bluep, redp, true, red, blue, tpr.getStart(), System.currentTimeMillis(), " n ", false, MAIN_COLOR + "ʏᴏᴜ ᴡᴏɴ!", MAIN_COLOR + "ʏᴏᴜ ʟᴏsᴛ", e);
                    } else {
                        resume(redp, bluep, true, red, blue, tpr.getStart(), System.currentTimeMillis(), " y ", false, "§eᴅʀᴀᴡ", "§eᴅʀᴀᴡ", e);
                    }

                    Bukkit.getScheduler().runTaskLater(Initializer.p, () -> {
                        Initializer.teams.remove(kuid);
                        Initializer.teams.remove(name);
                        Initializer.inDuel.remove(tpr);
                        kp.teleportAsync(Initializer.spawn);
                        p.teleportAsync(Initializer.spawn);
                        updateDuels();
                        updateSpectate();
                    }, 60L);
                    return;
                }

                tpr.setRounds(newrounds);
                tpr.setRed(red);
                tpr.setBlue(blue);
                start(kp, p, type, newrounds, tpr.getMaxrounds(), arena);
            }, 60L);
            Initializer.back.remove(name);
            return;
        }

        BackHolder back = Initializer.back.getOrDefault(name, null);
        if (back == null) {
            Initializer.back.put(name, new BackHolder(l));
        } else back.setBack(l);

        p.sendMessage(Languages.BACK);

        if (killer == null || killer == p) {
            e.setDeathMessage(SECOND_COLOR + "☠ " + name + " §7" + switch (p.getLastDamageCause().getCause()) {
                case ENTITY_EXPLOSION, BLOCK_EXPLOSION -> "blasted themselves";
                case FALL -> "broke their legs";
                case FALLING_BLOCK -> "suffocated";
                case FLY_INTO_WALL -> "thought they're a fly";
                case FIRE_TICK, LAVA -> "burnt into ashes";
                default -> "suicided";
            });
            return;
        } else {
            e.setDeathMessage(SECOND_COLOR + "☠ " + killer.getName() + " §7" + switch (p.getLastDamageCause().getCause()) {
                case ENTITY_EXPLOSION -> "exploded " + SECOND_COLOR + name;
                case BLOCK_EXPLOSION -> "imploded " + SECOND_COLOR + name;
                case FALL -> "broke " + SECOND_COLOR + name + "§7's legs";
                case ENTITY_ATTACK, ENTITY_SWEEP_ATTACK -> "sworded " + SECOND_COLOR + name;
                case PROJECTILE -> "shot " + SECOND_COLOR + name + " §7in the ass";
                case FIRE_TICK, LAVA -> "turned " + SECOND_COLOR + name + " §7into ashes";
                default -> "suicided";
            });
        }

        switch (Practice.config.getInt("r." + killer + ".c", -1)) {
            case 0 -> {
                Location loc = p.getLocation().add(0, 1, 0);
                for (double y = 0; y <= 10; y += 0.05) {
                    w.spawnParticle(Particle.TOTEM, new Location(w, (float) (loc.getX() + 2 * Math.cos(y)), (float) (loc.getY() + y), (float) (loc.getZ() + 2 * Math.sin(y))), 2, 0, 0, 0, 1.0);
                }
            }
            case 1 -> {
                Firework fw = (Firework) w.spawnEntity(l.add(0, 1, 0), EntityType.FIREWORK);
                FireworkMeta fwm = fw.getFireworkMeta();
                fwm.setPower(2);
                fwm.addEffect(FireworkEffect.builder().withColor(color.get(Initializer.RANDOM.nextInt(color.size()))).withColor(color.get(RANDOM.nextInt(color.size()))).with(FireworkEffect.Type.BALL_LARGE).flicker(true).build());
                fw.setFireworkMeta(fwm);
            }
            case 2 -> w.strikeLightningEffect(p.getLocation().add(0, 1, 0));
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        String name = p.getName();

        Initializer.THREAD.submit(() -> {
            if (Practice.config.get("r." + name + ".t") == null) Initializer.tpa.add(name);
            if (Practice.config.get("r." + name + ".m") == null) Initializer.msg.add(name);
        });
        e.setJoinMessage(JOIN_PREFIX + name);
        p.teleportAsync(spawn);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        e.setRespawnLocation(spawn);
    }
}