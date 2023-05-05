package bab.bbb.Events.misc.patches;

import bab.bbb.utils.ItemUtils;
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
    public void OnPlayerMove(PlayerMoveEvent e)
    {
        Player player = e.getPlayer();
        Location playerLocation = player.getLocation();
        Block burrowBlock = playerLocation.getBlock();
        Material burrowBlockMaterial = burrowBlock.getType();

        if (burrowBlockMaterial.equals(Material.AIR) || burrowBlockMaterial.equals(Material.SAND) || burrowBlockMaterial.equals(Material.GRAVEL) || ItemUtils.isShulkerBox(burrowBlockMaterial) || burrowBlockMaterial.equals(Material.FARMLAND))
            return;

        Block blockAboveBurrowBlock = burrowBlock.getRelative(BlockFace.UP);
        if (blockAboveBurrowBlock.getType().equals(Material.AIR)) {
            if (burrowBlockMaterial.isOccluding() && !isSinkInBlock(burrowBlockMaterial)) {
                if (!isSlab(burrowBlockMaterial)) {
                    player.damage(1.0);
                    player.teleport(blockAboveBurrowBlock.getLocation().add(0.5, 0, 0.5));
                }
            }

            if (burrowBlockMaterial.equals(Material.ENDER_CHEST) || isSinkInBlock(burrowBlockMaterial)) {
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

            if (isAnvil(burrowBlockMaterial)) {
                player.damage(1.0);
                burrowBlock.breakNaturally();
            }
        }

        if (burrowBlockMaterial.equals(Material.BEDROCK) || burrowBlockMaterial.equals(Material.BEACON)) {
            player.damage(1.0);
            player.teleport(blockAboveBurrowBlock.getLocation().add(0.5, 0, 0.5));
        }
    }

    private boolean isSinkInBlock(Material burrowBlockMaterial) {
        if (burrowBlockMaterial == null) return false;
        return switch (burrowBlockMaterial) {
            case SOUL_SAND, MUD -> true;
            default -> false;
        };
    }

    private boolean isAnvil(Material burrowBlockMaterial) {
        if (burrowBlockMaterial == null) return false;
        return switch (burrowBlockMaterial) {
            case ANVIL, CHIPPED_ANVIL, DAMAGED_ANVIL -> true;
            default -> false;
        };
    }

    private boolean isSlab(Material burrowBlockMaterial) {
        if (burrowBlockMaterial == null) return false;
        return switch (burrowBlockMaterial) {
            case ACACIA_SLAB, ANDESITE_SLAB, BIRCH_SLAB, BLACKSTONE_SLAB, BRICK_SLAB, COBBLED_DEEPSLATE_SLAB, COBBLESTONE_SLAB, CRIMSON_SLAB, CUT_COPPER_SLAB, CUT_RED_SANDSTONE_SLAB, CUT_SANDSTONE_SLAB, DARK_OAK_SLAB, DARK_PRISMARINE_SLAB, DEEPSLATE_BRICK_SLAB, DEEPSLATE_TILE_SLAB, DIORITE_SLAB, END_STONE_BRICK_SLAB, EXPOSED_CUT_COPPER_SLAB, GRANITE_SLAB, JUNGLE_SLAB, MANGROVE_SLAB, MOSSY_COBBLESTONE_SLAB, MOSSY_STONE_BRICK_SLAB, MUD_BRICK_SLAB, NETHER_BRICK_SLAB, OAK_SLAB, OXIDIZED_CUT_COPPER_SLAB, PETRIFIED_OAK_SLAB, POLISHED_ANDESITE_SLAB, POLISHED_BLACKSTONE_BRICK_SLAB, POLISHED_BLACKSTONE_SLAB, POLISHED_DEEPSLATE_SLAB, POLISHED_DIORITE_SLAB, POLISHED_GRANITE_SLAB, PRISMARINE_BRICK_SLAB, PRISMARINE_SLAB, PURPUR_SLAB, QUARTZ_SLAB, RED_NETHER_BRICK_SLAB, RED_SANDSTONE_SLAB, SANDSTONE_SLAB, SCULK_SENSOR, SCULK_SHRIEKER, SMOOTH_QUARTZ_SLAB, SMOOTH_RED_SANDSTONE_SLAB, SMOOTH_SANDSTONE_SLAB, SMOOTH_STONE_SLAB, SPRUCE_SLAB, STONE_BRICK_SLAB, STONE_SLAB, WARPED_SLAB, WAXED_CUT_COPPER_SLAB, WAXED_EXPOSED_CUT_COPPER_SLAB, WAXED_OXIDIZED_CUT_COPPER_SLAB, WAXED_WEATHERED_CUT_COPPER_SLAB, WEATHERED_CUT_COPPER_SLAB ->
                    true;
            default -> false;
        };
    }
}
