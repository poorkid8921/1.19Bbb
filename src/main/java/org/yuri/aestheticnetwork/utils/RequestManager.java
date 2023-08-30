package org.yuri.aestheticnetwork.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
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
        return tpa.stream().filter(r -> r.getReciever().getName().equals(user.getName()) ||
                r.getSender().getName().equals(user.getName())).toList().get(0);
    }

    public static TpaRequest getTPArequest(Player user, String lookup) {
        return tpa.stream().filter(r -> (r.getReciever().getName().equals(user.getName()) ||
                r.getSender().getName().equals(user.getName())) &&
                (r.getReciever().getName().toLowerCase().startsWith(lookup) ||
                        r.getSender().getName().toLowerCase().startsWith(lookup))).toList().get(0);
    }


    public static void removeTPArequest(TpaRequest user) {
        tpa.remove(user);
    }

    public static void addTPArequest(Player sender, Player receiver, Type type) {
        TpaRequest tpaRequest = new TpaRequest(sender,
                receiver,
                type);
        tpa.add(tpaRequest);

        String name = sender.getDisplayName().replace("&", "");
        boolean c = name.contains(" ");
        TextComponent tc = new TextComponent(c ? translate(name.substring(0, name.indexOf(" ")) + "&r "
                + name.substring(name.indexOf(" ") + 1)) :
                translateo(name));
        tc.setColor(ChatColor.valueOf(c ? name.substring(0, 7) : "#fc282f"));
        TextComponent tc1 = new TextComponent(translateo(" &7has requested that you teleport to them. "));

        TextComponent a = new TextComponent(translateo("&7[&a✔&7]"));
        a.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(
                translateo("&7Click to accept the teleportation request"))));
        a.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept"));

        TextComponent b = new TextComponent(translate("&7[#fc282fx&7]"));
        b.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(
                translateo("&7Click to deny the teleportation request"))));
        b.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpdeny"));
        receiver.playSound(receiver.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.f, 1.f);
        sender.sendMessage(translate("&7Request sent to #fc282f" +
                receiver.getDisplayName()));

        if (type == Type.TPA)
            tc.setText(translate("#fc282f" +
                    sender.getDisplayName() +
                    " &7has requested to teleport to you "));

        new BukkitRunnable() {
            @Override
            public void run() {
                tpa.remove(tpaRequest);
            }
        }.runTaskLater(AestheticNetwork.getInstance(), 120 * 20);

        TextComponent space = new TextComponent("  ");
        receiver.sendMessage(tc1, tc, a, space, b);
    }
}
