package org.yuri.eco;

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import io.papermc.lib.PaperLib;
import org.bukkit.inventory.PlayerInventory;
import org.yuri.eco.utils.Initializer;
import org.yuri.eco.utils.InventoryInstance;
import org.yuri.eco.utils.InventoryInstanceReport;
import org.yuri.eco.utils.Utils;
import net.luckperms.api.model.user.User;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.ServerOperator;
import org.bukkit.util.Vector;

import java.util.*;

public class events implements Listener {
    private static boolean isSuspectedScanPacket(String buffer) {
        return (buffer.split(" ").length == 1 && !buffer.endsWith(" ")) ||
                !buffer.startsWith("/") ||
                buffer.startsWith("/about");
    }

    public static void spawnFireworks(Location loc) {
        loc.add(new Vector(0, 1, 0));
        Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();
        fwm.setPower(2);
        Random random = new Random();
        fwm.addEffect(FireworkEffect.builder().withColor(Initializer.color.get(random.nextInt(Initializer.color.size()))).withColor(Initializer.color.get(random.nextInt(Initializer.color.size()))).with(FireworkEffect.Type.BALL_LARGE).flicker(true).build());
        fw.setFireworkMeta(fwm);
    }

    /*@EventHandler
    private void onDispense(BlockDispenseEvent e) {
        if (e.getItem().getType().equals(Material.BONE_MEAL)) {
            Container container = (Container) e.getBlock().getState();
            container.getInventory().addItem(e.getItem());
        }
    }*/

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
        Initializer.p.getServer().getScheduler().scheduleSyncDelayedTask(Initializer.p, () -> {
            Player player = event.getPlayer();
            if (player.getLocation().getBlock().getType().equals(Material.NETHER_PORTAL)) {
                PaperLib.teleportAsync(player, event.getFrom());
                player.playSound(player.getLocation(), Sound.BLOCK_PORTAL_TRAVEL, 1.0F, 1.0F);
            }
        }, 200);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onInteract(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        if (!Objects.requireNonNull(event.getClickedBlock()).getType().equals(Material.LEVER)) return;

        UUID playerUniqueId = event.getPlayer().getUniqueId();
        if (
                Initializer.cooldowns.containsKey(playerUniqueId)
                        && Initializer.cooldowns.get(playerUniqueId) > System.currentTimeMillis()
        ) {
            event.setCancelled(true);
        } else
            Initializer.cooldowns.put(playerUniqueId, System.currentTimeMillis() + 500);
    }

