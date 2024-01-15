package expansions.warps;

import com.mojang.datafixers.kinds.Const;
import main.utils.Constants;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static main.utils.Constants.MAIN_COLOR;

public class Flat implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player pp) {
            String sn = sender.getName();
            if (Constants.bannedFromflat.contains(sn)) {
                sender.sendMessage(MAIN_COLOR + "ʏᴏᴜ ᴀʀᴇ ʙᴀɴɴᴇᴅ ꜰʀᴏᴍ ᴛʜɪs ᴍᴏᴅᴇ.");
                return true;
            }
            Location l = Constants.flat;
            pp.teleportAsync(l, PlayerTeleportEvent.TeleportCause.COMMAND);
            Constants.inFlat.add(sn);
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return List.of();
    }
}