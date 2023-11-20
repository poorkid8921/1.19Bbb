package net.onyx.client.commands.structures;

import net.onyx.client.utils.ChatUtils;

import java.util.ArrayList;
import java.util.List;

public class Command {
    private final String command;
    private final String helpText;
    private String description = "A really cool command!";

    public String commandDisplay = "Onyx.ss v4";

    public String combineArgs(String[] args) {
        return String.join(" ", args);
    }

    public void displayChatMessage(String message) {
        ChatUtils.displayMessage(
            String.format("%s%s", ChatUtils.chatPrefix(commandDisplay), message)
        );
    }

    public void displayChatMessage(String format, Object... items) {
        this.displayChatMessage(String.format(format, items));
    }

    public String getCommand() {
        return this.command;
    }

    public List<String> getSuggestions() {
        return new ArrayList<>();
    }

    public String getDescription() {
        return this.description;
    }

    public String getHelpText() {
        return this.helpText;
    }

    public Command(String command, String helpText, String description) {
        this.command = command;
        this.helpText = helpText;
        this.description = description;
    }

    public boolean shouldShowHelp(String[] args) {
        return args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase("help"));
    }

    public Boolean handleHelp(String[] args) {
        if (!this.shouldShowHelp(args)) return false;

        this.displayChatMessage(String.format("%s%s", ChatUtils.WHITE, this.getHelpText()));
        return true;
    }

    // Returning false should give display the help text.
    // TODO make a raw message thing too.
    public Boolean trigger(String[] args) {
        return handleHelp(args); 
    }
}