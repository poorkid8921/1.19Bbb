package main.utils.modules.kits;

import main.Practice;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

import static main.utils.Initializer.SECOND_COLOR;

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

        if (Practice.kitMap.get(key).containsKey("name")) {
            kitName = (String) Practice.kitMap.get(key).get("name");
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
            if (sendMsg) {
                if (!kitName.equals("Kit " + i)) {
                    player.sendMessage("§7Saved " + SECOND_COLOR + kitName + "! §7Type " + SECOND_COLOR + "/k" + i + "§7, " + SECOND_COLOR + "/kit" + i + "§7, or " + SECOND_COLOR + "/kit " + kitName + " §7to load!");
                } else {
                    player.sendMessage(ChatColor.LIGHT_PURPLE + "Saved " + ChatColor.AQUA + kitName + ChatColor.LIGHT_PURPLE + "! Type" + ChatColor.AQUA + " /k" + i + ChatColor.LIGHT_PURPLE + " or" + ChatColor.AQUA + " /kit" + i + ChatColor.LIGHT_PURPLE + " to load!");
                }
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