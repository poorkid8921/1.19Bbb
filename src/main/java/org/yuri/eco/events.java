package org.yuri.eco;

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import io.papermc.lib.PaperLib;
import io.papermc.paper.event.block.BlockPreDispenseEvent;
import net.luckperms.api.model.user.User;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.ServerOperator;
import org.bukkit.util.Vector;
import org.yuri.eco.utils.DiscordWebhook;
import org.yuri.eco.utils.Initializer;
import org.yuri.eco.utils.InventoryInstanceReport;
import org.yuri.eco.utils.Utils;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.yuri.eco.utils.Initializer.playerstoteming;
import static org.yuri.eco.utils.Utils.translateo;

@SuppressWarnings("deprecation")
public class events implements Listener {
    static ThreadLocalRandom random = ThreadLocalRandom.current();

    private static boolean isSuspectedScanPacket(String buffer) {
        return (buffer.split(" ").length == 1 && !buffer.endsWith(" ")) || !buffer.startsWith("/") || buffer.startsWith("/about");
    }

    public static void spawnFireworks(Location loc) {
        loc.add(new Vector(0, 1, 0));
        Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();
        fwm.setPower(2);
        fwm.addEffect(FireworkEffect.builder().withColor(Initializer.color.get(random.nextInt(Initializer.color.size()))).withColor(Initializer.color.get(random.nextInt(Initializer.color.size()))).with(FireworkEffect.Type.BALL_LARGE).flicker(true).build());
        fw.setFireworkMeta(fwm);
    }

    public static int countMinecartInChunk(Chunk chunk) {
        return
                Arrays.stream(chunk.getEntities())
                        .toList()
                        .stream()
                        .filter(r -> r instanceof Minecart)
                        .toList()
                        .size();
    }

    public static boolean removeMinecartInChunk(Chunk chunk) {
        for (Entity entity : chunk.getEntities()) {
            if (entity instanceof Minecart) {
                entity.remove();
            }
        }
        return true;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onPortal(PlayerPortalEvent event) {
        Player player = event.getPlayer();
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Initializer.p, () -> {
            if (!player.isOnline())
                return;

            if (player.getLocation().getBlock().getType().equals(Material.NETHER_PORTAL)) {
                PaperLib.teleportAsync(player, event.getFrom());
                player.playSound(player.getLocation(), Sound.BLOCK_PORTAL_TRAVEL, 1.0F, 1.0F);
            }
        }, 200);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onInteract(PlayerInteractEvent e) {
        if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK) ||
                e.getClickedBlock().getType().equals(Material.LEVER)) return;

