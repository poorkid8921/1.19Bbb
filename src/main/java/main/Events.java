package main;

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.Pair;
import main.expansions.arenas.Arena;
import main.expansions.duels.Matchmaking;
import main.utils.*;
import main.utils.Instances.BackHolder;
import main.utils.Instances.DuelHolder;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.level.EntityPlayer;
import org.bukkit.*;
import org.bukkit.block.data.type.Piston;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static main.expansions.duels.Utils.*;
import static main.expansions.guis.Utils.*;
import static main.utils.Initializer.*;
import static main.utils.Languages.MAIN_COLOR;
import static main.utils.Languages.SECOND_COLOR;
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
    int b = 0;

    private static <T> T createDataSerializer(UnsafeFunction<PacketDataSerializer, T> callback) {
        PacketDataSerializer data = new PacketDataSerializer(Unpooled.buffer());
        T result = null;
        try {
            result = callback.apply(data);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            data.release();
        }
        return result;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onAsyncCommandTabComplete(AsyncTabCompleteEvent event) {
        event.setCancelled(Utils.isSuspectedScanPacket(event.getBuffer()));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        e.setFormat(chat.getPlayerPrefix("world", p).replace("&", "§") +
                p.getName() +
                SECOND_COLOR +
                " » §r" +
                e.getMessage());
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
            String pwn = pw.getName();
            int t1 = Initializer.teams.get(pwn);

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
                Initializer.teams.remove(pwn);
                Initializer.duel.remove(tpr);
                pw.teleportAsync(Initializer.spawn);
            }, 60L);
        }

        //tpa.remove(getTPArequest(playerName));
        //duel.remove(getDUELrequest(playerName));

        Initializer.back.remove(playerName);
        Initializer.lastReceived.remove(playerName);
        Initializer.msg.remove(playerName);
        Initializer.tpa.remove(playerName);
        Initializer.inFFA.remove(p);

        e.setQuitMessage(LEAVE_PREFIX + playerName);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Inventory c = e.getClickedInventory();
        if (c instanceof PlayerInventory) return;

        Player p = (Player) e.getWhoClicked();
        String pn = p.getName();
        Pair<Integer, String> inv = inInventory.getOrDefault(pn, null);
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
                        ItemStack item = e.getCurrentItem();
                        if (item.getType() == Material.PLAYER_HEAD) {
                            Initializer.spec.put(p.getName(), item.getItemMeta().getPersistentDataContainer()
                                    .get(spectateHead, PersistentDataType.STRING));
                            p.getInventory().close();
                        }
                    }
                    case "1" -> { // kit override

                    }
                    default -> {
                    }
                }
            } // duels: null\dynamic
            case 3 -> {
                String s = inv.second();
                if (s.equals("0")) {
                    e.setCancelled(true);
                    if (slot >= 10 && slot <= 12) {
                        if (e.isLeftClick()) {
                            KitClaimer.claim(p, slot - 9, false);
                            p.closeInventory();
                        } else
                            openKitEditor(p, slot - 9);
                    } else if (slot == 38)
                        openKitRoom(p);
                    else if (slot == 43)
                        openPublicKits(p, 1); // kit menu
                } else {
                    if (slot >= 41)
                        e.setCancelled(true);
                    switch (slot) {
                        case 45 -> openKitMenu(p);
                        case 47 -> {
                            for (int j = 0; j <= 40; ++j) {
                                c.setItem(j, p.getInventory().getItem(j));
                            }
                        }
                        case 50 -> {
                            for (int j = 0; j <= 40; ++j) {
                                c.setItem(j, null);
                            }
                        }
                        case 51 -> {
                                /*String key = pn + "-" + s;
                                if (!Practice.kitMap.get(key).containsKey("name")) {
                                    new me.gatligator.personalkits.GUIs.RenameKit(p, s);
                                    break;
                                }
                                Practice.kitMap.get(key).remove("name");
                                p.sendMessage("§dKit name removed.");
                                inInventory.remove(key);
                                new me.gatligator.personalkits.GUIs.KitEditor(p, "Kit " + s);*/
                        }
                        case 53 -> {
                            SaveEditor.save(p, s, false);

                            String key = pn + "-" + s;
                            if (Practice.kitMap.get(key).containsKey("public")) {
                                Practice.kitMap.get(key).remove("public");
                                p.sendMessage("§dKit made private.");
                                c.setItem(53, ItemCreator.getHead("§a§lMAKE PUBLIC", "Kevos", null));
                                break;
                            }
                            if (Practice.kitMap.get(key).containsKey("items")) {
                                p.sendMessage("§dPublished kit! Other players can now see it by clicking the §bglobe §din §b/kit§d.");
                                Practice.kitMap.get(key).put("public", "to make kit private, delete this entire line (incliding \"public\")");
                                c.setItem(53, ItemCreator.getItem(ChatColor.GREEN + "" + ChatColor.BOLD + "§a§lMAKE PRIVATE", Material.FIREWORK_STAR, null));
                                break;
                            }
                            p.sendMessage("§cCannot publish an empty kit.");
                        }
                    } // kit editor
                }
            } // kits
            case 4 -> {
                if (slot >= 45)
                    e.setCancelled(true);

                if (slot >= 47 && slot <= 51) {
                    int oldPage = Integer.parseInt(inInventory.get(pn).second()) + 46;
                    c.setItem(oldPage, ItemCreator.disEnchant(c.getItem(oldPage)));
                    int newPage = slot - 46;
                    c.setItem(slot, ItemCreator.enchant(e.getCurrentItem()));
                    for (int i = 0; i <= 44; ++i) {
                        c.setItem(i, Practice.kitRoomMap.get(newPage)[i]);
                    }
                    inInventory.put(pn, Pair.of(4, String.valueOf(newPage)));
                } else if (slot == 53) {
                    for (int j = 0; j <= 44; ++j) {
                        c.setItem(j, Practice.kitRoomMap.get(Integer.parseInt(inInventory.get(pn).second()))[j]);
                    }
                } else if (slot == 45)
                    main.expansions.guis.Utils.openKitMenu(p);
            } // virtual kit room
            case 5 -> {
                e.setCancelled(true);
                ItemStack ci = e.getCurrentItem();
                if (slot >= 10 && slot <= 43 && ci.getType() == Material.CHEST &&
                        e.isLeftClick()) {
                    ItemMeta meta = ci.getItemMeta();
                    PersistentDataContainer container = meta.getPersistentDataContainer();
                    KitClaimer.claimPublicKit(p, container.get(itemKey, PersistentDataType.STRING));
                    p.closeInventory();
                    return;
                }

                if (slot == 48 && ci.getType() == Material.PLAYER_HEAD) {
                    main.expansions.guis.Utils.openPublicKits(p, Integer.parseInt(inv.second()) - 1);
                } else if (slot == 50 && ci.getType() == Material.PLAYER_HEAD) {
                    main.expansions.guis.Utils.openPublicKits(p, Integer.parseInt(inv.second()) + 1);
                } else if (slot == 49) {
                    main.expansions.guis.Utils.openKitMenu(p);
                }
            } // public kits
        }
    }

    @EventHandler
    public void onGUIClose(InventoryCloseEvent e) {
        if (e.getInventory() instanceof PlayerInventory) return;
        Player p = (Player) e.getPlayer();
        String pn = p.getName();

        Pair<Integer, String> v = inInventory.get(pn);
        if (v.first() == 3) {
            int s = Integer.parseInt(v.second());
            if (s < 3)
                return;

            SaveEditor.save(p, v.second(), true);
        }

        inInventory.remove(pn);
    }

    private PacketPlayOutNamedEntitySpawn getEntitySpawnPacket(GameProfile gp, Location c, int id) {
        return createDataSerializer((data) -> {
            data.d(id);
            data.a(gp.getId());
            data.writeDouble(c.getX());
            data.writeDouble(c.getY());
            data.writeDouble(c.getZ());
            data.writeByte((byte) ((int) (c.getYaw() * 256.0F / 360.0F)));
            data.writeByte((byte) ((int) (c.getPitch() * 256.0F / 360.0F)));
            return new PacketPlayOutNamedEntitySpawn(data);
        });
    }

    @EventHandler
    private void onPlayerKill(PlayerDeathEvent e) {
        Player p = e.getPlayer();
        String name = p.getName();
        if (Initializer.inFFA.contains(p)) {
            Location c = p.getLocation();
            EntityPlayer ep = ((CraftPlayer) p).getHandle();
            int cached = b++;
            ep.b.a(getEntitySpawnPacket(new GameProfile(UUID.randomUUID(), name),
                    c,
                    cached));
            Bukkit.getScheduler().runTaskLater(Initializer.p, () -> {
                ep.b.a(new PacketPlayOutEntityDestroy(cached));
            }, 100L);
            Initializer.inFFA.remove(p);
        } else e.getDrops().clear();

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

        Location l = p.getLocation();
        BackHolder back = Initializer.back.getOrDefault(name, null);
        if (back == null) {
            Initializer.back.put(name, new BackHolder(l));
        } else back.setBack(l);

        p.sendMessage(Languages.BACK);

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
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        String name = p.getName();

        if (Practice.config.get("r." + name + ".t") == null) Initializer.tpa.add(name);
        if (Practice.config.get("r." + name + ".m") == null) Initializer.msg.add(name);

        p.teleportAsync(spawn);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        e.setRespawnLocation(spawn);
    }

    @FunctionalInterface
    private interface UnsafeFunction<K, T> {
        T apply(K k) throws Exception;
    }
}