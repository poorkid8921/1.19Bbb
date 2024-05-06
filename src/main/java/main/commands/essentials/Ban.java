package main.commands.essentials;

import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;

import static main.utils.Initializer.playerData;
import static main.utils.Utils.banEffect;
import static main.utils.storage.DB.connection;

public class Ban implements CommandExecutor, TabExecutor {
    private byte[] getIP(String name) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM data WHERE name = ?")) {
            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) return resultSet.getBytes(4);
        } catch (SQLException ignored) {
        }
        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String sn = sender.getName();
        if (!sender.isOp() && playerData.get(sn).getRank() < 7) {
            sender.sendMessage("§7You must be an Operator to ban others!");
            return true;
        } else if (args.length == 0) {
            sender.sendMessage("§7You must specify a player you want to ban!");
            return true;
        }
        byte[] ip = getIP(args[0]);
        if (ip == null) {
            sender.sendMessage("§7Couldn't find the specified player.");
            return true;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target != null) {
            banEffect(target);
            target.kickPlayer("Connection with the remote server has been closed.");
        }
        String outputName = target == null ? args[0] : target.getName();
        long time = args.length == 2 ? Integer.parseInt(args[1].replaceAll("d", "")) * 86400000L : 432000000L;
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO bans (ip, bantime, banner) VALUES (?, ?, ?)")) {
            statement.setBytes(1, ip);
            statement.setTimestamp(2, new Timestamp(System.currentTimeMillis() + time));
            statement.setString(3, sn.equals("CONSOLE") ? "Catto69420" : sn);
            statement.executeUpdate();
        } catch (SQLException ignored) {
        }
        Bukkit.broadcastMessage("§a" + outputName + " §fwas §cbanned §fby §a" + sn + " §ffor §a" + (args.length == 2 ? args[1].replace('d', ' ') + "days" : "5 days"));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return args.length == 2 ? ImmutableList.of("5d", "30d") : args.length == 3 ? ImmutableList.of("purge") : args.length < 2 ? null : Collections.emptyList();
    }
}
