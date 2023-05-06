package bab.bbb.Events.misc;

import bab.bbb.Bbb;
import bab.bbb.utils.Methods;
import bab.bbb.utils.RainbowText;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class BetterChat implements Listener {
    Methods cm = new Methods();

    @EventHandler
    private void CmdProcess(PlayerCommandPreprocessEvent e) {
        if (cm.checkCooldown(e.getPlayer()))
            cm.setCooldown(e.getPlayer());
        else {
            e.setCancelled(true);
            Methods.errormsg(e.getPlayer(), "you're sending messages too fast");
        }
    }

    @EventHandler
    private void onPlayerChat(AsyncPlayerChatEvent e) {
        e.setCancelled(true);

        if (cm.checkCooldown(e.getPlayer()))
            cm.setCooldown(e.getPlayer());
        else {
            Methods.errormsg(e.getPlayer(), "you're sending messages too fast");
            return;
        }

        if (e.getMessage().length() > 256) {
            Methods.errormsg(e.getPlayer(), "your message is too big");
            return;
        }

        String msg = Methods.placeholders(e.getMessage());
        RainbowText rainbow = new RainbowText(msg);
        String msgunicode = Methods.unicode(msg);

        if (e.getMessage().startsWith("||") && e.getMessage().endsWith("||"))
            Methods.spoiler(e.getPlayer(), msg);
        else if (e.getMessage().startsWith("[gay]") && e.getMessage().endsWith("[/gay]")) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                String b = Bbb.getInstance().getCustomConfig().getString("otherdata." + p.getUniqueId() + ".ignorelist");
                if (b != null && b.contains(e.getPlayer().getName()))
                    return;
                p.sendMessage(Methods.parseText(e.getPlayer(), "&7<" + e.getPlayer().getDisplayName() + "&7> " + rainbow.getText()));
            }
        } else if (e.getMessage().startsWith("[unicode]") && e.getMessage().endsWith("[/unicode]"))
            Methods.message(e.getPlayer(), msgunicode);
        else
            Methods.message(e.getPlayer(), msg);
    }
}