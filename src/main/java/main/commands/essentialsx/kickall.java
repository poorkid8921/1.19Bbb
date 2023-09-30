package main.commands.essentialsx;

import main.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class kickall implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.isOp())
            return true;

        String b = Utils.translate(args.length == 0 ? "&6Restarting" : args[0]);
        Bukkit.getOnlinePlayers().forEach(a -> a.kickPlayer(b));
        return true;
    }
}
