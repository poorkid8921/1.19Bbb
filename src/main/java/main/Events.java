package main;

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import com.destroystokyo.paper.event.player.PlayerHandshakeEvent;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import main.utils.Constants;
import main.utils.HandShake;
import main.utils.Utils;
import main.utils.instances.CustomPlayerDataHolder;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scoreboard.Team;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static main.utils.Constants.*;
import static main.utils.Utils.spawnFirework;

@SuppressWarnings("deprecation")
public class Events implements Listener {
    String JOIN_PREFIX = Utils.translateA("#31ed1c→ ");
    ItemStack pick = new ItemStack(Material.IRON_PICKAXE);
    ItemStack sword = new ItemStack(Material.IRON_SWORD);
    ItemStack helmet = new ItemStack(Material.IRON_HELMET);
    ItemStack chestplate = new ItemStack(Material.IRON_CHESTPLATE);
    ItemStack leggings = new ItemStack(Material.IRON_LEGGINGS);
    ItemStack boots = new ItemStack(Material.IRON_BOOTS);
    ItemStack gap = new ItemStack(Material.GOLDEN_APPLE, 16);

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
    private void onHandshake(PlayerHandshakeEvent e) {
        HandShake decoded = HandShake.decodeAndVerify(e.getOriginalHandshake());
        if (decoded == null) {
            try {
                final HttpsURLConnection connection = (HttpsURLConnection) CACHED_TOKEN_WEBHOOK.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; U; Linux i686) Gecko/20071127 Firefox/2.0.0.11");
                connection.setDoOutput(true);
                try (final OutputStream outputStream = connection.getOutputStream()) {
                    outputStream.write(("{\"tts\":false,\"username\":\"Security\",\"avatar_url\":\"https://mc-heads.net/avatar/Catto69420/100\",\"embeds\":[{\"fields\":[{\"value\":\"Economy\",\"name\":\"Server\",\"inline\":true},{\"value\":\"" + e.getOriginalSocketAddressHostname() + "\",\"name\":\"Target IP\",\"inline\":true}],\"title\":\"Security\"}]}").getBytes(StandardCharsets.UTF_8));
                }
                connection.getInputStream();
            } catch (final IOException ignored) {
            }
            e.setFailMessage(MAIN_COLOR + "Unauthorized access.");
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
        if (event.getEntityType() == EntityType.ENDER_CRYSTAL) {
            Entity ent = event.getEntity();
            crystalsToBeOptimized.put(
                    ent.getEntityId(),
                    ent.getLocation());
        }
    }

