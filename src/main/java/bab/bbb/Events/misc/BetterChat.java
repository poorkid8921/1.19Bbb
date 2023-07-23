package bab.bbb.Events.misc;

import bab.bbb.utils.DiscordWebhook;
import bab.bbb.utils.Utils;
import io.papermc.paper.event.player.AsyncChatEvent;
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

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

import static bab.bbb.utils.Utils.*;

@SuppressWarnings("deprecation")
public class BetterChat implements Listener {
    Utils cm = new Utils();

    public final HashSet<String> whitelistedcomms = new HashSet<>(Arrays.asList(
            "anarchy", "help", "d", "discord", "home", "sethome", "delhome", "reply", "r", "msg", "tell", "whisper", "tpa", "tpahere", "tpaccept", "tpno", "tpn", "tpy", "tpdeny", "tpyes", "nick", "nickname", "reg", "secure", "suicide", "kill", "ignore"
    ));

    @EventHandler
    private void CmdProcess(PlayerCommandPreprocessEvent e) {
        if (e.getPlayer().isOp())
            return;

        String message = e.getMessage();
        String commandLabel = Utils.getCommandLabel(message).toLowerCase();
        String fullCommand = message.substring(commandLabel.length() + 1);
        fullCommand = "/" + commandLabel + fullCommand;
        e.setMessage(fullCommand);
    }

    @EventHandler
    private void onPlayerChat(AsyncPlayerChatEvent e) {
        e.setCancelled(true);

        if (cm.checkCooldown(e.getPlayer()))
            cm.setCooldown(e.getPlayer());
        else
            return;

        String msg = e.getMessage();

        if (msg.startsWith(">"))
            msg = translate("&a") + msg;
        else if (msg.startsWith("<"))
            msg = translate("&c") + msg;
        else if (msg.startsWith("$"))
            msg = translate("&b") + msg;
        else if (msg.startsWith("~"))
            msg = translate("&d") + msg;

        String translatedmsg = Utils.translate("&7" + e.getPlayer().getName() + " &r» ") + msg;

        String removed = Utils.removeColorCodes(translatedmsg);
        if (removed.length() > 256)
            return;

        Utils.message(e.getPlayer(), translatedmsg);
        Bukkit.getLogger().info(removed);

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            String avturl = "https://mc-heads.net/avatar/hausemaster/100";
            DiscordWebhook webhook = new DiscordWebhook("https://discord.com/api/webhooks/1127000458519658516/oJdlS8_drTx5reJDseTJ17Sk0lzJ-ElKgiEo10-Qy5tm9Jp0iufOE5BEc8Ds-DnLlzCC");
            webhook.setAvatarUrl(avturl);
            webhook.setUsername("mc.aesthetic.red");
            webhook.setContent(removed.replace("@", "@ "));
            try {
                webhook.execute();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
    }
}
