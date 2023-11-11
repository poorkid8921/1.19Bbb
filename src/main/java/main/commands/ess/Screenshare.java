package main.commands.ess;

import main.utils.Languages;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Screenshare implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("has.staff")) {
            if (args.length < 1)
                return true;

            Player p = Bukkit.getPlayer(args[0]);
            if (p == null)
                return true;

            p.sendMessage(Languages.MAIN_COLOR + "ʏᴏᴜ ʜᴀᴠᴇ ʙᴇᴇɴ ʀᴇǫᴜᴇsᴛᴇᴅ ᴛᴏ ss §7| discord.gg/aestheticnetwork |" + Languages.MAIN_COLOR + " ᴘᴜʙʟɪᴄ ᴠᴄ");
        }
        return true;
    }
}
