package main.expansions.duels.commands;

import main.utils.Initializer;
import main.utils.Initializer;
import main.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import static main.utils.Initializer.DUELS_BLUE_COLOR;
import static main.utils.Initializer.MAIN_COLOR;

@SuppressWarnings("deprecation")
public class Event implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String pn = sender.getName();
        if (!Initializer.valid.contains(pn)) return true;

        TextComponent winner = new TextComponent("» " + (args[4].equals("y") ? "ɴᴏʙᴏᴅʏ" : args[5]));
        winner.setColor(ChatColor.of("#fc282f"));
        sender.sendMessage(Initializer.DUELS_RESULTS, Initializer.DUELS_DELIM);
        sender.sendMessage(Initializer.DUELS_WINNER, winner);
        sender.sendMessage("§7sᴄᴏʀᴇ " + MAIN_COLOR + "» " + Integer.parseInt(args[1]) + " §7- " + DUELS_BLUE_COLOR + Integer.parseInt(args[2]), Utils.translate("§7ᴅᴜʀᴀᴛɪᴏɴ " + MAIN_COLOR + "» " + args[3]), Initializer.DUELS_DELIM);
        Initializer.valid.remove(pn);
        return true;
    }
}
