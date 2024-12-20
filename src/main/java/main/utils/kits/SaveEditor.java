package main.utils.kits;

import main.Practice;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class SaveEditor {
    public static void save(Player player, int i, boolean sendMsg) {
        String key = player.getUniqueId() + "-kit" + i;
        String kitName = "Kit " + i;
        ItemStack[] itemsArray = Arrays.copyOfRange(player.getOpenInventory().getTopInventory().getContents(), 0, 41);
        ItemStack[] equipmentArray = new ItemStack[5];
        boolean arrayIsEmpty = true;
        int j;
        for (j = 0; j <= 40; ++j) {
            if (itemsArray[j] != null) {
                arrayIsEmpty = false;
                break;
            }
        }

        if (!arrayIsEmpty) {
            for (j = 36; j <= 40; ++j) {
                equipmentArray[j - 36] = itemsArray[j];
                itemsArray[j] = null;
            }

            for (j = 0; j <= 4; ++j) {
                if (equipmentArray[j] != null) {
                    if (equipmentArray[j].getType().toString().contains("BOOTS")) {
                        itemsArray[36] = equipmentArray[j];
                    } else if (equipmentArray[j].getType().toString().contains("LEGGINGS")) {
                        itemsArray[37] = equipmentArray[j];
                    } else if (!equipmentArray[j].getType().toString().contains("CHESTPLATE") && !equipmentArray[j].getType().toString().contains("ELYTRA")) {
                        if (equipmentArray[j].getType().toString().contains("HELMET")) {
                            itemsArray[39] = equipmentArray[j];
                        } else {
                            itemsArray[40] = equipmentArray[j];
                        }
                    } else {
                        itemsArray[38] = equipmentArray[j];
                    }
                }
            }

            Practice.kitMap.get(key).put("items", itemsArray);
            if (sendMsg)
                player.sendMessage("§dSaved §b" + kitName + "§d! Type §b/k" + i + "§d or§b /kit" + i + "§d to load!");
        } else {
            Practice.kitMap.get(key).remove("items");
            if (Practice.kitMap.get(key).containsKey("public")) {
                Practice.kitMap.get(key).remove("public");
                player.sendMessage("§6" + kitName + " §cwas empty, so it was removed from public kits.");
            }
        }
    }
}
