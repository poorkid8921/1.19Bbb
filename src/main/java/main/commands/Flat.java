package main.commands;

import main.utils.Constants;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static main.utils.Constants.MAIN_COLOR;

public class Flat implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player pp) {
            if (Constants.bannedFromflat.contains(sender.getName())) {
                sender.sendMessage(MAIN_COLOR + "ʏᴏᴜ ᴀʀᴇ ʙᴀɴɴᴇᴅ ꜰʀᴏᴍ ᴛʜɪs ᴍᴏᴅᴇ.");
                return true;
            }
            Location l = Constants.flat;
            Location pl = pp.getLocation();
            l.setYaw(pl.getYaw());
            l.setYaw(pl.getPitch());
            pp.teleportAsync(l);
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return List.of();
    }
}