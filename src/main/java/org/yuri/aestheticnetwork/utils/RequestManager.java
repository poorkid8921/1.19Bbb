package org.yuri.aestheticnetwork.utils;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.yuri.aestheticnetwork.AestheticNetwork;
import org.yuri.aestheticnetwork.commands.tpa.TpaRequest;

import java.util.ArrayList;

import static org.yuri.aestheticnetwork.utils.Utils.translate;
import static org.yuri.aestheticnetwork.utils.Utils.translateo;

public class RequestManager {
    public static final ArrayList<TpaRequest> tpa = new ArrayList<>();

    public static TpaRequest getTPArequest(Player user) {
        return tpa.stream().filter(req -> req.getReciever().getName().equalsIgnoreCase(user.getName()) ||
                        req.getSender().getName().equalsIgnoreCase(user.getName()))
                .toList()
                .get(0);
    }

    public static void removeTPArequest(TpaRequest user) {
        tpa.remove(user);
    }

    public static void addTPArequest(Player sender, Player receiver, Type type) {
        TpaRequest tpaRequest = new TpaRequest(sender,
                receiver,
                type);
        tpa.add(tpaRequest);

        TextComponent tc = new TextComponent();
        TextComponent accept = new TextComponent(translateo("&7[&a✔&7]"));
        Text acceptHoverText = new Text(translateo("&7Click to accept the teleportation request"));
        accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, acceptHoverText));
        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept"));
        TextComponent deny = new TextComponent(translateo("&7[&cX&7]"));
        Text denyHoverText = new Text(translateo("&7Click to deny the teleportation request"));
        deny.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, denyHoverText));
        deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpdeny"));

        receiver.playSound(receiver.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.f, 1.f);
        sender.sendMessage(translate("&7Request sent to &c" +
                receiver.getDisplayName() +
                "&7."));

        if (type == Type.TPAHERE)
            tc.setText(translate("&c" +
                    sender.getDisplayName() +
                    " &7has requested that you teleport to them. "));
        else tc.setText(translate("&c" +
                sender.getDisplayName() +
                " &7has requested to teleport to you "));

        new BukkitRunnable() {
            @Override
            public void run() {
                if (tpa.contains(tpaRequest)) removeTPArequest(tpaRequest);
            }
        }.runTaskLater(AestheticNetwork.getInstance(), 120 * 20);

        TextComponent space = new TextComponent("  ");
        receiver.sendMessage(tc, accept, space, deny);
    }
}
