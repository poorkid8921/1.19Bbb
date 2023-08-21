package org.yuri.aestheticnetwork;

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import io.papermc.lib.PaperLib;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.milkbowl.vault.permission.Permission;
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
import org.yuri.aestheticnetwork.commands.Report;
import org.yuri.aestheticnetwork.utils.InventoryInstance;
import org.yuri.aestheticnetwork.utils.InventoryInstanceReport;
import org.yuri.aestheticnetwork.utils.Utils;

import java.util.*;

import static org.yuri.aestheticnetwork.utils.Initializer.*;
import static org.yuri.aestheticnetwork.utils.Utils.removeRequest;
import static org.yuri.aestheticnetwork.utils.Utils.translate;

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
        fwm.addEffect(FireworkEffect.builder().withColor(color.get(random.nextInt(color.size()))).withColor(color.get(random.nextInt(color.size()))).with(FireworkEffect.Type.BALL_LARGE).flicker(true).build());
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
        p.getServer().getScheduler().scheduleSyncDelayedTask(p, () -> {
            Player player = event.getPlayer();
            if (player.getLocation().getBlock().getType().equals(Material.NETHER_PORTAL)) {
                PaperLib.teleportAsync(player, event.getFrom());
                player.playSound(player.getLocation(), Sound.BLOCK_PORTAL_TRAVEL, 1.0F, 1.0F);
            }
        }, 200);
    }

    @EventHandler
    private void antiAuto(PlayerSwapHandItemsEvent e) {
        Player ent = e.getPlayer();

        UUID playerUniqueId = ent.getUniqueId();
        if (playerstoteming.containsKey(playerUniqueId) && playerstoteming.get(playerUniqueId) > System.currentTimeMillis()) {
            for (Player i : Bukkit.getOnlinePlayers()) {
                if (!i.hasPermission("has.staff"))
                    continue;

                long ms = playerstoteming.get(playerUniqueId) - System.currentTimeMillis();
                i.sendMessage(translate("&6" + ent.getName() + " totemed in less than " + ms + "ms! &7" + ent.getPing() + "ms"));
            }
            //e.setCancelled(true);
            playerstoteming.remove(playerUniqueId);
        }
    }

    @EventHandler
    public void antiAuto2(EntityResurrectEvent e) {
        if (!playerstoteming.containsKey(e.getEntity().getUniqueId()))
            playerstoteming.put(e.getEntity().getUniqueId(), System.currentTimeMillis() + 500);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onInteract(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        if (!Objects.requireNonNull(event.getClickedBlock()).getType().equals(Material.LEVER)) return;

        UUID playerUniqueId = event.getPlayer().getUniqueId();
        if (
                cooldowns.containsKey(playerUniqueId)
                        && cooldowns.get(playerUniqueId) > System.currentTimeMillis()
        ) {
            event.setCancelled(true);
        } else
            cooldowns.put(playerUniqueId, System.currentTimeMillis() + 500);
    }

    @EventHandler
    private void onPlayerLeave(final PlayerQuitEvent e) {
        removeRequest(e.getPlayer());
        UUID playerUniqueId = e.getPlayer().getUniqueId();
        cooldowns.remove(playerUniqueId);
        playerstoteming.remove(playerUniqueId);
        lastReceived.remove(playerUniqueId);
        //AestheticNetwork.hm.remove(playerUniqueId);
        Report.cooldown.remove(playerUniqueId);
        msg.remove(e.getPlayer().getName());
        tpa.remove(e.getPlayer().getName());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onAsyncCommandTabComplete(AsyncTabCompleteEvent event) {
        if (!(event.getSender() instanceof Player)) return;
        event.setCancelled(isSuspectedScanPacket(event.getBuffer()));
    }

    @EventHandler
    public void onPlayerRegisterChannel(PlayerRegisterChannelEvent e) {
        if (e.getChannel().equals("hcscr:haram"))
            e.getPlayer().sendPluginMessage(p, "hcscr:haram", new byte[]{1});
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        if (e.getInventory().getHolder() instanceof InventoryInstance) {
            e.setCancelled(true);
            if (e.getClickedInventory() != null && e.getClickedInventory().equals(e.getInventory()))
                ((InventoryInstance) e.getInventory().getHolder()).whenClicked(e.getCurrentItem(),
                        e.getAction(),
                        e.getSlot());
        } else if (e.getInventory().getHolder() instanceof InventoryInstanceReport holder) {
            e.setCancelled(true);
            final ItemStack clickedItem = e.getCurrentItem();
            ItemMeta meta = clickedItem.getItemMeta();
            if (meta == null)
                return;

            holder.whenClicked(e.getCurrentItem(),
                    e.getAction(),
                    e.getSlot(),
                    holder.getArg());
        }
    }

    @EventHandler
    public void onInventoryClick(final InventoryDragEvent e) {
        if (e.getCursor() == null)
            return;

        e.setCancelled(e.getInventory().getHolder() instanceof InventoryInstance ||
                e.getInventory().getHolder() instanceof InventoryInstanceReport);
    }

    @EventHandler
    public void onCreatureSpawn(final CreatureSpawnEvent e) {
        e.setCancelled(e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.DISPENSE_EGG ||
                e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER_EGG ||
                e.getEntity() instanceof Fish);
    }

    @EventHandler
    public void onSpawner(final SpawnerSpawnEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    private void onPlayerKill(final PlayerDeathEvent e) {
        playerstoteming.remove(e.getPlayer().getUniqueId());

        if (e.getPlayer().getKiller() == null)
            return;

        User user = lp.getPlayerAdapter(Player.class).getUser(e.getPlayer().getKiller());
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

        Bukkit.getServer().getScheduler().runTask(p, () -> {
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
        if (chatlock && !e.getPlayer().hasPermission("has.staff")) {
            e.getPlayer().sendMessage(Utils.translate("&7Chat is currently locked. Try again later"));
            e.setCancelled(true);
            return;
        }

        if (e.getMessage().length() > 128) {
            e.setCancelled(true);
            return;
        }
        UUID playerUniqueId = e.getPlayer().getUniqueId();
        if (cooldowns.containsKey(playerUniqueId) && cooldowns.get(playerUniqueId) > System.currentTimeMillis())
            e.setCancelled(true);
        else
            cooldowns.put(playerUniqueId, System.currentTimeMillis() + 500);
    }

    @EventHandler
    public void onVehicleCollide(VehicleEntityCollisionEvent event) {
        if (countMinecartInChunk(event.getVehicle().getChunk()) >= 16) {
            event.setCancelled(removeMinecartInChunk(event.getVehicle().getChunk()));
            Bukkit.getOnlinePlayers().stream().filter(ServerOperator::isOp).
                    forEach(s ->
                            s.sendMessage(translate("&f*** minecraft lag machine at &6" +
                                    event.getVehicle().getChunk().getX() + " " +
                                    event.getVehicle().getChunk().getZ() + " " +
                                    event.getVehicle().getChunk().getWorld())));
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
                    p.getConfig().getDouble("Spawn.X"),
                    p.getConfig().getDouble("Spawn.Y"),
                    p.getConfig().getDouble("Spawn.Z")));
        }

        // fixes the "0 health no respawn" bug
        if (e.getPlayer().getHealth() == 0.0) {
            Bukkit.getServer().getScheduler().runTask(p, () -> {
                e.getPlayer().setHealth(20);
                e.getPlayer().kickPlayer("Disconnected");
            });
        }

        if (Utils.manager().get("r." + e.getPlayer().getUniqueId() + ".t") == null)
            tpa.add(e.getPlayer().getName());

        if (Utils.manager().get("r." + e.getPlayer().getUniqueId() + ".m") == null)
            msg.add(e.getPlayer().getName());

        /*final URI ENDPOINT = URI.create("https://api.uku3lig.net/tiers/vanilla");
        final HttpClient client = HttpClient.newHttpClient();
        final HttpRequest request = HttpRequest.newBuilder(ENDPOINT).GET().build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(s -> AestheticNetwork.hm.put(e.getPlayer().getUniqueId(), s));*/
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        //e.getPlayer().kickPlayer("You have lost the FFA event.");
        if (e.getPlayer().getBedSpawnLocation() == null)
            e.setRespawnLocation(new Location(Bukkit.getWorld("world"),
                    p.getConfig().getDouble("Spawn.X"),
                    p.getConfig().getDouble("Spawn.Y"),
                    p.getConfig().getDouble("Spawn.Z")));
    }

    @EventHandler
    public void onRedstone(BlockRedstoneEvent e) {
        e.setNewCurrent(AestheticNetwork.getTPSofLastSecond() > 18 ? e.getNewCurrent() : 0);
    }

    /*public static int amountOfMaterialInChunk(Chunk chunk, Material material) {
        final int minY = -64;
        final int maxY = chunk.getWorld().getMaxHeight();
        int count = 0;
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = minY; y < maxY; y++) {
                    if (chunk.getBlock(x, y, z).getType().equals(material))
                        count++;
                }
            }
        }
        return count;
    }

    @EventHandler
    private void onBlockPlace(BlockPlaceEvent event) {
        final Material blockPlayerWantsToPlace = event.getBlock().getType();
        event.setCancelled(event.getBlock() instanceof Redstone &&
                amountOfMaterialInChunk(event.getBlock().getChunk(), blockPlayerWantsToPlace) > 32);
    }*/
}