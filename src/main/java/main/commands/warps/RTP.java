package main.commands.warps;

import main.utils.instances.CustomPlayerDataHolder;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
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
import static main.utils.Utils.getTime;
import static main.utils.Utils.teleportEffect;

public class RTP implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        final CustomPlayerDataHolder D0 = playerData.get(sender.getName());
        if (D0.getLastRTPed() > System.currentTimeMillis()) {
            sender.sendMessage("§7You are on a cooldoname of " + SECOND_COLOR + getTime(D0.getLastRTPed() - System.currentTimeMillis()) + "!");
            return true;
        }
        D0.setLastRTPed(System.currentTimeMillis() + 180000L);
        final Player player = (Player) sender;
        final World world = player.getWorld();
        final String name = world.getName();
        final Location locC = (name.equals("world_nether") ? netherRTP : name.equals("world_the_end") ? endRTP : overworldRTP)[RANDOM.nextInt(100)];
        player.teleportAsync(locC, PlayerTeleportEvent.TeleportCause.COMMAND).thenAccept(result -> {
            player.playSound(player, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
            player.sendTitle(SECOND_COLOR + "ᴛᴇʟᴇᴘᴏʀᴛᴇᴅ", "§7" + locC.getBlockX() + " " + locC.getBlockY() + " " + locC.getBlockZ());
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1));
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 60, 1));
            teleportEffect(world, locC);
        });
        return true;
    }

    @Override
    public java.util.List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}