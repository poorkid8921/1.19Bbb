package main.commands.ess;

import main.utils.Initializer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Kickall implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);

            try {
                out.writeUTF("Connect");
                out.writeUTF("Economy");
            } catch (IOException e) {
                e.printStackTrace();
            }
            Bukkit.getOnlinePlayers().forEach(r -> {
                r.sendTitle("§aPractice is restarting", null, 20, 60, 30);
                r.sendPluginMessage(Initializer.p, "BungeeCord", b.toByteArray());
            });
        }
        return true;
    }
}
