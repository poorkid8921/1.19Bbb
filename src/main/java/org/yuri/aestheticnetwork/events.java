package org.yuri.aestheticnetwork;

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.papermc.lib.PaperLib;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.TextComponent;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Container;
import org.bukkit.block.ShulkerBox;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.TabCompleteEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Redstone;
import org.bukkit.permissions.ServerOperator;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import static org.yuri.aestheticnetwork.Utils.*;

public class events implements Listener {
    AestheticNetwork plugin = AestheticNetwork.getInstance();

    LuckPerms lp;
    Permission perms;
    private final HashMap<UUID, Long> cooldowns = new HashMap<>();
    private final HashMap<UUID, Long> playerstoteming = new HashMap<>();

    public events(LuckPerms lped, Permission perm) {
        lp = lped;
        perms = perm;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onPortal(PlayerPortalEvent event) {
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
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

    /*@EventHandler
    private void onDispense(BlockDispenseEvent e) {
        if (e.getItem().getType().equals(Material.BONE_MEAL)) {
            Container container = (Container) e.getBlock().getState();
            container.getInventory().addItem(e.getItem());
        }
    }*/

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
        AestheticNetwork.lastReceived.remove(playerUniqueId);
        //AestheticNetwork.hm.remove(playerUniqueId);
        Report.cooldown.remove(playerUniqueId);
        AestheticNetwork.msg.remove(e.getPlayer().getName());
        AestheticNetwork.tpa.remove(e.getPlayer().getName());
    }

    private static boolean isSuspectedScanPacket(String buffer) {
        return (buffer.split(" ").length == 1 && !buffer.endsWith(" ")) ||
                !buffer.startsWith("/") ||
                buffer.startsWith("/about");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onAsyncCommandTabComplete(AsyncTabCompleteEvent event) {
        if (!(event.getSender() instanceof Player)) return;
        event.setCancelled(isSuspectedScanPacket(event.getBuffer()));
    }

    @EventHandler
    public void onPlayerRegisterChannel(PlayerRegisterChannelEvent e) {
        if (e.getChannel().equals("hcscr:haram"))
            e.getPlayer().sendPluginMessage(plugin, "hcscr:haram", new byte[]{1});
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        if (e.getInventory().getHolder() instanceof InventoryInstance) {
            e.setCancelled(true);
            if (e.getClickedInventory() != null && e.getClickedInventory().equals(e.getInventory()))
                ((InventoryInstance) e.getInventory().getHolder()).whenClicked(e.getCurrentItem(), e.getAction(), e.getSlot());
            return;
        }

        final ItemStack clickedItem = e.getCurrentItem();
        if (clickedItem == null)
            return;

        ItemMeta meta = clickedItem.getItemMeta();
        if (meta == null)
            return;

        NamespacedKey key = new NamespacedKey(plugin, "reported");
        final Player p = (Player) e.getWhoClicked();
        String report = meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
        if (report != null) {
            if (e.getRawSlot() == 10)
                report(plugin, p, report, "Exploiting");
            else if (e.getRawSlot() == 11)
                report(plugin, p, report, "Doxxing");
            else if (e.getRawSlot() == 12)
                report(plugin, p, report, "Ban Evasion");
            else if (e.getRawSlot() == 13)
                report(plugin, p, report, "Spamming");
            else if (e.getRawSlot() == 14)
                report(plugin, p, report, "Advertising");
        }
    }

    @EventHandler
    public void onInventoryClick(final InventoryDragEvent e) {
        if (e.getCursor() == null)
            return;

        ItemMeta meta = e.getCursor().getItemMeta();
        e.setCancelled(meta.hasLore());
    }

    static ArrayList<Color> color = new ArrayList<>(List.of(Color.LIME,
            Color.ORANGE,
            Color.RED,
            Color.BLUE,
            Color.OLIVE,
            Color.PURPLE,
            Color.WHITE,
            Color.AQUA,
            Color.BLACK,
            Color.FUCHSIA,
            Color.GRAY,
            Color.GREEN,
            Color.MAROON,
            Color.NAVY,
            Color.SILVER,
            Color.TEAL,
            Color.YELLOW));

    public static void spawnFireworks(Location loc) {
        loc.add(new Vector(0, 1, 0));
        Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();
        fwm.setPower(2);
        Random random = new Random();
        fwm.addEffect(FireworkEffect.builder().withColor(color.get(random.nextInt(color.size()))).withColor(color.get(random.nextInt(color.size()))).with(FireworkEffect.Type.BALL_LARGE).flicker(true).build());
        fw.setFireworkMeta(fwm);
    }

    static World respawnWorld = Bukkit.getWorld("world");
    public static final ArrayList<Material> bannedblocks = new ArrayList<>(Arrays.asList(
            Material.LAVA, Material.WATER, Material.CACTUS, Material.BARRIER
    ));

    public static Location calcSpawnLocation() {
        int x = new Random().nextInt(25000);
        int z = new Random().nextInt(25000);
        assert respawnWorld != null;

        if (x > 10000)
            x = -x;

        if (z > 7500)
            z = -z;

        int y = respawnWorld.getHighestBlockYAt(x, z);
        Block blockAt = respawnWorld.getBlockAt(x, y, z);
        if (bannedblocks.contains(blockAt.getType()))
            return null;
        return new Location(respawnWorld, x, y, z).add(new Vector(0, 1, 0));
    }

    static Random rand = new Random();

    public static ItemStack gearEnch(ItemStack is, Enchantment em) {
        ItemMeta ism = is.getItemMeta();
        ism.setDisplayName(translate("&7ᴍᴄ.ᴀᴇꜱᴛʜᴇᴛɪᴄ.&cʀᴇᴅ"));
        is.setItemMeta(ism);
        is.addEnchantment(em, 4);
        is.addEnchantment(Enchantment.MENDING, 1);
        is.addEnchantment(Enchantment.DURABILITY, 3);
        return is;
    }

    public static void spawnLootdrop() {
        Location respawn = null;
        while (respawn == null) respawn = calcSpawnLocation();
        Bukkit.getServer().getWorld("world").loadChunk(respawn.getChunk());
        respawn.getBlock().setType(Material.RED_SHULKER_BOX);
        ShulkerBox c = (ShulkerBox) respawn.getBlock().getState();
        Inventory i = c.getInventory();

       /*Bukkit.getScheduler().runTaskAsynchronously(AestheticNetwork.getInstance(), () -> {
            int i1 = rand.nextInt(27);
            int i2 = rand.nextInt(27);
            int i3 = rand.nextInt(27);
            int i4 = rand.nextInt(27);
            int i5 = rand.nextInt(27);
            Bukkit.getScheduler().runTask(AestheticNetwork.getInstance(), () -> {
                for (int ii = 0; ii <= rand.nextInt(2); ii++) {
                    i.setItem(i1, gearEnch(new ItemStack(Material.NETHERITE_HELMET),
                            Enchantment.PROTECTION_ENVIRONMENTAL));
                    i.setItem(i2, gearEnch(new ItemStack(Material.NETHERITE_CHESTPLATE),
                            Enchantment.PROTECTION_ENVIRONMENTAL));
                    i.setItem(i3 + ii, gearEnch(new ItemStack(Material.NETHERITE_LEGGINGS),
                            Enchantment.PROTECTION_EXPLOSIONS));
                    i.setItem(i4 + ii, gearEnch(new ItemStack(Material.NETHERITE_BOOTS),
                            Enchantment.PROTECTION_ENVIRONMENTAL));
                }
                i.setItem(i5, gearEnch(new ItemStack(Material.NETHERITE_PICKAXE), Enchantment.DIG_SPEED));
                i.setItem(27 - i5, gearEnch(new ItemStack(Material.NETHERITE_SWORD), Enchantment.DAMAGE_ALL));

                for (int iii = 0; iii < rand.nextInt(5); iii++) {
                    i.setItem(iii*2, new ItemStack(Material.COBWEB, rand.nextInt(32)));
                }
            });
        });*/
        c.setLootTable(Bukkit.getServer().getLootTable(NamespacedKey.minecraft("chests/economyloot")));
        Bukkit.getServer().broadcastMessage(translate("&7A lootbox has spawned at &c" + (int) respawn.getX() + " " +
                (int) respawn.getY() + " " +
                (int) respawn.getZ() + "&7!"));
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

        Bukkit.getServer().getScheduler().runTask(plugin, () -> {
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
                && e.getCause() != EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {
            return;
        }
        final Material type = ((Item) e.getEntity()).getItemStack().getType();
        e.setCancelled(type.name().contains("DIAMOND") || type.name().contains("NETHERITE"));
    }

    @EventHandler
    public void playeruse(PlayerItemConsumeEvent e) {
        e.setCancelled(e.getItem().getType().equals(Material.ENCHANTED_GOLDEN_APPLE));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(final AsyncPlayerChatEvent e) {
        if (AestheticNetwork.chatlock && !e.getPlayer().hasPermission("has.staff")) {
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
            e.getPlayer().teleport(new Location(Bukkit.getWorld(plugin.getConfig().getString("Spawn.World")), plugin.getConfig().getDouble("Spawn.X"), plugin.getConfig().getDouble("Spawn.Y"), plugin.getConfig().getDouble("Spawn.Z")));
        }

        // fixes the "0 health no respawn" bug
        if (e.getPlayer().getHealth() == 0.0) {
            Bukkit.getServer().getScheduler().runTask(plugin, () -> {
                e.getPlayer().setHealth(20);
                e.getPlayer().kickPlayer("Disconnected");
            });
        }

        if (Utils.manager().get("r." + e.getPlayer().getUniqueId() + ".t") == null)
            AestheticNetwork.tpa.add(e.getPlayer().getName());

        if (Utils.manager().get("r." + e.getPlayer().getUniqueId() + ".m") == null)
            AestheticNetwork.msg.add(e.getPlayer().getName());

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
            e.setRespawnLocation(new Location(Bukkit.getWorld(plugin.getConfig().getString("Spawn.World")),
                    plugin.getConfig().getDouble("Spawn.X"),
                    plugin.getConfig().getDouble("Spawn.Y"),
                    plugin.getConfig().getDouble("Spawn.Z")));
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