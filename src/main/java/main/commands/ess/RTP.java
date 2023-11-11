package main.commands.ess;

import io.papermc.lib.PaperLib;
import main.Practice;
import main.utils.Initializer;
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
import java.util.concurrent.atomic.AtomicReference;

import static main.utils.Languages.MAIN_COLOR;
import static main.utils.Languages.SECOND_COLOR;

public class RTP implements CommandExecutor, TabExecutor {
    ArrayList<String> playersRTPing = new ArrayList<>();
    ArrayList<Location> baked = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player p)) return true;

        if (args.length > 0) {
            switch (args[0]) {
                case "overworld", "world" -> {}
                default -> {
                    String sn = p.getName();
                    if (playersRTPing.contains(sn))
                        return true;

                    World d = Bukkit.getWorld("world_the_end");
                    Location l = p.getLocation();
                    Location loc = null;
                    int ax = 0;
                    int ay = 0;
                    int az = 0;
                    int x2 = 0;
                    while (loc == null) {
                        ax = Initializer.RANDOM.nextInt(60000);
                        az = Initializer.RANDOM.nextInt(60000);
                        if (ax > 30000) ax = -ax;
                        if (az > 30000) az = -az;

                        Block b = d.getHighestBlockAt(ax, az);
                        p.playSound(p, Sound.ENTITY_TNT_PRIMED, 1, 1);
                        if (b.isSolid()) {
                            loc = new Location(d, ax, b.getLocation().getY() + 1, az, l.getYaw(), l.getPitch());
                        } else if (x2++ == 10) {
                            sender.sendMessage(MAIN_COLOR + "ᴛᴇʟᴇᴘᴏʀᴛᴀᴛɪᴏɴ ꜰᴀɪʟᴇᴅ.");
                            return true;
                        }
                    }

                    Location c = p.getLocation();
                    if (c.distance(l) < 2) {
                        Location finalLoc = loc;
                        int finalAx = ax;
                        int finalAz = az;
                        p.teleportAsync(loc).thenAccept(r -> {
                            p.playSound(p, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
                            p.sendTitle(SECOND_COLOR + "ᴛᴇʟᴇᴘᴏʀᴛᴇᴅ", "§7" + finalAx + " " + ay + " " + finalAz);
                            p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1));
                            p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 60, 1));
                            Location cd = finalLoc.add(0, 1, 0);
                            for (int index = 1; index < 16; index++) {
                                double p1 = (index * Math.PI) / 8;
                                double p2 = (index - 1) * Math.PI / 8;

                                int radius = 3;
                                double x1 = Math.cos(p1) * radius;
                                double xx2 = Math.cos(p2) * radius;
                                double z1 = Math.sin(p1) * radius;
                                double z2 = Math.sin(p2) * radius;
                                d.spawnParticle(Particle.TOTEM, cd.clone().add(xx2 - x1, 0, z2 - z1), 1, 1.5f);
                            }

                            baked.add(finalLoc);
                        });
                        return true;
                    }

                    p.sendTitle(null, SECOND_COLOR + "ʀᴛᴘ ᴡᴀꜱ ᴄᴀɴᴄᴇʟʟᴇᴅ!");
                }
            }
        }

        String sn = p.getName();
        if (playersRTPing.contains(sn))
            return true;
        else {
            if (playersRTPing.size() > 0 &&
                    baked.size() > 0) {
                Location loc = baked.get(Initializer.RANDOM.nextInt(baked.size()));
                baked.remove(loc);
                double x = loc.getX();
                double y = loc.getY();
                double z = loc.getZ();
                Location cd = loc.add(0, 1, 0);
                p.teleportAsync(loc).thenAccept(r -> {
                    p.playSound(p, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
                    p.sendTitle(SECOND_COLOR + "ᴛᴇʟᴇᴘᴏʀᴛᴇᴅ", "§7" + x + " " + y + " " + z);
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
                        Practice.d.spawnParticle(Particle.TOTEM, cd.clone().add(xx2 - x1, 0, z2 - z1), 1, 1.5f);
                    }
                });

                Bukkit.getPlayer(playersRTPing.get(Initializer.RANDOM.nextInt(baked.size()))).teleportAsync(loc).thenAccept(r -> {
                    playersRTPing.remove(sn);
                    p.playSound(p, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
                    p.sendTitle(SECOND_COLOR + "ᴛᴇʟᴇᴘᴏʀᴛᴇᴅ", "§7" + x + " " + y + " " + z);
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
                        Practice.d.spawnParticle(Particle.TOTEM, cd.clone().add(xx2 - x1, 0, z2 - z1), 1, 1.5f);
                    }
                });
                return true;
            }
            playersRTPing.add(sn);
        }

        Location l = p.getLocation();
        AtomicReference<Location> loc = null;
        int ax;
        int az;
        AtomicInteger x2 = new AtomicInteger();
        while (loc.get() == null) {
            ax = Initializer.RANDOM.nextInt(10000);
            az = Initializer.RANDOM.nextInt(10000);
            if (ax > 5000) ax = -ax;
            if (az > 5000) az = -az;

            Location ld = new Location(Practice.d, ax, 69, az, l.getYaw(), l.getPitch());
            int finalAx1 = ax;
            int finalAz1 = az;
            PaperLib.getChunkAtAsync(ld).thenAccept(r -> {
                Block b = Practice.d.getHighestBlockAt(finalAx1, finalAz1);
                p.playSound(p, Sound.ENTITY_TNT_PRIMED, 1, 1);
                if (b.isSolid()) {
                    if (p.getLocation().distance(l) < 2) {
                        double finalAy1 = b.getLocation().getY() + 1;
                        loc.get().setY(finalAy1);
                        p.teleportAsync(loc.get()).thenAccept(rd -> {
                            playersRTPing.remove(sn);
                            p.playSound(p, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
                            p.sendTitle(SECOND_COLOR + "ᴛᴇʟᴇᴘᴏʀᴛᴇᴅ", "§7" + finalAx1 + " " + finalAy1 + " " + finalAz1);
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
                                Practice.d.spawnParticle(Particle.TOTEM, loc.get().clone().add(xx2 - x1, 0, z2 - z1), 1, 1.5f);
                            }

                            baked.add(loc.get());
                        });
                    } else {
                        playersRTPing.remove(sn);
                        p.sendTitle(null, SECOND_COLOR + "ʀᴛᴘ ᴡᴀꜱ ᴄᴀɴᴄᴇʟʟᴇᴅ!");
                    }
                } else if (x2.getAndIncrement() == 10) {
                    sender.sendMessage(MAIN_COLOR + "ᴛᴇʟᴇᴘᴏʀᴛᴀᴛɪᴏɴ ꜰᴀɪʟᴇᴅ.");
                }
            });
        }

        return true;
    }

    @Override
    public @Nullable java.util.List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return List.of("overworld", "end");
    }
}