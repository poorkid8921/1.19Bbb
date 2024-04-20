package main;

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import main.utils.Initializer;
import main.utils.Instances.CustomPlayerDataHolder;
import main.utils.Utils;
import main.utils.storage.DB;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.vehicle.MinecartTNT;
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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import static main.Practice.d;
import static main.utils.Gui.inInventory;
import static main.utils.Initializer.*;
import static main.utils.Utils.getRequest;
import static main.utils.Utils.setPlayerData;
import static main.utils.npcs.Utils.NPCs;
import static main.utils.npcs.Utils.moveNPCs;
import static main.utils.storage.DB.connection;

@SuppressWarnings("deprecation")
public class Events implements Listener {
    private final String JOIN_PREFIX = Utils.translateA("#31ed1c→ ");
    private final String[] allowedCmds = new String[]{"/msg", "/r", "/reply", "/tell", "/whisper", "/report"};

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (p.getWorld() != d) return;
        Location to = e.getTo();
        Location from = e.getFrom();
        if (to.getX() == from.getX() && to.getY() == from.getY() && to.getZ() == from.getZ()) return;
        if (!atSpawn.contains(p.getName())) return;
        ServerGamePacketListenerImpl connection = ((CraftPlayer) p).getHandle().connection;
        for (ServerPlayer NPC : moveNPCs) {
            Entity entity = NPC.getBukkitEntity();
            Location loc = entity.getLocation();
            Vector vector = p.getLocation().subtract(loc).toVector();
            double x = vector.getX();
            double z = vector.getZ();
            double yaw = Math.toDegrees((Math.atan2(-x, z) + 6.283185307179586D) % 6.283185307179586D);
            double pitch = Math.toDegrees(Math.atan(-vector.getY() / Math.sqrt(NumberConversions.square(x) + NumberConversions.square(z))));
            connection.send(new ClientboundRotateHeadPacket(NPC, (byte) ((yaw % 360) * 256 / 360)));
            connection.send(new ClientboundMoveEntityPacket.Rot(entity.getEntityId(), (byte) ((yaw % 360.) * 256 / 360), (byte) ((pitch % 360.) * 256 / 360), false));
        }
    }

    @EventHandler
    private void onEntitySpawn(EntitySpawnEvent event) {
        Entity ent = event.getEntity();
        if (ent instanceof EnderCrystal) {
            crystalsToBeOptimized.put(ent.getEntityId(), ent.getLocation());
            return;
        } else if (ent instanceof MinecartTNT) {
            Location loc = ent.getLocation();
            if (disallowedTntMinecartsRegionHolder.test(loc.getBlockX(), loc.getBlockZ())) {
                event.setCancelled(true);
                return;
            }
        }
        event.setCancelled(ent.getChunk().getEntities().length > 32);
    }

    @EventHandler
    private void onEntityRemoveFromWorld(EntityRemoveFromWorldEvent e) {
        if (e.getEntity() instanceof EnderCrystal ent)
            Bukkit.getScheduler().runTaskLater(p, () -> crystalsToBeOptimized.remove(ent.getEntityId()), 40L);
    }

    @EventHandler
    private void onCommand(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();
        if (playerData.get(p.getName()).isTagged()) {
            String command = e.getMessage();
            for (String k : allowedCmds) {
                if (command.startsWith(k)) return;
            }
            p.sendMessage(EXCEPTION_TAGGED);
            e.setCancelled(true);
        }
    }

    @EventHandler
    private void onChat(AsyncPlayerChatEvent e) {
        String message = e.getMessage();
        if (message.length() > 96) {
            e.setCancelled(true);
            return;
        }
        Player player = e.getPlayer();
        String name = player.getName();
        CustomPlayerDataHolder D = playerData.get(name);
        if (System.currentTimeMillis() < D.getLastChatMS()) {
            e.setCancelled(true);
            return;
        }
        D.setLastChatMS(System.currentTimeMillis() + 500L);
        e.setFormat(D.getFRank(name) + SECOND_COLOR + " » §r" + message.replace("%", "%%"));
    }

    @EventHandler
    private void onTeleport(PlayerTeleportEvent e) {
        if (e.getCause() == PlayerTeleportEvent.TeleportCause.COMMAND) {
            Player p = e.getPlayer();
            String name = p.getName();
            atSpawn.remove(name);
            inFlat.remove(name);
            inFFA.remove(p);
        }
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        String name = p.getName();
        CustomPlayerDataHolder D0 = playerData.get(name);
        if (D0.isTagged()) D0.untag();
        e.setQuitMessage(MAIN_COLOR + "← " + name);

        requests.remove(getRequest(name));
        if (D0.getLastReceived() != null) {
            CustomPlayerDataHolder D1 = playerData.get(D0.getLastReceived());
            if (D1.getLastReceived() == name) D1.setLastReceived(null);
        }
        D0.setLastReceived(null);
        D0.setLastTaggedBy(null);
        msg.remove(name);
        tpa.remove(name);

        try (PreparedStatement statement = connection.prepareStatement("UPDATE data SET c = ?, m = ?, t = ?, pz = ?, pd = ?, pk = ?, fc = ? WHERE name = '?'")) {
            statement.setInt(1, D0.getKilleffect());
            statement.setInt(2, D0.getMtoggle());
            statement.setInt(3, D0.getTptoggle());
            statement.setInt(4, D0.getMoney());
            statement.setInt(5, D0.getDeaths());
            statement.setInt(6, D0.getKills());
            statement.setBoolean(7, D0.isFastCrystals());
            statement.setString(8, name);
            statement.executeUpdate();
        } catch (SQLException ignored) {
        }
        msg.sort(String::compareToIgnoreCase);
        tpa.sort(String::compareToIgnoreCase);

        inFFA.remove(p);
        atSpawn.remove(name);
        inFlat.remove(name);
    }

    @EventHandler
    private void onInventoryClick(InventoryClickEvent e) {
        Inventory c = e.getClickedInventory();
        if (c instanceof PlayerInventory)
            return;
        Player p = (Player) e.getWhoClicked();
        String name = p.getName();
        Pair<Integer, String> inv = inInventory.getOrDefault(name, null);
        if (inv == null) return;
        int slot = e.getSlot();
        switch (inv.first()) {
            case 0 -> {
                e.setCancelled(true);
                try {
                    if (!e.getCurrentItem().getItemMeta().hasLore()) return;
                } catch (NullPointerException ignored) {
                }
                switch (slot) {
                    case 12 -> Utils.killeffect(p);
                    case 13 -> Utils.killeffect(p, 0, "ᴛʜᴇ ʟɪɢʜᴛɴɪɴɢ ᴋɪʟʟ ᴇꜰꜰᴇᴄᴛ", 150);
                    case 14 -> Utils.killeffect(p, 1, "ᴛʜᴇ ᴇxᴘʟᴏꜱɪᴏɴ ᴋɪʟʟ ᴇꜰꜰᴇᴄᴛ", 250);
                    case 15 -> Utils.killeffect(p, 2, "ᴛʜᴇ ꜰɪʀᴇᴡᴏʀᴋ ᴋɪʟʟ ᴇꜰꜰᴇᴄᴛ", 450);
                }
            } // settings: killeffect
            case 1 -> {
                e.setCancelled(true);
                try {
                    if (!e.getCurrentItem().getItemMeta().hasLore()) return;
                } catch (NullPointerException ignored) {
                }
                inInventory.remove(name);
                Utils.submitReport(p, inv.second(), switch (slot) {
                    case 10 -> "Cheating";
                    case 11 -> "Doxxing";
                    case 12 -> "Ban Evading";
                    case 13 -> "Spamming";
                    case 14 -> "Interrupting";
                    case 15 -> "Anchor Spamming";
                    default -> null;
                });
            } // report
            case 6 -> {
                e.setCancelled(true);
                switch (slot) {
                    case 12 -> {
                        CustomPlayerDataHolder D0 = playerData.get(name);
                        boolean newVal = !D0.isFastCrystals();
                        D0.setFastCrystals(newVal);
                        ItemStack item = c.getItem(slot);
                        item.setLore(ImmutableList.of("§7sᴛᴀᴛᴜs: " + (newVal ? "§aᴇɴᴀʙʟᴇᴅ" : "§cᴅɪsᴀʙʟᴇᴅ")));
                        item.setType(newVal ? Material.LIME_STAINED_GLASS : Material.RED_STAINED_GLASS);
                        c.setItem(slot, item);
                    }
                    case 13 -> {
                        CustomPlayerDataHolder D0 = playerData.get(name);
                        int newVal = D0.getMtoggle() == 0 ? 1 : 0;
                        D0.setMtoggle(newVal);
                        ItemStack item = c.getItem(slot);
                        item.setLore(ImmutableList.of("§7sᴛᴀᴛᴜs: " + (newVal == 0 ? "§aᴇɴᴀʙʟᴇᴅ" : "§cᴅɪsᴀʙʟᴇᴅ")));
                        item.setType(newVal == 0 ? Material.LIME_STAINED_GLASS : Material.RED_STAINED_GLASS);
                        c.setItem(slot, item);
                    }
                    case 14 -> {
                        CustomPlayerDataHolder D0 = playerData.get(name);
                        int newVal = D0.getTptoggle() == 0 ? 1 : 0;
                        D0.setTptoggle(newVal);
                        ItemStack item = c.getItem(slot);
                        item.setLore(ImmutableList.of("§7sᴛᴀᴛᴜs: " + (newVal == 0 ? "§aᴇɴᴀʙʟᴇᴅ" : "§cᴅɪsᴀʙʟᴇᴅ")));
                        item.setType(newVal == 0 ? Material.LIME_STAINED_GLASS : Material.RED_STAINED_GLASS);
                        c.setItem(slot, item);
                    }
                }
            } // settings
        }
    }

    @EventHandler
    private void onInventoryClose(InventoryCloseEvent e) {
        inInventory.remove(e.getPlayer().getName());
    }

    @EventHandler
    private void onDeath(PlayerDeathEvent e) {
        Player p = e.getPlayer();
        String name = p.getName();
        if (inFlat.contains(name))
            e.getDrops().clear();
        else if(inFFA.contains(p)) inFFA.remove(p);
        Location l = p.getLocation();
        CustomPlayerDataHolder D0 = playerData.get(name);
        D0.untag();
        D0.incrementDeaths();
        D0.setBack(l);
        p.sendMessage(BACK);
        Player killer = p.getKiller();
        String lastTaggedBy = D0.getLastTaggedBy();
        if (lastTaggedBy != null) {
            CustomPlayerDataHolder lastTaggedD0 = playerData.get(lastTaggedBy);
            if (lastTaggedD0.getLastTaggedBy() == lastTaggedBy) {
                lastTaggedD0.untag();
                lastTaggedD0.setLastTaggedBy(null);
            }
            D0.setLastTaggedBy(null);
        }
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
            String killerName = killer.getName();
            CustomPlayerDataHolder D1 = playerData.get(killerName);
            D1.untag();
            D1.incrementKills();
            D1.setLastTaggedBy(null);
            e.setDeathMessage(SECOND_COLOR + "☠ " + killerName + " §7" + switch (p.getLastDamageCause().getCause()) {
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
                    World world = p.getWorld();
                    l.add(0, 1, 0);
                    for (double y = 0; y < 11; y += 0.05) {
                        world.spawnParticle(Particle.TOTEM, new Location(world, (float) (l.getX() + 2 * Math.cos(y)), (float) (l.getY() + y), (float) (l.getZ() + 2 * Math.sin(y))), 2, 0, 0, 0, 1.0);
                    }
                }
                case 1 -> {
                    Firework fw = (Firework) p.getWorld().spawnEntity(l.add(0D, 1D, 0D), EntityType.FIREWORK);
                    FireworkMeta fwm = fw.getFireworkMeta();
                    fwm.setPower(2);
                    fwm.addEffect(FireworkEffect.builder().withColor(color[RANDOM.nextInt(color.length)]).withColor(color[RANDOM.nextInt(color.length)]).with(FireworkEffect.Type.BALL_LARGE).flicker(true).build());
                    fw.setFireworkMeta(fwm);
                }
                case 2 -> p.getWorld().strikeLightningEffect(l.add(0D, 1D, 0D));
            }
        }
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        String name = p.getName();
        e.setJoinMessage(JOIN_PREFIX + name);
        p.teleport(spawn);
        ServerGamePacketListenerImpl connection = ((CraftPlayer) p).getHandle().connection;
        main.utils.holos.Utils.showForPlayerTickable(connection);
        Bukkit.getScheduler().runTaskLater(Initializer.p, () -> {
            main.utils.npcs.Utils.showForPlayer(connection);
        }, 3L);
        atSpawn.add(name);
        CustomPlayerDataHolder D = playerData.get(name);
        if (D == null) {
            tpa.add(name);
            msg.add(name);
            msg.sort(String::compareToIgnoreCase);
            tpa.sort(String::compareToIgnoreCase);
            setPlayerData(name);
        } else {
            int rank = DB.setUsefulData(name, D);
            ServerPlayer craftPlayer = ((CraftPlayer) p).getHandle();
            craftPlayer.listName = CraftChatMessage.fromString(D.getFRank(name))[0];
            for (ServerPlayer player : DedicatedServer.getServer().getPlayerList().players) {
                player.connection.send(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_DISPLAY_NAME, craftPlayer));
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
        String uUID = p.getUniqueId().toString();
        for (int i = 1; i <= 3; ++i) {
            String key = uUID + "-kit" + i;
            if (!Practice.kitMap.containsKey(key)) {
                Object2ObjectOpenHashMap<String, Object> newMap = new Object2ObjectOpenHashMap<>();
                newMap.put("player", name);
                newMap.put("UUID", uUID);
                Practice.kitMap.put(uUID + "-kit" + i, newMap);
            }
        }
        Bukkit.getScheduler().runTaskLater(Initializer.p, () -> p.sendMessage(MOTD), 5L);
    }

    @EventHandler
    private void onRespawn(PlayerRespawnEvent e) {
        Player p = e.getPlayer();
        String name = p.getName();
        atSpawn.add(name);
        e.setRespawnLocation(spawn);
        ServerGamePacketListenerImpl connection = ((CraftPlayer) p).getHandle().connection;
        main.utils.holos.Utils.showForPlayerTickable(connection);
        Bukkit.getScheduler().runTaskLater(Initializer.p, () -> {
            main.utils.npcs.Utils.showForPlayer(connection);
            for (main.utils.npcs.Utils.LoopableNPCHolder NPC : NPCs) {
                Entity entity = NPC.NPC().getBukkitEntity();
                Location loc = entity.getLocation();
                Vector vector = spawn.clone().subtract(loc).toVector();
                double x = vector.getX();
                double z = vector.getZ();
                double yaw = Math.toDegrees((Math.atan2(-x, z) + 6.283185307179586D) % 6.283185307179586D);
                double pitch = Math.toDegrees(Math.atan(-vector.getY() / Math.sqrt(NumberConversions.square(x) + NumberConversions.square(z))));
                connection.send(new ClientboundRotateHeadPacket(NPC.NPC(), (byte) ((yaw % 360) * 256 / 360)));
                connection.send(new ClientboundMoveEntityPacket.Rot(entity.getEntityId(), (byte) ((yaw % 360.) * 256 / 360), (byte) ((pitch % 360.) * 256 / 360), false));
            }
        }, 2L);
    }
}