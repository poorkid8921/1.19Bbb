package org.yuri.aestheticnetwork.utils;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.yuri.aestheticnetwork.AestheticNetwork;
import org.yuri.aestheticnetwork.commands.duel.DuelRequest;
import org.yuri.aestheticnetwork.commands.tpa.TpaRequest;

import java.util.ArrayList;

import static org.yuri.aestheticnetwork.utils.Utils.translate;

public class RequestManager {
    public static final ArrayList<TpaRequest> tpa = new ArrayList<>();
    public static final ArrayList<DuelRequest> duel = new ArrayList<>();

    public static DuelRequest getDUELrequest(Player user) {
        for (DuelRequest request : duel) {
            if (request.getReciever().getName().equalsIgnoreCase(user.getName()) ||
                    request.getSender().getName().equalsIgnoreCase(user.getName())) return request;
        }
        return null;
    }

    public static int getAvailable(String gm) {
        int r = 0;
        for (DuelRequest request : duel) {
            if (request.getType().equalsIgnoreCase(gm) && request.getRounds() > 0) r += 1;
        }
        return r;
    }

    public static void addDUELrequest(Player sender,
                                      Player receiver,
                                      String type,
                                      int rounds,
                                      int sr,
                                      int sb,
                                      int arena) {
        duel.remove(getDUELrequest(sender));
        DuelRequest tpaRequest = new DuelRequest(sender,
                receiver,
                type,
                rounds,
                0,
                sr,
                sb,
                System.currentTimeMillis(),
                arena);
        duel.add(tpaRequest);

        String type2 = type + (rounds == 1 ? " " : " &7with &c" + rounds + " rounds ");
        TextComponent tc = new TextComponent(translate("&c" + sender.getDisplayName() + " &7has requested that you duel them in &c" + type2));

        TextComponent accept = new TextComponent(translate("&7[&a✔&7]"));
        Text acceptHoverText = new Text(translate("&7Click to accept the duel request"));
        accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, acceptHoverText));
        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/duelaccept"));
        TextComponent deny = new TextComponent(translate("&7[&cX&7]"));
        Text denyHoverText = new Text(translate("Click to deny the duel request"));
        deny.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, denyHoverText));
        deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dueldeny"));

        receiver.playSound(receiver.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.f, 1.f);
        sender.sendMessage(translate("&7Request sent to &c" + receiver.getDisplayName() + "&7."));

        TextComponent space = new TextComponent("  ");
        receiver.sendMessage(tc, accept, space, deny);
    }

    public static TpaRequest getTPArequest(Player user) {
        for (TpaRequest request : tpa) {
            if (request.getReciever().getName().equalsIgnoreCase(user.getName())) return request;
        }

        return null;
    }

    public static void addTPArequest(Player sender, Player receiver, Type type) {
        TpaRequest tpaRequest = new TpaRequest(sender, receiver, type);
        tpa.add(tpaRequest);

        TextComponent tc = new TextComponent();
        TextComponent accept = new TextComponent(translate("&7[&a✔&7]"));
        Text acceptHoverText = new Text(translate("&7Click to accept the teleportation request"));
        accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, acceptHoverText));
        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept"));
        TextComponent deny = new TextComponent(translate("&7[&cX&7]"));
        Text denyHoverText = new Text(translate("&7Click to deny the teleportation request"));
        deny.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, denyHoverText));
        deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpdeny"));

        receiver.playSound(receiver.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.f, 1.f);
        sender.sendMessage(translate("&7Request sent to &c" + receiver.getDisplayName() + "&7."));

        if (type == Type.TPAHERE)
            tc.setText(translate("&c" + sender.getDisplayName() + " &7has requested that you teleport to them. "));
        else
            tc.setText(translate("&c" + sender.getDisplayName() + " &7has requested to teleport to you "));

        new BukkitRunnable() {
            @Override
            public void run() {
                if (getTPArequest(receiver) != null)
                    removeTPArequest(receiver);
            }
        }.runTaskLater(AestheticNetwork.getInstance(), 120 * 20);

        TextComponent space = new TextComponent("  ");
        receiver.sendMessage(tc, accept, space, deny);
    }

    public static void removeTPArequest(Player user) {
        tpa.remove(getTPArequest(user));
    }

    public static void removeDUELrequest(Player user) {
        duel.remove(getDUELrequest(user));
    }
}
