package commands;

import main.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpaAll implements CommandExecutor {
    public TpaAll() {
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player user)) return true;

        user.sendMessage(Utils.translateo("&7Requested everyone to teleport to you."));

        String i = user.getName();
        Bukkit.getOnlinePlayers().stream().filter(r ->
                        !r.getName().equals(i) &&
                                Utils.getRequest(i).getSender() == null &&
                                Utils.manager().get("r." + i + ".t") == null)
                .forEach(r -> Utils.addRequest(user,
                        r,
                        true,
                        false));
        return true;
    }
}