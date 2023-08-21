package org.yuri.aestheticnetwork.commands.duel;

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

public class Event implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull Command command,
                             @NotNull String label,
                             @NotNull String[] args) {
        Player p = ((Player) sender);
        if (!Initializer.valid.contains(p.getUniqueId()))
            return true;

        String arg = args[4];

        TextComponent hi = new TextComponent(translate(
                "&7ᴡɪɴɴᴇʀ: &e" +
                (arg.equals("y") ?
                        "ɴᴏʙᴏᴅʏ" :
                        args[5])));
        Text hit = new Text(translate("&7ʜᴇᴀʀᴛs ʀᴇᴍᴀɪɴɪɴɢ: &e" + Integer.parseInt(args[6]) +
                "\n&7ᴋɪʟʟs: &e" + Integer.parseInt(args[7]) +
                "\n&7ᴅᴇᴀᴛʜs: &e" + Integer.parseInt(args[8]) +
                "\n&7ᴡɪɴs: &e" + Integer.parseInt(args[10]) +
                "\n&7ʟᴏssᴇs: &e" + Integer.parseInt(args[11])));
        if (arg.equals("n"))
            hi.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hit));

        sender.sendMessage(translate("&7ᴅᴜᴇʟ ʀᴇsᴜʟᴛs"));
        sender.sendMessage(translate("&7------------------------"));
        sender.sendMessage(hi);
        /*sender.sendMessage(translate("&7Your team: " + (Boolean.parseBoolean(args[0]) ?
                "&9Blue" :
                "&cRed")));*/
        sender.sendMessage(translate("&7sᴄᴏʀᴇ: &c" + Integer.parseInt(args[1]) + " &7- &9" +
                Integer.parseInt(args[2])));
        if (args[9].equals("0"))
            sender.sendMessage(translate("&7ʟᴇɢᴀᴄʏ: ʏᴇs: &eYes"));
        sender.sendMessage(translate("&7ᴅᴜʀᴀᴛɪᴏɴ: &e" + args[3]));
        sender.sendMessage(translate("&7------------------------"));
        Initializer.valid.remove(p.getUniqueId());
        return true;
    }
}
