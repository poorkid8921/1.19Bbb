package expansions.kits;

import main.utils.Constants;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class KitClaimer {
   public static void claim(Player player, int kit, boolean fromCommand) {
      String key = player.getUniqueId() + "-" + kit;
      try {
         player.getInventory().setContents((ItemStack[]) Constants.kitMap.get(key).get("items"));
         player.sendMessage(ChatColor.LIGHT_PURPLE + "Loaded" + ChatColor.AQUA + " Kit " + kit + ChatColor.LIGHT_PURPLE + "!");
         Bukkit.getServer().broadcastMessage("§4▪§7 " + player.getName() + " loaded a kit.");
      } catch (Exception e) {
         player.sendMessage(ChatColor.GOLD + "Kit " + kit + ChatColor.RED + " has not been created! " +
                 (fromCommand ?
                         "Type" + ChatColor.GOLD + " /kit" + ChatColor.RED + " or" + ChatColor.GOLD + " /k" + ChatColor.RED + " to get started!" :
                         "Right click the chest to customize!"));
      }
   }

   public static void claimPublicKit(Player player, String key) {
      player.getInventory().setContents((ItemStack[]) Constants.kitMap.get(key).get("items"));
      player.sendMessage(ChatColor.LIGHT_PURPLE + "Loaded " + ChatColor.AQUA + "Kit" + ChatColor.LIGHT_PURPLE + "!");
      Bukkit.getServer().broadcastMessage("§4▪§7 " + player.getName() + " loaded " + Constants.kitMap.get(key).get("player") + "'s kit.");
   }
}
