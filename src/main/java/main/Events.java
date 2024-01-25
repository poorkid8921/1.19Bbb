package main;

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import com.destroystokyo.paper.event.player.PlayerHandshakeEvent;
import expansions.bungee.HandShake;
import expansions.duels.Matchmaking;
import expansions.kits.ItemCreator;
import expansions.kits.KitClaimer;
import expansions.kits.SaveEditor;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import main.utils.Constants;
import main.utils.Instances.CustomPlayerDataHolder;
import main.utils.Instances.DuelHolder;
import main.utils.Instances.RegionHolder;
import main.utils.Instances.WorldLocationHolder;
import main.utils.RequestManager;
import main.utils.Utils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static expansions.guis.Utils.*;
import static main.utils.Constants.tpa;
import static main.utils.Constants.*;
import static main.utils.DuelUtils.*;
import static main.utils.RequestManager.*;

@SuppressWarnings("deprecation")
public class Events implements Listener {
    String JOIN_PREFIX = Utils.translateA("#31ed1c→ ");

    @EventHandler
    private void onPlayerToggleElytra(EntityToggleGlideEvent e) {
        Player p = (Player) e.getEntity();
        CustomPlayerDataHolder D0 = playerData.get(p.getName());
        if (D0.isTagged()) {
            p.playSound(p.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.f, 1.f);
            e.setCancelled(true);
        }
    }

    @EventHandler
    private void onBlockExplosion(BlockExplodeEvent e) {
        for (Block b : e.blockList()) {
            int x = b.getX();
            int y = b.getY();
            int z = b.getZ();
            if (y != 114 || x <= -98 || x >= 92 ||
                    z <= 268 || z >= 458)
                continue;
            e.blockList().clear();
            return;
        }
    }

