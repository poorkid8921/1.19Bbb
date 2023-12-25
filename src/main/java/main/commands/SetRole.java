package main.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static main.utils.Initializer.playerData;
import static main.utils.Languages.*;

public class SetRole implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof CommandSender))
            return true;

        if (args.length < 2) {
            sender.sendMessage("§7Invalid args.");
            return true;
        }

        Player p = (Player) Bukkit.getOfflinePlayer(args[0]);
        int parsedInt = 0;
        try {
            parsedInt = Integer.parseInt(args[1]);
        } catch (NumberFormatException ignored) {
        }

        switch (parsedInt) {
            case 1 -> p.setDisplayName(PREFIX_MEDIA + p.getName());
            case 2 -> p.setDisplayName(PREFIX_BOOSTER + p.getName());
            case 3 -> p.setDisplayName(PREFIX_OWNER + p.getName());
            case 4 -> p.setDisplayName(PREFIX_MANAGER + p.getName());
            case 5 -> p.setDisplayName(PREFIX_ADMIN + p.getName());
            case 6 -> p.setDisplayName(PREFIX_MOD + p.getName());
            case 7 -> p.setDisplayName(PREFIX_JRMOD + p.getName());
            case 8 -> p.setDisplayName(PREFIX_HELPER + p.getName());
            case 9 -> p.setDisplayName(PREFIX_THELPER + p.getName());
        }

        playerData.get(p.getName()).setR(parsedInt);
        sender.sendMessage("§7Successfully setted the player's rank data to " + parsedInt);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return List.of();
    }
}