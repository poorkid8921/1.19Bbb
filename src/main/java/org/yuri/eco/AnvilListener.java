package org.yuri.eco;

import org.yuri.eco.utils.Initializer;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.yuri.eco.utils.Utils;

@SuppressWarnings("deprecation")
public class AnvilListener implements Listener {
    public static void updateColorTranslationForAnvilOutput(AnvilInventory anvilInventory) {
        new BukkitRunnable() {
            @Override
            public void run() {
                ItemStack inputItem = anvilInventory.getItem(0);
                ItemStack outputItem = anvilInventory.getItem(2);
                if (outputItem == null)
                    return;

                translateoOutputItemNameColorBasedOnInputItem(outputItem, inputItem);
            }
        }.runTaskLater(Initializer.p, 1L);
    }

    public static void translateoOutputItemNameColorBasedOnInputItem(ItemStack outputItem, ItemStack inputItem) {
        ItemMeta outputItemMeta = outputItem.getItemMeta();
        if (outputItemMeta == null || !outputItemMeta.hasDisplayName())
            return;

        String outputName = outputItemMeta.getDisplayName();
        ItemMeta inputItemMeta = inputItem.getItemMeta();
        if (inputItemMeta == null || !inputItemMeta.hasDisplayName()) {
            translateoNameColor(outputItem);
            return;
        }

        String inputName = inputItemMeta.getDisplayName();

        if (inputName.equals(""))
            inputName = "NULL";

        if (doesOutputNameMatchInputName(outputName, inputName)) {
            outputItemMeta.setDisplayName(inputName);
            outputItem.setItemMeta(outputItemMeta);
        }
        translateoNameColor(outputItem);
    }

    public static void translateoNameColor(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null)
            return;

        String untranslateodName = itemMeta.getDisplayName();
        String translateodName = Utils.translateo(untranslateodName);
        itemMeta.setDisplayName(translateodName);
        itemStack.setItemMeta(itemMeta);
    }

    public static boolean doesOutputNameMatchInputName(String outputName, String inputName) {
        return stripChars(outputName, '&', ChatColor.COLOR_CHAR).equals(stripChars(inputName, ChatColor.COLOR_CHAR));
    }

    public static String stripChars(String str, char... chars) {
        String strippedStr = str;
        for (char c : chars) {
            strippedStr = str.replaceAll(String.valueOf(c), "");
        }
        return strippedStr;
    }

    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        ItemStack newItem = event.getResult();
        if (newItem == null)
            return;

        if (event.getViewers().isEmpty())
            return;

        HumanEntity p = event.getViewers().get(0);

        AnvilInventory anvilInventory = event.getInventory();
        if (p.hasPermission("coloranvils.use"))
            updateColorTranslationForAnvilOutput(anvilInventory);
    }
}