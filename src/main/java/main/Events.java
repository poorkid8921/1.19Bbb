package main;

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import main.utils.Initializer;
import main.utils.Utils;
import main.utils.instances.CustomPlayerDataHolder;
import main.utils.storage.DB;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_19_R3.util.CraftChatMessage;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
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
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Map;

import static main.utils.Initializer.*;
import static main.utils.Utils.spawnFirework;

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
    private final ObjectOpenHashSet<String> allowedCmds = ObjectOpenHashSet.of("/msg", "/r", "/reply", "/tell", "/whisper", "/suicide");

    public Events() {
        pick.addEnchantments(Map.of(Enchantment.DIG_SPEED, 3, Enchantment.DURABILITY, 3, Enchantment.LOOT_BONUS_BLOCKS, 2, Enchantment.MENDING, 1));
        sword.addEnchantments(Map.of(Enchantment.DAMAGE_ALL, 2, Enchantment.DURABILITY, 3, Enchantment.MENDING, 1));
        helmet.addEnchantments(Map.of(Enchantment.PROTECTION_ENVIRONMENTAL, 2, Enchantment.DURABILITY, 3, Enchantment.MENDING, 1));
        chestplate.addEnchantments(Map.of(Enchantment.PROTECTION_ENVIRONMENTAL, 2, Enchantment.DURABILITY, 3, Enchantment.MENDING, 1));
        leggings.addEnchantments(Map.of(Enchantment.PROTECTION_ENVIRONMENTAL, 2, Enchantment.DURABILITY, 3, Enchantment.MENDING, 1));
        boots.addEnchantments(Map.of(Enchantment.PROTECTION_ENVIRONMENTAL, 2, Enchantment.DURABILITY, 3, Enchantment.MENDING, 1));
    }

    @EventHandler
    private void onEntitySpawn(EntitySpawnEvent e) {
        if (e.getEntityType() == EntityType.ENDER_CRYSTAL) {
            Entity ent = e.getEntity();
            crystalsToBeOptimized.put(ent.getEntityId(), ent.getLocation());
        }
    }

    @EventHandler
    private void onEntityRemoveFromWorld(EntityRemoveFromWorldEvent e) {
        if (e.getEntityType() == EntityType.ENDER_CRYSTAL)
            Bukkit.getScheduler().runTaskLater(p, () -> crystalsToBeOptimized.remove(e.getEntity().getEntityId()), 40L);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        String name = p.getName();
        CustomPlayerDataHolder D = playerData.get(name);
        if (System.currentTimeMillis() < D.getLastChatMS()) {
            e.setCancelled(true);
            return;
        }
        D.setLastChatMS(System.currentTimeMillis() + 500L);
        /*Team team = p.getScoreboard().getPlayerTeam(p);
        e.setFormat(team == null ? playerData.get(p.getName()).getFRank(name) + SECOND_COLOR + " » §r" + e.getMessage().replace("%", "%%") :
                ("§7[§6" + team.getDisplayName() + "§7] §f" +
                        playerData.get(p.getName()).getFRank(name) + SECOND_COLOR + " » §r" + e.getMessage().replace("%", "%%")));
        */
    }

    @EventHandler
    private void onCommand(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();
        if (playerData.get(p.getName()).isTagged()) {
            String command = e.getMessage();
            for (String k : allowedCmds) {
                if (command.startsWith(k)) return;
            }
            p.sendMessage(Initializer.EXCEPTION_TAGGED);
            e.setCancelled(true);
        }
    }

    @EventHandler
    private void onInteract(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK || e.getClickedBlock().getType() != Material.LEVER) return;
        String name = e.getPlayer().getName();
        if (Initializer.cooldowns.getOrDefault(name, 0L) > System.currentTimeMillis()) e.setCancelled(true);
        else Initializer.cooldowns.put(name, System.currentTimeMillis() + 500L);
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        String name = p.getName();
        CustomPlayerDataHolder D0 = playerData.get(name);
        if (D0.isTagged()) {
            D0.untag();
            p.setHealth(0);
        }
        e.setQuitMessage(MAIN_COLOR + "← " + name);
        Initializer.requests.remove(Utils.getRequest(name));

        if (D0.getLastReceived() != null) {
            CustomPlayerDataHolder D1 = playerData.get(D0.getLastReceived());
            if (D1.getLastReceived() == name) D1.setLastReceived(null);
        }
        D0.setLastReceived(null);
        Initializer.msg.remove(name);
        Initializer.tpa.remove(name);

        Initializer.msg.sort(String::compareToIgnoreCase);
        Initializer.tpa.sort(String::compareToIgnoreCase);
    }

    @EventHandler
    private void onInventoryClick(InventoryClickEvent e) {
        Inventory c = e.getClickedInventory();
        if (c instanceof PlayerInventory) return;

        Player p = (Player) e.getWhoClicked();
        String name = p.getName();
        CustomPlayerDataHolder D0 = playerData.get(name);
        Pair<Integer, String> inv = D0.getInventoryInfo();
        if (inv == null) return;

        int slot = e.getSlot();
        if (inv.first() == 0) {
            e.setCancelled(true);
            if (!e.getCurrentItem().getItemMeta().hasLore()) return;
            p.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
            D0.setInventoryInfo(null);
            Utils.submitReport(p, inv.second(), switch (slot) {
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
        Player p = e.getPlayer();
        String name = p.getName();
        Player killer = p.getKiller();
        if (killer == null || killer == p) {
            String death = SECOND_COLOR + "☠ " + name + " §7" + switch (p.getLastDamageCause().getCause()) {
                case ENTITY_EXPLOSION, BLOCK_EXPLOSION -> "blasted themselves";
                case FALL -> "broke their legs by not using feather falling";
                case FALLING_BLOCK -> "couldn't escape from ishowspeed's fart";
                case FLY_INTO_WALL -> "tried to bypass physics";
                case FIRE_TICK, LAVA -> "melted away";
                case DROWNING -> "forgot to breathe";
                case STARVATION -> "forgot to eat";
                case POISON -> "was poisoned from ishowspeed's fast";
                case MAGIC -> "thought they could cook meth";
                case FREEZE -> "belonged into the water";
                case SUFFOCATION -> "was mashed up pretty good";
                case HOT_FLOOR -> "was oiled up in lava";
                case VOID -> ": imagine falling into the void";
                default -> "suicided because they thought they are HIM";
            };
            e.setDeathMessage(death);
        } else {
            CustomPlayerDataHolder D0 = playerData.get(name);
            D0.untag();
            D0.incrementDeaths();

            String kp = killer.getName();
            CustomPlayerDataHolder D1 = playerData.get(kp);
            D1.untag();
            D1.incrementKills();

            int armor = 0;
            for (ItemStack item : p.getInventory().getArmorContents()) {
                if (item != null) armor++;
            }
            String death = armor == 0 ? SECOND_COLOR + "☠ " + kp + " §7touched " + SECOND_COLOR + name : SECOND_COLOR + "☠ " + kp + " §7" + switch (p.getLastDamageCause().getCause()) {
                case CONTACT -> "pricked " + SECOND_COLOR + name + " §7to death";
                case ENTITY_EXPLOSION -> "exploded " + SECOND_COLOR + name;
                case BLOCK_EXPLOSION -> "imploded " + SECOND_COLOR + name;
                case FALL -> "broke " + SECOND_COLOR + name + "§7's legs";
                case ENTITY_ATTACK, ENTITY_SWEEP_ATTACK -> "raped " + SECOND_COLOR + name;
                case PROJECTILE -> "shot " + SECOND_COLOR + name + " §7into the ass";
                case FIRE_TICK, LAVA -> "melted " + SECOND_COLOR + name + " §7by watching skibidi toilet";
                case VOID -> "pushed " + SECOND_COLOR + name + " §7into the void";
                default -> "suicided because they thought that they are HIM";
            };
            e.setDeathMessage(death);

            Location loc = p.getLocation();
            World w = loc.getWorld();
            if (D1.getRank() > 0) {
                loc.add(0, 1, 0);
                switch (Initializer.RANDOM.nextInt(4)) {
                    case 0 -> spawnFirework(loc);
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
                w.dropItemNaturally(loc, Utils.getHead(p, killer.getDisplayName()));
        }
    }

    @EventHandler
    private void onExplosion(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Item a)) return;
        EntityDamageEvent.DamageCause c = e.getCause();
        if (c != EntityDamageEvent.DamageCause.BLOCK_EXPLOSION && c != EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)
            return;
        String name = a.getItemStack().getType().name();
        e.setCancelled(name.contains("DIAMOND") || name.contains("NETHERITE"));
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if (!p.hasPlayedBefore()) {
            PlayerInventory inv = p.getInventory();
            inv.addItem(sword);
            inv.addItem(pick);
            inv.setHelmet(helmet);
            inv.setChestplate(chestplate);
            inv.setLeggings(leggings);
            inv.setBoots(boots);
            inv.setItemInOffHand(gap);
            p.teleportAsync(Initializer.spawn);

            String name = p.getName();
            e.setJoinMessage(JOIN_PREFIX + name);

            Initializer.tpa.add(name);
            Initializer.msg.add(name);
            Initializer.tpa.sort(String::compareToIgnoreCase);
            Initializer.msg.sort(String::compareToIgnoreCase);
            playerData.put(name, new CustomPlayerDataHolder(0, 0, 0, 0, 0, ObjectArrayList.of()));
            return;
        }
        String name = p.getName();
        e.setJoinMessage(JOIN_PREFIX + name);

        CustomPlayerDataHolder D = playerData.get(name);
        if (D == null) {
            Initializer.tpa.add(name);
            Initializer.msg.add(name);

            Initializer.tpa.sort(String::compareToIgnoreCase);
            Initializer.msg.sort(String::compareToIgnoreCase);

            playerData.put(name, new CustomPlayerDataHolder(0, 0, 0, 0, 0, ObjectArrayList.of()));
        } else {
            int rank = DB.parseRank(name);
            ServerPlayer craftPlayer = ((CraftPlayer) p).getHandle();
            craftPlayer.listName = CraftChatMessage.fromString(D.getFRank(name))[0];
            for (ServerPlayer player : DedicatedServer.getServer().getPlayerList().players) {
                player.connection.send(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_DISPLAY_NAME, craftPlayer));
            }
            switch (rank) {
                case 1 -> gayTeam.addPlayer(p);
                case 2 -> cattoHatesTeam.addPlayer(p);
                case 3 -> cattoLovesTeam.addPlayer(p);
                case 4 -> angelTeam.addPlayer(p);
                case 5 -> vipTeam.addPlayer(p);
                case 6 -> boosterTeam.addPlayer(p);
                case 7 -> mediaTeam.addPlayer(p);
                case 8 -> trialHelperTeam.addPlayer(p);
                case 9 -> helperTeam.addPlayer(p);
                case 10 -> jrmodTeam.addPlayer(p);
                case 11 -> modTeam.addPlayer(p);
                case 12 -> adminTeam.addPlayer(p);
                case 13 -> managerTeam.addPlayer(p);
                case 14 -> ownerTeam.addPlayer(p);
            }
            D.setRank(rank);
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
        e.setRespawnLocation(Initializer.spawn);
    }
}