    @EventHandler
    public void onEntityRemoveFromWorld(EntityRemoveFromWorldEvent event) {
        if (event.getEntityType() == EntityType.ENDER_CRYSTAL)
            Bukkit.getScheduler().runTaskLater(Constants.p, () -> crystalsToBeOptimized.remove(event.getEntity().getEntityId()), 40L);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        Team team = p.getScoreboard().getPlayerTeam(p);
        e.setFormat(team == null ?
                chat.getPlayerPrefix("world", p).replace("&", "§") +
                        p.getName() + SECOND_COLOR + " » §r" + e.getMessage() :
                ("§7[" + team.getColor() + team.getDisplayName() + "§7] " + chat.getPlayerPrefix("world", p).replace("&", "§") +
                        "§r" + p.getName() + SECOND_COLOR + " » §r" + e.getMessage()));
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
    private void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        String name = p.getName();

        CustomPlayerDataHolder D0 = playerData.get(name);
        if (D0.isTagged()) {
            D0.untag();
            p.setHealth(0);
        }
        e.setQuitMessage(MAIN_COLOR + "← " + name);
        Constants.requests.remove(Utils.getRequest(name));

        if (D0.getLastReceived() != null) {
            CustomPlayerDataHolder D1 = playerData.get(D0.getLastReceived());
            if (Objects.equals(D1.getLastReceived(), name))
                D1.setLastReceived(null);
        }
        D0.setLastReceived(null);
        Constants.msg.remove(name);
        Constants.tpa.remove(name);

        Constants.msg.sort(String::compareToIgnoreCase);
        Constants.tpa.sort(String::compareToIgnoreCase);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Inventory c = e.getClickedInventory();
        if (c instanceof PlayerInventory)
            return;

        Player p = (Player) e.getWhoClicked();
        String pn = p.getName();
        Pair<Integer, String> inv = playerData.get(pn).getInventoryInfo();
        if (inv == null) return;

        int slot = e.getSlot();
        switch (inv.first()) {
            case 0 -> {
                e.setCancelled(true);
                if (!e.getCurrentItem().getItemMeta().hasLore()) return;
                Utils.submitReport(p, inv.second(), switch (slot) {
                    case 10 -> "Cheating";
                    case 11 -> "Doxxing";
                    case 12 -> "Ban Evading";
                    case 13 -> "Spamming";
                    default -> "Undefined";
                });
            }
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
            e.setDeathMessage(death);
        } else {
            CustomPlayerDataHolder D0 = playerData.get(name);
            D0.untag();
            D0.incrementDeaths();

            String kp = killer.getName();
            CustomPlayerDataHolder D1 = playerData.get(kp);
            D1.untag();
            D1.incrementKills();

            String death = SECOND_COLOR + "☠ " + kp + " §7" + switch (p.getLastDamageCause().getCause()) {
                case CONTACT -> "pricked " + SECOND_COLOR + name + " §7to death";
                case ENTITY_EXPLOSION -> "exploded " + SECOND_COLOR + name;
                case BLOCK_EXPLOSION -> "imploded " + SECOND_COLOR + name;
                case FALL -> "broke " + SECOND_COLOR + name + "§7's legs";
                case ENTITY_ATTACK, ENTITY_SWEEP_ATTACK -> "sworded " + SECOND_COLOR + name;
                case PROJECTILE -> "shot " + SECOND_COLOR + name + " §7into the ass";
                case FIRE_TICK, LAVA -> "melted " + SECOND_COLOR + name + " §7away";
                case VOID -> "pushed" + SECOND_COLOR + name + " §7into the void";
                default -> "suicided";
            };
            e.setDeathMessage(death);

            Location loc = p.getLocation();
            World w = loc.getWorld();
            if (!Constants.lp.getPlayerAdapter(Player.class).getUser(killer).getPrimaryGroup().equals("default")) {
                loc.add(0, 1, 0);
                switch (Constants.RANDOM.nextInt(4)) {
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

            if (Constants.RANDOM.nextInt(100) <= 5)
                w.dropItemNaturally(loc, Utils.getHead(p, killer.getDisplayName()));
        }
    }

    @EventHandler
    private void onExplosion(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Item a)) return;
        EntityDamageEvent.DamageCause c = e.getCause();
        if (c != EntityDamageEvent.DamageCause.BLOCK_EXPLOSION &&
                c != EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)
            return;
        String name = a.getItemStack().getType().name();
        e.setCancelled(name.contains("DIAMOND") || name.contains("NETHERITE"));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        PlayerInventory inv = p.getInventory();
        if (inv.getContents().length == 0) {
            inv.addItem(sword);
            inv.addItem(pick);
            inv.setHelmet(helmet);
            inv.setChestplate(chestplate);
            inv.setLeggings(leggings);
            inv.setBoots(boots);
            inv.setItemInOffHand(gap);
            p.teleportAsync(Constants.spawn);
        }
        String name = p.getName();
        e.setJoinMessage(JOIN_PREFIX + name);

        CustomPlayerDataHolder D = playerData.get(name);
        if (D == null) {
            Constants.tpa.add(name);
            Constants.msg.add(name);

            Constants.tpa.sort(String::compareToIgnoreCase);
            Constants.msg.sort(String::compareToIgnoreCase);

            playerData.put(name, new CustomPlayerDataHolder(0, 0, 0, 0, 0, new Object2ObjectOpenHashMap<>(), ObjectArrayList.of()));
        } else {
            if (D.getTptoggle() == 0) {
                Constants.tpa.add(name);
                Constants.tpa.sort(String::compareToIgnoreCase);
            }
            if (D.getMtoggle() == 0) {
                Constants.msg.add(name);
                Constants.msg.sort(String::compareToIgnoreCase);
            }
            int mailsCount = D.getMails().size();
            if (mailsCount > 0) {
                p.playSound(p.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.f, 1.f);
                p.sendMessage("§7You have " + MAIN_COLOR + mailsCount + " §7unread mails! Use " + MAIN_COLOR + "/inbox §7to view them.");
            }
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        e.setRespawnLocation(Constants.spawn);
    }
}