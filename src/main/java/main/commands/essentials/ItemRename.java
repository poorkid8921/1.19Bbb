package main.commands.essentials;

import main.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;

import static main.utils.Initializer.SECOND_COLOR;

public class ItemRename implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§7You must specify an item name!");
            return true;
        }
        final Player player = (Player) sender;
        final ItemStack hand = player.getItemInHand();
        if (hand.getType() == Material.AIR) {
            sender.sendMessage("§7You must hold an item in order to use this command!");
            return true;
        }
        final StringBuilder msg = new StringBuilder();
        for (final String arg : args) msg.append(arg).append(" ");
        final String translatedMSG = Utils.translate(msg.toString());
        if (ChatColor.stripColor(translatedMSG).length() > 32) {
            sender.sendMessage("§7Your item name is too long!");
            return true;
        }
        final ItemMeta meta = hand.getItemMeta();
        meta.setDisplayName(translatedMSG);
        hand.setItemMeta(meta);
        player.setItemInHand(hand);
        sender.sendMessage("§7Successfully named your item " + SECOND_COLOR + translatedMSG);
        return true;
    }

    @Override
    public java.util.List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}
