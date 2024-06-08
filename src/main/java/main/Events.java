package main;

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import it.unimi.dsi.fastutil.Pair;
import main.utils.Initializer;
import main.utils.Utils;
import main.utils.instances.CustomPlayerDataHolder;
import main.utils.modules.storage.DB;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_19_R3.util.CraftChatMessage;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EnderCrystal;
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
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

import static main.Economy.d;
import static main.utils.Initializer.*;
import static main.utils.Utils.*;
import static main.utils.modules.storage.DB.connection;

@SuppressWarnings("deprecation")
public class Events implements Listener {
    private final String JOIN_PREFIX = Utils.translateA("#31ed1c→ ");
    private final ItemStack pick = new ItemStack(Material.IRON_PICKAXE);
    private final ItemStack sword = new ItemStack(Material.IRON_SWORD);
    private final ItemStack helmet = new ItemStack(Material.IRON_HELMET);
    private final ItemStack chestplate = new ItemStack(Material.IRON_CHESTPLATE);
    private final ItemStack leggings = new ItemStack(Material.IRON_LEGGINGS);
    private final ItemStack boots = new ItemStack(Material.IRON_BOOTS);
    private final ItemStack gap = new ItemStack(Material.GOLDEN_APPLE, 16);
    private final String[] allowedCmds = new String[]{
            "/msg",
            "/r",
            "/reply",
            "/tell",
            "/whisper",
            "/suicide",
            "/kill"
    };

    public Events() {
        pick.addEnchantments(Map.of(Enchantment.DIG_SPEED, 3, Enchantment.DURABILITY, 3, Enchantment.LOOT_BONUS_BLOCKS, 2, Enchantment.MENDING, 1));
        sword.addEnchantments(Map.of(Enchantment.DAMAGE_ALL, 2, Enchantment.DURABILITY, 3, Enchantment.MENDING, 1));
        helmet.addEnchantments(Map.of(Enchantment.PROTECTION_ENVIRONMENTAL, 2, Enchantment.DURABILITY, 3, Enchantment.MENDING, 1));
        chestplate.addEnchantments(Map.of(Enchantment.PROTECTION_ENVIRONMENTAL, 2, Enchantment.DURABILITY, 3, Enchantment.MENDING, 1));
        leggings.addEnchantments(Map.of(Enchantment.PROTECTION_ENVIRONMENTAL, 2, Enchantment.DURABILITY, 3, Enchantment.MENDING, 1));
        boots.addEnchantments(Map.of(Enchantment.PROTECTION_ENVIRONMENTAL, 2, Enchantment.DURABILITY, 3, Enchantment.MENDING, 1));
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        final Player player = e.getPlayer();
        final Location location = player.getLocation();
        if (location.getWorld() != d)
            return;
        if (Economy.spawnDistance.distance(location.getX(), location.getZ()) > 128)
            return;
        final Location to = e.getTo();
        final Location from = e.getFrom();
        if (to.getX() == from.getX() &&
                to.getY() == from.getY() &&
                to.getZ() == from.getZ())
            return;
        rotateNPCs(to, ((CraftPlayer) player).getHandle().connection);
    }

    @EventHandler
    private void onEntitySpawn(EntitySpawnEvent e) {
        if (e.getEntity() instanceof EnderCrystal entity)
            crystalsToBeOptimized.put(entity.getEntityId(), entity.getLocation());
    }

