package main.utils.modules.kits.events;

import main.utils.modules.kits.SaveEditor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.Collections;

import static main.Practice.editorChecker;

public class EditorCloseEvent implements Listener {
    @EventHandler
    public void onGUIClose(InventoryCloseEvent event) {
        for (int i = 1; i <= 3; ++i) {
            if (editorChecker.contains(event.getPlayer().getUniqueId() + "-kit" + i)) {
                SaveEditor.save((Player) event.getPlayer(), i, true);
                String key = event.getPlayer().getUniqueId() + "-kit" + i;
                editorChecker.removeAll(Collections.singleton(key));
                break;
            }
        }
    }
}
