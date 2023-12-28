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

import static main.utils.Initializer.MAIN_COLOR;

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
        Bukkit.getLogger().warning(packet.getWindowClickType().name() + " " + packet.getSlot() + " " + packet.getPacketId() + " " + packet.getButton());
        if (clickedItem != null && clickedItem.getType() == Material.TOTEM_OF_UNDYING && packet.getWindowClickType() == WrapperPlayClientClickWindow.WindowClickType.PICKUP) {
            Bukkit.getScheduler().runTaskLater(Initializer.p, () -> {
                if (player.getInventory().getItemInOffHand().getType() == Material.TOTEM_OF_UNDYING)
                    flag(player, "Auto Totem");
            }, 5L);
        }
    }
}