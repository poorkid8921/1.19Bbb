package main;

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import main.expansions.kits.*;
import main.utils.DiscordWebhook;
import main.utils.Instances.BackHolder;
import main.utils.Instances.InventoryInstanceReport;
import main.utils.Instances.InventoryInstanceShop;
import main.utils.Instances.TotemHolder;
import main.utils.ItemCreator;
import main.utils.Messages.Initializer;
import main.utils.Utils;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import org.bukkit.*;
import org.bukkit.block.data.type.Piston;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static main.utils.RequestManager.getTPArequest;
import static main.utils.RequestManager.removeTPArequest;

@SuppressWarnings("deprecation")
public class Events implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    private void onAsyncCommandTabComplete(AsyncTabCompleteEvent event) {
        event.setCancelled(Utils.isSuspectedScanPacket(event.getBuffer()));
    }

    public void flag(int ping, String name, long ms) {
        String msg = Utils.translateo("&6" + name + " totemed in less than " + ms + "ms! &7 " + ping + "ms");
        Bukkit.getOnlinePlayers().stream().filter(r -> r.hasPermission("has.staff")).forEach(r -> r.sendMessage(msg));
        Initializer.EXECUTOR.submit(() -> {
            DiscordWebhook webhook = new DiscordWebhook("https://discord.com/api/webhooks/1154454290233036810/DMkRG4YXKvUcbT5lq03QKNRL-YDYyFC0vFl0Akct_aCvc0-R7XG2C86KjsS7npR-PhjF");
            webhook.setUsername("Flag");
            webhook.addEmbed(new DiscordWebhook.EmbedObject().setTitle("Auto Totem").addField("Suspect", name, true).addField("Milliseconds", String.valueOf(ms), true).addField("Ping", String.valueOf(ping), true).setColor(java.awt.Color.ORANGE));
            try {
                webhook.execute();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    @EventHandler
    private void antiAuto(PlayerSwapHandItemsEvent e) {
        Player ent = e.getPlayer();

        String playerUniqueId = ent.getName();
        TotemHolder th = Initializer.playerstoteming.getOrDefault(playerUniqueId, null);
        if (th == null) return;

        long f = th.getF();
        long s = th.getS();
        long t = th.getT();

        flag(ent.getPing(), playerUniqueId, System.currentTimeMillis() - th.getF());
        Bukkit.getLogger().warning(playerUniqueId + " might be auto toteming.");
        if (s == 0L && t == 0L) {
            th.setS(System.currentTimeMillis() - 500L);
            return;
        } else if (t == 0L) {
            th.setT(System.currentTimeMillis() - 500L);
            return;
        }

        Initializer.playerstoteming.remove(playerUniqueId);
        if (((f + s + t) / 3) > 50L) {
            // FLAG
            ent.kickPlayer("Cheating");
        }
    }

    @EventHandler
    public void antiAuto2(EntityResurrectEvent e) {
        Initializer.playerstoteming.putIfAbsent(e.getEntity().getName(), new TotemHolder(System.currentTimeMillis() + 500, 0, 0));
    }

    @EventHandler
    private void ItemConsume(PlayerItemConsumeEvent e) {
        e.setCancelled(e.getItem().getType().equals(Material.ENCHANTED_GOLDEN_APPLE));
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
    //

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
        Initializer.ffaconst.remove(e.getPlayer());
    }

    @EventHandler
    private void onPlayerLeave(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        String playerName = p.getName();

        // combat related
        /*if (Initializer.inCombat.contains(playerName)) {
            e.getPlayer().setHealth(0.0D);
        }*/

        // requests
        removeTPArequest(getTPArequest(playerName));
        // misc
        Initializer.playerstoteming.remove(playerName);
        Initializer.back.remove(playerName);
        Initializer.lastReceived.remove(playerName);
        Initializer.msg.remove(playerName);
        Initializer.tpa.remove(playerName);
        Initializer.ffaconst.remove(p);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getClickedInventory() instanceof PlayerInventory) return;

        if (e.getInventory().getHolder() instanceof InventoryInstanceShop holder) {
            e.setCancelled(true);
            ItemStack currentItem = e.getCurrentItem();
            if (!currentItem.getItemMeta().hasLore()) return;

            holder.whenClicked(e.getCurrentItem(), e.getSlot());
            return;
        } else if (e.getInventory().getHolder() instanceof InventoryInstanceReport holder) {
            e.setCancelled(true);
            ItemStack currentItem = e.getCurrentItem();
            if (!currentItem.getItemMeta().hasLore()) return;

            holder.whenClicked(currentItem, e.getSlot(), holder.getArg());
            return;
        }

        Player player = (Player) e.getWhoClicked();
        String pn = player.getName();
        int k = main.expansions.kits.Utils.editorChecker.getOrDefault(pn, 0);
        if (k == 0) {
            int b = main.expansions.kits.Utils.checker.getOrDefault(pn, -1);
            switch (b) {
                case 0 -> {
                    e.setCancelled(true);
                    if (10 <= e.getSlot() && e.getSlot() <= 12) {
                        if (e.isLeftClick()) {
                            main.expansions.kits.Utils.claim(player, e.getSlot() - 9, false);
                            player.closeInventory();
                        } else new KitEditorInventory(player, e.getSlot() - 9);
                    } else if (e.getSlot() == 38) new KitRoomInventory(player);
                    else if (e.getSlot() == 43) new PublicKitsInventory(player, 1);
                }
                case 1 -> {
                    if (e.getSlot() >= 45 || (e.getCurrentItem() != null && e.getCurrentItem().getType().toString().contains("SIGN") && !e.getWhoClicked().hasPermission("personalkits.edit"))) {
                        e.setCancelled(true);
                    }
                    if (47 <= e.getSlot() && e.getSlot() <= 51) {
                        final ItemStack cleanedItem = ItemCreator.disEnchant(e.getInventory().getItem(47));
                        e.getInventory().setItem(47, cleanedItem);
                        final int newPage = e.getSlot() - 46;
                        final ItemStack enchantedItem = ItemCreator.enchant(e.getCurrentItem());
                        e.getInventory().setItem(e.getSlot(), enchantedItem);
                        for (int i = 0; i <= 44; ++i) {
                            e.getInventory().setItem(i, main.expansions.kits.Utils.kitRoomMap.get(newPage)[i]);
                        }
                        main.expansions.kits.Utils.checker.put(pn, newPage);
                    } else if (e.getSlot() == 53) {
                        for (int j = 0; j <= 44; ++j) {
                            e.getInventory().setItem(j, main.expansions.kits.Utils.kitRoomMap.get(main.expansions.kits.Utils.checker.get(pn))[j]);
                        }
                    } else if (e.getSlot() == 45) {
                        if (player.hasPermission("personalkits.edit")) {
                            final ItemStack[] items = Arrays.copyOfRange(e.getInventory().getContents(), 0, 45);
                            main.expansions.kits.Utils.kitRoomMap.put(main.expansions.kits.Utils.checker.get(pn), items);
                            player.sendMessage(ChatColor.AQUA + "Page " + main.expansions.kits.Utils.checker.get(pn) + ChatColor.LIGHT_PURPLE + " saved!");
                        } else new KitMenuInventory(player);
                    }
                }
                default -> {
                    e.setCancelled(true);
                    if (10 <= e.getSlot() && e.getSlot() <= 43 && e.getCurrentItem() != null && e.getCurrentItem().getType().toString().contains("CHEST") && (e.isLeftClick() || e.getClick().equals(ClickType.DOUBLE_CLICK))) {
                        final NamespacedKey itemKey = new NamespacedKey(Initializer.p, "key");
                        final ItemMeta meta = e.getCurrentItem().getItemMeta();
                        final PersistentDataContainer container = meta.getPersistentDataContainer();
                        if (container.has(itemKey, PersistentDataType.STRING)) {
                            final String foundValue = container.get(itemKey, PersistentDataType.STRING);
                            main.expansions.kits.Utils.claimPublicKit(player, foundValue);
                            player.closeInventory();
                        }
                    }
                    if (e.getSlot() == 48 && e.getCurrentItem().getType().toString().contains("PLAYER_HEAD"))
                        new PublicKitsInventory(player, main.expansions.kits.Utils.publicChecker.get(pn) - 1);
                    else if (e.getSlot() == 50 && e.getCurrentItem().getType().toString().contains("PLAYER_HEAD"))
                        new PublicKitsInventory(player, main.expansions.kits.Utils.publicChecker.get(pn) + 1);
                    else if (e.getSlot() == 49) {
                        new KitMenuInventory(player);
                    }
                }
            }
        } else {
            switch (e.getSlot()) {
                case 41 -> e.setCancelled(true);
                case 45 -> new KitMenuInventory(player);
                case 47 -> {
                    for (int j = 0; j <= 40; ++j) {
                        e.getInventory().setItem(j, player.getInventory().getItem(j));
                    }
                }
                case 50 -> {
                    for (int j = 0; j <= 40; ++j) {
                        e.getInventory().setItem(j, null);
                    }
                }
                case 53 -> {
                    String key = player.getName() + "-" + k;
                    main.expansions.kits.Utils.save(player, k, false);
                    if (main.expansions.kits.Utils.kitMap.get(key).containsKey("public")) {
                        main.expansions.kits.Utils.kitMap.get(key).remove("public");
                        player.sendMessage("§dKit made private.");
                        e.getInventory().setItem(53, ItemCreator.getHead(ChatColor.GREEN + "" + ChatColor.BOLD + "MAKE PUBLIC", "Kevos", null));
                        break;
                    }
                    if (main.expansions.kits.Utils.kitMap.get(key).containsKey("items")) {
                        player.sendMessage("§dPublished kit! Other players can now see it by clicking the §bglobe §din §b/kit§d.");
                        main.expansions.kits.Utils.kitMap.get(key).put("public", "to make kit private, delete this entire line (incliding \"public\")");
                        e.getInventory().setItem(53, ItemCreator.getItem(ChatColor.GREEN + "" + ChatColor.BOLD + "MAKE PRIVATE", Material.FIREWORK_STAR, null));
                        break;
                    }
                    player.sendMessage("§cCannot publish an empty kit.");
                }
            }
        }
    }

    @EventHandler
    public void onGUIClose(InventoryCloseEvent e) {
        if (e.getInventory() instanceof PlayerInventory) return;

        Player player = (Player) e.getPlayer();
        String pn = player.getName();
        int k = main.expansions.kits.Utils.editorChecker.getOrDefault(pn, -1);

        switch (k) {
            case 0, 1 -> main.expansions.kits.Utils.checker.remove(pn);
            case -1 -> main.expansions.kits.Utils.publicChecker.remove(pn);
            default -> {
                main.expansions.kits.Utils.save(player, k, true);
                main.expansions.kits.Utils.editorChecker.remove(pn);
            }
        }
    }

    @EventHandler
    private void onPlayerKill(PlayerDeathEvent e) {
        Player p = e.getPlayer();
        if (!Initializer.ffaconst.contains(p)) e.getDrops().clear();
        else Initializer.ffaconst.remove(p);

        String name = p.getName();
        Player killer = p.getKiller();
        if (killer == null) return;

        Location l = p.getLocation();
        BackHolder back = Initializer.back.getOrDefault(name, null);
        if (back == null) {
            Initializer.back.put(name, new BackHolder(Utils.Locationfrom(l)));
        } else back.setBack(Utils.Locationfrom(l));

        p.sendMessage(Utils.translate("&7Use #fc282f/back &7to return to your death location."));

        try {
            Initializer.econ.depositPlayer(killer, 5);
        } catch (RuntimeException en) {
            en.printStackTrace();
        }

        int peffect = Initializer.p.getCustomConfig().getInt("r." + killer + ".killeffect", -1);

        Location loc = p.getLocation().add(0, 1, 0);
        switch (peffect) {
            case 0 -> {
                World w = loc.getWorld();
                for (double y = 0; y <= 10; y += 0.05) {
                    double x = 2 * Math.cos(y);
                    double z = 2 * Math.sin(y);
                    w.spawnParticle(Particle.TOTEM, new Location(w, (float) (loc.getX() + x), (float) (loc.getY() + y), (float) (loc.getZ() + z)), 2, 0, 0, 0, 1.0);
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
            Initializer.s.getScheduler().scheduleSyncDelayedTask(Initializer.p, () -> {
                p.setHealth(20);
                p.kickPlayer("Internal Exception: io.netty.handler.timeout.ReadTimeoutException");
            }, 2L);
            e.setJoinMessage(null);
            return;
        }

        String name = p.getName();

        if (Utils.manager().get("r." + name + ".t") == null) Initializer.tpa.add(name);
        if (Utils.manager().get("r." + name + ".m") == null) Initializer.msg.add(name);

        Utils.spawn(p);
        Bukkit.getScheduler().scheduleSyncDelayedTask(Initializer.p, () -> {
            List<String> m = Languages.MOTD;
            p.sendMessage(m.get(0), m.get(1), m.get(2));
        }, 5L);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        e.setRespawnLocation(Initializer.spawn);
    }
}