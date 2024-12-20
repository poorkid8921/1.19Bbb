package main.commands;

import io.papermc.lib.PaperLib;
import main.Practice;
import main.utils.Initializer;
import main.utils.Instances.LocationHolder;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static main.utils.Initializer.*;
import static main.utils.Languages.MAIN_COLOR;
import static main.utils.Languages.SECOND_COLOR;

public class RTP implements CommandExecutor, TabExecutor {
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

                    LocationHolder locH = endRTP.get(RANDOM.nextInt(100));
                    Location locC = new Location(Practice.d0, locH.getX(), locH.getY(), locH.getZ());
                    p.teleportAsync(locC).thenAccept(r -> {
                        p.playSound(p, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
                        p.sendTitle(SECOND_COLOR + "ᴛᴇʟᴇᴘᴏʀᴛᴇᴅ", "§7" + locH.getX() + " " + locH.getY() + " " + locH.getZ());
                        p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1));
                        p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 60, 1));
                        for (int index = 1; index < 16; index++) {
                            double p1 = (index * Math.PI) / 8;
                            double p2 = (index - 1) * Math.PI / 8;

                            int radius = 3;
                            double x1 = Math.cos(p1) * radius;
                            double xx2 = Math.cos(p2) * radius;
                            double z1 = Math.sin(p1) * radius;
                            double z2 = Math.sin(p2) * radius;
                            Practice.d0.spawnParticle(Particle.TOTEM, locC.clone().add(xx2 - x1, 0, z2 - z1), 1, 1.5f);
                        }
                    });
                    return true;
                }
            }
        }

        if (playersRTPing.contains(sn))
            return true;
        else {
            if (playersRTPing.size() > 0) {
                LocationHolder locH = overworldRTP.get(RANDOM.nextInt(100));
                Location locC = new Location(Practice.d, locH.getX(), locH.getY(), locH.getZ());
                p.teleportAsync(locC).thenAccept(r -> {
                    playersRTPing.remove(sn);
                    p.playSound(p, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
                    p.sendTitle(SECOND_COLOR + "ᴛᴇʟᴇᴘᴏʀᴛᴇᴅ", "§7" + locH.getX() + " " + locH.getY() + " " + locH.getZ());
                    p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1));
                    p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 60, 1));
                    for (int index = 1; index < 16; index++) {
                        double p1 = (index * Math.PI) / 8;
                        double p2 = (index - 1) * Math.PI / 8;

                        int radius = 3;
                        double x1 = Math.cos(p1) * radius;
                        double xx2 = Math.cos(p2) * radius;
                        double z1 = Math.sin(p1) * radius;
                        double z2 = Math.sin(p2) * radius;
                        Practice.d.spawnParticle(Particle.TOTEM, locC.clone().add(xx2 - x1, 0, z2 - z1), 1, 1.5f);
                    }
                });

                Bukkit.getPlayer(playersRTPing.get(Initializer.RANDOM.nextInt(playersRTPing.size()))).teleportAsync(locC).thenAccept(r -> {
                    playersRTPing.remove(sn);
                    p.playSound(p, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
                    p.sendTitle(SECOND_COLOR + "ᴛᴇʟᴇᴘᴏʀᴛᴇᴅ", "§7" + locH.getX() + " " + locH.getY() + " " + locH.getZ());
                    p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1));
                    p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 60, 1));
                    for (int index = 1; index < 16; index++) {
                        double p1 = (index * Math.PI) / 8;
                        double p2 = (index - 1) * Math.PI / 8;

                        int radius = 3;
                        double x1 = Math.cos(p1) * radius;
                        double xx2 = Math.cos(p2) * radius;
                        double z1 = Math.sin(p1) * radius;
                        double z2 = Math.sin(p2) * radius;
                        Practice.d.spawnParticle(Particle.TOTEM, locC.clone().add(xx2 - x1, 0, z2 - z1), 1, 1.5f);
                    }
                });
                return true;
            }
            playersRTPing.add(sn);
        }

        LocationHolder locH = overworldRTP.get(RANDOM.nextInt(100));
        Location locC = new Location(Practice.d, locH.getX(), locH.getY(), locH.getZ());
        p.teleportAsync(locC).thenAccept(r -> {
            p.playSound(p, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
            p.sendTitle(SECOND_COLOR + "ᴛᴇʟᴇᴘᴏʀᴛᴇᴅ", "§7" + locH.getX() + " " + locH.getY() + " " + locH.getZ());
            p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1));
            p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 60, 1));
            for (int index = 1; index < 16; index++) {
                double p1 = (index * Math.PI) / 8;
                double p2 = (index - 1) * Math.PI / 8;

                int radius = 3;
                double x1 = Math.cos(p1) * radius;
                double xx2 = Math.cos(p2) * radius;
                double z1 = Math.sin(p1) * radius;
                double z2 = Math.sin(p2) * radius;
                Practice.d.spawnParticle(Particle.TOTEM, locC.clone().add(xx2 - x1, 0, z2 - z1), 1, 1.5f);
            }
        });
        return true;
    }

    @Override
    public @Nullable java.util.List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return List.of("overworld", "end");
    }
}