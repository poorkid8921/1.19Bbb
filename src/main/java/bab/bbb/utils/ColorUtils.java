package bab.bbb.utils;

import org.bukkit.ChatColor;
public class ColorUtils {
    static final String ALL_CODE_REGEX = "[§&][0-9a-f-A-Fk-rK-R]";
    static final String HEX_CODE_REGEX = "#[a-fA-F0-9]{6}";

    public static String removeColorCodes(String string) {
        String a = ChatColor.stripColor(string).replaceAll(ALL_CODE_REGEX, "");
        return ChatColor.stripColor(a).replace(HEX_CODE_REGEX, "");
    }

    public static String formatString(String string, boolean overrideDefaultFormat) {
        if (!overrideDefaultFormat || string.startsWith("&r")) {
            return Methods.translatestring(string);
        } else {
            return Methods.translatestring("&r" + string);
        }
    }

    public static String extractArgs(int nondik, String[] args) {
        StringBuilder sb = new StringBuilder();
        for (int i = nondik; i < args.length; i++) {
            sb.append(args[i]).append(" ");
        }
        String allArgs = sb.toString().trim();

        allArgs = allArgs.replace("[<3]", "\u2764");
        allArgs = allArgs.replace("[ARROW]", "\u279c");
        allArgs = allArgs.replace("[TICK]", "\u2714");
        allArgs = allArgs.replace("[X]", "\u2716");
        allArgs = allArgs.replace("[STAR]", "\u2605");
        allArgs = allArgs.replace("[POINT]", "\u25Cf");
        allArgs = allArgs.replace("[FLOWER]", "\u273f");
        allArgs = allArgs.replace("[XD]", "\u263b");
        allArgs = allArgs.replace("[DANGER]", "\u26a0");
        allArgs = allArgs.replace("[MAIL]", "\u2709");
        allArgs = allArgs.replace("[ARROW2]", "\u27a4");
        allArgs = allArgs.replace("[ROUND_STAR]", "\u2730");
        allArgs = allArgs.replace("[SUIT]", "\u2666");
        allArgs = allArgs.replace("[+]", "\u2726");
        allArgs = allArgs.replace("[CIRCLE]", "\u25CF");
        allArgs = allArgs.replace("[SUN]", "\u2739");
        return allArgs;
    }
}