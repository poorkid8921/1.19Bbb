package main.commands;

import com.google.common.collect.ImmutableList;
import main.utils.Utils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static main.utils.Constants.MAIN_COLOR;

public class ItemRename implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage("§7You must specify an item name.");
            return true;
        }
        Player p = (Player) sender;
        ItemStack hand = p.getItemInHand();
        if (hand.getType() == Material.AIR) {
            sender.sendMessage("§7You must hold an item in order to use this command.");
            return true;
        }

        StringBuilder msgargs = new StringBuilder();
        for (String arg : args) msgargs.append(arg).append(" ");
        String t = Utils.translate(String.valueOf(msgargs));
        ItemMeta im = hand.getItemMeta();
        im.setDisplayName(t);
        hand.setItemMeta(im);
        p.setItemInHand(hand);
        sender.sendMessage("§7Successfully named your item " + MAIN_COLOR + t);
        return true;
    }

    @Override
    public java.util.List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return ImmutableList.of();
    }
}
