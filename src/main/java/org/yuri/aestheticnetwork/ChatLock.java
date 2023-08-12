package org.yuri.aestheticnetwork;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ChatLock implements CommandExecutor {
    LuckPerms lp;
    Permission perms;

    public ChatLock(LuckPerms lped, Permission perm) {
        lp = lped;
        perms = perm;
    }

    public CompletableFuture<Boolean> isStaff(UUID who) {
        return lp.getUserManager().loadUser(who)
                .thenApplyAsync(user -> {
                    Collection<Group> inheritedGroups = user.getInheritedGroups(user.getQueryOptions());
                    return inheritedGroups.stream().anyMatch(g -> g.getName().equals("staff"));
                });
    }

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
            AestheticNetwork.chatlock = !AestheticNetwork.chatlock;
            if (AestheticNetwork.chatlock)
                sender.sendMessage(Utils.translate("&7Successfully locked the chat."));
            else
                sender.sendMessage(Utils.translate("&7Successfully un-locked the chat."));
        }
        else
            sender.sendMessage(Utils.translate("&7You must be an Operator to use this command."));

        return true;
    }
}
