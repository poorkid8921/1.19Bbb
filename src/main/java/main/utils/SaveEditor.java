package main.utils;

import main.Practice;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class SaveEditor {
    public static void save(Player player, String i, boolean sendMsg) {
        String key = player.getUniqueId() + "-kit" + i;
        String kitName = "Kit " + i;
        ItemStack[] itemsArray = Arrays.copyOfRange(player.getOpenInventory()
                .getTopInventory().getContents(), 0, 41);
        ItemStack[] equipmentArray = new ItemStack[5];
        boolean arrayIsEmpty = true;
        for (int j = 0; j <= 40; ++j) {
            if (itemsArray[j] != null) {
                arrayIsEmpty = false;
                break;
            }
        }
        if (Practice.kitMap.get(key).containsKey("name")) {
            kitName = Practice.kitMap.get(key).get("name").toString();
        }
        if (!arrayIsEmpty) {
            for (int j = 36; j <= 40; ++j) {
                equipmentArray[j - 36] = itemsArray[j];
                itemsArray[j] = null;
            }
            for (int j = 0; j <= 4; ++j) {
                if (equipmentArray[j] != null) {
                    if (equipmentArray[j].getType().toString().contains("BOOTS")) {
                        itemsArray[36] = equipmentArray[j];
                    } else if (equipmentArray[j].getType().toString().contains("LEGGINGS")) {
                        itemsArray[37] = equipmentArray[j];
                    } else if (equipmentArray[j].getType().toString().contains("CHESTPLATE") || equipmentArray[j].getType().toString().contains("ELYTRA")) {
                        itemsArray[38] = equipmentArray[j];
                    } else if (equipmentArray[j].getType().toString().contains("HELMET")) {
                        itemsArray[39] = equipmentArray[j];
                    } else {
                        itemsArray[40] = equipmentArray[j];
                    }
                }
            }
            Practice.kitMap.get(key).put("items", itemsArray);
            if (sendMsg) {
                player.sendMessage("§dSaved §b" + kitName + "§d! Type §b/k" + i +
                        (kitName.equals("Kit " + i) ?
                                " §dor §b/kit" + i + " §dto load!" :
                                "§d, or §b/kit " + kitName + " §dto load!"));
            }
        } else {
            Practice.kitMap.get(key).remove("items");
            if (Practice.kitMap.get(key).containsKey("public")) {
                Practice.kitMap.get(key).remove("public");
                player.sendMessage("§6" + kitName + " §cwas empty, so it was removed from public kits.");
            }
        }
    }
}
