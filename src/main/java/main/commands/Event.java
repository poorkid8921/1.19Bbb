package main.commands;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import main.Economy;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class Event implements CommandExecutor, TabExecutor {
    public static ObjectArrayList<String> verified = new ObjectArrayList<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String sn = sender.getName();
        if (verified.contains(sn))
            return true;

        verified.add(sn);
        if (Objects.equals(args[0], "y")) Economy.vote_yes++;
        else
            Economy.vote_no++;
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return List.of();
    }
}
