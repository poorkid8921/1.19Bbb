package org.yuri.aestheticnetwork.utils.duels;

import io.papermc.lib.PaperLib;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.yuri.aestheticnetwork.AestheticNetwork;
import org.yuri.aestheticnetwork.commands.duel.DuelRequest;
import org.yuri.aestheticnetwork.commands.duel.KitManager;
import org.yuri.aestheticnetwork.utils.Initializer;
import org.yuri.aestheticnetwork.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.*;

import static org.yuri.aestheticnetwork.utils.Initializer.*;
import static org.yuri.aestheticnetwork.utils.Utils.translate;
import static org.yuri.aestheticnetwork.utils.Utils.translateo;

@SuppressWarnings("deprecation")
public class DuelManager {
    static AestheticNetwork plugin = AestheticNetwork.getInstance();

    public static DuelRequest getDUELrequest(String user) {
        for (DuelRequest r : duel) {
            if (r.getReciever().getName().equals(user) || r.getSender().getName().equals(user)) return r;
        }

        return null;
    }

    public static void duelaccept(DuelRequest request, Player user) {
        String targetUID = request.getSender().getName();
        Player recipient = Bukkit.getPlayer(targetUID);
        teams.putAll(Map.of(targetUID, 0,
                user.getName(), 0));

        int check = getAvailable(request.getType());
        if (check >= 6) {
            removeDUELrequest(request);
            user.sendMessage(translateo("&7There are no open arenas yet."));
            return;
        }

        startduel(user,
                recipient,
                request.getType(),
                1,
                request.getMaxrounds(),
                check + 1);
        User up = lp.getUserManager().getUser(recipient.getUniqueId());
        up.data().add(Node.builder("permission:tab.scoreboard.duels").build());
        lp.getUserManager().saveUser(up);

        User u = lp.getUserManager().getUser(user.getUniqueId());
        u.data().add(Node.builder("permission:tab.scoreboard.duels").build());
        lp.getUserManager().saveUser(u);
    }

    public static void initializeSpectate(Inventory inv) {
        //inv.setContents(Initializer.duelInventory);

        int added = 9;
        for (DuelRequest r : duel) {
            if (r.getRounds() > 0) {
                added++;
                ItemStack i = new ItemStack(Objects.equals(r.getType(), "field") ? Material.END_CRYSTAL : Material.DIAMOND_SWORD);
                ItemMeta im = i.getItemMeta();
                im.setLore(List.of(translate("#fc282f" +
                        r.getSender().getDisplayName() +
                        " &7ᴀɢᴀɪɴsᴛ #fc282f" +
                        r.getReciever().getDisplayName())));
                i.setItemMeta(im);
                inv.setItem(added, i);

                if (added == 43)
                    break;
            }
        }
    }

    public static void updateDuels(Inventory inv) {
        ItemStack i = new ItemStack(Material.END_CRYSTAL, 1);
        ItemMeta meta = i.getItemMeta();
        meta.setDisplayName(translate("#fc282fꜰɪᴇʟᴅ"));
        meta.setLore(List.of(translate("#fc282f" + DuelManager.getAvailable("field"))));
        //meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        i.setItemMeta(meta);
        inv.setItem(10, i);
    }

