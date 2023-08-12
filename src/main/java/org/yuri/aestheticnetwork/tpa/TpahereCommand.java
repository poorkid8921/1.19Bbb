package org.yuri.aestheticnetwork.tpa;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.yuri.aestheticnetwork.AestheticNetwork;
import org.yuri.aestheticnetwork.Type;
import org.yuri.aestheticnetwork.Utils;

import static org.yuri.aestheticnetwork.Utils.*;

@SuppressWarnings("deprecation")
public class TpahereCommand implements CommandExecutor {
    public TpahereCommand() {
    }

    @Override
    public boolean onCommand(final @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player user))
            return true;

        if (args.length < 1) {
            user.sendMessage(translate("&7You must specify who you want to teleport to."));
            return true;
        }

        Player recipient = Bukkit.getPlayer(args[0]);

        if (recipient == null) {
            user.sendMessage(translate("&7You can't send teleport requests to offline people!"));
            return true;
        }

        if (recipient.getName().equalsIgnoreCase(sender.getName())) {
            user.sendMessage(translate("&7You can't teleport to yourself!"));
            return true;
        }

        TpaRequest tpr = getRequest(recipient);

        if (tpr != null && tpr.getSender().equals(sender))
        {
            user.sendMessage(translate("&7You already have an ongoing request to this player."));
            return true;
        }

        if (Utils.manager().get(
                "r." + recipient.getUniqueId() + ".t") != null) {
            user.sendMessage(translate("&7You can't request this player since they locked their tpa requests!"));
            return true;
        }

        requests.remove(tpr);
        Utils.addRequest(user, recipient, Type.TPAHERE, true);
        return true;
    }
}