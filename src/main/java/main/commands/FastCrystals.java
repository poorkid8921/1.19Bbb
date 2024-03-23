package main.commands;

import main.utils.Instances.CustomPlayerDataHolder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

import static main.utils.Initializer.playerData;

public class FastCrystals implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String name = sender.getName();
        CustomPlayerDataHolder D0 = playerData.get(name);
        boolean newValue = !D0.isFastCrystals();
        D0.setFastCrystals(newValue);
        sender.sendMessage(newValue ? "§7Successfully enabled the crystal optimizer." : "§7Successfully disabled the crystal optimizer.");
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}