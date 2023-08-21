package org.yuri.aestheticnetwork.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.yuri.aestheticnetwork.inventories.ShopInventory;

public class Shop implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, Command command, @NotNull String s, String[] strings) {
        new ShopInventory(((Player) commandSender).getPlayer()).open();
        return false;
    }
}