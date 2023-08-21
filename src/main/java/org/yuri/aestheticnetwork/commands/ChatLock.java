package org.yuri.aestheticnetwork.commands;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.yuri.aestheticnetwork.AestheticNetwork;
import org.yuri.aestheticnetwork.utils.Initializer;
import org.yuri.aestheticnetwork.utils.Utils;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.yuri.aestheticnetwork.utils.Initializer.chatlock;

public class ChatLock implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        /*isStaff(((Player) sender).getUniqueId()).thenAcceptAsync(result -> {
            if (!result)
                sender.sendMessage(Utils.translate("&7You must be an operator to use this command."));
            else
            {
                AestheticNetwork.chatlock = !AestheticNetwork.chatlock;
                if (AestheticNetwork.chatlock)
                    sender.sendMessage(Utils.translate("&7Successfully locked the chat."));
                else
                    sender.sendMessage(Utils.translate("&7Successfully un-locked the chat."));
            }
        });*/
        if (sender.hasPermission("has.staff"))
        {
            chatlock = !chatlock;
            if (chatlock)
                sender.sendMessage(Utils.translate("&7Successfully locked the chat."));
            else
                sender.sendMessage(Utils.translate("&7Successfully un-locked the chat."));
        }
        else
            sender.sendMessage(Utils.translate("&7You must be an Operator to use this command."));

        return true;
    }
}
