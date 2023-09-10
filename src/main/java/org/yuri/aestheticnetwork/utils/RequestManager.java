package org.yuri.aestheticnetwork.utils;

import io.papermc.lib.PaperLib;
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
import org.yuri.aestheticnetwork.AestheticNetwork;
import org.yuri.aestheticnetwork.commands.tpa.TpaRequest;

import java.util.ArrayList;
import java.util.UUID;

import static org.yuri.aestheticnetwork.utils.Utils.translate;
import static org.yuri.aestheticnetwork.utils.Utils.translateo;

@SuppressWarnings("deprecation")
public class RequestManager {
    public static final ArrayList<TpaRequest> tpa = new ArrayList<>();

    public static void tpaccept(TpaRequest request, Player user) {
        Player tempuser;
        Player temprecipient;

        if (request.getType() == Type.TPA) {
            tempuser = request.getSender();
            temprecipient = user;
            temprecipient.sendMessage(translate("&7You have accepted #fc282f" + tempuser.getDisplayName() + "&7's teleport request"));
            temprecipient.sendMessage(translateo("&7Teleporting..."));
            tempuser.sendMessage(translate("#fc282f" + tempuser.getDisplayName() + " &7has accepted your teleport request"));
        } else {
            tempuser = user;
            temprecipient = request.getSender();
            tempuser.sendMessage(translate("&7You have accepted #fc282f" + temprecipient.getDisplayName() + "&7's teleport request"));
            tempuser.sendMessage(translateo("&7Teleporting..."));
            temprecipient.sendMessage(translate("#fc282f" + tempuser.getDisplayName() + " &7has accepted your teleport request"));
        }

        PaperLib.teleportAsync(tempuser, temprecipient.getLocation()).thenAccept(reason -> tpa.remove(request));
    }

    public static TpaRequest getTPArequest(String user) {
        for (TpaRequest r : tpa) {
            if (r.getReciever().getName().equals(user) ||
                    r.getSender().getName().equals(user))
                return r;
        }

        return null;
    }

    public static TpaRequest getTPArequest(String user, String lookup) {
        for (TpaRequest r : tpa) {
            if ((r.getReciever().getName().equals(user) ||
                    r.getSender().getName().equals(user)) &&
                    (r.getReciever().getName().equals(lookup) ||
                            r.getSender().getName().equals(lookup)))
                return r;
        }

        return null;
    }


    public static void removeTPArequest(TpaRequest user) {
        tpa.remove(user);
    }

    public static void addTPArequest(Player sender, Player receiver, Type type) {
        TpaRequest tpaRequest = new TpaRequest(sender,
                receiver,
                type);
        tpa.add(tpaRequest);

        Bukkit.getLogger().warning(sender.getDisplayName());

        String clean = ChatColor.stripColor(sender.getDisplayName());
        int c = clean.indexOf(" ");

        TextComponent tc = new TextComponent(translateo(" &7has requested to teleport to you. "));

        TextComponent a = new TextComponent(translateo("&7[&a✔&7]"));
        a.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(
                translateo("&7Click to accept the teleportation request"))));
        a.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept"));

        TextComponent b = new TextComponent(translateo("&7[&cx&7]"));
        b.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(
                translateo("&7Click to deny the teleportation request"))));
        b.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpdeny"));
        receiver.playSound(receiver.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.f, 1.f);
        sender.sendMessage(translate("&7Request sent to #fc282f" +
                receiver.getDisplayName()));

        if (type == Type.TPAHERE)
            tc.setText(translate(" &7has requested that you teleport to them. "));

        new BukkitRunnable() {
            @Override
            public void run() {
                tpa.remove(tpaRequest);
            }
        }.runTaskLater(AestheticNetwork.getInstance(), 120 * 20);

        TextComponent space = new TextComponent("  ");
        if (c != -1) {
            String color = clean.substring(0, 7);
            String noHex = clean.replace(color, "");
            String rank = noHex.substring(0, c);
            String realName = noHex.replace(rank + " ", "");
            TextComponent nametc = new TextComponent(realName);
            nametc.setColor(ChatColor.of(color));
            TextComponent ranktc = new TextComponent(rank);
            receiver.sendMessage(ranktc, nametc, tc, a, space, b);
        } else
            receiver.sendMessage(new ComponentBuilder(sender.getDisplayName())
                    .color(ChatColor.of("#fc282f"))
                    .create()[0], tc, a, space, b);
    }
}
