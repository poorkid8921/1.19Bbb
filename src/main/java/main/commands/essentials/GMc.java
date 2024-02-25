package main.commands.essentials;

import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static main.utils.Initializer.playerData;

public class GMc implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (playerData.get(sender.getName()).getRank() > 8) {
            ((Player) sender).setGameMode(GameMode.CREATIVE);
            sender.sendMessage("§7Your gamemode has been changed to creative.");
        }

        return true;
    }
}
