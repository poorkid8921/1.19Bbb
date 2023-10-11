package main.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static main.utils.Languages.D_LINK;
import static main.utils.Languages.D_USING;

@SuppressWarnings("deprecation")
public class Discord implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player pp)
            pp.sendMessage(D_USING, D_LINK);

        return true;
    }
}