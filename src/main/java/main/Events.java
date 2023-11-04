package main;

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import main.utils.Initializer;
import main.utils.InventoryInstanceReport;
import main.utils.Utils;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.awt.*;

import static main.utils.Utils.isSuspectedScanPacket;
import static main.utils.Utils.spawnFireworks;

@SuppressWarnings("deprecation")
public class Events implements Listener {
    ItemStack pick = new ItemStack(Material.IRON_PICKAXE);
    ItemStack sword = new ItemStack(Material.IRON_SWORD);
    ItemStack helmet = new ItemStack(Material.IRON_HELMET);
    ItemStack chestplate = new ItemStack(Material.IRON_CHESTPLATE);
    ItemStack leggings = new ItemStack(Material.IRON_LEGGINGS);
    ItemStack boots = new ItemStack(Material.IRON_BOOTS);
    ItemStack bread = new ItemStack(Material.BREAD, 16);
    ItemStack totem = new ItemStack(Material.TOTEM_OF_UNDYING);

    public Events() {
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
    }

    @EventHandler
    private void onInteract(PlayerInteractEvent e) {
        if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK) ||
                !e.getClickedBlock().getType().equals(Material.LEVER))
            return;

        String name = e.getPlayer().getName();
        if (Initializer.cooldowns.getOrDefault(name, 0L) > System.currentTimeMillis())
            e.setCancelled(true);
        else Initializer.cooldowns.put(name, System.currentTimeMillis() + 500);
    }

    @EventHandler
    private void onPlayerLeave(PlayerQuitEvent e) {
        String name = e.getPlayer().getName();
        Initializer.requests.remove(Utils.getRequest(name));
        Initializer.lastReceived.remove(name);
        Initializer.msg.remove(name);
        Initializer.tpa.remove(name);
    }

    @EventHandler
    private void onAsyncCommandTabComplete(AsyncTabCompleteEvent event) {
        event.setCancelled(isSuspectedScanPacket(event.getBuffer()));
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onCreatureSpawn(CreatureSpawnEvent event) {
        if (!(event.getEntity() instanceof Wither w)) return;

        final Location witherLocation = w.getLocation();
        if (new Point(witherLocation.getBlockX(), witherLocation.getBlockZ())
                .distance(Utils.point) < 1500) {
            event.setCancelled(true);
            for (Player nearbyPlayer : witherLocation.getNearbyPlayers(8, 8, 8)) {
                nearbyPlayer.sendMessage("§7You can only spawn withers 1.5k blocks away from spawn");
            }
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();

        String name = p.getName();
        if (Initializer.cooldowns.getOrDefault(name, 0L) > System.currentTimeMillis()) e.setCancelled(true);
        else Initializer.cooldowns.put(name, System.currentTimeMillis() + 500);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (e.getInventory() instanceof PlayerInventory)
            return;

        Bukkit.getLogger().warning("not PlayerInventory");
        if (p.getInventory().getHolder() instanceof InventoryInstanceReport holder) {
            Bukkit.getLogger().warning("cancelled");
            e.setCancelled(true);
            ItemStack currentItem = e.getCurrentItem();
            if (!currentItem.getItemMeta().hasLore()) return;
            Bukkit.getLogger().warning("has lore");

            holder.whenClicked(currentItem, e.getAction(), e.getSlot(), holder.getArg());
        }
    }

    @EventHandler
    private void onPlayerKill(PlayerDeathEvent e) {
        Player p = e.getPlayer();
        Player kp = p.getKiller();
        if (kp == null) return;

        Location loc = p.getLocation();
        World w = loc.getWorld();
        if (!Initializer.lp.getPlayerAdapter(Player.class).getUser(kp).getPrimaryGroup().equals("default")) {
            loc.add(0, 1, 0);
            switch (Initializer.RANDOM.nextInt(4)) {
                case 0 -> spawnFireworks(loc);
                case 1 -> w.spawnParticle(Particle.TOTEM, loc, 50, 3, 1, 3, 0.0);
                case 2 -> w.strikeLightningEffect(loc);
                case 3 -> {
                    for (double y = 0; y <= 10; y += 0.05) {
                        w.spawnParticle(Particle.TOTEM, new Location(w, (float) (loc.getX() + (2 * Math.cos(y))), (float) (loc.getY() + (2 * Math.sin(y))), (float) (loc.getZ() + 2 * Math.sin(y))), 2, 0, 0, 0, 1.0);
                    }
                }
            }
        } else w.strikeLightningEffect(loc);

        if (Initializer.RANDOM.nextInt(100) <= 5)
            w.dropItemNaturally(loc, Utils.getHead(p, kp.getDisplayName()));
    }

    @EventHandler
    private void onEventExplosion(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Item a)) return;

        EntityDamageEvent.DamageCause c = e.getCause();
        if (c != EntityDamageEvent.DamageCause.BLOCK_EXPLOSION &&
                c != EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)
            return;

        String type = a.getItemStack().getType().name();
        e.setCancelled(type.contains("DIAMOND"));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if (!p.hasPlayedBefore()) {
            p.getInventory().addItem(sword);
            p.getInventory().addItem(pick);
            p.getInventory().addItem(bread);
            p.getInventory().setHelmet(helmet);
            p.getInventory().setChestplate(chestplate);
            p.getInventory().setLeggings(leggings);
            p.getInventory().setBoots(boots);
            p.getInventory().setItemInOffHand(totem);
            p.teleportAsync(Initializer.spawn);
        }

        String name = p.getName();
        if (Economy.cc.get("r." + name + ".t") == null) Initializer.tpa.add(name);
        if (Economy.cc.get("r." + name + ".m") == null) Initializer.msg.add(name);
    }
}