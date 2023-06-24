package bab.bbb.Events.misc;

import bab.bbb.utils.RainbowText;
import bab.bbb.utils.Utils;
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

import java.util.Arrays;
import java.util.HashSet;

import static bab.bbb.utils.Utils.translate;

@SuppressWarnings("deprecation")
public class BetterChat implements Listener {
    Utils cm = new Utils();
    public final HashSet<String> linkRegexes = new HashSet<>(Arrays.asList(
            "(https?://(?:www\\.|(?!www))[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|www\\.[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|https?://(?:www\\.|(?!www))[a-zA-Z0-9]+\\.[^\\s]{2,}|www\\.[a-zA-Z0-9]+\\.[^\\s]{2,})",
            "[-a-zA-Z0-9@:%._+~#=]{1,256}\\.[a-zA-Z()]{1,6}\\b([-a-zA-Z()@:%_+.~#?&/=]*)"
    ));
    public final HashSet<String> whitelistedcomms = new HashSet<>(Arrays.asList(
            "anarchy", "help", "d", "discord", "home", "sethome", "delhome", "reply", "r", "msg", "tell", "whisper", "tpa", "tpahere", "tpaccept", "tpno", "tpn", "tpy", "tpdeny", "tpyes", "nick", "nickname", "reg", "secure", "suicide", "kill", "ignore"
    ));

    @EventHandler
    private void CmdProcess(PlayerCommandPreprocessEvent e) {
        if (e.getPlayer().isOp())
            return;

        String message = e.getMessage();
        String commandLabel = Utils.getCommandLabel(message).toLowerCase();
        String fullCommand = message.substring(commandLabel.length()+1);
        fullCommand = "/"+commandLabel+fullCommand;
        e.setMessage(fullCommand);

        if (!whitelistedcomms.contains(commandLabel)) {
            e.setCancelled(true);
            Utils.errormsgs(e.getPlayer(),21, "");
        }
    }

    @EventHandler
    private void onPlayerChat(AsyncPlayerChatEvent e) {
        e.setCancelled(true);

        if (cm.checkCooldown(e.getPlayer()))
            cm.setCooldown(e.getPlayer());
        else {
            Utils.errormsgs(e.getPlayer(),23, "");
            return;
        }

        String translatedmsg = Utils.translate("#d6a7eb" +  e.getPlayer().getName() + " » &r") + e.getMessage();

        if (Utils.removeColorCodes(translatedmsg).length() > 200) {
            Utils.errormsgs(e.getPlayer(),24, "");
            return;
        }

        Utils.message(e.getPlayer(), translatedmsg);
        Bukkit.getLogger().info(e.getPlayer().getName() + " > " + translatedmsg);
    }
}