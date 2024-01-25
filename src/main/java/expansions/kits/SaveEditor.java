package expansions.kits;

import main.utils.Constants;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;

public class SaveEditor {
    public static void save(Player player, int i, boolean sendMsg) {
        String key = player.getUniqueId() + "-kit" + i;
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

            Constants.kitMap.get(key).put("items", itemsArray);
            if (sendMsg)
                player.sendMessage("§dSaved §bKit " + i + "§d! Type §b/k" + i + "§d, §b/kit" + i + "§d, or §b/kit" + i + " §dto load!");
        } else {
            Constants.kitMap.get(key).remove("items");
            if (Constants.kitMap.get(key).containsKey("public")) {
                Constants.kitMap.get(key).remove("public");
                player.sendMessage("§6Kit " + i + " §cwas empty, so it was removed from public kits.");
            }
        }
    }
}
