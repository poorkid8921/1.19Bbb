package main.utils;

import main.Practice;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static main.expansions.duels.KitOverrider.*;
import static main.utils.Languages.*;

public class DuelUtils {
    public static NamespacedKey spectateHead = new NamespacedKey(Initializer.p, "against");
    static SimpleDateFormat MM_HH = new SimpleDateFormat("mm:ss", Locale.ENGLISH);
    static TextComponent hi = new TextComponent("§7ᴄʟɪᴄᴋ ᴛᴏ ꜱʜᴏᴡ ᴛʜᴇ ᴅᴜᴇʟ ʀᴇꜱᴜʟᴛꜱ");

    public static int duelsavailable(int gm) {
        return Initializer.duel.stream().filter(r -> r.getType() == gm && r.getRounds() > 0).toList().size();
    }

    public static String formattedtype(int i) {
        return switch (i) {
            case 0 -> "Field";
            case 1 -> "Flat";
            case 2 -> "Tank";
            default -> null;
        };
    }

    public static void resume(Player pl, Player p, boolean i, int r, int b, long o, long n, String t, boolean rw, String f, String ff) {
        String rd = pl.getName();
        String ad = p.getName();

        hi.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/event " + rw + " " + r + " " + b + " " + MM_HH.format(new Date(n - o)) + t + pl.getName() + " " + Math.round(pl.getHealth() / 2) + " " + pl.getStatistic(Statistic.PLAYER_KILLS) + " " + pl.getStatistic(Statistic.DEATHS) + " " + Practice.config.getInt("r." + pl.getName() + ".wins") + " " + Practice.config.getInt("r." + pl.getName() + ".losses")));
        Initializer.valid.add(rd);
        pl.sendMessage(TELEPORTING_BACK);
        pl.sendMessage(hi);
        pl.sendTitle(ff, null, 1, 100, 1);
        pl.getInventory().clear();
        if (i) {
            Initializer.valid.add(ad);
            p.sendMessage(TELEPORTING_BACK);
            p.sendMessage(hi);
            p.sendTitle(f, null, 1, 100, 1);
            p.getInventory().clear();
        }

        Bukkit.broadcastMessage(SECOND_COLOR + "⚔ " + rd + " §7won in a duel against " + SECOND_COLOR + ad);
        Practice.config.set("r." + rd + ".wins", Practice.config.getInt("r." + rd + ".wins") + 1);
        Practice.config.set("r." + ad + ".losses", Practice.config.getInt("r." + ad + ".losses") + 1);

        Initializer.p.saveCustomConfig();
    }

    public static void resume(Player pl, Player p, boolean i, int r, int b, long o, long n, String t, boolean rw, String f, String ff, PlayerDeathEvent e) {
        String rd = pl.getName();
        String ad = p.getName();

        hi.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/event " + rw + " " + r + " " + b + " " + MM_HH.format(new Date(n - o)) + t + pl.getName() + " " + Math.round(pl.getHealth() / 2) + " " + pl.getStatistic(Statistic.PLAYER_KILLS) + " " + pl.getStatistic(Statistic.DEATHS) + " " + Practice.config.getInt("r." + pl.getName() + ".wins") + " " + Practice.config.getInt("r." + pl.getName() + ".losses")));
        Initializer.valid.add(rd);
        pl.sendMessage(TELEPORTING_BACK);
        pl.sendMessage(hi);
        pl.sendTitle(ff, null, 1, 100, 1);
        pl.getInventory().clear();
        if (i) {
            Initializer.valid.add(ad);
            p.sendMessage(TELEPORTING_BACK);
            p.sendMessage(hi);
            p.sendTitle(f, null, 1, 100, 1);
            p.getInventory().clear();
        }

        e.setDeathMessage(SECOND_COLOR + "⚔ " + rd + " §7won in a duel against " + SECOND_COLOR + ad);
        Practice.config.set("r." + rd + ".wins", Practice.config.getInt("r." + rd + ".wins") + 1);
        Practice.config.set("r." + ad + ".losses", Practice.config.getInt("r." + ad + ".losses") + 1);

        Initializer.p.saveCustomConfig();
    }

