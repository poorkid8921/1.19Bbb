package main.expansions.duels;

import main.Practice;
import main.utils.Initializer;
import main.utils.Instances.DuelHolder;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static main.expansions.duels.KitOverrider.*;
import static main.expansions.guis.Utils.updateDuels;
import static main.expansions.guis.Utils.updateSpectate;
import static main.utils.Languages.DUEL_STARTED;
import static main.utils.Languages.MAIN_COLOR;

public class Utils {
    public static NamespacedKey spectateHead = new NamespacedKey(Initializer.p, "against");
    static TextComponent tc = new TextComponent(main.utils.Utils.translateo(" &7has requested that you duel them in "));
    static TextComponent a = new TextComponent(main.utils.Utils.translateo("&7[&a✔&7]"));
    static TextComponent b = new TextComponent(main.utils.Utils.translateo("&7[&cX&7]"));
    static TextComponent duelType2 = new TextComponent(main.utils.Utils.translateo("&7with "));
    static TextComponent space = new TextComponent("  ");
    static SimpleDateFormat MM_HH = new SimpleDateFormat("mm:ss", Locale.ENGLISH);
    static TextComponent hi = new TextComponent(main.utils.Utils.translateo("&7ᴄʟɪᴄᴋ ᴛᴏ ꜱʜᴏᴡ ᴛʜᴇ ᴅᴜᴇʟ ʀᴇꜱᴜʟᴛꜱ"));

