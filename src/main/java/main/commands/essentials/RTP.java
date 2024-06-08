package main.commands.essentials;

import com.google.common.collect.ImmutableList;
import main.Practice;
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
        final String name = sender.getName();
        if (playersRTPing.contains(name)) return true;
        playersRTPing.add(name);
        final Player player = (Player) sender;
        final Location location = args.length > 0 && args[0].equals("end") ? endRTP[RANDOM.nextInt(100)] : overworldRTP[RANDOM.nextInt(100)];
        player.teleportAsync(location, PlayerTeleportEvent.TeleportCause.COMMAND).thenAccept(result -> {
            playersRTPing.remove(name);
            player.playSound(player, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
            player.sendTitle(SECOND_COLOR + "ᴛᴇʟᴇᴘᴏʀᴛᴇᴅ", "§7" + location.getBlockX() + " " + location.getBlockY() + " " + location.getBlockZ());
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1));
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 60, 1));
            teleportEffect(Practice.d, location);
        });
        return true;
    }

    @Override
    public java.util.List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return args.length < 2 ? ImmutableList.of("end") : Collections.emptyList();
    }
}