package main.commands.essentials;

import com.google.common.collect.ImmutableList;
import main.utils.Initializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import static main.utils.Initializer.playerData;

public class EnderChest implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p = (Player) sender;
        if (playerData.get(sender.getName()).getRank() < 4) {
            sender.sendMessage("§7You must be ranked in order to use this command!");
            return true;
        }
        p.openInventory(p.getEnderChest());
        return true;
    }

    @Override
    public java.util.List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return ImmutableList.of();
    }
}
