package main.commands.essentials;

import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

import static main.utils.Initializer.playerData;

public class GMs implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (playerData.get(sender.getName()).getRank() > 6) {
            ((Player) sender).setGameMode(GameMode.SURVIVAL);
            sender.sendMessage("§7Your gamemode has been changed to survival.");
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}
