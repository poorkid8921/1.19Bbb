package org.yuri.aestheticnetwork.commands;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.data.DataMutateResult;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yuri.aestheticnetwork.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class rank implements CommandExecutor, TabCompleter {
    LuckPerms lp;
ArrayList<String> tiers = new ArrayList<>(List.of("HT1",
        "HT2",
        "HT3",
        "HT4",
        "HT5",
        "LT1",
        "LT2",
        "LT3",
        "LT4",
        "LT5"));

    public rank(LuckPerms lped) {
        lp = lped;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("tester.y")) {
            sender.sendMessage(Utils.translate("&7You must be a tester in order to use this command."));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(Utils.translate("&7You must specify who you want to give a rank."));
            return true;
        } else if (args.length == 1) {
            sender.sendMessage(Utils.translate("&7You must specify a rank to give."));
            return true;
        }

        Player p = Bukkit.getPlayer(args[1]);
        if (p == null) {
            sender.sendMessage(Utils.translate("&7You must specify an online player."));
            return true;
        }

        if (!tiers.contains(args[0])) {
            sender.sendMessage(Utils.translate("&7You can't give other ranks than the one in tab completion: " + tiers));
            return true;
        }

        sender.sendMessage(Utils.translate("&7Succesfully gave the rank &c" + args[0] + " &&to &c" + p.getDisplayName()));

        User user = lp.getUserManager().getUser(p.getUniqueId());
        if (p.hasPermission("group." + args[0])) {
            InheritanceNode node = InheritanceNode.builder(args[0]).value(true).build();
            user.data().remove(node);
            return true;
        }
        InheritanceNode node = InheritanceNode.builder(args[0]).value(true).build();
        user.data().add(node);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return tiers;
    }
}
