package main.expansions;

import com.github.retrooper.packetevents.event.SimplePacketListenerAbstract;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import main.utils.Initializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static main.utils.Languages.MAIN_COLOR;

public class AntiCheat extends SimplePacketListenerAbstract {
    void flag(Player p,
              String reason) {
        Bukkit.getOnlinePlayers().stream().filter(r -> r.hasPermission("has.staff")).forEach(r -> r.sendMessage(MAIN_COLOR + p.getDisplayName() + " §7has been flagged for " + MAIN_COLOR + reason));
        Bukkit.getLogger().warning("AntiCheat: " + p.getName() + " has been flagged for " + reason);
    }

    @Override
    public void onPacketPlayReceive(PacketPlayReceiveEvent event) {
        if (event.getPacketType() != PacketType.Play.Client.CLICK_WINDOW) return;

        Player player = (Player) event.getPlayer();
        WrapperPlayClientClickWindow packet = new WrapperPlayClientClickWindow(event);
        ItemStack clickedItem = player.getOpenInventory().getItem(packet.getSlot());
        if (clickedItem != null && clickedItem.getType() == Material.TOTEM_OF_UNDYING) {
            final ItemStack oldOffHandItem = player.getOpenInventory().getItem(45);
            Bukkit.getScheduler().runTaskLaterAsynchronously(Initializer.p, () -> {
                ItemStack newOffHandItem = player.getOpenInventory().getItem(45);
                if (newOffHandItem != null &&
                        newOffHandItem.getType() == Material.TOTEM_OF_UNDYING &&
                        oldOffHandItem.getType() == Material.AIR) {
                    flag(player, "Auto Totem");
                    event.setCancelled(true);
                }
            }, 5L);
        }
    }
}