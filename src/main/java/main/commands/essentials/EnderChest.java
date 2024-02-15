package main.commands.essentials;

import com.google.common.collect.ImmutableList;
import main.utils.Initializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

public class EnderChest implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p = (Player) sender;
        String group = Initializer.lp.getPlayerAdapter(Player.class).getUser(p).getPrimaryGroup();
        if (!Initializer.upperHierarchyRanks.contains(group)) {
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
