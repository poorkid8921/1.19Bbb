package commands;

import main.utils.Initializer;
import main.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class RTP implements CommandExecutor {
    List<String> BLACKLIST = List.of("WATER", "LAVA");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player p))
            return true;

        Location l = p.getLocation();
        World d = l.getWorld();
        Location loc = null;
        int ax = 0;
        int ay = 0;
        int az = 0;
        p.playSound(p, Sound.ENTITY_TNT_PRIMED, 1, 1);
        while (loc == null) {
            int x = Initializer.RANDOM.nextInt(3000);
            int z = Initializer.RANDOM.nextInt(3000);
            if (x > 1500)
                x = -x;

            if (z > 1500)
                z = -z;
            int i = d.getHighestBlockYAt(x, z);
            if (BLACKLIST.contains(d.getBlockAt(x, i, z).getType().name()))
                continue;

            loc = new Location(d, x, i + 1, z, l.getYaw(), l.getPitch());
        }
        if (p.getLocation().distance(l) < 2) {
            p.teleportAsync(loc).thenAccept(r -> {
                p.playSound(p, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
                p.sendTitle(Utils.translateA("#d6a7ebᴛᴇʟᴇᴘᴏʀᴛᴇᴅ"), "§7" + ax + " " + ay + " " + az);
            });
            return true;
        }

        p.sendTitle("", Utils.translateA("#d6a7ebʀᴛᴘ ᴡᴀꜱ ᴄᴀɴᴄᴇʟʟᴇᴅ!"));
        return true;
    }
}
