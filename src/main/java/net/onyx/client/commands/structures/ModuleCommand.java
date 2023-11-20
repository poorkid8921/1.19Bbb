package net.onyx.client.commands.structures;

import net.onyx.client.commands.settings.SettingsCommand;
import net.onyx.client.modules.Module;

public class ModuleCommand extends CommandNode {
    public Module module;

    public ModuleCommand(String command, Module module) {
        super(command, module.getDescription());
        
        this.module = module;

        // Register the settings.
        if (module.getSettings().size() > 0) this.addSubCommand(new SettingsCommand(this.module));

        // Add module specific commands
        for (Command cmd : this.module.getCommands()) {
            this.addSubCommand(cmd);
        }
    }

    @Override
    public Boolean trigger(String[] args) {
        if (args.length == 0) {
            // Toggle the module.

            module.toggle();
            return true;
        }

        // Handle help
        if (handleHelp(args)) return true;

        return super.trigger(args);
    }
}
