package main.utils.instances;

import lombok.Getter;
import org.bukkit.command.CommandExecutor;

@Getter
public class CommandHolder {
    private final String name;
    private final CommandExecutor clazz;

    public CommandHolder(String name,
                         CommandExecutor clazz) {
        this.name = name;
        this.clazz = clazz;
    }
}
