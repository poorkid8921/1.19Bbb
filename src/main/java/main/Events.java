package main;

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import io.papermc.paper.event.block.BlockPreDispenseEvent;
import main.utils.DiscordWebhook;
import main.utils.Initializer;
import main.utils.InventoryInstanceReport;
import main.utils.Utils;
import net.luckperms.api.model.user.User;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.permissions.ServerOperator;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("deprecation")
public class Events implements Listener {
    private static boolean isSuspectedScanPacket(String buffer) {
        return (buffer.split(" ").length == 1 && !buffer.endsWith(" ")) || !buffer.startsWith("/") || buffer.startsWith("/about");
    }

    public static void spawnFireworks(Location loc) {
        loc.add(new Vector(0, 1, 0));
        Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();
        fwm.setPower(2);
        fwm.addEffect(FireworkEffect.builder().withColor(Initializer.color.get(Initializer.RANDOM.nextInt(Initializer.color.size()))).withColor(Initializer.color.get(Initializer.RANDOM.nextInt(Initializer.color.size()))).with(FireworkEffect.Type.BALL_LARGE).flicker(true).build());
        fw.setFireworkMeta(fwm);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onPortal(PlayerPortalEvent event) {
        Player player = event.getPlayer();
        Location loc = player.getLocation();
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Initializer.p, () -> {
            if (!player.isOnline()) return;

            if (loc.getBlock().getType().equals(Material.NETHER_PORTAL)) {
                player.teleportAsync(event.getFrom());
                player.playSound(loc, Sound.BLOCK_PORTAL_TRAVEL, 1.0F, 1.0F);
            }
        }, 200);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onInteract(PlayerInteractEvent e) {
        if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getClickedBlock().getType().equals(Material.LEVER))
            return;

        String name = e.getPlayer().getName();
        if (Initializer.cooldowns.containsKey(name) && Initializer.cooldowns.get(name) > System.currentTimeMillis())
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

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onAsyncCommandTabComplete(AsyncTabCompleteEvent event) {
        event.setCancelled(isSuspectedScanPacket(event.getBuffer()));
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (e.getInventory() instanceof PlayerInventory)
            /*final ItemStack clickedItem = e.getCurrentItem();
            if (clickedItem != null && clickedItem.getType() == Material.TOTEM_OF_UNDYING) {
                final ItemStack oldOffHandItem = d.getItem(45);
                new BukkitRunnable() {
                    public void run() {
                        final ItemStack newOffHandItem = d.getItem(45);
                        if (newOffHandItem != null && newOffHandItem.getType() == Material.TOTEM_OF_UNDYING && oldOffHandItem.getType() == Material.AIR) {
                            e.setCancelled(true);
                            int ping = p.getPing();
                            String a = p.getName();
                            String msg = Utils.translateo("&6" + a + " failed auto totem on &7" + ping + "ms");

                            Bukkit.getScheduler().runTask(Initializer.p, () -> Bukkit.getOnlinePlayers().stream().filter(r -> r.hasPermission("has.staff")).forEach(r -> r.sendMessage(msg)));
                            DiscordWebhook webhook = new DiscordWebhook("https://discord.com/api/webhooks/1154454195437572257/Y6dRpPyFLmlr7nhSIHEGL4-ByTAhb2ReyztwLEuzGoIZy5rTr_KUet86N9gUiw1vrKUg");
                            webhook.setUsername("Flag");
                            webhook.addEmbed(new DiscordWebhook.EmbedObject().setTitle("Auto Totem").addField("Suspect", a, true).addField("Ping", String.valueOf(ping), true).setColor(java.awt.Color.ORANGE));
                            try {
                                webhook.execute();
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                        }
                    }
                }.runTaskLaterAsynchronously(Initializer.p, 2L);
            }*/
            return;

        if (p.getInventory().getHolder() instanceof InventoryInstanceReport holder) {
            e.setCancelled(true);
            ItemStack currentItem = e.getCurrentItem();
            if (!currentItem.getItemMeta().hasLore()) return;

            holder.whenClicked(currentItem, e.getAction(), e.getSlot(), holder.getArg());
        }
    }

    @EventHandler
    public void onSpawner(SpawnerSpawnEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onDispense(BlockPreDispenseEvent e) {
        e.setCancelled(e.getItemStack().getType().equals(Material.TNT));
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player && event.getEntity() instanceof EnderCrystal d && player.getPing() > 75)
            ((CraftPlayer) player).getHandle().b.a(new PacketPlayOutEntityDestroy(d.getEntityId()));
    }

    @EventHandler
    private void onPlayerKill(PlayerDeathEvent e) {
        Player p = e.getPlayer();
        if (p.getKiller() == null) return;
        Player kp = p.getKiller();

        User user = Initializer.lp.getPlayerAdapter(Player.class).getUser(kp);
        Location loc = p.getLocation();
        if (!user.getPrimaryGroup().equals("default")) {
            loc.add(new Vector(0, 1, 0));
            switch (Initializer.RANDOM.nextInt(4)) {
                case 0 -> spawnFireworks(loc);
                case 1 -> loc.getWorld().spawnParticle(Particle.TOTEM, loc, 50, 3, 1, 3, 0.0);
                case 2 -> loc.getWorld().strikeLightningEffect(loc);
                case 3 -> {
                    World w = loc.getWorld();
                    for (double y = 0; y <= 10; y += 0.05) {
                        double z = 2 * Math.sin(y);
                        w.spawnParticle(Particle.TOTEM, new Location(w, (float) (loc.getX() + (2 * Math.cos(y))), (float) (loc.getY() + (2 * Math.sin(y))), (float) (loc.getZ() + z)), 2, 0, 0, 0, 1.0);
                    }
                }
            }
        } else loc.getWorld().strikeLightningEffect(loc);

        if (Initializer.RANDOM.nextInt(100) <= 5)
            p.getWorld().dropItemNaturally(p.getLocation(), Utils.getHead(p, kp.getDisplayName()));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onEventExplosion(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Item a)) return;

        EntityDamageEvent.DamageCause c = e.getCause();
        if (c != EntityDamageEvent.DamageCause.BLOCK_EXPLOSION && c != EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)
            return;

        String type = a.getItemStack().getType().name();
        e.setCancelled(type.contains("DIAMOND") || type.contains("NETHERITE"));
    }