    static {
        a.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(main.utils.Utils.translateo("&7Click to accept the duel request"))));
        b.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(main.utils.Utils.translateo("&7Click to deny the duel request"))));
    }

    public static DuelHolder getDUELrequest(String user) {
        for (DuelHolder r : Initializer.duel) {
            if (r.getReceiver().getName().equals(user) || r.getSender().getName().equals(user)) return r;
        }

        return null;
    }

    public static DuelHolder getPlayerDuel(String user) {
        for (DuelHolder r : Initializer.inDuel) {
            if (r.getReceiver().getName().equals(user) || r.getSender().getName().equals(user)) return r;
        }

        return null;
    }

    public static void Duel_Accept_Request(DuelHolder request, Player user) {
        int check = Duel_GetDuelsAvailableForGM(request.getType());
        if (check >= 32) {
            Initializer.duel.remove(request);
            user.sendMessage(main.utils.Utils.translateo("&7There are no open arenas yet."));
            return;
        }

        String targetUID = request.getSender().getName();
        Player recipient = Bukkit.getPlayer(targetUID);
        Initializer.teams.putAll(Map.of(targetUID, 0, user.getName(), 0));

        Duel_Start(user, recipient, request.getType(), 1, request.getMaxrounds(), check + 1);
        updateDuels();
        updateSpectate();
    }

    public static DuelHolder getDUELrequest(String user, String lookup) {
        for (DuelHolder r : Initializer.duel) {
            if ((r.getReceiver().getName().equals(user) || r.getSender().getName().equals(user)) && (r.getReceiver().getName().equals(lookup) || r.getSender().getName().equals(lookup)))
                return r;
        }

        return null;
    }

    public static int Duel_GetDuelsAvailableForGM(int gm) {
        return Initializer.duel.stream().filter(r -> r.getType() == gm && r.getRounds() > 0).toList().size();
    }

    public static String Duel_Formatted_Type(int i) {
        return switch (i) {
            case 0 -> "Field";
            case 1 -> "Flat";
            case 2 -> "Tank";
            default -> throw new IllegalStateException("Unexpected value: " + i);
        };
    }

    public static void addDUELrequest(Player sender, Player receiver, int t, int rounds, int sr, int sb, int arena, int length) {
        String sn = sender.getName();
        Initializer.duel.remove(getDUELrequest(sn));
        DuelHolder duelRequest = new DuelHolder(sn, receiver.getName(), t, rounds, 0, sr, sb, System.currentTimeMillis(), arena, length);
        Initializer.duel.add(duelRequest);
        receiver.playSound(receiver.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.f, 1.f);
        sender.sendMessage(main.utils.Utils.translate("&7Request sent to #fc282f" + receiver.getDisplayName()));

        a.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept " + sn));
        b.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpdeny " + sn));

        String type = Duel_Formatted_Type(t);

        TextComponent duelType = new TextComponent(type + " ");
        duelType.setColor(ChatColor.of("#fc282f"));

        TextComponent duelType3 = new TextComponent(rounds + " rounds.");
        duelType3.setColor(ChatColor.of("#fc282f"));

        TextComponent e = new TextComponent(sn);
        e.setColor(ChatColor.of("#fc282f"));

        receiver.sendMessage(e, tc, duelType, duelType2, duelType3, a, space, b);

        Bukkit.getScheduler().scheduleSyncDelayedTask(Initializer.p, () -> Initializer.duel.remove(duelRequest), 2400L);
    }

    public static void Duel_Resume(Player pl, Player p, boolean i, int r, int b, long o, long n, String t, boolean rw, String f, String ff) {
        String rd = pl.getName();
        String ad = p.getName();

        hi.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/event " + rw + " " + r + " " + b + " " + MM_HH.format(new Date(n - o)) + t + pl.getName() + " " + Math.round(pl.getHealth() / 2) + " " + pl.getStatistic(Statistic.PLAYER_KILLS) + " " + pl.getStatistic(Statistic.DEATHS) + " " + Practice.cc1.getInt("r." + pl.getName() + ".wins") + " " + Practice.cc1.getInt("r." + pl.getName() + ".losses")));
        Initializer.valid.add(rd);
        pl.sendMessage(main.utils.Utils.translate("&7Teleporting back to spawn in #fc282f3 seconds..."));
        pl.sendMessage(hi);
        pl.sendTitle(ff, "", 1, 100, 1);
        pl.getInventory().clear();
        if (i) {
            Initializer.valid.add(ad);
            p.sendMessage(main.utils.Utils.translate("&7Teleporting back to spawn in #fc282f3 seconds..."));
            p.sendMessage(hi);
            p.sendTitle(f, "", 1, 100, 1);
            p.getInventory().clear();
        }

        Practice.cc1.set("r." + rd + ".wins", Practice.cc1.getInt("r." + rd + ".wins") + 1);
        Practice.cc1.set("r." + ad + ".losses", Practice.cc1.getInt("r." + ad + ".losses") + 1);

        Initializer.p.saveCustomConfig();
    }

    public static void Duel_Start(Player user, Player recipient, int type, int round, int maxi, int arena) {
        Location loc = Duel_Loc1(type, user, arena);
        Location loc2 = Duel_Loc0(type, recipient, arena);
        user.teleportAsync(loc).thenAccept(r -> {
            user.setNoDamageTicks(100);
            user.setFoodLevel(20);
            user.setHealth(20);
        });

        recipient.teleportAsync(loc2).thenAccept(r -> {
            recipient.setNoDamageTicks(100);
            recipient.setFoodLevel(20);
            recipient.setHealth(20);
        });

        World w = Bukkit.getWorld("world");
        String a = "&7" + (maxi > 1 ? "Round " + round : "Duel");
        new BukkitRunnable() {
            int i = 5;

            @Override
            public void run() {
                user.playSound(user.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.f, 1.f);
                recipient.playSound(recipient.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.f, 1.f);

                if (i == 1) {
                    user.sendMessage(a + DUEL_STARTED);
                    recipient.sendMessage(a + DUEL_STARTED);

                    user.sendTitle(MAIN_COLOR + "Fight!", "", 1, 30, 1);
                    recipient.sendTitle(MAIN_COLOR + "Fight!", "", 1, 30, 1);

                    user.teleportAsync(loc).thenAccept(r -> w.setType(loc.subtract(0, 1, 0), Material.GOLD_BLOCK));
                    recipient.teleportAsync(loc2).thenAccept(r -> w.setType(loc2.subtract(0, 1, 0), Material.GOLD_BLOCK));

                    this.cancel();
                    return;
                }

                String s = MAIN_COLOR + i;
                user.sendTitle(s, "", 1, 20, 1);
                recipient.sendTitle(s, "", 1, 20, 1);
                i--;
                String sec = i == 1 ? MAIN_COLOR + "a second" : s + " seconds";
                String msg = round > 1 ? "§7Round " + MAIN_COLOR + round + " / " + maxi + " §7starting in " + sec :
                        "§7" + (maxi > 1 ? "Round" : "Duel") + " starting in " + sec;
                user.sendMessage(msg);
                recipient.sendMessage(msg);
            }
        }.runTaskTimer(Initializer.p, 0L, 20L);
    }

    public static Location Duel_Loc1(int type, Player p, int i) {
        Location loc = new Location(Bukkit.getWorld("world"), 0, 0, 0);
        p.getInventory().clear();

        switch (type) {
            case 0 -> {
                Duels_Kit_Field(p);
                loc = i == 1 ? new Location(Bukkit.getWorld("world"), -391.5, 149, -303.5) : i == 2 ? new Location(Bukkit.getWorld("world"), -279.5, 149, -432.5) : i == 3 ? new Location(Bukkit.getWorld("world"), -408.5, 149, -432.5) : i == 4 ? new Location(Bukkit.getWorld("world"), -408.5, 149, -303.5) : i == 5 ? new Location(Bukkit.getWorld("world"), -537.5, 149, -303.5) : new Location(Bukkit.getWorld("world"), -637.5, 149.5, -432.5);
            }
            case 1 -> {
                Duels_Kit_Flat(p);
                loc = i == 1 ? new Location(Bukkit.getWorld("world"), -391.5, 149, -303.5) : i == 2 ? new Location(Bukkit.getWorld("world"), -279.5, 149, -432.5) : i == 3 ? new Location(Bukkit.getWorld("world"), -408.5, 149, -432.5) : i == 4 ? new Location(Bukkit.getWorld("world"), -408.5, 149, -303.5) : i == 5 ? new Location(Bukkit.getWorld("world"), -537.5, 149, -303.5) : new Location(Bukkit.getWorld("world"), -637.5, 149.5, -432.5);
            }
            case 2 -> {
                Duels_Kit_Tank(p);
                loc = i == 1 ? new Location(Bukkit.getWorld("world"), -391.5, 149, -303.5) : i == 2 ? new Location(Bukkit.getWorld("world"), -279.5, 149, -432.5) : i == 3 ? new Location(Bukkit.getWorld("world"), -408.5, 149, -432.5) : i == 4 ? new Location(Bukkit.getWorld("world"), -408.5, 149, -303.5) : i == 5 ? new Location(Bukkit.getWorld("world"), -537.5, 149, -303.5) : new Location(Bukkit.getWorld("world"), -637.5, 149.5, -432.5);
            }
        }
        loc.setYaw(-45.0F);

        return loc;
    }

    public static Location Duel_Loc0(int type, Player p, int i) {
        Location loc = new Location(Bukkit.getWorld("world"), 0, 0, 0);
        p.getInventory().clear();

        switch (type) {
            case 0 -> {
                Duels_Kit_Field(p);
                loc = i == 1 ? new Location(Bukkit.getWorld("world"), -391.5, 149, -303.5) : i == 2 ? new Location(Bukkit.getWorld("world"), -279.5, 149, -432.5) : i == 3 ? new Location(Bukkit.getWorld("world"), -408.5, 149, -432.5) : i == 4 ? new Location(Bukkit.getWorld("world"), -408.5, 149, -303.5) : i == 5 ? new Location(Bukkit.getWorld("world"), -537.5, 149, -303.5) : new Location(Bukkit.getWorld("world"), -637.5, 149.5, -432.5);
            }
            case 1 -> {
                Duels_Kit_Flat(p);
                loc = i == 1 ? new Location(Bukkit.getWorld("world"), -391.5, 149, -303.5) : i == 2 ? new Location(Bukkit.getWorld("world"), -279.5, 149, -432.5) : i == 3 ? new Location(Bukkit.getWorld("world"), -408.5, 149, -432.5) : i == 4 ? new Location(Bukkit.getWorld("world"), -408.5, 149, -303.5) : i == 5 ? new Location(Bukkit.getWorld("world"), -537.5, 149, -303.5) : new Location(Bukkit.getWorld("world"), -637.5, 149.5, -432.5);
            }
            case 2 -> {
                Duels_Kit_Tank(p);
                loc = i == 1 ? new Location(Bukkit.getWorld("world"), -391.5, 149, -303.5) : i == 2 ? new Location(Bukkit.getWorld("world"), -279.5, 149, -432.5) : i == 3 ? new Location(Bukkit.getWorld("world"), -408.5, 149, -432.5) : i == 4 ? new Location(Bukkit.getWorld("world"), -408.5, 149, -303.5) : i == 5 ? new Location(Bukkit.getWorld("world"), -537.5, 149, -303.5) : new Location(Bukkit.getWorld("world"), -637.5, 149.5, -432.5);
            }
        }
        loc.setYaw(45.0F);

        return loc;
    }

    public static String getLengthofDuel(int i) {
        return "§7" + i + "V" + i;
    }

    public static Material Duel_Formatted_Type_Material(int i) {
        return switch (i) {
            case 0 -> Material.RESPAWN_ANCHOR;
            case 1 -> Material.END_CRYSTAL;
            case 2 -> Material.DIAMOND_SWORD;
            default -> throw new IllegalStateException("Unexpected value: " + i);
        };
    }

    public static void GUI_Duels_Init_Spectate(Inventory inv) {
        int added = 9;
        for (DuelHolder r : Initializer.duel) {
            if (r.getRounds() > 0) {
                added++;
                ItemStack i = new ItemStack(Duel_Formatted_Type_Material(r.getType()));
                ItemMeta im = i.getItemMeta();
                im.setLore(List.of(getLengthofDuel(r.getMaxPlayers()), main.utils.Utils.translate("#fc282f" + r.getSender().getDisplayName() + " &7ᴀɢᴀɪɴsᴛ #fc282f" + r.getReceiver().getDisplayName())));
                im.getPersistentDataContainer().set(spectateHead, PersistentDataType.STRING, r.getSender().getName());
                i.setItemMeta(im);
                inv.setItem(added, i);

                if (added == 43) break;
            }
        }
    }

    public static void GUI_Duels_Update(Inventory inv) {
        ItemStack i = new ItemStack(Material.END_CRYSTAL, 1);
        ItemMeta meta = i.getItemMeta();
        meta.setDisplayName(main.utils.Utils.translate("#fc282fꜰɪᴇʟᴅ"));
        meta.setLore(List.of("1V1"));
        i.setItemMeta(meta);
        i.setAmount(Math.min(Duel_GetDuelsAvailableForGM(0), 1));
        inv.setItem(10, i);
    }
}