    @EventHandler
    private void onEntityRemoveFromWorld(EntityRemoveFromWorldEvent e) {
        if (e.getEntity() instanceof EnderCrystal entity)
            Bukkit.getScheduler().runTaskLater(p, () -> crystalsToBeOptimized.remove(entity.getEntityId()), 40L);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onChat(AsyncPlayerChatEvent e) {
        final String message = e.getMessage();
        if (message.length() > 128) {
            e.setCancelled(true);
            return;
        }
        final CustomPlayerDataHolder D0 = playerData.get(e.getPlayer().getName());
        if (System.currentTimeMillis() < D0.getLastChatMS()) {
            e.setCancelled(true);
            return;
        }
        D0.setLastChatMS(System.currentTimeMillis() + 500L);
        /*Team team = player.getScoreboard().getPlayerTeam(p);
        e.setFormat(team == null ? playerData.get(player.getName()).getFRank(name) + SECOND_COLOR + " » §r" + e.getMessage().replace("%", "%%") :
                ("§7[§6" + team.getDisplayName() + "§7] §f" +
                        playerData.get(player.getName()).getFRank(name) + SECOND_COLOR + " » §r" + e.getMessage().replace("%", "%%")));
        */
    }

    @EventHandler
    private void onCommand(PlayerCommandPreprocessEvent e) {
        final Player player = e.getPlayer();
        if (playerData.get(player.getName()).isTagged()) {
            final String command = e.getMessage();
            for (final String k : allowedCmds) {
                if (command.startsWith(k)) return;
            }
            player.sendMessage(Initializer.EXCEPTION_TAGGED);
            e.setCancelled(true);
        }
    }

    @EventHandler
    private void onInteract(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK ||
                e.getClickedBlock().getType() != Material.LEVER) return;
        final String name = e.getPlayer().getName();
        final Integer o = leverFlickCount.getIfPresent(name);
        int count = o == null ? 0 : o;
        if (count++ > 4) e.setCancelled(true);
        leverFlickCount.put(name, count);
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent e) {
        final Player player = e.getPlayer();
        final String name = player.getName();
        final CustomPlayerDataHolder D0 = playerData.get(name);
        if (D0.isTagged()) {
            D0.untag();
            player.setLastDamageCause(new EntityDamageEvent(player, EntityDamageEvent.DamageCause.SONIC_BOOM, 0.0D));
            player.setHealth(0.0D);
            ((CraftPlayer) player).getHandle().setPosRaw(-0.5D, 140.0D, 0.5D);
        }
        e.setQuitMessage(MAIN_COLOR + "← " + name);
        Initializer.requests.remove(Utils.getRequest(name));
        requests.removeIf(k -> k.getSenderF().equals(name) || k.getReceiver().equals(name));
        if (D0.getLastReceived() != null) {
            CustomPlayerDataHolder D1 = playerData.get(D0.getLastReceived());
            if (D1.getLastReceived() == name) D1.setLastReceived(null);
        }
        D0.setLastReceived(null);
        try (final PreparedStatement statement = connection.prepareStatement("UPDATE data SET m = ?, t = ?, ed = ?, ek = ?, fc = ? WHERE name = '?'")) {
            statement.setInt(1, D0.getMtoggle());
            statement.setInt(2, D0.getTptoggle());
            statement.setInt(3, D0.getDeaths());
            statement.setInt(4, D0.getKills());
            statement.setBoolean(5, D0.isFastCrystals());
            statement.setString(6, name);
            statement.executeUpdate();
        } catch (SQLException ignored) {
        }
        Initializer.msg.remove(name);
        Initializer.tpa.remove(name);

        Initializer.msg.sort(String::compareToIgnoreCase);
        Initializer.tpa.sort(String::compareToIgnoreCase);
    }

    @EventHandler
    private void onInventoryClick(InventoryClickEvent e) {
        if (e.getClickedInventory() instanceof PlayerInventory)
            return;
        final Player player = (Player) e.getWhoClicked();
        final CustomPlayerDataHolder D0 = playerData.get(player.getName());
        final Pair<Integer, String> inv = D0.getInventoryInfo();
        if (inv == null) return;
        if (inv.first() == 0) {
            e.setCancelled(true);
            if (!e.getCurrentItem().getItemMeta().hasLore()) return;
            player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
            D0.setInventoryInfo(null);
            Utils.submitReport(player, inv.second(), switch (e.getSlot()) {
                case 10 -> "Cheating";
                case 11 -> "Doxxing";
                case 12 -> "Ban Evading";
                case 13 -> "Spamming";
                default -> "Undefined";
            });
        }
    }

