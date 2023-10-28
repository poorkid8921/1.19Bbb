// 
// Decompiled by Procyon v0.5.36
// 

package main.utils;

import main.Practice;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class KitClaimer {
    public static void claim(Player player, int kit, boolean fromCommand) {
        String pn = player.getName();
        String key = pn + "-" + kit;
        try {
            player.getInventory().setContents((ItemStack[]) Practice.kitMap.get(key).get("items"));
            player.sendMessage("§dLoaded §bKit " + kit + "§d!");
            Bukkit.broadcastMessage("§4▪§7 " + pn + " §7loaded a kit.");
        } catch (Exception e) {
            player.sendMessage("§6Kit " + kit + " §chas not been created! " +
                    (fromCommand ?
                            "Type §6/kit §cor §6/k §cto get started!" :
                            "Right click the chest to customize!"));
        }
    }

    public static void claimFromName(Player player, String kit) {
        boolean hasKit = false;
        String pn = player.getName();
        for (int i = 1; i <= 3; ++i) {
            String key = pn + "-" + i;
            try {
                if (Practice.kitMap.get(key).get("name").toString().equalsIgnoreCase(kit)) {
                    player.getInventory().setContents((ItemStack[]) Practice.kitMap.get(key).get("items"));
                    String n = Practice.kitMap.get(key).get("name").toString();
                    player.sendMessage("§dLoaded §b" + n + "§d's kit!");
                    Bukkit.broadcastMessage("§4▪§7 " + pn + " §7loaded " + n + "''s kit.");
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
        player.getInventory().setContents((ItemStack[]) Practice.kitMap.get(key).get("items"));
        String name = Practice.kitMap.get(key).containsKey("name") ?
                Practice.kitMap.get(key).get("name").toString() :
                "Kit";
        player.sendMessage("§dLoaded §b" + name + "§d!");
        Bukkit.broadcastMessage("PLAYER LOADED 'S KIT");
    }
}
