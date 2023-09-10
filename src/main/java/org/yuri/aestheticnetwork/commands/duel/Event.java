package org.yuri.aestheticnetwork.commands.duel;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.yuri.aestheticnetwork.utils.Initializer;

import static org.yuri.aestheticnetwork.utils.Utils.translate;
import static org.yuri.aestheticnetwork.utils.Utils.translateo;

@SuppressWarnings("deprecation")
public class Event implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player p = ((Player) sender);
        if (!Initializer.valid.contains(p.getName())) return true;

        String arg = args[4];

        TextComponent hi = new TextComponent(translateo("&7ᴡɪɴɴᴇʀ "));
        TextComponent hi2 = new TextComponent("» " + (arg.equals("y") ? "ɴᴏʙᴏᴅʏ" : args[5]));
        hi2.setColor(ChatColor.of("#fc282f"));
        // REMOVED TILL FIND A WAY TO USE HEX
        /*if (arg.equals("n"))
            hi.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    new Text(translate("&7ʜᴇᴀʀᴛs ʀᴇᴍᴀɪɴɪɴɢ #fc282f» " + Integer.parseInt(args[6]) + "\n&7ᴋɪʟʟs #fc282f» " + Integer.parseInt(args[7]) + "\n&7ᴅᴇᴀᴛʜs #fc282f» " + Integer.parseInt(args[8]) + "\n&7ᴡɪɴs #fc282f» " + Integer.parseInt(args[9]) + "\n&7ʟᴏssᴇs #fc282f» " + Integer.parseInt(args[10])))));
        */

        sender.sendMessage(translateo("&7ᴅᴜᴇʟ ʀᴇsᴜʟᴛs"));
        sender.sendMessage(translateo("&7------------------------"));
        sender.sendMessage(hi,
                hi2);
        sender.sendMessage(translate("&7sᴄᴏʀᴇ #fc282f» " + Integer.parseInt(args[1]) + " &7- &9" + Integer.parseInt(args[2])));
        sender.sendMessage(translate("&7ᴅᴜʀᴀᴛɪᴏɴ #fc282f» " + args[3]));
        sender.sendMessage(translateo("&7------------------------"));
        Initializer.valid.remove(p.getName());
        return true;
    }
}
