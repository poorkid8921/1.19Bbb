package bab.bbb.tpa;

import bab.bbb.Bbb;
import bab.bbb.Events.misc.MiscEvents;
import bab.bbb.utils.Utils;
import bab.bbb.utils.Type;
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

public class TpaCommand implements CommandExecutor {
    private final Bbb plugin;

    public TpaCommand(final Bbb plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(final CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player user))
            return true;

        if (args.length < 1) {
            Utils.errormsg(user, "the arguments are invalid");
            return true;
        }

        Player recipient = Bukkit.getPlayer(args[0]);

        if (recipient == null) {
            Utils.errormsg(user, "player &e" + args[0] + " &7couldn't be found");
            return true;
        }

        if (recipient.getName().equalsIgnoreCase(sender.getName())) {
            Utils.errormsg(user, "you can't teleport to yourself");
            return true;
        }

        if (plugin.getRequest(recipient) != null) {
            Utils.errormsg(user, "player &e" + recipient.getDisplayName() + " &7already has an active request");
            return true;
        }

        if (MiscEvents.combattag.contains(user.getName())) {
            Utils.errormsg(user, "you can't send tpa requests whilst being combat tagged");
            return true;
        }

        if (plugin.getRequest(user) != null)
            plugin.removeRequest(user);

        plugin.addRequest(user, recipient, Type.TPA);
        recipient.playSound(recipient.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.f, 1.f);
        Utils.tpmsg(recipient, user, 3);
        TextComponent accept = new TextComponent(Utils.parseText("&7[&2✔&7] &2ACCEPT"));
        Text acceptHoverText = new Text("Click to accept the teleport request");
        accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, acceptHoverText));
        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept"));
        TextComponent deny = new TextComponent(Utils.parseText("&7[&c✖&7] &cDENY"));
        Text denyHoverText = new Text("Click to deny the teleport request");
        deny.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, denyHoverText));
        deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpdeny"));

        TextComponent bemptyspace = new TextComponent("       ");
        recipient.sendMessage(bemptyspace, accept, bemptyspace, deny);
        Utils.tpmsg(user, recipient, 1);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (plugin.getRequest(recipient) != null) {
                    plugin.removeRequest(recipient);
                    Utils.tpmsg(user, recipient, 2);
                }
            }
        }.runTaskLater(plugin, 30 * 20);
        return true;
    }
}