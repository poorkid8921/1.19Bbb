package org.yuri.aestheticnetwork.commands.duel;

import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static org.yuri.aestheticnetwork.utils.Utils.translate;

public class Event implements CommandExecutor {
    public static ArrayList<UUID> valid = new ArrayList<>();
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player p = ((Player) sender);
        if (!valid.contains(p.getUniqueId()))
            return true;

        String arg = args[4];

        TextComponent hi = new TextComponent(translate("&7Winner: &c" + (arg.equals("y") ? "Nobody" : args[5])));
        Text hit = new Text("&7Hearts remaining: &c" + Integer.parseInt(args[6]) +
                "\n&7Kills: &c" + Integer.parseInt(args[7]) +
                "\n&7Deaths: &c" + Integer.parseInt(args[8]));
        if (arg.equals("n"))
            hi.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hit));

        sender.sendMessage(translate("&7Round results"));
        sender.sendMessage(translate("&7------------------------"));
        sender.sendMessage(hi);
        sender.sendMessage(translate("&7Your team: " + (Boolean.parseBoolean(args[0]) ?
                "&9Blue" :
                "&cRed")));
        sender.sendMessage(translate("&7Total score: &c" + Integer.parseInt(args[1]) + " &7- &9" +
                Integer.parseInt(args[2])));
        sender.sendMessage(translate("&7Duration: " + args[3]));
        sender.sendMessage(translate("&7------------------------"));
        valid.remove(p.getUniqueId());
        return true;
    }
}