    @EventHandler
    public void playeruse(PlayerItemConsumeEvent e) {
        e.setCancelled(e.getItem().getType().equals(Material.ENCHANTED_GOLDEN_APPLE));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        if (Initializer.chatlock && !p.hasPermission("has.staff")) {
            p.sendMessage(Utils.translateo("&7Chat is currently locked, Try again later"));
            e.setCancelled(true);
            return;
        }

        if (e.getMessage().length() > 128) {
            e.setCancelled(true);
            return;
        }

        String name = p.getName();
        if (Initializer.cooldowns.getOrDefault(name, 0L) > System.currentTimeMillis()) e.setCancelled(true);
        else Initializer.cooldowns.put(name, System.currentTimeMillis() + 500);
    }

    @EventHandler
    public void onVehicleCollide(VehicleEntityCollisionEvent event) {
        List<Entity> c = Arrays.stream(event.getVehicle().getChunk().getEntities()).toList().stream().filter(r -> r instanceof Minecart).toList();
        if (c.size() >= 16) {
            c.forEach(Entity::remove);
            Bukkit.getOnlinePlayers().stream().filter(ServerOperator::isOp).forEach(s -> s.sendMessage(Utils.translateo("&f*** minecart lag machine at &6" + event.getVehicle().getLocation().getX() + " " + event.getVehicle().getLocation().getZ() + " " + event.getVehicle().getLocation().getWorld().getName())));
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if (!p.hasPlayedBefore()) {
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
        if (Utils.manager().get("r." + name + ".t") == null) Initializer.tpa.add(name);

        if (Utils.manager().get("r." + name + ".m") == null) Initializer.msg.add(name);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        e.setRespawnLocation(Initializer.spawn);
    }
}