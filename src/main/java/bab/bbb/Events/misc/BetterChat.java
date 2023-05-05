package bab.bbb.Events.misc;

import bab.bbb.Bbb;
import bab.bbb.utils.Cooldown;
import bab.bbb.utils.Methods;
import bab.bbb.utils.RainbowText;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Content;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class BetterChat implements Listener {
    Cooldown cm = new Cooldown();

    @EventHandler
    private void CmdProcess(PlayerCommandPreprocessEvent e) {
        if (cm.checkCooldown(e.getPlayer())) {
            cm.setCooldown(e.getPlayer());
        } else {
            e.setCancelled(true);
            Methods.errormsg(e.getPlayer(), "you're sending messages too fast");
        }
    }

    @EventHandler
    private void onPlayerChat(AsyncPlayerChatEvent e) {
        e.setCancelled(true);

        if (cm.checkCooldown(e.getPlayer())) {
            cm.setCooldown(e.getPlayer());
        } else {
            Methods.errormsg(e.getPlayer(), "you're sending messages too fast");
            return;
        }

        if (e.getMessage().length() > 256) {
            Methods.errormsg(e.getPlayer(), "your message is too big");
            return;
        }

        String msg = e.getMessage().replace("<3", "❤")
                .replace("[ARROW]", "➜")
                .replace("[TICK]", "✔")
                .replace("[X]", "✖")
                .replace("[STAR]", "★")
                .replace("[POINT]", "●")
                .replace("[FLOWER]", "✿")
                .replace("[XD]", "☻")
                .replace("[DANGER]", "⚠")
                .replace("[MAIL]", "✉")
                .replace("[ARROW2]", "➤")
                .replace("[ROUND_STAR]", "✰")
                .replace("[SUIT]", "♦")
                .replace("[+]", "✦")
                .replace("[CIRCLE]", "●")
                .replace("[SUN]", "✹")
                .replace("||", "")
                .replace("[gay]", "")
                .replace("[/gay]", "");

        RainbowText rainbow = new RainbowText(msg);
        if (e.getMessage().startsWith("||") && e.getMessage().endsWith("||")) {
            StringBuilder msgg = new StringBuilder();
            msgg.append("█".repeat(Math.max(1, e.getMessage().length() / 3 - 2)));

            TextComponent spoiler = new TextComponent(Methods.parseText("&7<" + e.getPlayer().getDisplayName() + ChatColor.RESET + "&7> " + ChatColor.GRAY + msgg));
            Text HoverText = new Text(Methods.parseText(e.getPlayer(), msg.replace("||", "")));

            if (msg.contains("[gay]") && msg.contains("[/gay]"))
                HoverText = new Text(Methods.parseText(e.getPlayer(), rainbow.getText()));

            spoiler.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Content[]{HoverText}));

            for (Player p : Bukkit.getOnlinePlayers()) {
                String b = Bbb.getInstance().getCustomConfig().getString("otherdata." + p.getUniqueId() + ".ignorelist");
                if (b != null && b.contains(e.getPlayer().getName()))
                    return;
                p.sendMessage(new BaseComponent[]{spoiler});
            }
        } else if (e.getMessage().startsWith("[gay]") && e.getMessage().endsWith("[/gay]")) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                String b = Bbb.getInstance().getCustomConfig().getString("otherdata." + p.getUniqueId() + ".ignorelist");
                if (b != null && b.contains(e.getPlayer().getName()))
                    return;
                p.sendMessage(Methods.parseText(e.getPlayer(), "&7<" + e.getPlayer().getDisplayName() + "&7> " + rainbow.getText()));
            }
        } else {
            e.setCancelled(true);
            for (Player p : Bukkit.getOnlinePlayers()) {
                String b = Bbb.getInstance().getCustomConfig().getString("otherdata." + p.getUniqueId() + ".ignorelist");
                if (b != null && b.contains(e.getPlayer().getName()))
                    return;
                p.sendMessage(Methods.parseText(e.getPlayer(), "&7<" + e.getPlayer().getDisplayName() + "&7> " + e.getMessage()));
            }
        }
    }
}