    public static void start(Player user, Player recipient, int type, int round, int maxi, int arena) {
        Location loc1 = null;
        Location loc2 = null;
        user.getInventory().clear();
        recipient.getInventory().clear();

        World w = Bukkit.getWorld("world");
        switch (type) {
            case 0 -> {
                Duels_Kit_Field(user);
                Duels_Kit_Field(recipient);
                loc1 = switch (arena) {
                    case 1 -> new Location(w, -391.5, 149, -303.5);
                    case 2 -> new Location(w, -391.5, 149, -303.5);
                    case 3 -> new Location(w, -391.5, 149, -303.5);
                    case 4 -> new Location(w, -391.5, 149, -303.5);
                    case 5 -> new Location(w, -391.5, 149, -303.5);
                    case 6 -> new Location(w, -391.5, 149, -303.5);
                    case 7 -> new Location(w, -391.5, 149, -303.5);
                    case 8 -> new Location(w, -391.5, 149, -303.5);
                    case 9 -> new Location(w, -391.5, 149, -303.5);
                    case 10 -> new Location(w, -391.5, 149, -303.5);
                    case 11 -> new Location(w, -391.5, 149, -303.5);
                    case 12 -> new Location(w, -391.5, 149, -303.5);
                    case 13 -> new Location(w, -391.5, 149, -303.5);
                    case 14 -> new Location(w, -391.5, 149, -303.5);
                    case 15 -> new Location(w, -391.5, 149, -303.5);
                    case 16 -> new Location(w, -391.5, 149, -303.5);
                    case 17 -> new Location(w, -391.5, 149, -303.5);
                    case 18 -> new Location(w, -391.5, 149, -303.5);
                    case 19 -> new Location(w, -391.5, 149, -303.5);
                    case 20 -> new Location(w, -391.5, 149, -303.5);
                    case 21 -> new Location(w, -391.5, 149, -303.5);
                    case 22 -> new Location(w, -391.5, 149, -303.5);
                    case 23 -> new Location(w, -391.5, 149, -303.5);
                    case 24 -> new Location(w, -391.5, 149, -303.5);
                    case 25 -> new Location(w, -391.5, 149, -303.5);
                    case 26 -> new Location(w, -391.5, 149, -303.5);
                    case 27 -> new Location(w, -391.5, 149, -303.5);
                    case 28 -> new Location(w, -391.5, 149, -303.5);
                    case 29 -> new Location(w, -391.5, 149, -303.5);
                    case 30 -> new Location(w, -391.5, 149, -303.5);
                    case 31 -> new Location(w, -391.5, 149, -303.5);
                    case 32 -> new Location(w, -391.5, 149, -303.5);
                    default -> null;
                };
                loc2 = switch (arena) {
                    case 1 -> new Location(w, -391.5, 149, -303.5);
                    case 2 -> new Location(w, -391.5, 149, -303.5);
                    case 3 -> new Location(w, -391.5, 149, -303.5);
                    case 4 -> new Location(w, -391.5, 149, -303.5);
                    case 5 -> new Location(w, -391.5, 149, -303.5);
                    case 6 -> new Location(w, -391.5, 149, -303.5);
                    case 7 -> new Location(w, -391.5, 149, -303.5);
                    case 8 -> new Location(w, -391.5, 149, -303.5);
                    case 9 -> new Location(w, -391.5, 149, -303.5);
                    case 10 -> new Location(w, -391.5, 149, -303.5);
                    case 11 -> new Location(w, -391.5, 149, -303.5);
                    case 12 -> new Location(w, -391.5, 149, -303.5);
                    case 13 -> new Location(w, -391.5, 149, -303.5);
                    case 14 -> new Location(w, -391.5, 149, -303.5);
                    case 15 -> new Location(w, -391.5, 149, -303.5);
                    case 16 -> new Location(w, -391.5, 149, -303.5);
                    case 17 -> new Location(w, -391.5, 149, -303.5);
                    case 18 -> new Location(w, -391.5, 149, -303.5);
                    case 19 -> new Location(w, -391.5, 149, -303.5);
                    case 20 -> new Location(w, -391.5, 149, -303.5);
                    case 21 -> new Location(w, -391.5, 149, -303.5);
                    case 22 -> new Location(w, -391.5, 149, -303.5);
                    case 23 -> new Location(w, -391.5, 149, -303.5);
                    case 24 -> new Location(w, -391.5, 149, -303.5);
                    case 25 -> new Location(w, -391.5, 149, -303.5);
                    case 26 -> new Location(w, -391.5, 149, -303.5);
                    case 27 -> new Location(w, -391.5, 149, -303.5);
                    case 28 -> new Location(w, -391.5, 149, -303.5);
                    case 29 -> new Location(w, -391.5, 149, -303.5);
                    case 30 -> new Location(w, -391.5, 149, -303.5);
                    case 31 -> new Location(w, -391.5, 149, -303.5);
                    case 32 -> new Location(w, -391.5, 149, -303.5);
                    default -> null;
                };
            }
            case 1 -> {
                Duels_Kit_Flat(user);
                Duels_Kit_Flat(recipient);
                loc1 = switch (arena) {
                    case 1 -> new Location(w, -391.5, 149, -303.5);
                    case 2 -> new Location(w, -391.5, 149, -303.5);
                    case 3 -> new Location(w, -391.5, 149, -303.5);
                    case 4 -> new Location(w, -391.5, 149, -303.5);
                    case 5 -> new Location(w, -391.5, 149, -303.5);
                    case 6 -> new Location(w, -391.5, 149, -303.5);
                    case 7 -> new Location(w, -391.5, 149, -303.5);
                    case 8 -> new Location(w, -391.5, 149, -303.5);
                    case 9 -> new Location(w, -391.5, 149, -303.5);
                    case 10 -> new Location(w, -391.5, 149, -303.5);
                    case 11 -> new Location(w, -391.5, 149, -303.5);
                    case 12 -> new Location(w, -391.5, 149, -303.5);
                    case 13 -> new Location(w, -391.5, 149, -303.5);
                    case 14 -> new Location(w, -391.5, 149, -303.5);
                    case 15 -> new Location(w, -391.5, 149, -303.5);
                    case 16 -> new Location(w, -391.5, 149, -303.5);
                    case 17 -> new Location(w, -391.5, 149, -303.5);
                    case 18 -> new Location(w, -391.5, 149, -303.5);
                    case 19 -> new Location(w, -391.5, 149, -303.5);
                    case 20 -> new Location(w, -391.5, 149, -303.5);
                    case 21 -> new Location(w, -391.5, 149, -303.5);
                    case 22 -> new Location(w, -391.5, 149, -303.5);
                    case 23 -> new Location(w, -391.5, 149, -303.5);
                    case 24 -> new Location(w, -391.5, 149, -303.5);
                    case 25 -> new Location(w, -391.5, 149, -303.5);
                    case 26 -> new Location(w, -391.5, 149, -303.5);
                    case 27 -> new Location(w, -391.5, 149, -303.5);
                    case 28 -> new Location(w, -391.5, 149, -303.5);
                    case 29 -> new Location(w, -391.5, 149, -303.5);
                    case 30 -> new Location(w, -391.5, 149, -303.5);
                    case 31 -> new Location(w, -391.5, 149, -303.5);
                    case 32 -> new Location(w, -391.5, 149, -303.5);
                    default -> null;
                };
                loc2 = switch (arena) {
                    case 1 -> new Location(w, -391.5, 149, -303.5);
                    case 2 -> new Location(w, -391.5, 149, -303.5);
                    case 3 -> new Location(w, -391.5, 149, -303.5);
                    case 4 -> new Location(w, -391.5, 149, -303.5);
                    case 5 -> new Location(w, -391.5, 149, -303.5);
                    case 6 -> new Location(w, -391.5, 149, -303.5);
                    case 7 -> new Location(w, -391.5, 149, -303.5);
                    case 8 -> new Location(w, -391.5, 149, -303.5);
                    case 9 -> new Location(w, -391.5, 149, -303.5);
                    case 10 -> new Location(w, -391.5, 149, -303.5);
                    case 11 -> new Location(w, -391.5, 149, -303.5);
                    case 12 -> new Location(w, -391.5, 149, -303.5);
                    case 13 -> new Location(w, -391.5, 149, -303.5);
                    case 14 -> new Location(w, -391.5, 149, -303.5);
                    case 15 -> new Location(w, -391.5, 149, -303.5);
                    case 16 -> new Location(w, -391.5, 149, -303.5);
                    case 17 -> new Location(w, -391.5, 149, -303.5);
                    case 18 -> new Location(w, -391.5, 149, -303.5);
                    case 19 -> new Location(w, -391.5, 149, -303.5);
                    case 20 -> new Location(w, -391.5, 149, -303.5);
                    case 21 -> new Location(w, -391.5, 149, -303.5);
                    case 22 -> new Location(w, -391.5, 149, -303.5);
                    case 23 -> new Location(w, -391.5, 149, -303.5);
                    case 24 -> new Location(w, -391.5, 149, -303.5);
                    case 25 -> new Location(w, -391.5, 149, -303.5);
                    case 26 -> new Location(w, -391.5, 149, -303.5);
                    case 27 -> new Location(w, -391.5, 149, -303.5);
                    case 28 -> new Location(w, -391.5, 149, -303.5);
                    case 29 -> new Location(w, -391.5, 149, -303.5);
                    case 30 -> new Location(w, -391.5, 149, -303.5);
                    case 31 -> new Location(w, -391.5, 149, -303.5);
                    case 32 -> new Location(w, -391.5, 149, -303.5);
                    default -> null;
                };
            }
            case 2 -> {
                Duels_Kit_Tank(user);
                Duels_Kit_Tank(recipient);
                loc1 = switch (arena) {
                    case 1 -> new Location(w, -391.5, 149, -303.5);
                    case 2 -> new Location(w, -391.5, 149, -303.5);
                    case 3 -> new Location(w, -391.5, 149, -303.5);
                    case 4 -> new Location(w, -391.5, 149, -303.5);
                    case 5 -> new Location(w, -391.5, 149, -303.5);
                    case 6 -> new Location(w, -391.5, 149, -303.5);
                    case 7 -> new Location(w, -391.5, 149, -303.5);
                    case 8 -> new Location(w, -391.5, 149, -303.5);
                    case 9 -> new Location(w, -391.5, 149, -303.5);
                    case 10 -> new Location(w, -391.5, 149, -303.5);
                    case 11 -> new Location(w, -391.5, 149, -303.5);
                    case 12 -> new Location(w, -391.5, 149, -303.5);
                    case 13 -> new Location(w, -391.5, 149, -303.5);
                    case 14 -> new Location(w, -391.5, 149, -303.5);
                    case 15 -> new Location(w, -391.5, 149, -303.5);
                    case 16 -> new Location(w, -391.5, 149, -303.5);
                    case 17 -> new Location(w, -391.5, 149, -303.5);
                    case 18 -> new Location(w, -391.5, 149, -303.5);
                    case 19 -> new Location(w, -391.5, 149, -303.5);
                    case 20 -> new Location(w, -391.5, 149, -303.5);
                    case 21 -> new Location(w, -391.5, 149, -303.5);
                    case 22 -> new Location(w, -391.5, 149, -303.5);
                    case 23 -> new Location(w, -391.5, 149, -303.5);
                    case 24 -> new Location(w, -391.5, 149, -303.5);
                    case 25 -> new Location(w, -391.5, 149, -303.5);
                    case 26 -> new Location(w, -391.5, 149, -303.5);
                    case 27 -> new Location(w, -391.5, 149, -303.5);
                    case 28 -> new Location(w, -391.5, 149, -303.5);
                    case 29 -> new Location(w, -391.5, 149, -303.5);
                    case 30 -> new Location(w, -391.5, 149, -303.5);
                    case 31 -> new Location(w, -391.5, 149, -303.5);
                    case 32 -> new Location(w, -391.5, 149, -303.5);
                    default -> null;
                };
                loc2 = switch (arena) {
                    case 1 -> new Location(w, -391.5, 149, -303.5);
                    case 2 -> new Location(w, -391.5, 149, -303.5);
                    case 3 -> new Location(w, -391.5, 149, -303.5);
                    case 4 -> new Location(w, -391.5, 149, -303.5);
                    case 5 -> new Location(w, -391.5, 149, -303.5);
                    case 6 -> new Location(w, -391.5, 149, -303.5);
                    case 7 -> new Location(w, -391.5, 149, -303.5);
                    case 8 -> new Location(w, -391.5, 149, -303.5);
                    case 9 -> new Location(w, -391.5, 149, -303.5);
                    case 10 -> new Location(w, -391.5, 149, -303.5);
                    case 11 -> new Location(w, -391.5, 149, -303.5);
                    case 12 -> new Location(w, -391.5, 149, -303.5);
                    case 13 -> new Location(w, -391.5, 149, -303.5);
                    case 14 -> new Location(w, -391.5, 149, -303.5);
                    case 15 -> new Location(w, -391.5, 149, -303.5);
                    case 16 -> new Location(w, -391.5, 149, -303.5);
                    case 17 -> new Location(w, -391.5, 149, -303.5);
                    case 18 -> new Location(w, -391.5, 149, -303.5);
                    case 19 -> new Location(w, -391.5, 149, -303.5);
                    case 20 -> new Location(w, -391.5, 149, -303.5);
                    case 21 -> new Location(w, -391.5, 149, -303.5);
                    case 22 -> new Location(w, -391.5, 149, -303.5);
                    case 23 -> new Location(w, -391.5, 149, -303.5);
                    case 24 -> new Location(w, -391.5, 149, -303.5);
                    case 25 -> new Location(w, -391.5, 149, -303.5);
                    case 26 -> new Location(w, -391.5, 149, -303.5);
                    case 27 -> new Location(w, -391.5, 149, -303.5);
                    case 28 -> new Location(w, -391.5, 149, -303.5);
                    case 29 -> new Location(w, -391.5, 149, -303.5);
                    case 30 -> new Location(w, -391.5, 149, -303.5);
                    case 31 -> new Location(w, -391.5, 149, -303.5);
                    case 32 -> new Location(w, -391.5, 149, -303.5);
                    default -> null;
                };
            }
        }

        loc1.setYaw(-45.0F);
        loc2.setYaw(45.0F);

        user.teleportAsync(loc1).thenAccept(r -> {
            user.setNoDamageTicks(100);
            user.setFoodLevel(20);
            user.setHealth(20);
        });

        recipient.teleportAsync(loc2).thenAccept(r -> {
            recipient.setNoDamageTicks(100);
            recipient.setFoodLevel(20);
            recipient.setHealth(20);
        });

        String a = "§7" + (maxi > 1 ? "Round " + round : "Duel");
        Location finalLoc1 = loc1;
        Location finalLoc2 = loc2;
        new BukkitRunnable() {
            int i = 5;

            @Override
            public void run() {
                user.playSound(user.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.f, 1.f);
                recipient.playSound(recipient.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.f, 1.f);

                if (i == 0) {
                    user.sendMessage(a + startED);
                    recipient.sendMessage(a + startED);

                    user.sendTitle(MAIN_COLOR + "Fight!", "", 1, 30, 1);
                    recipient.sendTitle(MAIN_COLOR + "Fight!", "", 1, 30, 1);

                    user.teleportAsync(finalLoc1).thenAccept(r -> w.setType(finalLoc1.subtract(0, 1, 0), Material.GOLD_BLOCK));
                    recipient.teleportAsync(finalLoc2).thenAccept(r -> w.setType(finalLoc2.subtract(0, 1, 0), Material.GOLD_BLOCK));

                    this.cancel();
                    return;
                }

                String s = MAIN_COLOR + i;
                user.sendTitle(s, null, 1, 20, 1);
                recipient.sendTitle(s, null, 1, 20, 1);
                i--;
                String sec = i == 0 ? MAIN_COLOR + "a second" : s + " seconds";
                String msg = round > 1 ? "§7Round " + MAIN_COLOR + round + " / " + maxi + " §7starting in " + sec :
                        "§7" + (maxi > 1 ? "Round" : "Duel") + " starting in " + sec;
                user.sendMessage(msg);
                recipient.sendMessage(msg);
            }
        }.runTaskTimer(Initializer.p, 0L, 20L);
    }

    public static String getLengthofDuel(int i) {
        return "§7" + i + "V" + i;
    }

    public static Material formattedtype_Material(int i) {
        return switch (i) {
            case 0 -> Material.RESPAWN_ANCHOR;
            case 1 -> Material.END_CRYSTAL;
            case 2 -> Material.DIAMOND_SWORD;
            default -> null;
        };
    }

    public static void spawnFireworks(Location loc, World w) {
        Firework fw = (Firework) w.spawnEntity(loc.add(0, 1, 0), EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();
        fwm.setPower(2);
        fwm.addEffect(FireworkEffect.builder().withColor(Color.WHITE).withColor(Color.RED).with(FireworkEffect.Type.BALL_LARGE).flicker(true).build());
        fw.setFireworkMeta(fwm);
        fw.detonate();
    }
}
