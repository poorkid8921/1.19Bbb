package org.yuri.aestheticnetwork.commands.essentialsx;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static org.yuri.aestheticnetwork.utils.Utils.translate;

public class kickall implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.isOp())
            return true;

        String b = translate(args.length == 0 ? "&6Restarting" : args[0]);
        Bukkit.getOnlinePlayers().stream().forEach(a -> a.kickPlayer(b));
        return true;
    }
}
