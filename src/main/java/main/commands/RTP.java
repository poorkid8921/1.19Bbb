package main.commands;

import main.utils.Initializer;
import main.utils.Utils;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.List;

import static main.utils.Languages.MAIN_COLOR;

public class RTP implements CommandExecutor {
    List<Material> BLACKLIST = List.of(Material.WATER, Material.LAVA);

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player p)) return true;

        Location l = p.getLocation();
        World d = l.getWorld();
        Location loc = null;
        int ax = 0;
        int ay = 0;
        int az = 0;
        int x2 = 0;
        while (loc == null) {
            int x = Initializer.RANDOM.nextInt(3000);
            int z = Initializer.RANDOM.nextInt(3000);
            if (x > 1500) x = -x;
            if (z > 1500) z = -z;

            int i = d.getHighestBlockYAt(x, z);
            p.playSound(p, Sound.ENTITY_TNT_PRIMED, 1, 1);
            if (!BLACKLIST.contains(d.getBlockAt(x, i, z).getType()))
                loc = new Location(d, x, i + 1, z, l.getYaw(), l.getPitch());
            else if (x2++ == 10) {
                p.sendMessage(MAIN_COLOR + "ᴛᴇʟᴇᴘᴏʀᴛᴀᴛɪᴏɴ ꜰᴀɪʟᴇᴅ.");
                return true;
            }
        }

        Location c = p.getLocation();
        if (c.distance(l) < 2) {
            Location finalLoc = loc;
            p.teleportAsync(loc).thenAccept(r -> {
                p.playSound(p, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
                p.sendTitle(Utils.translateA("#d6a7ebᴛᴇʟᴇᴘᴏʀᴛᴇᴅ"), "§7" + ax + " " + ay + " " + az);
                p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 35, 1));
                Location cd = finalLoc.add(0, 1, 0);
                for (int index = 1; index < 16; index++) {
                    Vector vec = getVecCircle(index);
                    d.spawnParticle(Particle.TOTEM, cd.clone().add(vec), 0, 1.5f);
                }
            });
            return true;
        }

        p.sendTitle("", Utils.translateA("#d6a7ebʀᴛᴘ ᴡᴀꜱ ᴄᴀɴᴄᴇʟʟᴇᴅ!"));
        return true;
    }

    private Vector getVecCircle(int index) {
        double p1 = (index * Math.PI) / 8;
        double p2 = (index - 1) * Math.PI / 8;

        int radius = 3;
        double x1 = Math.cos(p1) * radius;
        double x2 = Math.cos(p2) * radius;
        double z1 = Math.sin(p1) * radius;
        double z2 = Math.sin(p2) * radius;
        return new Vector(x2 - x1,
                0,
                z2 - z1);
    }
}