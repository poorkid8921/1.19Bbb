package main.commands;

import com.google.common.collect.ImmutableList;
import main.utils.instances.CustomPlayerDataHolder;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import static main.utils.Constants.*;

public class RTP implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String sn = sender.getName();
        CustomPlayerDataHolder D0 = playerData.get(sn);
        if (D0.isRTPing())
            return true;
        else
            D0.setRTPing(true);

        Player p = (Player) sender;
        World w = p.getWorld();
        String wn = w.getName();
        Location locC = (wn.equals("world_nether") ? netherRTP : wn.equals("world_the_end") ? endRTP : overworldRTP).get(RANDOM.nextInt(100));
        p.teleportAsync(locC).thenAccept(result -> {
            D0.setRTPing(false);
            p.playSound(p, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
            p.sendTitle(SECOND_COLOR + "ᴛᴇʟᴇᴘᴏʀᴛᴇᴅ", "§7" + (int) locC.getX() + " " + (int) locC.getY() + " " + (int) locC.getZ());
            p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1));
            p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 60, 1));
            for (int index = 1; index < 16; index++) {
                double p1 = (index * Math.PI) / 8;
                double p2 = (index - 1) * Math.PI / 8;
                double x1 = Math.cos(p1) * 3;
                double xx2 = Math.cos(p2) * 3;
                double z1 = Math.sin(p1) * 3;
                double z2 = Math.sin(p2) * 3;
                w.spawnParticle(Particle.TOTEM, locC.clone().add(xx2 - x1, 0, z2 - z1), 1, 1.5f);
            }
        });
        return true;
    }

    @Override
    public java.util.List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return ImmutableList.of();
    }
}