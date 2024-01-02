package main;

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import com.destroystokyo.paper.event.player.PlayerHandshakeEvent;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import main.expansions.bungee.HandShake;
import main.expansions.duels.Matchmaking;
import main.utils.Constants;
import main.utils.Instances.CustomPlayerDataHolder;
import main.utils.Instances.DuelHolder;
import main.utils.Instances.WorldLocationHolder;
import main.utils.RequestManager;
import main.utils.Utils;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import static main.expansions.guis.Utils.*;
import static main.utils.Constants.*;
import static main.utils.DuelUtils.*;
import static main.utils.RequestManager.*;

@SuppressWarnings("deprecation")
public class Events implements Listener {
    String JOIN_PREFIX = Utils.translateA("#31ed1c→ ");

    @EventHandler
    public void onProjectileLaunchEvent(ProjectileLaunchEvent e) {
        if (!(e.getEntity() instanceof EnderPearl pearl))
            return;
        ((Player) pearl.getShooter()).setCooldown(Material.ENDER_PEARL, 5);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    private void onHandshake(PlayerHandshakeEvent e) {
        HandShake decoded = HandShake.decodeAndVerify(e.getOriginalHandshake());
        if (decoded == null) {
            e.setFailMessage("Server closed");
            e.setFailed(true);
            return;
        }

        HandShake.Success data = (HandShake.Success) decoded;
        e.setServerHostname(data.serverHostname());
        e.setSocketAddressHostname(data.socketAddressHostname());
        e.setUniqueId(data.uniqueId());
        e.setPropertiesJson(data.propertiesJson());
    }

    @EventHandler
    private void onEntitySpawn(EntitySpawnEvent event) {
        if (event.getEntityType() == EntityType.ENDER_CRYSTAL)
            crystalsToBeOptimized.put(
                    event.getEntity().getEntityId(),
                    event.getEntity().getLocation()
            );
    }

    @EventHandler
    private void onEntityRemoveFromWorld(EntityRemoveFromWorldEvent event) {
        if (event.getEntityType() == EntityType.ENDER_CRYSTAL)
            Bukkit.getScheduler().runTaskLater(Constants.p, () -> crystalsToBeOptimized.remove(event.getEntity().getEntityId()), 40L);
    }

    @EventHandler
    private void onCommand(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();
        String pn = p.getName();
        boolean tagged = playerData.get(pn).isTagged();
        if (tagged)
            p.sendMessage(Constants.EXCEPTION_TAGGED);
        e.setCancelled(tagged || teams.containsKey(pn));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        e.setFormat(chat.getPlayerPrefix("world", p).replace("&", "§") + p.getName() + SECOND_COLOR + " » §r" + e.getMessage());
    }

    @EventHandler
    private void onTeleport(PlayerTeleportEvent e) {
        if (e.getCause() == PlayerTeleportEvent.TeleportCause.PLUGIN)
            Constants.inFFA.remove(e.getPlayer());
    }

    @EventHandler
    private void onPlayerLeave(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        String playerName = p.getName();

        if (Constants.teams.containsKey(playerName)) {
            DuelHolder tpr = getPlayerDuel(playerName);
            ObjectArrayList<Player> plist = new ObjectArrayList<>(p.getWorld().getNearbyPlayers(p.getLocation(), 100));
            Player pw = plist.get(1);
            int red = tpr.getRed();
            int blue = tpr.getBlue();
            String pwn = pw.getName();
            int t1 = Constants.teams.get(pwn);

            if (t1 == 1) red += 1;
            else blue += 1;
            showDuelResume(pw, p, false, red, blue, tpr.getStart(), System.currentTimeMillis(), " n ", t1 == 1, MAIN_COLOR + (t1 == 1 ? "ʏᴏᴜ ᴡᴏɴ!" : "ʏᴏᴜ ʟᴏsᴛ"), MAIN_COLOR + (t1 == 0 ? "ʏᴏᴜ ᴡᴏɴ!" : "ʏᴏᴜ ʟᴏsᴛ"));
            plist.clear();
            Bukkit.getScheduler().scheduleAsyncDelayedTask(Constants.p, () -> {
                Constants.teams.remove(playerName);
                Constants.teams.remove(pwn);
                Constants.duel.remove(tpr);
                pw.teleportAsync(Constants.spawn);
            }, 60L);
        }
        CustomPlayerDataHolder D = playerData.get(playerName);
        Bukkit.getScheduler().cancelTask(D.getRunnableid());
        D.setTagged(false);

        RequestManager.tpa.remove(getTPArequest(playerName));
        duel.remove(getDUELrequest(playerName));

        Constants.lastReceived.remove(playerName);
        Constants.msg.remove(playerName);
        Constants.tpa.remove(playerName);
        Constants.inFFA.remove(p);
        e.setQuitMessage(MAIN_COLOR + "← " + playerName);
    }

    @EventHandler
    private void onInventoryClick(InventoryClickEvent e) {
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
                    case 12 -> Utils.killeffect(p, -1, null, 0);
                    case 13 -> Utils.killeffect(p, 0, "ᴛʜᴇ ʟɪɢʜᴛɴɪɴɢ ᴋɪʟʟ ᴇꜰꜰᴇᴄᴛ", 250);
                    case 14 -> Utils.killeffect(p, 1, "ᴛʜᴇ ᴇxᴘʟᴏꜱɪᴏɴ ᴋɪʟʟ ᴇꜰꜰᴇᴄᴛ", 425);
                    case 15 -> Utils.killeffect(p, 2, "ᴛʜᴇ ꜰɪʀᴇᴡᴏʀᴋ ᴋɪʟʟ ᴇꜰꜰᴇᴄᴛ", 750);
                }
            } // settings: killeffect
            case 1 -> {
                e.setCancelled(true);
                if (!e.getCurrentItem().getItemMeta().hasLore()) return;

                Utils.submitReport(p, inv.second(), switch (slot) {
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
                                inInventory.remove(pn);
                                inInventory.put(pn, Pair.of(2, "0"));
                            }
                        }
                    }
                    case "0" -> { // spectate
                        ItemStack item = e.getCurrentItem();
                        if (item.getType() == Material.PLAYER_HEAD) {
                            Constants.spec.put(p.getName(), item.getItemMeta().getPersistentDataContainer().get(spectateHead, PersistentDataType.STRING));
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
    private void onGUIClose(InventoryCloseEvent e) {
        if (!(e.getInventory() instanceof PlayerInventory))
            inInventory.remove(e.getPlayer().getName());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerDamage(EntityDamageByEntityEvent e) {
        if (e.isCancelled())
            return;

        if (!(e.getEntity() instanceof Player p) ||
                !(e.getDamager() instanceof Player attacker))
            return;

        CustomPlayerDataHolder D0 = playerData.get(p.getName());
        CustomPlayerDataHolder D1 = playerData.get(attacker.getName());
        if (D0.isTagged())
            Bukkit.getScheduler().cancelTask(D0.getRunnableid());
        else
            D0.setTagged(true);
        if (D1.isTagged())
            Bukkit.getScheduler().cancelTask(D1.getRunnableid());
        else
            D1.setTagged(true);

        BukkitRunnable runnable = new BukkitRunnable() {
            int time = 5;

            @Override
            public void run() {
                p.sendActionBar("§7ᴄᴏᴍʙᴀᴛ: §4" + time);
                time--;

                if (time == 0) {
                    D0.setTagged(false);
                    this.cancel();
                }
            }
        };

        BukkitRunnable runnable2 = new BukkitRunnable() {
            int time = 5;

            @Override
            public void run() {
                attacker.sendActionBar("§7ᴄᴏᴍʙᴀᴛ: §4" + time);
                time--;

                if (time == 0) {
                    D1.setTagged(false);
                    this.cancel();
                }
            }
        };

        runnable.runTaskTimer(Constants.p, 0L, 20L);
        runnable2.runTaskTimer(Constants.p, 0L, 20L);
        D0.setRunnableid(runnable.getTaskId());
        D1.setRunnableid(runnable2.getTaskId());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerKill(PlayerDeathEvent e) {
        Player p = e.getPlayer();
        String name = p.getName();
        Location l = p.getLocation();
        if (Constants.inFFA.contains(p)) {
            Constants.inFFA.remove(p);
        } else e.getDrops().clear();

        Player killer = p.getKiller();
        World w = p.getWorld();
        if (Constants.teams.containsKey(name)) {
            e.setCancelled(true);
            p.setNoDamageTicks(100);
            p.setFoodLevel(20);
            p.setHealth(20);

            DuelHolder tpr = getPlayerDuel(name);
            Player kp = (killer == p || killer == null) ? w.getNearbyPlayers(l, 100).stream().toList().get(1) : killer;

            kp.setNoDamageTicks(100);
            kp.setFoodLevel(20);
            kp.setHealth(20);

            Firework fw = (Firework) w.spawnEntity(l.clone().add(0, 1, 0), EntityType.FIREWORK);
            FireworkMeta fwm = fw.getFireworkMeta();
            fwm.setPower(2);
            fwm.addEffect(FireworkEffect.builder()
                    .withColor(Color.WHITE)
                    .withColor(Color.RED)
                    .with(FireworkEffect.Type.BALL_LARGE)
                    .flicker(true)
                    .build());
            fw.setFireworkMeta(fwm);
            fw.detonate();

            String kuid = kp.getName();
            Bukkit.getScheduler().scheduleSyncDelayedTask(Constants.p, () -> {
                int newrounds = tpr.getRounds() + 1;
                int red = tpr.getRed();
                int blue = tpr.getBlue();
                int t1 = Constants.teams.get(kuid);
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
                        showDuelResume(redp, bluep, true, red, blue, tpr.getStart(), System.currentTimeMillis(), " n ", true, MAIN_COLOR + "ʏᴏᴜ ʟᴏsᴛ", MAIN_COLOR + "ʏᴏᴜ ᴡᴏɴ!", e);
                    } else if (blue > red) {
                        showDuelResume(bluep, redp, true, red, blue, tpr.getStart(), System.currentTimeMillis(), " n ", false, MAIN_COLOR + "ʏᴏᴜ ᴡᴏɴ!", MAIN_COLOR + "ʏᴏᴜ ʟᴏsᴛ", e);
                    } else {
                        showDuelResume(redp, bluep, true, red, blue, tpr.getStart(), System.currentTimeMillis(), " y ", false, "§eᴅʀᴀᴡ", "§eᴅʀᴀᴡ", e);
                    }

                    Bukkit.getScheduler().runTaskLater(Constants.p, () -> {
                        Constants.teams.remove(kuid);
                        Constants.teams.remove(name);
                        kp.teleportAsync(Constants.spawn);
                        p.teleportAsync(Constants.spawn);
                    }, 60L);
                    //boolean resetted;
                    //do {
                    //resetted = true;

                    Constants.inDuel.remove(tpr);
                    updateDuels();
                    updateSpectate();
                    //} while (!ffa.loopyReset(data) && !resetted);
                    return;
                }

                if (newrounds == tpr.getMaxrounds()) {
                    if (red > blue) {
                        showDuelResume(redp, bluep, true, red, blue, tpr.getStart(), System.currentTimeMillis(), " n ", true, MAIN_COLOR + "ʏᴏᴜ ʟᴏsᴛ", MAIN_COLOR + "ʏᴏᴜ ᴡᴏɴ!", e);
                    } else if (blue > red) {
                        showDuelResume(bluep, redp, true, red, blue, tpr.getStart(), System.currentTimeMillis(), " n ", false, MAIN_COLOR + "ʏᴏᴜ ᴡᴏɴ!", MAIN_COLOR + "ʏᴏᴜ ʟᴏsᴛ", e);
                    } else {
                        showDuelResume(redp, bluep, true, red, blue, tpr.getStart(), System.currentTimeMillis(), " y ", false, "§eᴅʀᴀᴡ", "§eᴅʀᴀᴡ", e);
                    }

                    Bukkit.getScheduler().runTaskLater(Constants.p, () -> {
                        Constants.teams.remove(kuid);
                        Constants.teams.remove(name);
                        Constants.inDuel.remove(tpr);
                        kp.teleportAsync(Constants.spawn);
                        p.teleportAsync(Constants.spawn);
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
            playerData.get(name).setBack(null);
            return;
        }
        CustomPlayerDataHolder D0 = playerData.get(name);
        if (D0.isTagged()) {
            D0.setTagged(false);
            Bukkit.getScheduler().cancelTask(D0.getRunnableid());
        }
        D0.setBack(new WorldLocationHolder(
                (int) l.getX(),
                (int) l.getY(),
                (int) l.getZ(),
                l.getWorld()));

        p.sendMessage(Constants.BACK);
        String kp;

        if (killer == null || killer == p) {
            e.setDeathMessage(SECOND_COLOR + "☠ " + name + " §7" + switch (p.getLastDamageCause().getCause()) {
                case ENTITY_EXPLOSION, BLOCK_EXPLOSION -> "blasted themselves";
                case FALL -> "broke their legs";
                case FALLING_BLOCK -> "suffocated";
                case FLY_INTO_WALL -> "thought they're a fly";
                case FIRE_TICK, LAVA -> "burnt into ashes";
                default -> "suicided";
            });
        } else {
            kp = killer.getName();
            CustomPlayerDataHolder D1 = playerData.get(kp);
            Bukkit.getScheduler().cancelTask(D1.getRunnableid());
            D1.setTagged(false);
            D1.incrementMoney(500);
            e.setDeathMessage(SECOND_COLOR + "☠ " + kp + " §7" + switch (p.getLastDamageCause().getCause()) {
                case ENTITY_EXPLOSION -> "exploded " + SECOND_COLOR + name;
                case BLOCK_EXPLOSION -> "imploded " + SECOND_COLOR + name;
                case FALL -> "broke " + SECOND_COLOR + name + "§7's legs";
                case ENTITY_ATTACK, ENTITY_SWEEP_ATTACK -> "sworded " + SECOND_COLOR + name;
                case PROJECTILE -> "shot " + SECOND_COLOR + name + " §7in the ass";
                case FIRE_TICK, LAVA -> "turned " + SECOND_COLOR + name + " §7into ashes";
                default -> "suicided";
            });

            switch (D1.getC()) {
                case 0 -> {
                    Location loc = l.add(0, 1, 0);
                    for (double y = 0; y <= 10; y += 0.05) {
                        w.spawnParticle(Particle.TOTEM, new Location(w, (float) (loc.getX() + 2 * Math.cos(y)), (float) (loc.getY() + y), (float) (loc.getZ() + 2 * Math.sin(y))), 2, 0, 0, 0, 1.0);
                    }
                }
                case 1 -> {
                    Firework fw = (Firework) w.spawnEntity(l.add(0, 1, 0), EntityType.FIREWORK);
                    FireworkMeta fwm = fw.getFireworkMeta();
                    fwm.setPower(2);
                    fwm.addEffect(FireworkEffect.builder().withColor(color.get(Constants.RANDOM.nextInt(color.size()))).withColor(color.get(RANDOM.nextInt(color.size()))).with(FireworkEffect.Type.BALL_LARGE).flicker(true).build());
                    fw.setFireworkMeta(fwm);
                }
                case 2 -> w.strikeLightningEffect(l.add(0, 1, 0));
            }
        }
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        String name = p.getName();
        e.setJoinMessage(JOIN_PREFIX + name);
        p.teleport(spawn);

        CustomPlayerDataHolder D = playerData.get(name);
        if (D == null) {
            Constants.tpa.add(name);
            Constants.msg.add(name);
            playerData.put(name, new CustomPlayerDataHolder(0, 0, 0, 0, 0, 0));
        } else {
            if (D.getT() == 0)
                Constants.tpa.add(name);
            if (D.getM() == 0)
                Constants.msg.add(name);
        }
    }

    @EventHandler
    private void onRespawn(PlayerRespawnEvent e) {
        e.setRespawnLocation(spawn);
    }
}