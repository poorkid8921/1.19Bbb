package main.commands.duel;

import main.utils.Initializer;
import main.utils.Languages;
import main.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static main.utils.Languages.MAIN_COLOR;

@SuppressWarnings("deprecation")
public class Event implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p = ((Player) sender);
        if (!Initializer.valid.contains(p.getName())) return true;

        String arg = args[4];

        TextComponent hi2 = new TextComponent("» " + (arg.equals("y") ? "ɴᴏʙᴏᴅʏ" : args[5]));
        hi2.setColor(ChatColor.of("#fc282f"));

        sender.sendMessage(Languages.DUELS_RESULTS,
                Languages.DUELS_DELIM);
        sender.sendMessage(Languages.DUELS_WINNER,
                hi2);
        sender.sendMessage("§7sᴄᴏʀᴇ " + MAIN_COLOR + "» " + Integer.parseInt(args[1]) + " §7- " + Utils.translateA(" #4d8eff" + Integer.parseInt(args[2])),
                Utils.translate("§7ᴅᴜʀᴀᴛɪᴏɴ " + MAIN_COLOR + "» " + args[3]),
                Languages.DUELS_DELIM);
        Initializer.valid.remove(p.getName());
        return true;
    }
}
