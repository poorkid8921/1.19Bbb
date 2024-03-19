package main.utils.kits;

import main.Practice;
import main.utils.Instances.CustomPlayerDataHolder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static main.utils.Initializer.MAIN_COLOR;
import static main.utils.Initializer.playerData;

public class KitClaimer {
    public static void claim(Player player, String kit, boolean fromCommand) {
        String key = player.getUniqueId() + "-" + kit.replaceAll("\\s", "").toLowerCase();

        try {
            player.getInventory().setContents((ItemStack[]) (Practice.kitMap.get(key)).get("items"));
            player.sendMessage("§7Loaded" + MAIN_COLOR + " " + kit + "!");
            String name = player.getName();
            CustomPlayerDataHolder D0 = playerData.get(name);
            D0.setLastTimeKitWasUsed(System.currentTimeMillis());
            String lastTaggedBy = D0.getLastTaggedBy();
            if (lastTaggedBy == null)
                return;
            Player player1 = Bukkit.getPlayer(D0.getLastTaggedBy());
            if (player1 != null)
                player1.sendMessage("§4▪§7 " + name + " loaded a kit.");
        } catch (Exception e) {
            player.sendMessage(fromCommand ? ChatColor.GOLD + kit + ChatColor.RED + " has not been created! Type" + ChatColor.GOLD + " /kit" + ChatColor.RED + " or" + ChatColor.GOLD + " /k" + ChatColor.RED + " to get started!" :
                    ChatColor.GOLD + kit + ChatColor.RED + " has not been created! Right click the chest to customize!");
        }
    }

    public static void claimFromName(Player player, String kit) {
        boolean hasKit = false;

        for (int i = 1; i <= 3; ++i) {
            String key = player.getUniqueId() + "-kit" + i;

            try {
                if (((String) Practice.kitMap.get(key).get("name")).equalsIgnoreCase(kit)) {
                    if (Practice.kitMap.get(key).containsKey("items")) {
                        player.getInventory().setContents((ItemStack[]) (Practice.kitMap.get(key)).get("items"));
                        player.sendMessage(ChatColor.LIGHT_PURPLE + "Loaded" + ChatColor.AQUA + " " + Practice.kitMap.get(key).get("name").toString() + ChatColor.LIGHT_PURPLE + "!");
                        String name = player.getName();
                        CustomPlayerDataHolder D0 = playerData.get(name);
                        D0.setLastTimeKitWasUsed(System.currentTimeMillis());
                        String lastTaggedBy = D0.getLastTaggedBy();
                        if (lastTaggedBy == null)
                            return;
                        Player player1 = Bukkit.getPlayer(D0.getLastTaggedBy());
                        if (player1 != null)
                            player1.sendMessage("§4▪§7 " + name + " loaded a kit.");
                    } else {
                        player.sendMessage("§6" + Practice.kitMap.get(key).get("name").toString() + "§c has no items.");
                    }

                    hasKit = true;
                    break;
                }
            } catch (Exception ignored) {
            }
        }

        if (!hasKit)
            player.sendMessage("§cNo kit by that name.");
    }

    public static void claimPublicKit(Player player, String key) {
        player.getInventory().setContents((ItemStack[]) (Practice.kitMap.get(key)).get("items"));
        String name = "Kit";
        if (Practice.kitMap.get(key).containsKey("name"))
            name = (String) Practice.kitMap.get(key).get("name");
        player.sendMessage(ChatColor.LIGHT_PURPLE + "Loaded " + ChatColor.AQUA + name + ChatColor.LIGHT_PURPLE + "!");
        String pn = player.getName();
        CustomPlayerDataHolder D0 = playerData.get(pn);
        D0.setLastTimeKitWasUsed(System.currentTimeMillis());
        String lastTaggedBy = D0.getLastTaggedBy();
        if (lastTaggedBy == null)
            return;
        Player player1 = Bukkit.getPlayer(D0.getLastTaggedBy());
        if (player1 != null)
            player1.sendMessage("§4▪§7 " + pn + " loaded a kit.");
    }
}
