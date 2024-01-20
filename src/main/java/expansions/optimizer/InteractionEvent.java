package expansions.optimizer;

import com.github.retrooper.packetevents.event.SimplePacketListenerAbstract;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.InteractionHand;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import main.utils.Constants;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;

import java.util.ArrayList;
import java.util.List;

import static main.utils.Constants.crystalsToBeOptimized;

public class InteractionEvent extends SimplePacketListenerAbstract {
    @Override
    public void onPacketPlayReceive(PacketPlayReceiveEvent event) {
        if (event.getPacketType() != PacketType.Play.Client.INTERACT_ENTITY) return;

        WrapperPlayClientInteractEntity wrapper = new WrapperPlayClientInteractEntity(event);
        if (wrapper.getAction() != WrapperPlayClientInteractEntity.InteractAction.INTERACT_AT) return;
        Player player = (Player) event.getPlayer();

        if (player.getPing() < 50)
            return;

        ItemStack item = wrapper.getHand() == InteractionHand.MAIN_HAND ? player.getInventory().getItemInMainHand() :
                player.getInventory().getItemInOffHand();
        if (item.getType() != Material.END_CRYSTAL)
            return;

        Location loc = crystalsToBeOptimized.get(wrapper.getEntityId());
        if (loc == null) return;

        Location blockLoc = loc.clone().subtract(0.5, 1.0, 0.5);
        RayTraceResult result = player.rayTraceBlocks(4.5,
                FluidCollisionMode.NEVER);

        if (result == null || result.getHitBlock().getType() != Material.OBSIDIAN) return;
        if (!result.getHitBlock().getLocation().equals(blockLoc)) return;

        Bukkit.getScheduler().runTask(Constants.p, () -> {
            Location clonedLoc = loc.clone().subtract(0.5, 0.0, 0.5);
            if (clonedLoc.getBlock().getType() != Material.AIR) return;

            clonedLoc.add(0.5, 1.0, 0.5);
            List<Entity> nearbyEntities = new ArrayList<>(clonedLoc.getWorld().getNearbyEntities(clonedLoc, 0.5, 1, 0.5));

            if (nearbyEntities.isEmpty()) {
                loc.getWorld().spawn(clonedLoc.subtract(0.0, 1.0, 0.0), EnderCrystal.class, entity -> entity.setShowingBottom(false));
                item.setAmount(item.getAmount() - 1);
            }
        });
    }
}
