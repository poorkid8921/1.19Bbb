package main.expansions.duels.commands;

import main.utils.Constants;
import main.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import static main.utils.Constants.DUELS_BLUE_COLOR;
import static main.utils.Constants.MAIN_COLOR;

@SuppressWarnings("deprecation")
public class Event implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String pn = sender.getName();
        if (!Constants.valid.contains(pn)) return true;

        TextComponent winner = new TextComponent("» " + (args[4].equals("y") ? "ɴᴏʙᴏᴅʏ" : args[5]));
        winner.setColor(ChatColor.of("#fc282f"));
        sender.sendMessage(Constants.DUELS_RESULTS, Constants.DUELS_DELIM);
        sender.sendMessage(Constants.DUELS_WINNER, winner);
        sender.sendMessage("§7sᴄᴏʀᴇ " + MAIN_COLOR + "» " + Integer.parseInt(args[1]) + " §7- " + DUELS_BLUE_COLOR + Integer.parseInt(args[2]), Utils.translate("§7ᴅᴜʀᴀᴛɪᴏɴ " + MAIN_COLOR + "» " + args[3]), Constants.DUELS_DELIM);
        Constants.valid.remove(pn);
        return true;
    }
}
