package main.commands.essentials;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import static main.utils.Initializer.playerData;

public class SetRank implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.isOp()) {
            return true;
        } else if (args.length == 0) {
            sender.sendMessage("You must specify a player you want to rank!");
            return true;
        } else if (args.length == 1) {
            sender.sendMessage("You must specify a rank you want to give to the desired player!");
            return true;
        }

        playerData.get(args[0]).setRank(Integer.parseInt(args[1]));
        return true;
    }
}
