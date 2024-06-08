package main;

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import main.utils.Initializer;
import main.utils.Instances.CustomPlayerDataHolder;
import main.utils.Utils;
import main.utils.modules.storage.DB;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.vehicle.MinecartTNT;
import net.minecraft.world.phys.Vec3;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_19_R3.util.CraftChatMessage;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import static main.Practice.d;
import static main.utils.Gui.inInventory;
import static main.utils.Initializer.*;
import static main.utils.Utils.*;
import static main.utils.modules.storage.DB.connection;

@SuppressWarnings("deprecation")
public class Events implements Listener {
    private final String JOIN_PREFIX = Utils.translateA("#31ed1c→ ");
    private final String[] allowedCmds = new String[]{"/msg", "/r", "/reply", "/tell", "/whisper", "/report"};

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if (player.getWorld() != d) return;
        final Location to = e.getTo();
        final Location from = e.getFrom();
        if (to.getX() == from.getX() && to.getY() == from.getY() && to.getZ() == from.getZ()) return;
        if (!atSpawn.contains(player.getName())) return;
        rotateNPCs(to, ((CraftPlayer) player).getHandle().connection);
    }

    @EventHandler
    private void onEntitySpawn(EntitySpawnEvent event) {
        final Entity entity = event.getEntity();
        if (entity instanceof EnderCrystal) {
            crystalsToBeOptimized.put(entity.getEntityId(), entity.getLocation());
            return;
        } else if (entity instanceof MinecartTNT) {
            final Location location = entity.getLocation();
            if (disallowedTntMinecartsRegionHolder.test(location.getBlockX(), location.getBlockZ())) {
                event.setCancelled(true);
                return;
            }
        }
        event.setCancelled(entity.getChunk().getEntities().length > 32);
    }

    @EventHandler
    private void onEntityRemoveFromWorld(EntityRemoveFromWorldEvent e) {
        if (e.getEntity() instanceof EnderCrystal entity)
            Bukkit.getScheduler().runTaskLater(p, () -> crystalsToBeOptimized.remove(entity.getEntityId()), 40L);
    }

    @EventHandler
    private void onCommand(PlayerCommandPreprocessEvent e) {
        final Player player = e.getPlayer();
        if (playerData.get(player.getName()).isTagged()) {
            final String command = e.getMessage();
            for (final String k : allowedCmds)
                if (command.startsWith(k)) return;
            player.sendMessage(EXCEPTION_TAGGED);
            e.setCancelled(true);
        }
    }

    @EventHandler
    private void onChat(AsyncPlayerChatEvent e) {
        final String message = e.getMessage();
        if (message.length() > 96) {
            e.setCancelled(true);
            return;
        }
        final Player player = e.getPlayer();
        final String name = player.getName();
        final CustomPlayerDataHolder D = playerData.get(name);
        if (System.currentTimeMillis() < D.getLastChatMS()) {
            e.setCancelled(true);
            return;
        }
        if (message.equalsIgnoreCase("!rs")) {
            player.setStatistic(Statistic.DEATHS, 0);
            player.setStatistic(Statistic.PLAYER_KILLS, 0);
            final String msg = SECOND_COLOR + name + " §7has reset their stats!";
            for (final Player k : Bukkit.getOnlinePlayers())
                k.sendMessage(msg);
            D.setLastChatMS(System.currentTimeMillis() + 1000L);
            e.setCancelled(true);
            return;
        }
        D.setLastChatMS(System.currentTimeMillis() + 500L);
        e.setFormat(D.getFRank(name) + SECOND_COLOR + " » §r" + message.replace("%", "%%"));
    }

    @EventHandler
    private void onTeleport(PlayerTeleportEvent e) {
        if (e.getCause() == PlayerTeleportEvent.TeleportCause.COMMAND) {
            final Player player = e.getPlayer();
            final String name = player.getName();
            atSpawn.remove(name);
            inFlat.remove(name);
            inFFA.remove(player);
        }
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent e) {
        final Player player = e.getPlayer();
        final String name = player.getName();
        final CustomPlayerDataHolder D0 = playerData.get(name);
        if (D0.isTagged()) D0.untag();
        e.setQuitMessage(MAIN_COLOR + "← " + name);
        requests.remove(getRequest(name));
        if (D0.getLastReceived() != null) {
            final CustomPlayerDataHolder D1 = playerData.get(D0.getLastReceived());
            if (D1.getLastReceived() == name) D1.setLastReceived(null);
        }
        D0.setLastReceived(null);
        D0.setLastTaggedBy(null);
        msg.remove(name);
        tpa.remove(name);
        switch (D0.getRank()) {
            case 1 -> cattoLovesTeam.removeEntity(player);
            case 2 -> cattoHatesTeam.removeEntity(player);
            case 3 -> gayTeam.removeEntity(player);
            case 4 -> vipTeam.removeEntity(player);
            case 5 -> boosterTeam.removeEntity(player);
            case 6 -> mediaTeam.removeEntity(player);
            case 7 -> trialHelperTeam.removeEntity(player);
            case 8 -> helperTeam.removeEntity(player);
            case 9 -> jrmodTeam.removeEntity(player);
            case 10 -> modTeam.removeEntity(player);
            case 11 -> adminTeam.removeEntity(player);
            case 12 -> managerTeam.removeEntity(player);
            case 13 -> ownerTeam.removeEntity(player);
        }

        try (final PreparedStatement statement = connection.prepareStatement("UPDATE data SET c = ?, m = ?, t = ?, pz = ?, pd = ?, pk = ?, pt = ?, fc = ? WHERE name = '?'")) {
            statement.setInt(1, D0.getKilleffect());
            statement.setInt(2, D0.getMtoggle());
            statement.setInt(3, D0.getTptoggle());
            statement.setInt(4, D0.getMoney());
            statement.setInt(5, D0.getDeaths());
            statement.setInt(6, D0.getKills());
            statement.setInt(7, player.getStatistic(Statistic.PLAY_ONE_MINUTE));
            statement.setBoolean(8, D0.isFastCrystals());
            statement.setString(9, name);
            statement.executeUpdate();
        } catch (SQLException ignored) {
        }
        msg.sort(String::compareToIgnoreCase);
        tpa.sort(String::compareToIgnoreCase);

        inFFA.remove(player);
        atSpawn.remove(name);
        inFlat.remove(name);
    }

    @EventHandler
    private void onInventoryClick(InventoryClickEvent e) {
        if (e.getClickedInventory() instanceof PlayerInventory) return;
        final Player player = (Player) e.getWhoClicked();
        final String name = player.getName();
        final Pair<Integer, String> inv = inInventory.getOrDefault(name, null);
        if (inv == null) {
            // InvMoveA
            if (e.getCurrentItem().getType() == Material.TOTEM_OF_UNDYING) {
                Vector vector = player.getVelocity();
                double Vx = vector.getX();
                double Vz = vector.getZ();
                Bukkit.getLogger().warning(name + " | " + Vx + " | " + Vz);
            }
            return;
        }
        final int slot = e.getSlot();
        switch (inv.first()) {
            case 0 -> {
                e.setCancelled(true);
                try {
                    if (!e.getCurrentItem().getItemMeta().hasLore()) return;
                } catch (NullPointerException ignored) {
                }
                player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
                switch (slot) {
                    case 12 -> Utils.killeffect(player);
                    case 13 -> Utils.killeffect(player, 0, "ᴛʜᴇ ʟɪɢʜᴛɴɪɴɢ ᴋɪʟʟ ᴇꜰꜰᴇᴄᴛ", 150);
                    case 14 -> Utils.killeffect(player, 1, "ᴛʜᴇ ᴇxᴘʟᴏꜱɪᴏɴ ᴋɪʟʟ ᴇꜰꜰᴇᴄᴛ", 250);
                    case 15 -> Utils.killeffect(player, 2, "ᴛʜᴇ ꜰɪʀᴇᴡᴏʀᴋ ᴋɪʟʟ ᴇꜰꜰᴇᴄᴛ", 450);
                }
            } // settings: killeffect
            case 1 -> {
                e.setCancelled(true);
                try {
                    if (!e.getCurrentItem().getItemMeta().hasLore()) return;
                } catch (NullPointerException ignored) {
                }
                player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
                Utils.submitReport(player, inv.second(), switch (slot) {
                    case 10 -> "Cheating";
                    case 11 -> "Doxxing";
                    case 12 -> "Ban Evading";
                    case 13 -> "Spamming";
                    case 14 -> "Interrupting";
                    case 15 -> "Anchor Spamming";
                    default -> null;
                });
            } // report
        }
    }

    @EventHandler
    private void onInventoryClose(InventoryCloseEvent e) {
        inInventory.remove(e.getPlayer().getName());
    }

    @EventHandler
    private void onDeath(PlayerDeathEvent e) {
        final Player player = e.getPlayer();
        final String name = player.getName();
        if (inFlat.contains(name)) e.getDrops().clear();
        else inFFA.remove(player);
        inFlat.remove(name);
        final Location location = player.getLocation();
        final CustomPlayerDataHolder D0 = playerData.get(name);
        D0.untag();
        D0.incrementDeaths();
        D0.setBack(location);
        player.sendMessage(BACK);
        final Player killer = player.getKiller();
        final String lastTaggedBy = D0.getLastTaggedBy();
        if (lastTaggedBy != null) {
            final CustomPlayerDataHolder lastTaggedD0 = playerData.get(lastTaggedBy);
            if (lastTaggedD0.getLastTaggedBy() == lastTaggedBy) {
                lastTaggedD0.untag();
                lastTaggedD0.setLastTaggedBy(null);
            }
            D0.setLastTaggedBy(null);
        }
        if (killer == null || killer == player) {
            final String death = SECOND_COLOR + "☠ " + name + " §7" + switch (player.getLastDamageCause().getCause()) {
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
            final String killerName = killer.getName();
            final CustomPlayerDataHolder D1 = playerData.get(killerName);
            D1.untag();
            D1.incrementKills();
            D1.setLastTaggedBy(null);
            e.setDeathMessage(SECOND_COLOR + "☠ " + killerName + " §7" + switch (player.getLastDamageCause().getCause()) {
                case CONTACT -> "pricked " + SECOND_COLOR + name + " §7to death";
                case ENTITY_EXPLOSION -> "crystalled " + SECOND_COLOR + name;
                case BLOCK_EXPLOSION -> "imploded " + SECOND_COLOR + name;
                case FALL -> "broke " + SECOND_COLOR + name + "§7's legs";
                case ENTITY_ATTACK, ENTITY_SWEEP_ATTACK -> "sworded " + SECOND_COLOR + name;
                case PROJECTILE -> "shot " + SECOND_COLOR + name + " §7into the ass";
                case FIRE_TICK, LAVA -> "melted " + SECOND_COLOR + name + " §7away";
                case VOID -> "pushed " + SECOND_COLOR + name + " §7into the void";
                default -> "suicided";
            });
            D1.incrementMoney(50);

            switch (D1.getKilleffect()) {
                case 0 -> {
                    final World world = location.getWorld();
                    location.add(0, 1, 0);
                    final double x = location.getX();
                    final double y1 = location.getY();
                    final double z = location.getZ();
                    for (double y = 0; y < 11; y += 0.05) {
                        world.spawnParticle(Particle.TOTEM, new Location(world, (float) (x + 2 * Math.cos(y)), (float) (y1 + y), (float) (z + 2 * Math.sin(y))), 2, 0, 0, 0, 1.0);
                    }
                }
                case 1 -> {
                    Firework fw = (Firework) location.getWorld().spawnEntity(location.add(0D, 1D, 0D), EntityType.FIREWORK);
                    FireworkMeta fwm = fw.getFireworkMeta();
                    fwm.setPower(2);
                    fwm.addEffect(FireworkEffect.builder().withColor(color[RANDOM.nextInt(color.length)]).withColor(color[RANDOM.nextInt(color.length)]).with(FireworkEffect.Type.BALL_LARGE).flicker(true).build());
                    fw.setFireworkMeta(fwm);
                }
                case 2 -> location.getWorld().strikeLightningEffect(location.add(0D, 1D, 0D));
            }
        }
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent e) {
        final Player player = e.getPlayer();
        final String name = player.getName();
        e.setJoinMessage(JOIN_PREFIX + name);
        player.teleport(spawn);
        player.getInventory().clear();
        final ServerGamePacketListenerImpl connection = ((CraftPlayer) player).getHandle().connection;
        main.utils.modules.holos.Utils.showForPlayerTickable(connection);
        main.utils.modules.npcs.Utils.showForPlayerFirstTime(connection);
        rotateNPCs(spawn, connection);
        atSpawn.add(name);
        final CustomPlayerDataHolder D = playerData.get(name);
        if (D == null) {
            tpa.add(name);
            msg.add(name);
            msg.sort(String::compareToIgnoreCase);
            tpa.sort(String::compareToIgnoreCase);
            playerData.put(name, getPlayerData(player, name));
        } else {
            DB.setUsefulData(player, name, D);
            final ServerPlayer craftPlayer = ((CraftPlayer) player).getHandle();
            craftPlayer.listName = CraftChatMessage.fromString(D.getFRank(name))[0];
            for (final ServerPlayer k : DedicatedServer.getServer().getPlayerList().players) {
                k.connection.send(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_DISPLAY_NAME, craftPlayer));
            }
            D.setLastTimeKitWasUsed(System.currentTimeMillis());
            if (D.getTptoggle() == 0) {
                tpa.add(name);
                tpa.sort(String::compareToIgnoreCase);
            }
            if (D.getMtoggle() == 0) {
                msg.add(name);
                msg.sort(String::compareToIgnoreCase);
            }
        }
        final String uUID = player.getUniqueId().toString();
        for (int i = 1; i <= 3; ++i) {
            final String key = uUID + "-kit" + i;
            if (!Practice.kitMap.containsKey(key)) {
                final Object2ObjectOpenHashMap<String, Object> newMap = new Object2ObjectOpenHashMap<>();
                newMap.put("player", name);
                newMap.put("UUID", uUID);
                Practice.kitMap.put(uUID + "-kit" + i, newMap);
            }
        }
        Bukkit.getScheduler().runTaskLater(Initializer.p, () -> player.sendMessage(main.utils.modules.broadcast.Utils.MOTD), 5L);
    }

    @EventHandler
    private void onRespawn(PlayerRespawnEvent e) {
        final Player player = e.getPlayer();
        atSpawn.add(player.getName());
        e.setRespawnLocation(spawn);
        final ServerGamePacketListenerImpl connection = ((CraftPlayer) player).getHandle().connection;
        showCosmetics(connection);
        rotateNPCs(spawn, connection);
    }
}