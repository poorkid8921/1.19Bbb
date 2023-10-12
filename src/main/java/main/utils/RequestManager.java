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

import java.util.ArrayList;

import static main.utils.Languages.MAIN_COLOR;

@SuppressWarnings("deprecation")
public class RequestManager {
    public static ArrayList<TpaRequest> tpa = new ArrayList<>();
    static TextComponent tc = new TextComponent(Utils.translateo(" &7has requested to teleport to you. "));
    static TextComponent a = new TextComponent(Utils.translateo("&7[&a✔&7]"));
    static TextComponent b = new TextComponent(Utils.translateo("&7[&cX&7]"));
    static TextComponent space = new TextComponent("  ");

    static {
        a.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(Utils.translateo("&7Click to accept the teleportation request"))));
        b.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(Utils.translateo("&7Click to deny the teleportation request"))));
    }

    public static void tpaccept(TpaRequest request, Player user) {
        Player tempuser;
        Player temprecipient;

        if (request.isHere()) {
            tempuser = user;
            temprecipient = request.getSender();
            temprecipient.sendMessage("§7You have accepted " + MAIN_COLOR + tempuser.getDisplayName() + "§7's teleport request");
            temprecipient.sendMessage("§7Teleporting...");
            tempuser.sendMessage(MAIN_COLOR + temprecipient.getDisplayName() + " §7has accepted your teleport request");
        } else {
            tempuser = request.getSender();
            temprecipient = user;
            temprecipient.sendMessage("§7You have accepted " + MAIN_COLOR + temprecipient.getDisplayName() + "§7's teleport request");
            temprecipient.sendMessage("§7Teleporting...");
            tempuser.sendMessage(MAIN_COLOR + temprecipient.getDisplayName() + " §7has accepted your teleport request");
        }

        tempuser.teleportAsync(temprecipient.getLocation()).thenAccept(reason -> tpa.remove(request));
    }

    public static TpaRequest getTPArequest(String user) {
        for (TpaRequest r : tpa) {
            if (r.getReceiver().getName().equals(user) || r.getSender().getName().equals(user)) return r;
        }

        return null;
    }

    public static TpaRequest getTPArequest(String user, String lookup) {
        for (TpaRequest r : tpa) {
            if ((r.getReceiver().getName().equals(user) || r.getSender().getName().equals(user)) && (r.getReceiver().getName().equals(lookup) || r.getSender().getName().equals(lookup)))
                return r;
        }

        return null;
    }

    public static void addTPArequest(Player sender, Player receiver, boolean tpahere) {
        String sn = sender.getName();
        tpa.remove(getTPArequest(sn));
        TpaRequest tpaRequest = new TpaRequest(sn, receiver.getName(), tpahere);
        tpa.add(tpaRequest);

        a.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept " + sn));
        b.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpdeny " + sn));
        receiver.playSound(receiver.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.f, 1.f);
        sender.sendMessage(Utils.translate("&7Request sent to #fc282f" + receiver.getDisplayName()));

        if (tpahere) tc.setText(Utils.translateo(" &7has requested that you teleport to them. "));
        receiver.sendMessage(new ComponentBuilder(sn).color(ChatColor.of("#fc282f")).create()[0], tc, a, space, b);

        Bukkit.getScheduler().scheduleSyncDelayedTask(Initializer.p, () -> tpa.remove(tpaRequest), 2400L);
    }
}