    @EventHandler
    private void onDeath(PlayerDeathEvent e) {
        final Player player = e.getPlayer();
        final String name = player.getName();
        final Player killer = player.getKiller();
        final CustomPlayerDataHolder D0 = playerData.get(name);
        D0.untag();
        D0.incrementDeaths();
        if (killer == null || killer == player) {
            final EntityDamageEvent.DamageCause cause = player.getLastDamageCause().getCause();
            final String death = SECOND_COLOR + "☠ " + name + " §7" + switch (cause) {
                case ENTITY_EXPLOSION, BLOCK_EXPLOSION -> "blasted themselves";
                case FALL -> "broke their legs";
                case FALLING_BLOCK -> "suffocated";
                case FLY_INTO_WALL -> "tried to bypass physics";
                case FIRE_TICK, LAVA -> "melted away";
                case DROWNING -> "forgot to breathe";
                case STARVATION -> "forgot to eat";
                case POISON -> "was poisoned";
                case MAGIC -> "thought they could cook meth";
                case FREEZE -> "belonged into the water";
                case SUFFOCATION -> "was mashed up pretty good";
                case HOT_FLOOR -> "was heated up pretty good";
                case VOID -> "fell into the void";
                default -> "suicided";
            };
            if (cause == EntityDamageEvent.DamageCause.SONIC_BOOM)
                player.setStatistic(Statistic.DEATHS, player.getStatistic(Statistic.DEATHS) - 1);
            e.setDeathMessage(death);
        } else {
            final String killerName = killer.getName();
            final CustomPlayerDataHolder D1 = playerData.get(killerName);
            D1.untag();
            D1.incrementKills();
            final String death = SECOND_COLOR + "☠ " + killerName + " §7" + switch (player.getLastDamageCause().getCause()) {
                case CONTACT -> "pricked " + SECOND_COLOR + name + " §7to death";
                case ENTITY_EXPLOSION -> "crystalled " + SECOND_COLOR + name;
                case BLOCK_EXPLOSION -> "imploded " + SECOND_COLOR + name;
                case FALL -> "broke " + SECOND_COLOR + name + "§7's legs";
                case ENTITY_ATTACK, ENTITY_SWEEP_ATTACK -> "sworded " + SECOND_COLOR + name;
                case PROJECTILE -> "shot " + SECOND_COLOR + name + " §7into the ass";
                case FIRE_TICK, LAVA -> "melted " + SECOND_COLOR + name + " §7away";
                case VOID -> "pushed " + SECOND_COLOR + name + " §7into the void";
                default -> "suicided";
            };
            e.setDeathMessage(death);
            final Location location = player.getLocation();
            final World world = location.getWorld();
            if (D1.getRank() > 0) {
                location.add(0, 1, 0);
                switch (Initializer.RANDOM.nextInt(5)) {
                    case 0 -> spawnFirework(location);
                    case 1 -> world.spawnParticle(Particle.TOTEM, location, 50, 3, 1, 3, 0.0);
                    case 2 -> world.strikeLightningEffect(location);
                    case 3 -> {
                        for (double y = 0; y < 11; y += 0.05) {
                            world.spawnParticle(Particle.TOTEM, new Location(world, (float) (location.getX() + (2 * Math.cos(y))), (float) (location.getY() + (2 * Math.sin(y))), (float) (location.getZ() + 2 * Math.sin(y))), 2, 0, 0, 0, 1.0);
                        }
                    }
                    case 4 -> {
                        for (double y = 0; y < 11; y += 0.05) {
                            world.spawnParticle(Particle.FLAME, new Location(world, (float) (location.getX() + (2 * Math.cos(y))), (float) (location.getY() + (2 * Math.sin(y))), (float) (location.getZ() + 2 * Math.sin(y))), 2, 0, 0, 0, 1.0);
                        }
                    }
                }
            } else world.strikeLightningEffect(location);
            if (Initializer.RANDOM.nextInt(100) < 6)
                e.getDrops().add(Utils.getHead(name, D1.getFRank(killerName)));
        }
    }

