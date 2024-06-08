package main.utils.instances;

import main.utils.Initializer;
import org.bukkit.command.CommandExecutor;

public class CommandHolder {
    public CommandHolder(String name,
                         CommandExecutor clazz) {
        Initializer.p.getCommand(name).setExecutor(clazz);
    }
}
