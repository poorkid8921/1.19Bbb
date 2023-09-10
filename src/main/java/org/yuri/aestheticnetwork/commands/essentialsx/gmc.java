package org.yuri.aestheticnetwork.commands.essentialsx;

import io.papermc.lib.PaperLib;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.yuri.aestheticnetwork.utils.Initializer;
import org.yuri.aestheticnetwork.utils.Location;

import static org.yuri.aestheticnetwork.utils.Utils.translateo;

public class gmc implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.isOp())
            return true;

        ((Player) sender).setGameMode(GameMode.CREATIVE);
        return true;
    }
}
