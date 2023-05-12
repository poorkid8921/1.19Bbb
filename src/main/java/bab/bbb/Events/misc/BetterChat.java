package bab.bbb.Events.misc;

import bab.bbb.Bbb;
import bab.bbb.utils.Utils;
import bab.bbb.utils.RainbowText;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import static bab.bbb.utils.Utils.parseText;

public class BetterChat implements Listener {
    Utils cm = new Utils();

    @EventHandler
    private void CmdProcess(PlayerCommandPreprocessEvent e) {
        if (e.getPlayer().getName().equals("Gr1f"))
            return;

        if (cm.checkCooldown(e.getPlayer()))
            cm.setCooldown(e.getPlayer());
        else {
            e.setCancelled(true);
            Utils.errormsg(e.getPlayer(), "you're executing commands too fast");
        }
    }

    @EventHandler
    private void onPlayerChat(AsyncPlayerChatEvent e) {
        e.setCancelled(true);

        if (cm.checkCooldown(e.getPlayer()))
            cm.setCooldown(e.getPlayer());
        else {
            Utils.errormsg(e.getPlayer(), "you're sending messages too fast");
            return;
        }

        String msg = Utils.parseText(e.getPlayer(), e.getMessage());

        if (Utils.removeColorCodes(msg).length() > 256) {
            Utils.errormsg(e.getPlayer(), "your message is too big");
            return;
        }

        if (e.getMessage().contains("[rainbow]"))
            msg = new RainbowText(msg).getText();

        if (e.getMessage().contains("[unicode]"))
            msg = Utils.unicode(msg.replace("[unicode]", ""));

        //if (e.getMessage().contains("[base64]"))
        //    msg = Base64.getEncoder().encodeToString(msg.replace("[base64]", "").getBytes());

        if (e.getMessage().startsWith(">"))
            msg = "&2" + msg.replace(">", "");

        if (e.getMessage().startsWith("<"))
            msg = "&4" + msg.replace("<", "");

        if (e.getMessage().startsWith("||") && e.getMessage().endsWith("||")) {
            TextComponent spoiler = new TextComponent(parseText("&7<" + e.getPlayer().getDisplayName() + "&7> " + "█".repeat(Math.max(1, msg.length() / 3 - 2))));
            Text HoverText = new Text(parseText(e.getPlayer(), msg.replace("||", "")));

            spoiler.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, HoverText));

            for (Player p : Bukkit.getOnlinePlayers()) {
                String b = Bbb.getInstance().getCustomConfig().getString("otherdata." + p.getUniqueId() + ".ignorelist");
                if (b != null && b.contains(e.getPlayer().getName()))
                    continue;
                p.sendMessage(new BaseComponent[]{spoiler});
            }
            Bukkit.getLogger().info(e.getPlayer().getDisplayName() + " > " + msg);
            return;
        }

        Utils.message(e.getPlayer(), msg);
        Bukkit.getLogger().info(e.getPlayer().getDisplayName() + " > " + msg);
    }
}