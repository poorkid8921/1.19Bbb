package main.commands;

import main.managers.instances.PlayerDataHolder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

import static main.utils.Initializer.playerData;

public class FastCrystals implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        final PlayerDataHolder D0 = playerData.get(sender.getName());
        final boolean newValue = !D0.isFastCrystals();
        D0.setFastCrystals(newValue);
        sender.sendMessage(newValue ? "§7Successfully enabled the crystal optimizer." : "§7Successfully disabled the crystal optimizer.");
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}