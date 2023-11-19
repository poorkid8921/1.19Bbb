package main.utils;

import main.utils.Instances.DuelHolder;
import main.utils.Instances.TpaRequest;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static main.utils.DuelUtils.formattedtype;
import static main.utils.Languages.MAIN_COLOR;

@SuppressWarnings("deprecation")
public class RequestManager {
    public static ArrayList<TpaRequest> tpa = new ArrayList<>();
    public static Map<String, Integer> bukkitTasks = new HashMap<>();
    static TextComponent space = new TextComponent("  ");
    static TextComponent duelType2 = new TextComponent(" §7with ");
    static TextComponent tc = new TextComponent(" §7has requested that you duel them in ");

    public static TpaRequest getTPArequest(String user) {
        for (TpaRequest r : tpa) {
            if (r.getReceiver().equals(user) || r.getSenderF().equals(user)) return r;
        }

        return null;
    }

    public static TpaRequest getTPArequest(String user, String lookup) {
        for (TpaRequest r : tpa) {
            if ((r.getReceiver().equals(user) || r.getSenderF().equals(user)) && (r.getReceiver().equals(lookup) || r.getSenderF().equals(lookup)))
                return r;
        }

        return null;
    }

    public static void addTPArequest(Player sender, Player receiver, boolean tpahere) {
        String sn = sender.getName();
        TextComponent a = new TextComponent("§7[§a✔§7]");
        a.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept " + sn));
        TextComponent b = new TextComponent("§7[§cX§7]");
        b.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpdeny " + sn));

        a.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§7Click to accept the teleportation request")));
        b.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§7Click to deny the teleportation request")));

        receiver.playSound(receiver.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.f, 1.f);
        sender.sendMessage("§7Request sent to " + MAIN_COLOR + Utils.translate(receiver.getDisplayName()));

        receiver.sendMessage(new ComponentBuilder(sn)
                        .color(ChatColor.of("#fc282f")).create()[0],
                new TextComponent(tpahere ? " §7has requested that you teleport to them. " :
                        " §7has requested to teleport to you. "),
                a,
                space,
                b);
        TpaRequest tpaRequest = new TpaRequest(sn, receiver.getName(), tpahere);
        tpa.add(tpaRequest);

        BukkitTask br = new BukkitRunnable() {
            @Override
            public void run() {
                tpa.remove(tpaRequest);
            }
        }.runTaskLaterAsynchronously(Initializer.p, 2400L);

        bukkitTasks.put(sn, br.getTaskId());
    }

    public static DuelHolder getDUELrequest(String user) {
        for (DuelHolder r : Initializer.duel) {
            if (r.getReceiver().equals(user) || r.getSender().getName().equals(user)) return r;
        }

        return null;
    }

    public static DuelHolder getPlayerDuel(String user) {
        for (DuelHolder r : Initializer.inDuel) {
            if (r.getReceiver().equals(user) || r.getSender().getName().equals(user)) return r;
        }

        return null;
    }

    public static DuelHolder getDUELrequest(String user, String lookup) {
        for (DuelHolder r : Initializer.duel) {
            if ((r.getReceiver().equals(user) || r.getSenderF().equals(user)) && (r.getReceiver().equals(lookup) || r.getSender().getName().equals(lookup)))
                return r;
        }

        return null;
    }

    public static void addDUELrequest(Player sender, Player receiver, int t, int rounds, int sr, int sb, int arena, int length) {
        String sn = sender.getName();
        String rn = receiver.getName();
        DuelHolder duelRequest = new DuelHolder(sn, rn, t, rounds, 0, sr, sb, System.currentTimeMillis(), arena, length);
        Initializer.duel.add(duelRequest);
        receiver.playSound(receiver.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.f, 1.f);
        sender.sendMessage("§7Request sent to " + MAIN_COLOR + main.utils.Utils.translate(receiver.getDisplayName()));

        TextComponent a = new TextComponent("§7[§a✔§7]");
        a.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/duelaccept " + sn));
        TextComponent b = new TextComponent("§7[§cX§7]");
        b.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dueldeny " + sn));

        a.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§7Click to accept the teleportation request")));
        b.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§7Click to deny the teleportation request")));

        TextComponent duelType = new TextComponent(formattedtype(t));
        duelType.setColor(ChatColor.of("#fc282f"));

        TextComponent duelType3 = new TextComponent(rounds == 1 ? " a round" : rounds + " rounds");
        duelType3.setColor(ChatColor.of("#fc282f"));

        TextComponent e = new TextComponent(sn);
        e.setColor(ChatColor.of("#fc282f"));

        receiver.sendMessage(e, tc, duelType, duelType2, duelType3, a, space, b);
        Bukkit.getScheduler().scheduleAsyncDelayedTask(Initializer.p, () -> Initializer.duel.remove(duelRequest), 2400L);
    }
}