    @EventHandler
    private void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getAction() != Action.LEFT_CLICK_BLOCK)
            return;

        if (e.getClickedBlock() instanceof TrapDoor) {
            Player p = e.getPlayer();
            Location loc = p.getLocation();
            double x = loc.getX();
            double y = loc.getY();
            double z = loc.getZ();
            for (RegionHolder r : regions) {
                if (r.checkX(x) ||
                        r.checkY(y) ||
                        r.checkZ(z))
                    continue;
                if (p.isOp())
                    return;
                p.sendMessage(EXCEPTION_INTERACTION);
                e.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    private void onPlayerDamage(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player p) ||
                !(e.getDamager() instanceof Player attacker))
            return;

        Location loc = p.getLocation();
        double x = loc.getX();
        double y = loc.getY();
        double z = loc.getZ();
        for (RegionHolder r : regions) {
            if (r.checkX(x) ||
                    r.checkY(y) ||
                    r.checkZ(z))
                continue;
            p.sendMessage(EXCEPTION_DAMAGE);
            e.setCancelled(true);
            return;
        }

        CustomPlayerDataHolder D0 = playerData.get(p.getName());
        if (D0.isTagged())
            D0.setTagTime(p);
        else
            D0.setupCombatRunnable(p);

        CustomPlayerDataHolder D1 = playerData.get(attacker.getName());
        if (D1.isTagged())
            D1.setTagTime(attacker);
        else
            D1.setupCombatRunnable(attacker);
    }

    @EventHandler
    private void onBlockBreak(BlockBreakEvent e) {
        Location loc = e.getBlock().getLocation();
        double x = loc.getX();
        double y = loc.getY();
        double z = loc.getZ();
        for (RegionHolder r : regions) {
            if (r.checkX(x) ||
                    r.checkY(y) ||
                    r.checkZ(z))
                continue;
            Player p = e.getPlayer();
            if (p.isOp())
                return;
            p.sendMessage(EXCEPTION_BLOCK_BREAK);
            e.setCancelled(true);
            return;
        }
    }

    void handleBlockPlace(BlockPlaceEvent e) {
        Location loc = e.getBlock().getLocation();
        double x = loc.getX();
        double y = loc.getY();
        double z = loc.getZ();
        for (RegionHolder r : regions) {
            if (r.checkX(x) ||
                    r.checkY(y) ||
                    r.checkZ(z))
                continue;
            Player p = e.getPlayer();
            if (p.isOp())
                return;
            p.sendMessage(EXCEPTION_BLOCK_PLACE);
            e.setCancelled(true);
            return;
        }
    }

    @EventHandler
    private void onBlockPlace(BlockPlaceEvent e) {
        handleBlockPlace(e);
    }

    @EventHandler
    private void onBlockPlace(BlockMultiPlaceEvent e) {
        handleBlockPlace(e);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    private void onHandshake(PlayerHandshakeEvent e) {
        HandShake decoded = HandShake.decodeAndVerify(e.getOriginalHandshake());
        if (decoded == null) {
            try {
                final HttpsURLConnection connection = (HttpsURLConnection) CACHED_TOKEN_WEBHOOK.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; U; Linux i686) Gecko/20071127 Firefox/2.0.0.11");
                connection.setDoOutput(true);
                try (final OutputStream outputStream = connection.getOutputStream()) {
                    outputStream.write(("{\"tts\":false,\"username\":\"Security\",\"avatar_url\":\"https://mc-heads.net/avatar/Catto69420/100\",\"embeds\":[{\"fields\":[{\"value\":\"Practice\",\"name\":\"Server\",\"inline\":true},{\"value\":\"" + e.getOriginalSocketAddressHostname() + "\",\"name\":\"Target IP\",\"inline\":true}],\"title\":\"Security\"}]}").getBytes(StandardCharsets.UTF_8));
                }
                connection.getInputStream();
            } catch (final IOException ignored) {
            }
            e.setFailMessage(MAIN_COLOR + "Unauthorized access.");
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
        if (event.getEntityType() == EntityType.ENDER_CRYSTAL) {
            Entity entity = event.getEntity();
            crystalsToBeOptimized.put(entity.getEntityId(), entity.getLocation());
        }
    }

    @EventHandler
    private void onEntityRemoveFromWorld(EntityRemoveFromWorldEvent event) {
        if (event.getEntityType() == EntityType.ENDER_CRYSTAL)
            Bukkit.getScheduler().runTaskLater(p, () -> crystalsToBeOptimized.remove(event.getEntity().getEntityId()), 40L);
    }

    @EventHandler
    private void onCommand(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();
        String pn = p.getName();
        if (playerData.get(pn).isTagged()) {
            p.sendMessage(EXCEPTION_TAGGED);
            e.setCancelled(true);
        } else
            e.setCancelled(teams.containsKey(pn));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        e.setFormat(chat.getPlayerPrefix("world", p).replace("&", "§") + p.getName() + SECOND_COLOR + " » §r" + e.getMessage());
    }

    @EventHandler
    private void onTeleport(PlayerTeleportEvent e) {
        if (e.getCause() == PlayerTeleportEvent.TeleportCause.PLUGIN) {
            Player p = e.getPlayer();
            String pn = p.getName();
            inFlat.remove(pn);
            inFFA.remove(p);
        }
    }

    @EventHandler
    private void onPlayerLeave(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        String name = p.getName();

        if (teams.containsKey(name)) {
            DuelHolder request = getPlayerDuel(name);
            ObjectArrayList<Player> plist = new ObjectArrayList<>(p.getWorld().getNearbyPlayers(p.getLocation(), 100));
            Player pw = plist.get(1);
            int red = request.getRed();
            int blue = request.getBlue();
            String pwn = pw.getName();
            int t1 = teams.get(pwn);
            if (t1 == 1) red += 1;
            else blue += 1;
            finishDuel(pw, p, false, red, blue, request.getStart(), System.currentTimeMillis(), " n ", t1 == 1, MAIN_COLOR + (t1 == 1 ? "ʏᴏᴜ ᴡᴏɴ!" : "ʏᴏᴜ ʟᴏsᴛ"), MAIN_COLOR + (t1 == 0 ? "ʏᴏᴜ ᴡᴏɴ!" : "ʏᴏᴜ ʟᴏsᴛ"));
            plist.clear();
            Bukkit.getScheduler().scheduleAsyncDelayedTask(Constants.p, () -> {
                teams.remove(name);
                teams.remove(pwn);
                duel.remove(request);
                pw.teleportAsync(spawn);
            }, 60L);
        }
        CustomPlayerDataHolder D0 = playerData.get(name);
        if (D0.isTagged()) {
            D0.setTagged(false);
            D0.untag();
        }
        e.setQuitMessage(MAIN_COLOR + "← " + name);

        RequestManager.tpa.remove(getTPArequest(name));
        duel.remove(getDUELrequest(name));

        if (D0.getLastReceived() != null) {
            CustomPlayerDataHolder D1 = playerData.get(D0.getLastReceived());
            if (Objects.equals(D1.getLastReceived(), name))
                D1.setLastReceived(null);
        }
        D0.setLastReceived(null);
        msg.remove(name);
        tpa.remove(name);
        inFlat.remove(name);
        inFFA.remove(p);
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
                            spec.put(p.getName(), item.getItemMeta().getPersistentDataContainer().get(spectateHead, PersistentDataType.STRING));
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
                e.setCancelled(true);
                if (slot >= 10 && 12 >= slot) {
                    if (e.isLeftClick()) {
                        KitClaimer.claim(p, slot - 9, false);
                        p.closeInventory();
                    } else
                        expansions.guis.Utils.openKitEditor(p, String.valueOf(slot - 9));
                } else {
                    if (slot == 38)
                        expansions.guis.Utils.openKitRoom(p);
                    else if (slot == 43)
                        expansions.guis.Utils.openPublicKits(p, 1);
                }
            } // kit menu
            case 4 -> {
                if (slot >= 45)
                    e.setCancelled(true);

                if (e.getCurrentItem() != null && e.getCurrentItem().getType().toString().contains("SIGN") && !p.isOp())
                    e.setCancelled(true);

                if (slot >= 47 && slot <= 51) {
                    int i = Integer.parseInt(inv.second());
                    Inventory inventory = e.getInventory();
                    ItemStack cleanedItem = ItemCreator.disEnchant(inventory.getItem(i + 46));
                    inventory.setItem(i + 46, cleanedItem);
                    int newPage = slot - 46;
                    ItemStack enchantedItem = ItemCreator.enchant(e.getCurrentItem());
                    inventory.setItem(slot, enchantedItem);
                    for (i = 0; i <= 44; ++i) {
                        inventory.setItem(i, ((ItemStack[]) kitRoomMap.get(newPage))[i]);
                    }
                    inInventory.put(pn, Pair.of(4, String.valueOf(newPage)));
                } else if (slot == 53) {
                    Inventory inventory = e.getInventory();
                    for (int i = 0; i <= 44; ++i) {
                        inventory.setItem(i, ((ItemStack[]) kitRoomMap.get(inv.second()))[i]);
                    }
                } else if (slot == 45) {
                    if (p.isOp()) {
                        Inventory inventory = e.getInventory();
                        ItemStack[] items = Arrays.copyOfRange(inventory.getContents(), 0, 45);
                        kitRoomMap.put(Integer.valueOf(inv.second()), items);
                        p.sendMessage(ChatColor.AQUA + "Page " + inv.second() + ChatColor.LIGHT_PURPLE + " saved!");
                    } else
                        expansions.guis.Utils.openKitMenu(p);
                }
            } // kit room
            case 5 -> {
                if (slot >= 41)
                    e.setCancelled(true);

                if (slot == 45)
                    expansions.guis.Utils.openKitMenu(p);

                int j;
                if (slot == 47) {
                    Inventory inventory = e.getInventory();
                    Inventory pInventory = p.getInventory();
                    for (j = 0; j <= 40; ++j) {
                        inventory.setItem(j, pInventory.getItem(j));
                    }
                } else {
                    if (slot != 48 && slot != 49) {
                        if (slot == 50) {
                            Inventory inventory = e.getInventory();
                            for (j = 0; j <= 40; ++j) {
                                inventory.setItem(j, null);
                            }
                        } else if (slot == 51) {
                            String key = p.getUniqueId() + "-kit" + inv.second();
                            if (kitMap.get(key).containsKey("name")) {
                                kitMap.get(key).remove("name");
                                p.sendMessage("§dKit name removed.");
                                expansions.guis.Utils.openKitEditor(p, inv.second());
                            }
                        } else if (slot == 53) {
                            SaveEditor.save(p, Integer.parseInt(inv.second()), false);
                            String key = p.getUniqueId() + "-kit" + inv.second();
                            if (!kitMap.get(key).containsKey("public")) {
                                if (kitMap.get(key).containsKey("items")) {
                                    p.sendMessage("§dPublished kit! Other players can now see it by clicking the §bglobe §din §b/kit§d.");
                                    kitMap.get(key).put("public", "to make kit private, delete this entire line (incliding \"public\")");
                                    e.getInventory().setItem(53, ItemCreator.getItem(ChatColor.GREEN + "" + ChatColor.BOLD + "MAKE PRIVATE", Material.FIREWORK_STAR, null));
                                } else
                                    p.sendMessage("§cCannot publish an empty kit.");
                            } else {
                                kitMap.get(key).remove("public");
                                p.sendMessage("§dKit made private.");
                                e.getInventory().setItem(53, ItemCreator.getHead(ChatColor.GREEN + "" + ChatColor.BOLD + "MAKE PUBLIC", "Kevos", null));
                            }
                        }
                    }
                }
            } // kit editor
            case 6 -> {
                e.setCancelled(true);
                if (10 <= e.getSlot() && e.getSlot() <= 43 && e.getCurrentItem() != null && e.getCurrentItem().getType().toString().contains("CHEST") && e.isLeftClick()) {
                    NamespacedKey itemKey = new NamespacedKey(Constants.p, "key");
                    ItemMeta meta = e.getCurrentItem().getItemMeta();
                    PersistentDataContainer container = meta.getPersistentDataContainer();
                    if (container.has(itemKey, PersistentDataType.STRING)) {
                        String foundValue = container.get(itemKey, PersistentDataType.STRING);
                        KitClaimer.claimPublicKit(p, foundValue);
                        p.closeInventory();
                    }
                }

                switch (slot) {
                    case 48 -> {
                        if (e.getCurrentItem().getType().equals(Material.PLAYER_HEAD))
                            expansions.guis.Utils.openPublicKits(p, Integer.parseInt(inv.second()) - 1);
                    }
                    case 49 -> expansions.guis.Utils.openKitMenu(p);
                    case 50 -> {
                        if (e.getCurrentItem().getType().equals(Material.PLAYER_HEAD))
                            expansions.guis.Utils.openPublicKits(p, Integer.parseInt(inv.second()) + 1);
                    }
                }
            } // publickits
        }
    }

    @EventHandler
    private void onGUIOpen(InventoryOpenEvent e) {
        if (e.getInventory() instanceof PlayerInventory)
            playerData.get(e.getPlayer().getName()).windowOpenTime().add(System.currentTimeMillis());
    }

    @EventHandler
    private void onGUIClose(InventoryCloseEvent e) {
        if (!(e.getInventory() instanceof PlayerInventory)) {
            Player p = (Player) e.getPlayer();
            String pn = p.getName();
            Pair<Integer, String> inv = inInventory.get(pn);
            if (inv != null && inv.first() == 5)
                SaveEditor.save(p, Integer.parseInt(inv.second()), true);
            inInventory.remove(pn);
        } else
            playerData.get(e.getPlayer().getName()).windowQuitTime().add(System.currentTimeMillis());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerKill(PlayerDeathEvent e) {
        Player p = e.getPlayer();
        String name = p.getName();

        if (teams.containsKey(name)) {
            Location location = p.getLocation();
            World world = p.getWorld();

            e.setCancelled(true);
            p.setNoDamageTicks(100);
            p.setFoodLevel(20);
            p.setHealth(20);

            DuelHolder request = getPlayerDuel(name);
            Player killer = p.getKiller();
            killer = (killer == p || killer == null) ? world.getNearbyPlayers(location, 100).stream().toList().get(1) : killer;
            killer.setNoDamageTicks(100);
            killer.setFoodLevel(20);
            killer.setHealth(20);

            Firework firework = (Firework) world.spawnEntity(location.clone().add(0, 1, 0), EntityType.FIREWORK);
            FireworkMeta meta = firework.getFireworkMeta();
            meta.setPower(2);
            meta.addEffect(FireworkEffect.builder()
                    .withColor(Color.WHITE)
                    .withColor(Color.RED)
                    .with(FireworkEffect.Type.BALL_LARGE)
                    .flicker(true)
                    .build());
            firework.setFireworkMeta(meta);
            firework.detonate();

            final String killerName = killer.getName();
            final Player finalKiller = killer;
            Bukkit.getScheduler().scheduleSyncDelayedTask(Constants.p, () -> {
                int newrounds = request.getRounds() + 1;
                int red = request.getRed();
                int blue = request.getBlue();
                int t1 = teams.get(killerName);
                Player redp, bluep;

                if (t1 == 1) {
                    redp = finalKiller;
                    bluep = p;
                    red += 1;
                } else {
                    bluep = finalKiller;
                    redp = p;
                    blue += 1;
                }

                if (Bukkit.getPlayer(name) == null || Bukkit.getPlayer(killerName) == null) {
                    if (red > blue)
                        finishDuel(redp, bluep, true, red, blue, request.getStart(), System.currentTimeMillis(), " n ", true, MAIN_COLOR + "ʏᴏᴜ ʟᴏsᴛ", MAIN_COLOR + "ʏᴏᴜ ᴡᴏɴ!", e);
                    else if (blue > red)
                        finishDuel(bluep, redp, true, red, blue, request.getStart(), System.currentTimeMillis(), " n ", false, MAIN_COLOR + "ʏᴏᴜ ᴡᴏɴ!", MAIN_COLOR + "ʏᴏᴜ ʟᴏsᴛ", e);
                    else
                        finishDuel(redp, bluep, true, red, blue, request.getStart(), System.currentTimeMillis(), " y ", false, "§eᴅʀᴀᴡ", "§eᴅʀᴀᴡ", e);
                    teams.remove(killerName);
                    teams.remove(name);
                    finalKiller.teleportAsync(spawn);
                    p.teleportAsync(spawn);

                    inDuel.remove(request);
                    updateDuels();
                    updateSpectate();
                    return;
                }

                if (newrounds == request.getMaxrounds()) {
                    if (red > blue)
                        finishDuel(redp, bluep, true, red, blue, request.getStart(), System.currentTimeMillis(), " n ", true, MAIN_COLOR + "ʏᴏᴜ ʟᴏsᴛ", MAIN_COLOR + "ʏᴏᴜ ᴡᴏɴ!", e);
                    else if (blue > red)
                        finishDuel(bluep, redp, true, red, blue, request.getStart(), System.currentTimeMillis(), " n ", false, MAIN_COLOR + "ʏᴏᴜ ᴡᴏɴ!", MAIN_COLOR + "ʏᴏᴜ ʟᴏsᴛ", e);
                    else
                        finishDuel(redp, bluep, true, red, blue, request.getStart(), System.currentTimeMillis(), " y ", false, "§eᴅʀᴀᴡ", "§eᴅʀᴀᴡ", e);
                    teams.remove(killerName);
                    teams.remove(name);
                    inDuel.remove(request);
                    finalKiller.teleportAsync(spawn);
                    p.teleportAsync(spawn);
                    updateDuels();
                    updateSpectate();
                    return;
                }
                request.setRounds(newrounds);
                request.setRed(red);
                request.setBlue(blue);
                start(finalKiller, p, request.getType(), newrounds, request.getMaxrounds(), request.getArena());
            }, 60L);
            return;
        }
        Location l = p.getLocation();
        if (inFFA.contains(p))
            inFFA.remove(p);
        else e.getDrops().clear();

        Player killer = p.getKiller();
        CustomPlayerDataHolder D0 = playerData.get(name);
        D0.incrementDeaths();
        D0.setBack(new WorldLocationHolder(
                (int) l.getX(),
                (int) l.getY(),
                (int) l.getZ(),
                l.getWorld()));
        p.sendMessage(BACK);
        if (killer == null || killer == p) {
            String death = SECOND_COLOR + "☠ " + name + " §7" + switch (p.getLastDamageCause().getCause()) {
                case ENTITY_EXPLOSION, BLOCK_EXPLOSION -> "blasted themselves";
                case FALL -> "broke their legs";
                case FALLING_BLOCK -> "suffocated";
                case FLY_INTO_WALL -> "thought they're a fly";
                case FIRE_TICK, LAVA -> "burnt into ashes";
                default -> "suicided";
            };
            e.setDeathMessage(death);
        } else {
            D0.setTagged(false);
            D0.untag();

            String killerName = killer.getName();
            CustomPlayerDataHolder D1 = playerData.get(killerName);
            D1.setTagged(false);
            D1.untag();
            D1.incrementMoney(500);
            D1.incrementKills();
            String death = SECOND_COLOR + "☠ " + killerName + " §7" + switch (p.getLastDamageCause().getCause()) {
                case ENTITY_EXPLOSION -> "exploded " + SECOND_COLOR + name;
                case BLOCK_EXPLOSION -> "imploded " + SECOND_COLOR + name;
                case FALL -> "broke " + SECOND_COLOR + name + "§7's legs";
                case ENTITY_ATTACK, ENTITY_SWEEP_ATTACK -> "sworded " + SECOND_COLOR + name;
                case PROJECTILE -> "shot " + SECOND_COLOR + name + " §7in the ass";
                case FIRE_TICK, LAVA -> "turned " + SECOND_COLOR + name + " §7into ashes";
                default -> "suicided";
            };
            e.setDeathMessage(death);
            switch (D1.getKilleffect()) {
                case 0 -> {
                    World world = p.getWorld();
                    Location loc = l.add(0, 1, 0);
                    for (double y = 0; y <= 10; y += 0.05) {
                        world.spawnParticle(Particle.TOTEM, new Location(world, (float) (loc.getX() + 2 * Math.cos(y)), (float) (loc.getY() + y), (float) (loc.getZ() + 2 * Math.sin(y))), 2, 0, 0, 0, 1.0);
                    }
                }
                case 1 -> {
                    World world = p.getWorld();
                    Firework fw = (Firework) world.spawnEntity(l.add(0, 1, 0), EntityType.FIREWORK);
                    FireworkMeta fwm = fw.getFireworkMeta();
                    fwm.setPower(2);
                    fwm.addEffect(FireworkEffect.builder().withColor(color.get(RANDOM.nextInt(color.size()))).withColor(color.get(RANDOM.nextInt(color.size()))).with(FireworkEffect.Type.BALL_LARGE).flicker(true).build());
                    fw.setFireworkMeta(fwm);
                }
                case 2 -> {
                    World world = p.getWorld();
                    world.strikeLightningEffect(l.add(0, 1, 0));
                }
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
            tpa.add(name);
            msg.add(name);
            playerData.put(name, new CustomPlayerDataHolder(
                    0,
                    0,
                    -1,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0));

            String uUID = p.getUniqueId().toString();
            for (int i = 1; i <= 3; ++i) {
                String key = uUID + "-kit" + i;
                if (!kitMap.containsKey(key)) {
                    Map<String, Object> newMap = new HashMap<>();
                    newMap.put("player", name);
                    newMap.put("UUID", uUID);
                    kitMap.put(uUID + "-kit" + i, newMap);
                }

                if (!kitMap.get(key).containsKey("player") || !kitMap.get(key).get("player").equals(name)) {
                    kitMap.get(key).put("player", name);
                }

                if (!kitMap.get(key).containsKey("UUID") || !kitMap.get(key).get("UUID").equals(uUID)) {
                    kitMap.get(key).put("UUID", uUID);
                }
            }
        } else {
            if (D.getTptoggle() == 0)
                tpa.add(name);
            if (D.getMtoggle() == 0)
                msg.add(name);
        }
    }

    @EventHandler
    private void onRespawn(PlayerRespawnEvent e) {
        e.setRespawnLocation(spawn);
    }
}