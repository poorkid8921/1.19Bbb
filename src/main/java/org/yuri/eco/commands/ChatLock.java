package org.yuri.eco.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.yuri.eco.utils.Utils;

import static org.yuri.eco.utils.Initializer.chatlock;

public class ChatLock implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        /*isStaff(((Player) sender).getUniqueId()).thenAcceptAsync(result -> {
            if (!result)
                sender.sendMessage(Utils.translateo("&7You must be an operator to use this command."));
            else
            {
                AestheticNetwork.chatlock = !AestheticNetwork.chatlock;
                if (AestheticNetwork.chatlock)
                    sender.sendMessage(Utils.translateo("&7Successfully locked the chat."));
                else
                    sender.sendMessage(Utils.translateo("&7Successfully un-locked the chat."));
            }
        });*/
        if (sender.hasPermission("has.staff"))
        {
            chatlock = !chatlock;
            if (chatlock)
                sender.sendMessage(Utils.translateo("&7Successfully locked the chat."));
            else
                sender.sendMessage(Utils.translateo("&7Successfully un-locked the chat."));
        }
        else
            sender.sendMessage(Utils.translateo("&7You must be an Operator to use this command."));

        return true;
    }
}
