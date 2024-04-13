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
        String sn = sender.getName();
        CustomPlayerDataHolder D0 = playerData.get(sn);
        if (D0.getLastRTPed() > System.currentTimeMillis()) {
            sender.sendMessage("§7You are on a cooldown of " + SECOND_COLOR + getTime(D0.getLastRTPed() - System.currentTimeMillis()) + "!");
            return true;
        }
        D0.setLastRTPed(System.currentTimeMillis() + 180000L);

        Player p = (Player) sender;
        World w = p.getWorld();
        String wn = w.getName();
        Location locC = (wn.equals("world_nether") ? netherRTP : wn.equals("world_the_end") ? endRTP : overworldRTP)[RANDOM.nextInt(100)];
        p.teleportAsync(locC, PlayerTeleportEvent.TeleportCause.COMMAND).thenAccept(result -> {
            p.playSound(p, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
            p.sendTitle(SECOND_COLOR + "ᴛᴇʟᴇᴘᴏʀᴛᴇᴅ", "§7" + locC.getBlockX() + " " + locC.getBlockY() + " " + locC.getBlockZ());
            p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1));
            p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 60, 1));
            teleportEffect(w, locC);
        });
        return true;
    }

    @Override
    public java.util.List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}