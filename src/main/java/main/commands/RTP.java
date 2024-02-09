package main.commands;

import com.google.common.collect.ImmutableList;
import main.Practice;
import main.utils.Constants;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import static main.utils.Constants.*;

public class RTP implements CommandExecutor, TabExecutor {
    private void teleportEffect(World world, Location loc) {
        for (int i = 1; i < 16; i++) {
            double p1 = (i * Math.PI) / 8;
            double p2 = (i - 1) * Math.PI / 8;
            double x1 = Math.cos(p1) * 3;
            double xx2 = Math.cos(p2) * 3;
            double z1 = Math.sin(p1) * 3;
            double z2 = Math.sin(p2) * 3;
            world.spawnParticle(Particle.TOTEM, loc.clone().add(xx2 - x1, 0, z2 - z1), 1, 1.5f);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player p)) return true;
        String sn = p.getName();

        if (args.length > 0) {
            switch (args[0]) {
                case "overworld", "world" -> {
                }
                default -> {
                    if (playersRTPing.contains(sn))
                        return true;

                    Location locH = endRTP.get(RANDOM.nextInt(100));
                    p.teleportAsync(locH).thenAccept(result -> {
                        p.playSound(p, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
                        p.sendTitle(SECOND_COLOR + "ᴛᴇʟᴇᴘᴏʀᴛᴇᴅ", "§7" + locH.getX() + " " + locH.getY() + " " + locH.getZ());
                        p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1));
                        p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 60, 1));
                        teleportEffect(Practice.d0, locH);
                    });
                    return true;
                }
            }
        }

        if (playersRTPing.contains(sn))
            return true;
        else {
            if (playersRTPing.size() > 0) {
                Location locH = overworldRTP.get(RANDOM.nextInt(100));
                p.teleportAsync(locH).thenAccept(result -> {
                    playersRTPing.remove(sn);
                    p.playSound(p, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
                    p.sendTitle(SECOND_COLOR + "ᴛᴇʟᴇᴘᴏʀᴛᴇᴅ", "§7" + (int) locH.getX() + " " + (int) locH.getY() + " " + (int) locH.getZ());
                    p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1));
                    p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 60, 1));
                    teleportEffect(Practice.d, locH);
                });

                String name = playersRTPing.get(Constants.RANDOM.nextInt(playersRTPing.size()));
                Player pd = Bukkit.getPlayer(name);
                pd.teleportAsync(locH).thenAccept(result -> {
                    playersRTPing.remove(name);
                    p.playSound(p, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
                    p.sendTitle(SECOND_COLOR + "ᴛᴇʟᴇᴘᴏʀᴛᴇᴅ", "§7" + (int) locH.getX() + " " + (int) locH.getY() + " " + (int) locH.getZ());
                    p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1));
                    p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 60, 1));
                });
                return true;
            }
            playersRTPing.add(sn);
        }

        Location locH = overworldRTP.get(RANDOM.nextInt(100));
        p.teleportAsync(locH).thenAccept(result -> {
            playersRTPing.remove(sn);
            p.playSound(p, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
            p.sendTitle(SECOND_COLOR + "ᴛᴇʟᴇᴘᴏʀᴛᴇᴅ", "§7" + (int) locH.getX() + " " + (int) locH.getY() + " " + (int) locH.getZ());
            p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1));
            p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 60, 1));
            teleportEffect(Practice.d, locH);
        });
        return true;
    }

    @Override
    public java.util.List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return ImmutableList.of("overworld", "end");
    }
}