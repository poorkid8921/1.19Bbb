package org.yuri.aestheticnetwork.commands.essentialsx;

import io.papermc.lib.PaperLib;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.yuri.aestheticnetwork.utils.Initializer;
import org.yuri.aestheticnetwork.utils.Location;

import static org.yuri.aestheticnetwork.utils.Utils.translateo;

public class back implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Location p = Initializer.users.get(sender.getName()).getBack();
        if (p == null) {
            sender.sendMessage(translateo("&7You got no back location."));
            return true;
        }

        PaperLib.teleportAsync((Player) sender, p.to()).thenAccept(r ->
                sender.sendMessage(translateo("&7Teleported you to your previous location!")));
        return true;
    }
}
