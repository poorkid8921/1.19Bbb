package main;

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import main.utils.Initializer;
import main.utils.InventoryInstanceReport;
import main.utils.Utils;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import static main.utils.Utils.isSuspectedScanPacket;
import static main.utils.Utils.spawnFireworks;

@SuppressWarnings("deprecation")
public class Events implements Listener {
    ItemStack pick = new ItemStack(Material.IRON_PICKAXE, 1);
    ItemStack sword = new ItemStack(Material.IRON_SWORD, 1);
    ItemStack helmet = new ItemStack(Material.IRON_HELMET, 1);
    ItemStack chestplate = new ItemStack(Material.IRON_CHESTPLATE, 1);
    ItemStack leggings = new ItemStack(Material.IRON_LEGGINGS, 1);
    ItemStack boots = new ItemStack(Material.IRON_BOOTS, 1);
    int stock = 0;

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
    private void onPortal(PlayerPortalEvent event) {
        Player player = event.getPlayer();
        Location loc = player.getLocation();
        Bukkit.getScheduler().scheduleSyncDelayedTask(Initializer.p, () -> {
            if (!player.isOnline()) return;

            if (loc.getBlock().getType().equals(Material.NETHER_PORTAL)) {
                player.teleportAsync(event.getFrom());
                player.playSound(loc, Sound.BLOCK_PORTAL_TRAVEL, 1.0F, 1.0F);
            }
        }, 200);
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
        Initializer.cooldowns.remove(name);
        Initializer.lastReceived.remove(name);
        Initializer.msg.remove(name);
        Initializer.tpa.remove(name);
    }

    @EventHandler
    private void onAsyncCommandTabComplete(AsyncTabCompleteEvent event) {
        event.setCancelled(isSuspectedScanPacket(event.getBuffer()));
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (e.getInventory() instanceof PlayerInventory)
            return;

        if (p.getInventory().getHolder() instanceof InventoryInstanceReport holder) {
            e.setCancelled(true);
            ItemStack currentItem = e.getCurrentItem();
            if (!currentItem.getItemMeta().hasLore()) return;

            holder.whenClicked(currentItem, e.getAction(), e.getSlot(), holder.getArg());
        }
    }

    @EventHandler
    private void onPlayerKill(PlayerDeathEvent e) {
        Player p = e.getPlayer();
        Player kp = p.getKiller();
        if (kp == null) return;

        Location loc = p.getLocation();
        if (!Initializer.lp.getPlayerAdapter(Player.class).getUser(kp).getPrimaryGroup().equals("default")) {
            loc.add(0, 1, 0);
            switch (Initializer.RANDOM.nextInt(4)) {
                case 0 -> spawnFireworks(loc);
                case 1 -> loc.getWorld().spawnParticle(Particle.TOTEM, loc, 50, 3, 1, 3, 0.0);
                case 2 -> loc.getWorld().strikeLightningEffect(loc);
                case 3 -> {
                    World w = loc.getWorld();
                    for (double y = 0; y <= 10; y += 0.05) {
                        w.spawnParticle(Particle.TOTEM, new Location(w, (float) (loc.getX() + (2 * Math.cos(y))), (float) (loc.getY() + (2 * Math.sin(y))), (float) (loc.getZ() + 2 * Math.sin(y))), 2, 0, 0, 0, 1.0);
                    }
                }
            }
        } else loc.getWorld().strikeLightningEffect(loc);

        if (Initializer.RANDOM.nextInt(100) <= 5)
            p.getWorld().dropItemNaturally(p.getLocation(), Utils.getHead(p, kp.getDisplayName()));
    }

    @EventHandler
    private void onEventExplosion(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Item a)) return;

        EntityDamageEvent.DamageCause c = e.getCause();
        if (c != EntityDamageEvent.DamageCause.BLOCK_EXPLOSION &&
                c != EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)
            return;

        String type = a.getItemStack().getType().name();
        e.setCancelled(type.contains("DIAMOND") || type.contains("NETHERITE"));
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();

        if (e.getMessage().length() > 128) {
            e.setCancelled(true);
            return;
        }

        String name = p.getName();
        if (Initializer.cooldowns.getOrDefault(name, 0L) > System.currentTimeMillis()) e.setCancelled(true);
        else Initializer.cooldowns.put(name, System.currentTimeMillis() + 500);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if (!p.hasPlayedBefore()) {
            if (stock++ <= 50) {
                p.getInventory().addItem(sword);
                p.getInventory().addItem(pick);
                p.getInventory().setItemInOffHand(new ItemStack(Material.BREAD, 16));
                p.getInventory().setHelmet(helmet);
                p.getInventory().setChestplate(chestplate);
                p.getInventory().setLeggings(leggings);
                p.getInventory().setBoots(boots);
            }
            p.teleportAsync(Initializer.spawn);
        } else if (p.getHealth() == 0.0) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(Initializer.p, () -> {
                p.setHealth(20);
                p.kickPlayer("Internal Exception: io.netty.handler.timeout.ReadTimeoutException");
            }, 2L);
            e.setJoinMessage(null);
            return;
        }

        String name = p.getName();
        if (Economy.cc.get("r." + name + ".t") == null) Initializer.tpa.add(name);
        if (Economy.cc.get("r." + name + ".m") == null) Initializer.msg.add(name);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        e.setRespawnLocation(Initializer.spawn);
    }
}