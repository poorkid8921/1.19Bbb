package org.yuri.eco;

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import io.papermc.lib.PaperLib;
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
import org.yuri.eco.utils.Initializer;
import org.yuri.eco.utils.InventoryInstanceReport;
import org.yuri.eco.utils.Utils;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

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
        int count = 0;

        for (Entity entity : chunk.getEntities()) {
            if (entity instanceof Minecart) {
                count++;
            }
        }
        return count;
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
        Initializer.p.getServer().getScheduler().scheduleSyncDelayedTask(Initializer.p, () -> {
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
        if (!Objects.requireNonNull(e.getClickedBlock()).getType().equals(Material.LEVER)) return;
        if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;

        String name = e.getPlayer().getName();
        if (Initializer.cooldowns.containsKey(name) && Initializer.cooldowns.get(name) > System.currentTimeMillis())
            e.setCancelled(true);
        else Initializer.cooldowns.put(name, System.currentTimeMillis() + 500);
    }

    @EventHandler
    private void onPlayerLeave(final PlayerQuitEvent e) {
        String name = e.getPlayer().getName();
        Initializer.requests.remove(Utils.getRequest(name));
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
        if (e.getInventory() instanceof PlayerInventory) return;

        if (e.getInventory().getHolder() instanceof InventoryInstanceReport holder) {
            e.setCancelled(true);
            final ItemStack clickedItem = e.getCurrentItem();
            ItemMeta meta = clickedItem.getItemMeta();
            if (!meta.hasLore()) return;

            holder.whenClicked(e.getCurrentItem(), e.getAction(), e.getSlot(), holder.getArg());
        }
    }

    @EventHandler
    public void onSpawner(final SpawnerSpawnEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    private void onPlayerKill(final PlayerDeathEvent e) {
        Player p = e.getPlayer();
        if (p.getKiller() == null) return;
        Player kp = p.getKiller();

        User user = Initializer.lp.getPlayerAdapter(Player.class).getUser(kp);
        Location loc = e.getPlayer().getLocation();
        if (!user.getPrimaryGroup().equals("default")) {
            loc.add(new Vector(0, 1, 0));
            switch (random.nextInt(4)) {
                case 0 -> spawnFireworks(loc);
                case 1 -> p.getWorld().spawnParticle(Particle.TOTEM, loc, 50, 3, 1, 3, 0.0);
                case 2 -> p.getWorld().strikeLightningEffect(loc);
                case 3 -> createHelix(loc);
            }
        } else p.getWorld().strikeLightningEffect(loc);

        if (random.nextInt(100) <= 5)
            Bukkit.getWorld(e
                            .getPlayer()
                            .getWorld()
                            .getName())
                    .dropItemNaturally(new Location(
                                    e.getPlayer().getLocation().getWorld(),
                                    e.getEntity().getLocation().getX(),
                                    e.getEntity().getLocation().getY(),
                                    e.getPlayer().getLocation().getZ()),
                            Utils.getHead(e.getPlayer(),
                                    e.getPlayer().getKiller().getDisplayName()));
    }

    public void createHelix(Location loc) {
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

    @EventHandler(priority = EventPriority.LOWEST)
    private void onEventExplosion(final EntityDamageEvent e) {
        if (e.getEntity().getType() != EntityType.DROPPED_ITEM || !(e.getEntity() instanceof Item)) return;

        if (e.getCause() != EntityDamageEvent.DamageCause.BLOCK_EXPLOSION && e.getCause() != EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)
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
        Player ent = e.getPlayer();
        if (!e.getOffHandItem().getType().equals(Material.AIR)) return;

        String playerUniqueId = ent.getName();
        if (playerstoteming.containsKey(playerUniqueId) &&
                playerstoteming.get(playerUniqueId) > System.currentTimeMillis()) {
            for (Player i : Bukkit.getOnlinePlayers()) {
                if (!i.hasPermission("has.staff")) continue;

                long ms = playerstoteming.get(playerUniqueId) - System.currentTimeMillis();
                i.sendMessage(translateo("&6" + ent.getName() + " totemed in less than " + ms + "ms! &7 " + ent.getPing() + "ms"));
            }
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
            Bukkit.getOnlinePlayers().stream().filter(ServerOperator::isOp).forEach(s -> s.sendMessage(translateo("&f*** minecart lag machine at &6" + event.getVehicle().getLocation().getX() + " " + event.getVehicle().getLocation().getZ() + " " + event.getVehicle().getLocation().getWorld().getName())));
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
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

            e.getPlayer().getInventory().addItem(sword);
            e.getPlayer().getInventory().addItem(pick);
            e.getPlayer().getInventory().setItemInOffHand(new ItemStack(Material.BREAD, 16));
            e.getPlayer().getInventory().setHelmet(helmet);
            e.getPlayer().getInventory().setChestplate(chestplate);
            e.getPlayer().getInventory().setLeggings(leggings);
            e.getPlayer().getInventory().setBoots(boots);
            e.getPlayer().teleport(new Location(Bukkit.getWorld("world"), Initializer.p.getConfig().getDouble("Spawn.X"), Initializer.p.getConfig().getDouble("Spawn.Y"), Initializer.p.getConfig().getDouble("Spawn.Z")));
        }

        if (e.getPlayer().getHealth() == 0.0) {
            Bukkit.getServer().getScheduler().runTask(Initializer.p, () -> {
                e.getPlayer().setHealth(20);
                e.getPlayer().kickPlayer("Internal Exception: io.netty.handler.timeout.ReadTimeoutException");
            });
        }

        String name = e.getPlayer().getName();
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