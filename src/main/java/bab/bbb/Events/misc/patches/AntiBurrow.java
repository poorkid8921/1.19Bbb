package bab.bbb.Events.misc.patches;

import bab.bbb.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class AntiBurrow implements Listener {
    @EventHandler
    public void OnPlayerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        Location playerLocation = player.getLocation();
        Block burrowBlock = playerLocation.getBlock();
        Material burrowBlockMaterial = burrowBlock.getType();

        if (burrowBlockMaterial.equals(Material.AIR)
                || burrowBlockMaterial.equals(Material.DIRT)
                || burrowBlockMaterial.equals(Material.SAND)
                || burrowBlockMaterial.equals(Material.GRAVEL)
                || Utils.isShulkerBox(burrowBlockMaterial))
            return;

        Block blockAboveBurrowBlock = burrowBlock.getRelative(BlockFace.UP);
        if (burrowBlock.getRelative(BlockFace.UP).getType().equals(Material.AIR)) {
            if (burrowBlockMaterial.isOccluding() && !Utils.isSinkInBlock(burrowBlockMaterial)) {
                if (!Utils.isSlab(burrowBlockMaterial)) {
                    player.damage(1.0);
                    player.teleport(blockAboveBurrowBlock.getLocation().add(0.5, 0, 0.5));
                }
            }

            if (burrowBlockMaterial.equals(Material.ENDER_CHEST) || Utils.isSinkInBlock(burrowBlockMaterial)) {
                if (playerLocation.getY() - playerLocation.getBlockY() < 0.875) {
                    player.damage(1.0);
                    player.teleport(blockAboveBurrowBlock.getLocation().add(0.5, 0, 0.5));
                }
            }

            if (burrowBlockMaterial.equals(Material.ENCHANTING_TABLE)) {
                if (playerLocation.getY() - playerLocation.getBlockY() < 0.75) {
                    player.damage(1.0);
                    player.teleport(blockAboveBurrowBlock.getLocation().add(0.5, 0, 0.5));
                }
            }

            if (Utils.isAnvil(burrowBlockMaterial)) {
                player.damage(1.0);
                burrowBlock.breakNaturally();
            }
        }

        if (burrowBlockMaterial.equals(Material.BEDROCK) || burrowBlockMaterial.equals(Material.BEACON)) {
            player.damage(1.0);
            player.teleport(blockAboveBurrowBlock.getLocation().add(0.5, 0, 0.5));
        }
    }
}