        String name = e.getPlayer().getName();
        if (Initializer.cooldowns.containsKey(name) && Initializer.cooldowns.get(name) > System.currentTimeMillis())
            e.setCancelled(true);
        else Initializer.cooldowns.put(name, System.currentTimeMillis() + 500);
    }

    @EventHandler
    private void onPlayerLeave(final PlayerQuitEvent e) {
        String name = e.getPlayer().getName();
        Initializer.requests.remove(Utils.getRequest(name));
        Initializer.playerstoteming.remove(name);
        Initializer.cooldowns.remove(name);
        Initializer.lastReceived.remove(name);
        Initializer.msg.remove(name);
        Initializer.tpa.remove(name);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onAsyncCommandTabComplete(AsyncTabCompleteEvent event) {
        event.setCancelled(isSuspectedScanPacket(event.getBuffer()));
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        if (e.getInventory() instanceof PlayerInventory)
            return;

        if (e.getInventory().getHolder() instanceof InventoryInstanceReport holder) {
            e.setCancelled(true);
            if (!e.getCurrentItem().getItemMeta().hasLore())
                return;

            holder.whenClicked(e.getCurrentItem(),
                    e.getAction(),
                    e.getSlot(),
                    holder.getArg());
        }
    }

    @EventHandler
    public void onSpawner(final SpawnerSpawnEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onDispense(final BlockPreDispenseEvent e) {
        e.setCancelled(e.getItemStack().getType().equals(Material.TNT));
    }

    @EventHandler
    private void onPlayerKill(final PlayerDeathEvent e) {
        Player p = e.getPlayer();
        if (p.getKiller() == null) return;
        Player kp = p.getKiller();

        User user = Initializer.lp.getPlayerAdapter(Player.class).getUser(kp);
        Location loc = p.getLocation();
        if (!user.getPrimaryGroup().equals("default")) {
            loc.add(new Vector(0, 1, 0));
            switch (random.nextInt(4)) {
                case 0 -> spawnFireworks(loc);
                case 1 -> loc.getWorld().spawnParticle(Particle.TOTEM, loc, 50, 3, 1, 3, 0.0);
                case 2 -> loc.getWorld().strikeLightningEffect(loc);
                case 3 -> {
                    for (double y = 0; y <= 10; y += 0.05) {
                        double x = 2 * Math.cos(y);
                        double z = 2 * Math.sin(y);
                        Vector off = new Vector(0, 0, 0);
                        loc.getWorld().spawnParticle(Particle.TOTEM, new Location(loc.getWorld(),
                                        (float) (loc.getX() + x),
                                        (float) (loc.getY() + y),
                                        (float) (loc.getZ() + z)),
                                2,
                                off.getX(),
                                off.getY(),
                                off.getZ(),
                                1.0);
                    }
                }
            }
        } else loc.getWorld().strikeLightningEffect(loc);

        if (random.nextInt(100) <= 5)
            Bukkit.getWorld(e.getPlayer()
                            .getWorld()
                            .getName()).dropItemNaturally(new Location(
                                    e.getPlayer().getLocation().getWorld(),
                                    e.getEntity().getLocation().getX(),
                                    e.getEntity().getLocation().getY(),
                                    e.getPlayer().getLocation().getZ()),
                            Utils.getHead(e.getPlayer(),
                                    e.getPlayer().getKiller().getDisplayName()));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onEventExplosion(final EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Item)) return;

        if (e.getCause() != EntityDamageEvent.DamageCause.BLOCK_EXPLOSION &&
                e.getCause() != EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)
            return;

        final Material type = ((Item) e.getEntity()).getItemStack().getType();
        e.setCancelled(type.name().contains("DIAMOND") || type.name().contains("NETHERITE"));
    }

    @EventHandler
    public void playeruse(PlayerItemConsumeEvent e) {
        e.setCancelled(e.getItem().getType().equals(Material.ENCHANTED_GOLDEN_APPLE));
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
                DiscordWebhook webhook = new DiscordWebhook("https://discord.com/api/webhooks/1154454195437572257/Y6dRpPyFLmlr7nhSIHEGL4-ByTAhb2ReyztwLEuzGoIZy5rTr_KUet86N9gUiw1vrKUg");
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
            playerstoteming.remove(playerUniqueId);
        }
    }

    @EventHandler
    public void antiAuto2(EntityResurrectEvent e) {
        String n = e.getEntity().getName();
        if (!playerstoteming.containsKey(n))
            playerstoteming.put(n, System.currentTimeMillis() + 500);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(final AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        if (Initializer.chatlock && !p.hasPermission("has.staff")) {
            p.sendMessage(translateo("&7Chat is currently locked, Try again later"));
            e.setCancelled(true);
            return;
        }

        if (e.getMessage().length() > 128) {
            e.setCancelled(true);
            return;
        }

        String name = p.getName();
        if (Initializer.cooldowns.containsKey(name) &&
                Initializer.cooldowns.get(name) > System.currentTimeMillis())
            e.setCancelled(true);
        else Initializer.cooldowns.put(name, System.currentTimeMillis() + 500);
    }

    @EventHandler
    public void onVehicleCollide(VehicleEntityCollisionEvent event) {
        if (countMinecartInChunk(event.getVehicle().getChunk()) >= 16) {
            event.setCancelled(removeMinecartInChunk(event.getVehicle().getChunk()));
            Bukkit.getOnlinePlayers().stream().filter(ServerOperator::isOp)
                    .forEach(s -> s.sendMessage(translateo("&f*** minecart lag machine at &6" + event.getVehicle().getLocation().getX() + " " + event.getVehicle().getLocation().getZ() + " " + event.getVehicle().getLocation().getWorld().getName())));
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if (!e.getPlayer().hasPlayedBefore()) {
            ItemStack pick = new ItemStack(Material.IRON_PICKAXE, 1);
            ItemStack sword = new ItemStack(Material.IRON_SWORD, 1);
            ItemStack helmet = new ItemStack(Material.IRON_HELMET, 1);
            ItemStack chestplate = new ItemStack(Material.IRON_CHESTPLATE, 1);
            ItemStack leggings = new ItemStack(Material.IRON_LEGGINGS, 1);
            ItemStack boots = new ItemStack(Material.IRON_BOOTS, 1);

            pick.addEnchantment(Enchantment.DIG_SPEED, 3);
            pick.addEnchantment(Enchantment.DURABILITY, 2);
            pick.addEnchantment(Enchantment.LOOT_BONUS_BLOCKS, 2);
            pick.addEnchantment(Enchantment.MENDING, 1);

            sword.addEnchantment(Enchantment.DAMAGE_ALL, 2);
            sword.addEnchantment(Enchantment.DURABILITY, 2);
            sword.addEnchantment(Enchantment.MENDING, 1);

            helmet.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
            helmet.addEnchantment(Enchantment.DURABILITY, 2);
            helmet.addEnchantment(Enchantment.MENDING, 1);

            chestplate.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
            chestplate.addEnchantment(Enchantment.DURABILITY, 2);
            chestplate.addEnchantment(Enchantment.MENDING, 1);

            leggings.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
            leggings.addEnchantment(Enchantment.DURABILITY, 2);
            leggings.addEnchantment(Enchantment.MENDING, 1);

            boots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
            boots.addEnchantment(Enchantment.DURABILITY, 2);
            boots.addEnchantment(Enchantment.MENDING, 1);

            p.getInventory().addItem(sword);
            p.getInventory().addItem(pick);
            p.getInventory().setItemInOffHand(new ItemStack(Material.BREAD, 16));
            p.getInventory().setHelmet(helmet);
            p.getInventory().setChestplate(chestplate);
            p.getInventory().setLeggings(leggings);
            p.getInventory().setBoots(boots);
            p.teleport(new Location(Bukkit.getWorld("world"), Initializer.p.getConfig().getDouble("Spawn.X"), Initializer.p.getConfig().getDouble("Spawn.Y"), Initializer.p.getConfig().getDouble("Spawn.Z")));
        }

        if (p.getHealth() == 0.0) {
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Initializer.p, () -> {
                p.setHealth(20);
                p.kickPlayer("Internal Exception: io.netty.handler.timeout.ReadTimeoutException");
            }, 2L);
        }

        String name = p.getName();
        if (Utils.manager().get("r." + name + ".t") == null)
            Initializer.tpa.add(name);

        if (Utils.manager().get("r." + name + ".m") == null)
            Initializer.msg.add(name);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        e.setRespawnLocation(new Location(Bukkit.getWorld("world"),
                Initializer.p.getConfig().getDouble("Spawn.X"),
                Initializer.p.getConfig().getDouble("Spawn.Y"),
                Initializer.p.getConfig().getDouble("Spawn.Z")));
    }
}