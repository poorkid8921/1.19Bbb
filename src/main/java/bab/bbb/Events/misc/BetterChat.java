package bab.bbb.Events.misc;

import bab.bbb.Bbb;
import bab.bbb.utils.Methods;
import bab.bbb.utils.RainbowText;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Arrays;
import java.util.Base64;

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

        String msg = Methods.placeholders(e.getMessage());

        if (Methods.removeColorCodes(msg).length() > 256) {
            Methods.errormsg(e.getPlayer(), "your message is too big");
            return;
        }

        RainbowText rainbow = new RainbowText(msg);

        if (e.getMessage().contains("[rainbow]"))
            msg = rainbow.getText();

        if (e.getMessage().contains("[unicode]"))
            msg = Methods.unicode(msg);

        if (e.getMessage().contains("[base64]"))
            msg = Base64.getEncoder().encodeToString(msg.getBytes());

        if (e.getMessage().startsWith(">"))
            msg = "&2" + msg.replace(">","");

        if (e.getMessage().startsWith("<"))
            msg = "&4" + msg.replace("<","");

        if (e.getMessage().startsWith("||") && e.getMessage().endsWith("||"))
        {
            Methods.messagecomponent(e.getPlayer(), new BaseComponent[]{Methods.spoiler(msg)});
            Bukkit.getLogger().info(e.getPlayer().getDisplayName() + " > " + msg);
            return;
        }

        Methods.message(e.getPlayer(), msg);
        Bukkit.getLogger().info(e.getPlayer().getDisplayName() + " > " + msg);
    }
}