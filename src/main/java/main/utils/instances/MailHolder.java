package main.utils.instances;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;

@Getter
public class MailHolder {
    private final String sender;
    private final ItemStack[] contents;

    public MailHolder(String sender, ItemStack[] contents) {
        this.sender = sender;
        this.contents = contents;
    }
}
