package main.utils.kits;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import main.Practice;
import main.utils.Instances.CustomPlayerDataHolder;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static main.utils.Initializer.SECOND_COLOR;
import static main.utils.Initializer.playerData;

public class KitClaimer {
    public static void claim(Player player, String kit, boolean fromCommand) {
        String key = player.getUniqueId() + "-" + kit.replaceAll("\\s", "").toLowerCase();
        try {
            player.getInventory().setContents((ItemStack[]) (Practice.kitMap.get(key)).get("items"));
            String name = player.getName();
            CustomPlayerDataHolder D0 = playerData.get(name);
            D0.setLastTimeKitWasUsed(System.currentTimeMillis());
            ObjectArrayList<Player> players = new ObjectArrayList<>(Bukkit.getOnlinePlayers());
            players.remove(player);
            if (players.size() > 48) {
                String lastTaggedBy = D0.getLastTaggedBy();
                if (lastTaggedBy == null) return;
                Player player1 = Bukkit.getPlayer(D0.getLastTaggedBy());
                if (player1 != null) player1.sendMessage("§4▪§7 " + name + " loaded a kit.");
            } else {
                Component component = Component.translatable("§4▪§7 " + name + " loaded a kit.");
                for (Player p : players) {
                    p.sendMessage(component);
                }
            }
            player.setHealth(20D);
            player.sendMessage("§7Loaded " + SECOND_COLOR + kit + "!");
        } catch (Exception e) {
            player.sendMessage(fromCommand ? ChatColor.GOLD + kit + ChatColor.RED + " has not been created! Type" + ChatColor.GOLD + " /kit" + ChatColor.RED + " or" + ChatColor.GOLD + " /k" + ChatColor.RED + " to get started!" : ChatColor.GOLD + kit + ChatColor.RED + " has not been created! Right click the chest to customize!");
        }
    }

    public static void claimPublicKit(Player player, String key) {
        player.getInventory().setContents((ItemStack[]) (Practice.kitMap.get(key)).get("items"));
        String name = (String) Practice.kitMap.get(key).get("player");
        String pn = player.getName();
        CustomPlayerDataHolder D0 = playerData.get(pn);
        D0.setLastTimeKitWasUsed(System.currentTimeMillis());
        ObjectArrayList<Player> players = new ObjectArrayList<>(Bukkit.getOnlinePlayers());
        players.remove(player);
        if (players.size() > 98) {
            String lastTaggedBy = D0.getLastTaggedBy();
            if (lastTaggedBy == null) return;
            Player player1 = Bukkit.getPlayer(D0.getLastTaggedBy());
            if (player1 != null) player1.sendMessage("§4▪§7 " + pn + " loaded " + name + "'s kit.");
        } else {
            Component component = Component.translatable("§4▪§7 " + pn + " loaded " + name + "'s kit");
            for (Player p : players) {
                p.sendMessage(component);
            }
        }
        player.sendMessage("§7Loaded " + SECOND_COLOR + name + "'s §7kit!");
    }
}
