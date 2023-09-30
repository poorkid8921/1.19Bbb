package commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import main.utils.Utils;

import static main.utils.Initializer.chatlock;

public class ChatLock implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("has.staff")) {
            chatlock = !chatlock;
            if (chatlock) sender.sendMessage(Utils.translateo("&7Successfully locked the chat."));
            else sender.sendMessage(Utils.translateo("&7Successfully un-locked the chat."));
        } else sender.sendMessage(Utils.translateo("&7You must be an Operator to use this command."));

        return true;
    }
}
