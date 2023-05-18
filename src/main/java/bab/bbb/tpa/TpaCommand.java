package bab.bbb.tpa;

import bab.bbb.Bbb;
import bab.bbb.utils.Type;
import bab.bbb.utils.Utils;
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

import static bab.bbb.utils.Utils.*;

@SuppressWarnings("deprecation")
public class TpaCommand implements CommandExecutor {
    public TpaCommand() {
    }

    @Override
    public boolean onCommand(final @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player user))
            return true;

        if (args.length < 1) {
            errormsgs(user, 1, "");
            return true;
        }

        Player recipient = Bukkit.getPlayer(args[0]);

        if (recipient == null) {
            errormsgs(user, 2, args[0]);
            return true;
        }

        if (recipient.getName().equalsIgnoreCase(sender.getName())) {
            tpmsg(user, recipient, 14);
            return true;
        }

        if (Utils.getRequest(recipient) != null) {
            tpmsg(user, recipient, 13);
            return true;
        }

        if (combattag.contains(user.getName())) {
            tpmsg(user, recipient, 11);
            return true;
        }

        if (Utils.getRequest(user) != null)
            Utils.removeRequest(user);

        Utils.addRequest(user, recipient, Type.TPA);
        recipient.playSound(recipient.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.f, 1.f);
        tpmsg(recipient, user, 3);
        TextComponent accept = new TextComponent(translate("&7[&2✔&7] &2ACCEPT"));
        Text acceptHoverText = new Text("Click to accept the teleport request");
        accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, acceptHoverText));
        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept"));
        TextComponent deny = new TextComponent(translate("&7[&c✖&7] &cDENY"));
        Text denyHoverText = new Text("Click to deny the teleport request");
        deny.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, denyHoverText));
        deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpdeny"));

        TextComponent bemptyspace = new TextComponent("       ");
        recipient.sendMessage(bemptyspace, accept, bemptyspace, deny);
        tpmsg(user, recipient, 1);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (Utils.getRequest(recipient) != null) {
                    Utils.removeRequest(recipient);
                    tpmsg(user, recipient, 2);
                }
            }
        }.runTaskLater(Bbb.getInstance(), 30 * 20);
        return true;
    }
}