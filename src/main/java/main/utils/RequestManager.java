package main.utils;

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

import static main.utils.Languages.MAIN_COLOR;

@SuppressWarnings("deprecation")
public class RequestManager {
    public static ArrayList<TpaRequest> tpa = new ArrayList<>();
    public static Map<String, Integer> bukkitTasks = new HashMap<>();
    static TextComponent space = new TextComponent("  ");

    public static TpaRequest getTPArequest(String user) {
        for (TpaRequest r : tpa) {
            if (r.getReceiverF().equals(user) || r.getSenderF().equals(user)) return r;
        }

        return null;
    }

    public static TpaRequest getTPArequest(String user, String lookup) {
        for (TpaRequest r : tpa) {
            if ((r.getReceiverF().equals(user) || r.getSenderF().equals(user)) && (r.getReceiverF().equals(lookup) || r.getSenderF().equals(lookup)))
                return r;
        }

        return null;
    }

    public static void addTPArequest(Player sender, Player receiver, boolean tpahere) {
        String rn = receiver.getName();

        TextComponent a = new TextComponent("§7[§a✔§7]");
        a.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept " + rn));

        TextComponent b = new TextComponent("§7[§cX§7]");
        b.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpdeny " + rn));

        a.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§7Click to accept the teleportation request")));
        b.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§7Click to deny the teleportation request")));

        receiver.playSound(receiver.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.f, 1.f);
        sender.sendMessage("§7Request sent to " + MAIN_COLOR + Utils.translate(receiver.getDisplayName()));

        TextComponent tc = new TextComponent(tpahere ? " §7has requested that you teleport to them. " :
                " §7has requested to teleport to you. ");
        String sn = sender.getName();
        receiver.sendMessage(new ComponentBuilder(sn)
                        .color(ChatColor.of("#fc282f")).create()[0],
                tc,
                a,
                space,
                b);
        TpaRequest tpaRequest = new TpaRequest(sn, rn, tpahere);
        tpa.add(tpaRequest);

        BukkitTask br = new BukkitRunnable() {
            @Override
            public void run() {
                tpa.remove(tpaRequest);
            }
        }.runTaskLaterAsynchronously(Initializer.p, 2400L);

        bukkitTasks.put(sn, br.getTaskId());
    }
}