    public static void startMatchmaking(Player p, String gm) {
        p.closeInventory();
        p.sendActionBar(translateo("&aYou have been placed into the " + gm.substring(0, 1).toUpperCase() + gm.substring(1) + " queue."));
        new BukkitRunnable() {
            int timeout = 0;

            @Override
            public void run() {
                timeout++;
                if (inMatchmaking.containsKey(gm) && !inMatchmaking.get(gm).equals(p.getName())) {
                    inMatchmaking.remove(gm);
                    int check = getAvailable(gm);
                    if (check >= 6) {
                        p.sendActionBar(translateo("&aCouldn't find any open arena. Please try again later"));
                        this.cancel();
                        return;
                    }
                    startduel(p, Bukkit.getPlayer(inMatchmaking.get("gm")), gm, 1, 1, check + 1);
                    this.cancel();
                }

                if (timeout == 20) {
                    p.sendActionBar(translateo("&aYour session has timed out. Please try again later"));
                    inMatchmaking.remove(gm);
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 60L);
    }

    public static DuelRequest getDUELrequest(String user, String lookup) {
        for (DuelRequest r : duel) {
            if ((r.getReciever().getName().equals(user) || r.getSender().getName().equals(user)) && (r.getReciever().getName().equals(lookup) || r.getSender().getName().equals(lookup)))
                return r;
        }

        return null;
    }

    public static int getAvailable(String gm) {
        int r = 0;
        for (DuelRequest request : duel) {
            if (request.getType().equalsIgnoreCase(gm) && request.getRounds() > 0) r++;
        }

        return r;
    }

    public static boolean alreadyInMatchmaking(String user, String gm) {
        for (DuelRequest r : duel) {
            if ((r.getReciever().getName().equals(user) || r.getSender().getName().equals(user)) && r.getType().equals(gm))
                return true;
        }

        return false;
    }

    public static void addDUELrequest(Player sender, Player receiver, String type, int rounds, int sr, int sb, int arena) {
        duel.remove(getDUELrequest(sender.getName()));
        DuelRequest tpaRequest = new DuelRequest(sender,
                receiver,
                type.toLowerCase(),
                rounds,
                0,
                sr,
                sb,
                System.currentTimeMillis(),
                arena);
        duel.add(tpaRequest);

        String clean = ChatColor.stripColor(sender.getDisplayName());
        int c = clean.indexOf(" ");
        String n = clean.

        TextComponent name = new TextComponent();
        TextComponent tc = new TextComponent(translateo(" &7has requested that you duel them in "));

        TextComponent accept = new TextComponent(translateo("&7[&a✔&7]"));
        Text acceptHoverText = new Text(translateo("&7Click to accept the duel request"));
        accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, acceptHoverText));
        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/duelaccept"));
        TextComponent deny = new TextComponent(translateo("&7[&cX&7]"));
        Text denyHoverText = new Text(translateo("Click to deny the duel request"));
        deny.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, denyHoverText));
        deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dueldeny"));

        receiver.playSound(receiver.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.f, 1.f);
        sender.sendMessage(translate("&7Request sent to #fc282f" + receiver.getDisplayName()));

        new BukkitRunnable() {
            @Override
            public void run() {
                if (duel.contains(tpaRequest)) removeDUELrequest(tpaRequest);
            }
        }.runTaskLater(AestheticNetwork.getInstance(), 120 * 20);

        TextComponent space = new TextComponent("  ");
        if (c != -1)

            else
        receiver.sendMessage(tc, accept, space, deny);
    }

    public static void removeDUELrequest(DuelRequest user) {
        duel.remove(user);
    }

    public static void displayduelresume(Player pl, Player p, boolean i, int r, int b, long o, long n, String t, boolean rw, String f, String ff) {
        SimpleDateFormat sdfDate = new SimpleDateFormat("mm:ss");
        sdfDate.setTimeZone(TimeZone.getTimeZone("UTC"));
        String strDate = sdfDate.format(new Date(n - o));
        String m = translate("#fc282f" + pl.getName() + " &fᴡᴏɴ ᴛʜᴇ ᴅᴜᴇʟ!");

        TextComponent hi = new TextComponent(translateo("&7ᴄʟɪᴄᴋ ᴛᴏ ꜱʜᴏᴡ ᴛʜᴇ ᴅᴜᴇʟ ʀᴇꜱᴜʟᴛꜱ"));
        hi.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/event " + rw + " " + r + " " + b + " " + strDate + t + pl.getName() + " " + Math.round(pl.getHealth() / 2) + " " + pl.getStatistic(Statistic.PLAYER_KILLS) + " " + pl.getStatistic(Statistic.DEATHS) + " " + Utils.manager().getInt("r." + pl.getName() + ".wins") + " " + Utils.manager().getInt("r." + pl.getName() + ".losses")));
        valid.add(pl.getName());
        pl.sendMessage(translate("&7Teleporting back to spawn in #fc282f3 seconds..."));
        pl.sendMessage(hi);
        pl.sendTitle(ff, m, 1, 100, 1);
        pl.getInventory().clear();
        if (i) {
            valid.add(p.getName());
            p.sendMessage(translate("&7Teleporting back to spawn in #fc282f3 seconds..."));
            p.sendMessage(hi);
            p.sendTitle(f, m, 1, 100, 1);
            p.getInventory().clear();
        }

        Utils.manager().set("r." + pl.getName() + ".wins", Utils.manager().getInt("r." + pl.getName() + ".wins") + 1);
        Utils.manager().set("r." + p.getName() + ".losses", Utils.manager().getInt("r." + p.getName() + ".losses") + 1);

        AestheticNetwork.getInstance().saveCustomConfig();
    }

    public static void startduel(Player user, Player recipient, String type, int round, int maxi, int arena) {
        Location loc = duelloc(type, true, user, arena);
        Location loc2 = duelloc(type, false, recipient, arena);
        PaperLib.teleportAsync(user, loc).thenAccept((reason) -> {
            user.setNoDamageTicks(100);
            user.setFoodLevel(20);
            user.setHealth(20);
        });

        PaperLib.teleportAsync(recipient, loc2).thenAccept((reason) -> {
            recipient.setNoDamageTicks(100);
            recipient.setFoodLevel(20);
            recipient.setHealth(20);
        });

        //Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "arena reset duel_" + type + arena + " veryfast");

        new BukkitRunnable() {
            int i = 5;

            @Override
            public void run() {
                user.playSound(user.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.f, 1.f);
                recipient.playSound(recipient.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.f, 1.f);

                if (i == 1) {
                    user.sendMessage(translate("&7" + (maxi > 1 ? "Round " + round : "Duel") + " started! #fc282fFight!"));
                    recipient.sendMessage(translate("&7" + (maxi > 1 ? "Round " + round : "Duel") + " started! #fc282fFight!"));

                    user.sendTitle(translate("#fc282fFight!"), "", 1, 30, 1);
                    recipient.sendTitle(translate("#fc282fFight!"), "", 1, 30, 1);

                    PaperLib.teleportAsync(user, loc).thenAccept(r -> Bukkit.getWorld("world").setType(loc.subtract(new Vector(0, 1, 0)), Material.GOLD_BLOCK));
                    PaperLib.teleportAsync(recipient, loc2).thenAccept(r -> Bukkit.getWorld("world").setType(loc2.subtract(new Vector(0, 1, 0)), Material.GOLD_BLOCK));

                    this.cancel();
                    return;
                }

                user.sendTitle(translate("#fc282f" + i), "", 1, 20, 1);
                recipient.sendTitle(translate("#fc282f" + i), "", 1, 20, 1);
                i--;
                String sec = i == 1 ? "#fc282fa second" : "#fc282f" + i + " seconds";
                String msg = round > 1 ? translate("&7Round #fc282f" + round + " / " + maxi + " &7starting in " + sec) : translateo("&7" + (maxi > 1 ? "Round" : "Duel") + " starting in " + sec);
                user.sendMessage(msg);
                recipient.sendMessage(msg);
            }
        }.runTaskTimer(AestheticNetwork.getInstance(), 0L, 20L);
    }

    public static Location duelloc(String type, boolean red, Player p, int i) {
        Location loc = new Location(Bukkit.getWorld("world"), 0, 0, 0);
        p.getInventory().clear();

        if (type.equals("field")) {
            KitManager.field(p);
            loc = i == 1 ? red ? new Location(Bukkit.getWorld("world"), -391.5, 149, -303.5) : new Location(Bukkit.getWorld("world"), -279.5, 149, -303.5) : i == 2 ? red ? new Location(Bukkit.getWorld("world"), -391.5, 149, -432.5) : new Location(Bukkit.getWorld("world"), -279.5, 149, -432.5) : i == 3 ? red ? new Location(Bukkit.getWorld("world"), -520.5, 149, -432.5) : new Location(Bukkit.getWorld("world"), -408.5, 149, -432.5) : i == 4 ? red ? new Location(Bukkit.getWorld("world"), -520.5, 149, -303.5) : new Location(Bukkit.getWorld("world"), -408.5, 149, -303.5) : i == 5 ? red ? new Location(Bukkit.getWorld("world"), -649.5, 149, -303.5) : new Location(Bukkit.getWorld("world"), -537.5, 149, -303.5) : red ? new Location(Bukkit.getWorld("world"), -649.5, 149, -432.5) : new Location(Bukkit.getWorld("world"), -637.5, 149.5, -432.5);
        } else if (type.equals("flat")) {
            KitManager.flat(p);
            loc = i == 1 ? red ? new Location(Bukkit.getWorld("world"), 89.5, 115, 700.5) :
                    new Location(Bukkit.getWorld("world"), -31.5, 115, 580.5) : i == 2 ? red ?
                    new Location(Bukkit.getWorld("world"), -391.5, 149, -432.5) :
                    new Location(Bukkit.getWorld("world"), -279.5, 149, -432.5) : i == 3 ? red ?
                    new Location(Bukkit.getWorld("world"), -520.5, 149, -432.5) :
                    new Location(Bukkit.getWorld("world"), -408.5, 149, -432.5) : i == 4 ? red ?
                    new Location(Bukkit.getWorld("world"), -520.5, 149, -303.5) :
                    new Location(Bukkit.getWorld("world"), -408.5, 149, -303.5) : i == 5 ? red ?
                    new Location(Bukkit.getWorld("world"), -649.5, 149, -303.5) :
                    new Location(Bukkit.getWorld("world"), -537.5, 149, -303.5) : red ?
                    new Location(Bukkit.getWorld("world"), -649.5, 149, -432.5) :
                    new Location(Bukkit.getWorld("world"), -637.5, 149.5, -432.5);
        }
        loc.setYaw(red ? 45.0F : -45.0F);

        return loc;
    }
}