package bab.bbb.tpa;

import bab.bbb.Bbb;
import bab.bbb.Events.misc.MiscEvents;
import bab.bbb.utils.Methods;
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

public class TpahereCommand implements CommandExecutor {
    private final Bbb plugin;

    public TpahereCommand(Bbb plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(final @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }

        if (args.length < 1) {
            Methods.errormsg(player, "the arguments are invalid");
            return true;
        }

        Player recipient = Bukkit.getPlayer(args[0]);

        if (recipient == null) {
            Methods.errormsg(player, "player &e" + args[0] + " &7couldn't be found");
            return true;
        }

        if (recipient.getName().equalsIgnoreCase(sender.getName())) {
            Methods.errormsg(player, "you can't teleport to yourself");
            return true;
        }

        if (plugin.getRequest(recipient) != null) {
            Methods.errormsg(player, "player &e" + recipient.getDisplayName() + " &7already has an active request");
            return true;
        }

        if (MiscEvents.antilog.contains(player.getName())) {
            Methods.errormsg(player, "you can't send tpa requests whilst being combat tagged");
            return true;
        }

        plugin.addRequest((Player) sender, recipient, Type.TPAHERE);
        recipient.playSound(recipient.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.f, 1.f);
        Methods.tpmsg(recipient, player, 4);
        TextComponent accept = new TextComponent(Methods.parseText("&7[&2✔&7] &2ACCEPT"));
        Text acceptHoverText = new Text("Click to accept the teleport request");
        accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, acceptHoverText));
        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept"));
        TextComponent deny = new TextComponent(Methods.parseText("&7[&c✖&7] &cDENY"));
        Text denyHoverText = new Text("Click to deny the teleport request");
        deny.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, denyHoverText));
        deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpdeny"));

        TextComponent bemptyspace = new TextComponent("       ");
        recipient.sendMessage(bemptyspace, accept, bemptyspace, deny);
        Methods.tpmsg(player, recipient, 1);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (plugin.getRequest(recipient) != null) {
                    plugin.removeRequest(recipient);
                    Methods.tpmsg(player, recipient, 2);
                }
            }
        }.runTaskLater(plugin, 30 * 20);
        return true;
    }
}