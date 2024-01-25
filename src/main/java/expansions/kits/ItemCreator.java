package expansions.kits;

import java.util.List;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class ItemCreator {
   public static ItemStack getItem(String name, Material mat, List<String> lore) {
      ItemStack item = new ItemStack(mat, 1);
      ItemMeta meta = item.getItemMeta();
      meta.setDisplayName(name);
      meta.setLore(lore);
      item.setItemMeta(meta);
      return item;
   }

   public static ItemStack getHead(String name, String player, List<String> lore) {
      ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1);
      SkullMeta meta = (SkullMeta)item.getItemMeta();
      meta.setOwner(player);
      meta.setDisplayName(name);
      meta.setLore(lore);
      item.setItemMeta(meta);
      return item;
   }

   public static ItemStack enchant(ItemStack item) {
      ItemMeta meta = item.getItemMeta();
      meta.addEnchant(Enchantment.DURABILITY, 1, true);
      item.setItemMeta(meta);
      return item;
   }

   public static ItemStack disEnchant(ItemStack item) {
      ItemMeta meta = item.getItemMeta();
      meta.removeEnchant(Enchantment.DURABILITY);
      item.setItemMeta(meta);
      return item;
   }
}
