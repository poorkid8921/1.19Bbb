package main;

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import com.destroystokyo.paper.event.player.PlayerHandshakeEvent;
import main.expansions.bungee.HandShake;
import main.utils.Constants;
import main.utils.Utils;
import main.utils.instances.CustomPlayerDataHolder;
import main.utils.instances.InventoryInstanceReport;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import static main.utils.Constants.crystalsToBeOptimized;
import static main.utils.Constants.playerData;
import static main.utils.Languages.SECOND_COLOR;
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
    public void onEntitySpawn(EntitySpawnEvent event) {
        if (event.getEntityType() == EntityType.ENDER_CRYSTAL)
            crystalsToBeOptimized.put(
                    event.getEntity().getEntityId(),
                    event.getEntity().getLocation());
    }

    @EventHandler
    public void onEntityRemoveFromWorld(EntityRemoveFromWorldEvent event) {
        if (event.getEntityType() == EntityType.ENDER_CRYSTAL)
            Bukkit.getScheduler().runTaskLater(Constants.p, () -> crystalsToBeOptimized.remove(event.getEntity().getEntityId()), 40L);
    }

    @EventHandler
    private void onCommand(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();
        String pn = p.getName();
        if (playerData.get(pn).isTagged()) {
            p.sendMessage(Constants.EXCEPTION_TAGGED);
            e.setCancelled(true);
        }
    }

    @EventHandler
    private void onInteract(PlayerInteractEvent e) {
        if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK) ||
                !e.getClickedBlock().getType().equals(Material.LEVER))
            return;
        String name = e.getPlayer().getName();
        if (Constants.cooldowns.getOrDefault(name, 0L) > System.currentTimeMillis())
            e.setCancelled(true);
        else Constants.cooldowns.put(name, System.currentTimeMillis() + 500);
    }

    @EventHandler
    private void onPlayerLeave(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        String playerName = p.getName();

        CustomPlayerDataHolder D = playerData.get(playerName);
        if (D.isTagged()) {
            Bukkit.getScheduler().cancelTask(D.getRunnableid());
            D.setTagged(false);
            p.setHealth(0);
        }

        Constants.requests.remove(Utils.getRequest(playerName));
        Constants.lastReceived.remove(playerName);
        Constants.msg.remove(playerName);
        Constants.tpa.remove(playerName);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Inventory inv = e.getClickedInventory();
        if (!(inv instanceof PlayerInventory) && inv instanceof InventoryInstanceReport holder) {
            e.setCancelled(true);
            ItemStack currentItem = e.getCurrentItem();
            if (!currentItem.getItemMeta().hasLore()) return;
            holder.whenClicked(currentItem, e.getAction(), e.getSlot(), holder.getArg());
        }
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

    @EventHandler
    private void onPlayerKill(PlayerDeathEvent e) {
        Player p = e.getPlayer();
        String playerName = p.getName();
        Player killer = p.getKiller();
        if (killer == null || killer == p) {
            String death = SECOND_COLOR + "☠ " + playerName + " §7" + switch (p.getLastDamageCause().getCause()) {
                case ENTITY_EXPLOSION, BLOCK_EXPLOSION -> "blasted themselves";
                case FALL -> "broke their legs";
                case FALLING_BLOCK -> "suffocated";
                case FLY_INTO_WALL -> "thought they're a fly";
                case FIRE_TICK, LAVA -> "burnt into ashes";
                default -> "suicided";
            };
            e.setDeathMessage(death);
        } else {
            CustomPlayerDataHolder D0 = playerData.get(playerName);
            D0.setTagged(false);
            Bukkit.getScheduler().cancelTask(D0.getRunnableid());

            String kp = killer.getName();
            CustomPlayerDataHolder D1 = playerData.get(kp);
            Bukkit.getScheduler().cancelTask(D1.getRunnableid());
            D1.setTagged(false);

            String death = SECOND_COLOR + "☠ " + kp + " §7" + switch (p.getLastDamageCause().getCause()) {
                case ENTITY_EXPLOSION -> "exploded " + SECOND_COLOR + playerName;
                case BLOCK_EXPLOSION -> "imploded " + SECOND_COLOR + playerName;
                case FALL -> "broke " + SECOND_COLOR + playerName + "§7's legs";
                case ENTITY_ATTACK, ENTITY_SWEEP_ATTACK -> "sworded " + SECOND_COLOR + playerName;
                case PROJECTILE -> "shot " + SECOND_COLOR + playerName + " §7in the ass";
                case FIRE_TICK, LAVA -> "turned " + SECOND_COLOR + playerName + " §7into ashes";
                default -> "suicided";
            };
            e.setDeathMessage(death);

            Location loc = p.getLocation();
            World w = loc.getWorld();
            if (!Constants.lp.getPlayerAdapter(Player.class).getUser(killer).getPrimaryGroup().equals("default")) {
                loc.add(0, 1, 0);
                switch (Constants.RANDOM.nextInt(4)) {
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

            if (Constants.RANDOM.nextInt(100) <= 5)
                w.dropItemNaturally(loc, Utils.getHead(p, killer.getDisplayName()));
        }
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
            p.teleportAsync(Constants.spawn);
        }
        String name = p.getName();
        CustomPlayerDataHolder D = playerData.get(name);
        if (D == null) {
            Constants.tpa.add(name);
            Constants.msg.add(name);
            playerData.put(name, new CustomPlayerDataHolder(0, 0));
        } else {
            if (D.getT() == 0)
                Constants.tpa.add(name);
            if (D.getM() == 0)
                Constants.msg.add(name);
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        e.setRespawnLocation(Constants.spawn);
    }
}