    @EventHandler
    private void onPlayerLeave(final PlayerQuitEvent e) {
        Utils.removeRequest(e.getPlayer());
        UUID playerUniqueId = e.getPlayer().getUniqueId();
        Initializer.cooldowns.remove(playerUniqueId);
        Initializer.lastReceived.remove(playerUniqueId);
        Initializer.msg.remove(e.getPlayer().getName());
        Initializer.tpa.remove(e.getPlayer().getName());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onAsyncCommandTabComplete(AsyncTabCompleteEvent event) {
        event.setCancelled(isSuspectedScanPacket(event.getBuffer()));
    }

    @EventHandler
    public void onPlayerRegisterChannel(PlayerRegisterChannelEvent e) {
        if (e.getChannel().equals("hcscr:haram"))
            e.getPlayer().sendPluginMessage(Initializer.p, "hcscr:haram", new byte[]{1});
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        if (e.getInventory() instanceof PlayerInventory)
            return;

        if (e.getInventory().getHolder() instanceof InventoryInstance holder) {
            e.setCancelled(true);
            holder.whenClicked(e.getCurrentItem(),
                    e.getAction(),
                    e.getSlot());
        } else if (e.getInventory().getHolder() instanceof InventoryInstanceReport holder) {
            e.setCancelled(true);
            final ItemStack clickedItem = e.getCurrentItem();
            ItemMeta meta = clickedItem.getItemMeta();
            if (!meta.hasLore())
                return;

            holder.whenClicked(e.getCurrentItem(),
                    e.getAction(),
                    e.getSlot(),
                    holder.getArg());
        }
    }

    @EventHandler
    public void onCreatureSpawn(final CreatureSpawnEvent e) {
        e.setCancelled(e.getEntity() instanceof Fish);
    }

    @EventHandler
    public void onSpawner(final SpawnerSpawnEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    private void onPlayerKill(final PlayerDeathEvent e) {
        if (e.getPlayer().getKiller() == null)
            return;

        User user = Initializer.lp.getPlayerAdapter(Player.class).getUser(e.getPlayer().getKiller());
        if (!user.getPrimaryGroup().equals("default")) {
            Random rnd = new Random();
            float floati = rnd.nextInt(4);
            Location loc = e.getPlayer().getLocation();
            loc.add(new Vector(0, 1, 0));
            if (floati == 0)
                spawnFireworks(e.getPlayer().getLocation());
            else if (floati == 1) {
                Vector off = new Vector(3, 1, 3);
                e.getPlayer().getWorld().spawnParticle(Particle.TOTEM, loc, 50, off.getX(), off.getY(), off.getZ(), 0.0);
            } else if (floati == 2)
                e.getPlayer().getWorld().strikeLightningEffect(e.getPlayer().getLocation());
            else
                createHelix(e.getPlayer());
        } else
            e.getPlayer().getWorld().strikeLightningEffect(e.getPlayer().getLocation());

        Bukkit.getServer().getScheduler().runTask(Initializer.p, () -> {
            Random ran = new Random();
            int b = ran.nextInt(100);
            if (b <= 5)
                Objects.requireNonNull(Bukkit.getWorld(e.getPlayer().getWorld().getName())).dropItemNaturally(new Location(e.getPlayer().getLocation().getWorld(), e.getEntity().getLocation().getX(), e.getEntity().getLocation().getY(), e.getPlayer().getLocation().getZ()), Utils.getHead(e.getPlayer()));
        });
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

    @EventHandler(priority = EventPriority.LOWEST)
    private void onEventExplosion(final EntityDamageEvent e) {
        if (e.getEntity().getType() != EntityType.DROPPED_ITEM || !(e.getEntity() instanceof Item))
            return;

        if (e.getCause() != EntityDamageEvent.DamageCause.BLOCK_EXPLOSION
                && e.getCause() != EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)
            return;

        final Material type = ((Item) e.getEntity()).getItemStack().getType();
        e.setCancelled(type.name().contains("DIAMOND") ||
                type.name().contains("NETHERITE"));
    }

    @EventHandler
    public void playeruse(PlayerItemConsumeEvent e) {
        e.setCancelled(e.getItem().getType().equals(Material.ENCHANTED_GOLDEN_APPLE));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(final AsyncPlayerChatEvent e) {
        if (Initializer.chatlock && !e.getPlayer().hasPermission("has.staff")) {
            e.getPlayer().sendMessage(Utils.translateo("&7Chat is currently locked, Try again later"));
            e.setCancelled(true);
            return;
        }

        if (e.getMessage().length() > 128) {
            e.setCancelled(true);
            return;
        }
        UUID playerUniqueId = e.getPlayer().getUniqueId();
        if (Initializer.cooldowns.containsKey(playerUniqueId) && Initializer.cooldowns.get(playerUniqueId) > System.currentTimeMillis())
            e.setCancelled(true);
        else
            Initializer.cooldowns.put(playerUniqueId, System.currentTimeMillis() + 500);
    }

    @EventHandler
    public void onVehicleCollide(VehicleEntityCollisionEvent event) {
        if (countMinecartInChunk(event.getVehicle().getChunk()) >= 16) {
            event.setCancelled(removeMinecartInChunk(event.getVehicle().getChunk()));
            Bukkit.getOnlinePlayers().stream().filter(ServerOperator::isOp).
                    forEach(s ->
                            s.sendMessage(Utils.translateo("&f*** minecraft lag machine at &6" +
                                    event.getVehicle().getLocation().getX() + " " +
                                    event.getVehicle().getLocation().getZ() + " " +
                                    event.getVehicle().getLocation().getWorld().getName())));
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
            e.getPlayer().teleport(new Location(Bukkit.getWorld("world"),
                    Initializer.p.getConfig().getDouble("Spawn.X"),
                    Initializer.p.getConfig().getDouble("Spawn.Y"),
                    Initializer.p.getConfig().getDouble("Spawn.Z")));
        }

        // fixes the "0 health no respawn" bug
        if (e.getPlayer().getHealth() == 0.0) {
            Bukkit.getServer().getScheduler().runTask(Initializer.p, () -> {
                e.getPlayer().setHealth(20);
                e.getPlayer().kickPlayer("Disconnected");
            });
        }

        if (Utils.manager().get("r." + e.getPlayer().getUniqueId() + ".t") == null)
            Initializer.tpa.add(e.getPlayer().getName());

        if (Utils.manager().get("r." + e.getPlayer().getUniqueId() + ".m") == null)
            Initializer.msg.add(e.getPlayer().getName());
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        //if (e.getPlayer().getBedSpawnLocation() == null)
        e.setRespawnLocation(new Location(Bukkit.getWorld("world"),
                Initializer.p.getConfig().getDouble("Spawn.X"),
                Initializer.p.getConfig().getDouble("Spawn.Y"),
                Initializer.p.getConfig().getDouble("Spawn.Z")));
    }

    @EventHandler
    public void onRedstone(BlockRedstoneEvent e) {
        e.setNewCurrent(AestheticNetwork.getTPSofLastSecond() > 18 ? e.getNewCurrent() : 0);
    }
}