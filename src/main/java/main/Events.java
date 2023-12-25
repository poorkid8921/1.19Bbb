package main;

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import com.destroystokyo.paper.event.player.PlayerHandshakeEvent;
import main.expansions.bungee.HandShake;
import main.utils.Initializer;
import main.utils.instances.CustomPlayerDataHolder;
import main.utils.instances.InventoryInstanceReport;
import main.utils.Utils;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import static main.utils.Initializer.crystalsToBeOptimized;
import static main.utils.Initializer.playerData;
import static main.utils.Utils.spawnFireworks;

@SuppressWarnings("deprecation")
public class Events implements Listener {
    ItemStack pick = new ItemStack(Material.IRON_PICKAXE);
    ItemStack sword = new ItemStack(Material.IRON_SWORD);
    ItemStack helmet = new ItemStack(Material.IRON_HELMET);
    ItemStack chestplate = new ItemStack(Material.IRON_CHESTPLATE);
    ItemStack leggings = new ItemStack(Material.IRON_LEGGINGS);
    ItemStack boots = new ItemStack(Material.IRON_BOOTS);
    ItemStack gap = new ItemStack(Material.GOLDEN_APPLE, 16);
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

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onHandshake(PlayerHandshakeEvent e) {
        HandShake decoded = HandShake.decodeAndVerify(e.getOriginalHandshake());

        if (decoded == null) {
            String ip = e.getOriginalSocketAddressHostname() + " - ";
            e.setFailMessage("Server closed");
            HandShake.Success data = (HandShake.Success) decoded;
            Bukkit.getLogger().warning("ip = " + ip + "; serverHost = " + data.serverHostname() + "; socketAddress = " + data.socketAddressHostname());
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
    public void onEntitySpawn(EntitySpawnEvent event) {
        if (event.getEntityType() == EntityType.ENDER_CRYSTAL)
            crystalsToBeOptimized.put(event.getEntity()
                            .getEntityId(),
                    event.getEntity()
                            .getLocation());
    }

    @EventHandler
    public void onEntityRemoveFromWorld(EntityRemoveFromWorldEvent event) {
        if (event.getEntityType() == EntityType.ENDER_CRYSTAL)
            Bukkit.getScheduler().runTaskLater(Initializer.p, () -> crystalsToBeOptimized.remove(event.getEntity().getEntityId()), 40L);
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
    public void onInventoryClick(InventoryClickEvent e) {
        Inventory inv = e.getClickedInventory();
        if (inv instanceof PlayerInventory)
            return;

        if (inv instanceof InventoryInstanceReport holder) {
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
        e.setCancelled(type.contains("DIAMOND") || type.contains("NETHERITE"));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if (!p.hasPlayedBefore()) {
            p.getInventory().addItem(sword);
            p.getInventory().addItem(pick);
            p.getInventory().addItem(gap);
            p.getInventory().setHelmet(helmet);
            p.getInventory().setChestplate(chestplate);
            p.getInventory().setLeggings(leggings);
            p.getInventory().setBoots(boots);
            p.getInventory().setItemInOffHand(totem);
            p.teleportAsync(Initializer.spawn);
        }

        String name = p.getName();
        CustomPlayerDataHolder D = playerData.get(name);
        if (D == null) {
            Initializer.tpa.add(name);
            Initializer.msg.add(name);
            playerData.put(name, new CustomPlayerDataHolder(0, 0));
        } else {
            if (D.getT() == 0)
                Initializer.tpa.add(name);

            if (D.getM() == 0)
                Initializer.msg.add(name);
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        e.setRespawnLocation(Initializer.spawn);
    }
}