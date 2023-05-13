package bab.bbb.Events.misc;

import bab.bbb.Bbb;
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
    Bbb plugin = Bbb.getInstance();
    public final HashSet<String> linkRegexes = new HashSet<>(Arrays.asList(
            "(https?://(?:www\\.|(?!www))[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|www\\.[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|https?://(?:www\\.|(?!www))[a-zA-Z0-9]+\\.[^\\s]{2,}|www\\.[a-zA-Z0-9]+\\.[^\\s]{2,})",
            "[-a-zA-Z0-9@:%._+~#=]{1,256}\\.[a-zA-Z()]{1,6}\\b([-a-zA-Z()@:%_+.~#?&/=]*)"
    ));
    public final HashSet<String> whitelistedcomms = new HashSet<>(Arrays.asList(
            "help", "d", "discord", "home", "sethome", "delhome", "reply", "r", "msg", "tell", "whisper", "tpa", "tpahere", "tpaccept", "tpno", "tpn", "tpy", "tpdeny", "tpyes", "nick", "nickname", "reg", "secure", "suicide", "kill", "ignore"
    ));

    @EventHandler
    private void CmdProcess(PlayerCommandPreprocessEvent e) {
        if (e.getPlayer().getName().equals("Gr1f") || e.getPlayer().isOp())
            return;

        if (cm.checkCooldown(e.getPlayer()))
            cm.setCooldown(e.getPlayer());
        else {
            e.setCancelled(true);
            Utils.errormsg(e.getPlayer(), "You're executing commands too fast");
            return;
        }

        String message = e.getMessage();
        String commandLabel = Utils.getCommandLabel(message).toLowerCase();
        String fullCommand = message.substring(commandLabel.length()+1);
        fullCommand = "/"+commandLabel+fullCommand;
        e.setMessage(fullCommand);

        if (whitelistedcomms.contains(commandLabel)) {
            e.setCancelled(true);
            Utils.errormsg(e.getPlayer(), "&4Bad command&7.");
        }
    }

    @EventHandler
    private void onPlayerChat(AsyncPlayerChatEvent e) {
        e.setCancelled(true);

        if (cm.checkCooldown(e.getPlayer()))
            cm.setCooldown(e.getPlayer());
        else {
            Utils.errormsg(e.getPlayer(), "You're sending messages too fast");
            return;
        }

        String msg = e.getMessage();

        if (Utils.removeColorCodes(msg).length() > 256) {
            Utils.errormsg(e.getPlayer(), "Your message is too long");
            return;
        }

        for (String word : msg.split(" ")) {
            for (String regex : linkRegexes) {
                if (word.matches(regex)) {
                    Utils.errormsg(e.getPlayer(), "Links aren't allowed");
                    return;
                }
            }
        }

        if (e.getMessage().contains("[rainbow]"))
            msg = new RainbowText(msg).getText();

        if (e.getMessage().contains("[unicode]"))
            msg = Utils.unicode(msg);

        //if (e.getMessage().contains("[base64]"))
        //    msg = Base64.getEncoder().encodeToString(msg.replace("[base64]", "").getBytes());

        if (e.getMessage().startsWith(">"))
            msg = "&2" + msg.replace(">", "");

        if (e.getMessage().startsWith("<"))
            msg = "&4" + msg.replace("<", "");

        if (e.getMessage().startsWith("||") && e.getMessage().endsWith("||")) {
            TextComponent spoiler = new TextComponent(translate("&7<" + e.getPlayer().getDisplayName() + "&7> " + "█".repeat(Math.max(1, msg.length() / 3 - 2))));
            Text HoverText = new Text(translate(e.getPlayer(), msg.replace("||", "")));

            spoiler.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, HoverText));

            for (Player p : Bukkit.getOnlinePlayers()) {
                String b = Utils.getString("otherdata." + p.getUniqueId() + ".ignorelist");
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