    @EventHandler
    private void onExplosion(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Item ent))
            return;
        final EntityDamageEvent.DamageCause cause = e.getCause();
        if (cause != EntityDamageEvent.DamageCause.BLOCK_EXPLOSION &&
                cause != EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)
            return;
        e.setCancelled(ent.getItemStack().getType().name().contains("NETHERITE"));
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent e) {
        final Player player = e.getPlayer();
        final String name = player.getName();
        e.setJoinMessage(JOIN_PREFIX + name);
        showCosmetics(((CraftPlayer) player).getHandle().connection);
        if (!player.hasPlayedBefore()) {
            final PlayerInventory inv = player.getInventory();
            inv.addItem(sword);
            inv.addItem(pick);
            inv.setHelmet(helmet);
            inv.setChestplate(chestplate);
            inv.setLeggings(leggings);
            inv.setBoots(boots);
            inv.setItemInOffHand(gap);
            player.teleport(Initializer.spawn);
            Initializer.tpa.add(name);
            Initializer.msg.add(name);
            Initializer.tpa.sort(String::compareToIgnoreCase);
            Initializer.msg.sort(String::compareToIgnoreCase);
            playerData.put(name, new CustomPlayerDataHolder(0, 0, 0, 0, 0));
            return;
        }
        final CustomPlayerDataHolder D = playerData.get(name);
        if (D == null) {
            Initializer.tpa.add(name);
            Initializer.msg.add(name);
            Initializer.tpa.sort(String::compareToIgnoreCase);
            Initializer.msg.sort(String::compareToIgnoreCase);
            playerData.put(name, getPlayerData(name));
        } else {
            final int rank = DB.setUsefulData(name, D);
            final ServerPlayer craftPlayer = ((CraftPlayer) player).getHandle();
            craftPlayer.listName = CraftChatMessage.fromString(D.getFRank(name))[0];
            for (final ServerPlayer k : DedicatedServer.getServer().getPlayerList().players) {
                k.connection.send(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_DISPLAY_NAME, craftPlayer));
            }
            Bukkit.getScheduler().runTaskLater(Initializer.p, () -> {
                switch (rank) {
                    case 1 -> cattoLovesTeam.addEntry(name);
                    case 2 -> cattoHatesTeam.addEntry(name);
                    case 3 -> gayTeam.addEntry(name);
                    case 4 -> vipTeam.addEntry(name);
                    case 5 -> boosterTeam.addEntry(name);
                    case 6 -> mediaTeam.addEntry(name);
                    case 7 -> trialHelperTeam.addEntry(name);
                    case 8 -> helperTeam.addEntry(name);
                    case 9 -> jrmodTeam.addEntry(name);
                    case 10 -> modTeam.addEntry(name);
                    case 11 -> adminTeam.addEntry(name);
                    case 12 -> managerTeam.addEntry(name);
                    case 13 -> ownerTeam.addEntry(name);
                }
            }, 5L);
            if (D.getTptoggle() == 0) {
                Initializer.tpa.add(name);
                Initializer.tpa.sort(String::compareToIgnoreCase);
            }
            if (D.getMtoggle() == 0) {
                Initializer.msg.add(name);
                Initializer.msg.sort(String::compareToIgnoreCase);
            }
        }
    }

    @EventHandler
    private void onRespawn(PlayerRespawnEvent e) {
        e.setRespawnLocation(spawn);
        final ServerGamePacketListenerImpl connection = ((CraftPlayer) e.getPlayer()).getHandle().connection;
        Bukkit.getScheduler().runTaskLater(Initializer.p, () -> {
            showCosmetics(connection);
            rotateNPCs(spawn, connection);
        }, 2L);
    }
}