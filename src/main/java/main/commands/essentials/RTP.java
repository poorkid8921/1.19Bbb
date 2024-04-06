package main.commands.essentials;

import com.google.common.collect.ImmutableList;
import main.Practice;
import main.utils.Initializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collections;

import static main.utils.Initializer.*;
import static main.utils.Utils.teleportEffect;

public class RTP implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p = (Player) sender;
        if (args.length > 0 && args[0].equals("end")) {
            if (playersRTPing.contains(sender.getName())) return true;

            Location locH = endRTP[RANDOM.nextInt(100)];
            p.teleportAsync(locH, PlayerTeleportEvent.TeleportCause.COMMAND).thenAccept(result -> {
                p.playSound(p, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
                p.sendTitle(SECOND_COLOR + "ᴛᴇʟᴇᴘᴏʀᴛᴇᴅ", "§7" + locH.getBlockX() + " " + locH.getBlockY() + " " + locH.getBlockZ());
                p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1));
                p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 60, 1));
                teleportEffect(Practice.d0, locH);
            });
            return true;
        }

        String sn = sender.getName();
        if (playersRTPing.contains(sn)) return true;
        else {
            if (!playersRTPing.isEmpty()) {
                Location locH = overworldRTP[RANDOM.nextInt(100)];
                p.teleportAsync(locH, PlayerTeleportEvent.TeleportCause.COMMAND).thenAccept(result -> {
                    playersRTPing.remove(sn);
                    p.playSound(p, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
                    p.sendTitle(SECOND_COLOR + "ᴛᴇʟᴇᴘᴏʀᴛᴇᴅ", "§7" + locH.getBlockX() + " " + locH.getBlockY() + " " + locH.getBlockZ());
                    p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1));
                    p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 60, 1));
                    teleportEffect(Practice.d, locH);
                });

                String name = playersRTPing.get(Initializer.RANDOM.nextInt(playersRTPing.size()));
                Player pd = Bukkit.getPlayer(name);
                pd.teleportAsync(locH, PlayerTeleportEvent.TeleportCause.COMMAND).thenAccept(result -> {
                    playersRTPing.remove(name);
                    p.playSound(p, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
                    p.sendTitle(SECOND_COLOR + "ᴛᴇʟᴇᴘᴏʀᴛᴇᴅ", "§7" + locH.getBlockX() + " " + locH.getBlockY() + " " + locH.getBlockZ());
                    p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1));
                    p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 60, 1));
                });
                return true;
            }
            playersRTPing.add(sn);
        }

        Location locH = overworldRTP[RANDOM.nextInt(100)];
        p.teleportAsync(locH, PlayerTeleportEvent.TeleportCause.COMMAND).thenAccept(result -> {
            playersRTPing.remove(sn);
            p.playSound(p, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
            p.sendTitle(SECOND_COLOR + "ᴛᴇʟᴇᴘᴏʀᴛᴇᴅ", "§7" + locH.getBlockX() + " " + locH.getBlockY() + " " + locH.getBlockZ());
            p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1));
            p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 60, 1));
            teleportEffect(Practice.d, locH);
        });
        return true;
    }

    @Override
    public java.util.List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return args.length < 2 ? ImmutableList.of("end") : Collections.emptyList();
    }
}