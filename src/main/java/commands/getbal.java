package commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class getbal implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        File[] rc = new File(Bukkit.getWorld("world")
                .getWorldFolder()
                .getAbsolutePath() + "/logs/").listFiles();

        try {
            Pattern p = Pattern.compile("\\$[0-9]+(.[0-9]+)?\\w", Pattern.MULTILINE);
            String toLookup = "???? » " + strings[0] + " bought";
            String toLookup2 = "???? » " + strings[0] + " sold";
            double sold = 0.0;
            double bought = 0.0;
            int fails = 0;
            for (File r : Arrays.stream(rc).sorted().toList()) {
                try {
                    Scanner myReader = new Scanner(r);
                    while (myReader.hasNextLine()) {
                        String data = myReader.nextLine();
                        if (data.contains(toLookup2)) {
                            Matcher dc = p.matcher(data.replace(",", ""));
                            while (dc.find()) {
                                sold += Double.parseDouble(dc.group(0).replace("$", ""));
                                Bukkit.getLogger().warning(data);
                            }
                        } else if (data.contains(toLookup)) {
                            Matcher dc = p.matcher(data.replace(",", ""));
                            while (dc.find()) {
                                bought += Double.parseDouble(dc.group(0).replace("$", ""));
                                Bukkit.getLogger().warning(data);
                            }
                        } else
                            fails++;
                    }
                    myReader.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

            Bukkit.getLogger().warning("sum: " + new DecimalFormat("#0.00").format(sold - bought) + " | fails: " + fails);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        return true;
    }
}
