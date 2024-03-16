package main.commands;

import main.Economy;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.EntityType;

import java.util.Collections;

import static main.utils.Initializer.overworldRTP;

public class BombRTP implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.isOp())
            return true;
        int amt = args.length == 1 ? Integer.parseInt(args[0]) : 5;
        for (int id = 0; id < amt; id++) {
            for (Location l : overworldRTP) {
                l.setY(l.getY() + 1);
                Economy.d.spawnEntity(l, EntityType.MINECART_TNT);
            }
        }
        sender.sendMessage("§7Successfully bombed the RTP.");
        return true;
    }

    @Override
    public java.util.List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}
