package main.managers;

import main.utils.Utils;
import net.md_5.bungee.api.chat.TextComponent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MessageManager {
    public HashMap<Character, Character> smallCapsDict = new HashMap<>();

    public final TextComponent DISCORD_USING = new TextComponent("§7ᴊᴏɪɴ ᴏᴜʀ ᴅɪsᴄᴏʀᴅ sᴇʀᴠᴇʀ ᴜsɪɴɢ ");
    public final TextComponent DISCORD_LINK = new TextComponent("ᴅɪsᴄᴏʀᴅ.ɢɢ/ᴄᴀᴛsᴍᴘ");

    public static final String MAIN_COLOR = Utils.translateA("#fc282f");
    public static final String SECOND_COLOR = Utils.translateA("#d6a7eb");

    public String EXPLOITING_KICK = MAIN_COLOR + "ʏᴏᴜ ᴀʀᴇ ʙᴀɴɴᴇᴅ ꜰʀᴏᴍ ᴄᴀᴛ ɴᴇᴛᴡᴏʀᴋ!\n\n" + "§7ʙᴀɴɴᴇᴅ ᴏɴ " + MAIN_COLOR + "» §7" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "\n" + "§7ʙᴀɴɴᴇᴅ ʙʏ " + MAIN_COLOR + "» §7ᴀɴᴛɪᴄʜᴇᴀᴛ\n" + "§7ʀᴇᴀsᴏɴ " + MAIN_COLOR + "» §7ᴄʜᴇᴀᴛɪɴɢ\n" + "§7ᴅᴜʀᴀᴛɪᴏɴ " + MAIN_COLOR + "» §730 days\n\n" + "§7ᴅɪsᴄᴏʀᴅ " + MAIN_COLOR + "» §7discord.gg/catsmp";

    public static String EXCEPTION_INTERACTION = SECOND_COLOR + "You can't interact here!";
    public static String EXCEPTION_PVP = SECOND_COLOR + "You can't PvP here!";
    public static String EXCEPTION_TAGGED = MAIN_COLOR + "ʏᴏᴜ ᴄᴀɴ'ᴛ ᴜsᴇ ᴄᴏᴍᴍᴀɴᴅs ɪɴ ᴄᴏᴍʙᴀᴛ!";

    public MessageManager() {
        /*smallCapsDict.putAll(Map.of(
                'a', 'ᴀ',
                'b', 'ʙ',
                'c', 'ᴄ',
                'd', 'ᴅ',
                'e', 'ᴇ',
                'f', 'ꜰ',
                'g', 'ɢ',
                'h', 'ʜ',
                'i', 'ɪ',
                'j', 'ᴊ'));
        smallCapsDict.putAll(Map.of(
                'k', 'ᴋ',
                'l', 'ʟ',
                'm', 'ᴍ',
                'n', 'ɴ',
                'o', 'ᴏ',
                'p', 'ᴘ',
                'q', 'ǫ',
                'r', 'ʀ',
                's', 'ꜱ',
                't', 'ᴛ'));
        smallCapsDict.putAll(Map.of(
                'u', 'ᴜ',
                'v', 'ᴠ',
                'w', 'ᴡ',
                'x', 'x',
                'y', 'ʏ',
                'z', 'ᴢ'));

        for (Map.Entry<Character, Character> entry : smallCapsDict.entrySet()) {
            char key = entry.getKey();
            char value = entry.getValue();

            EXCEPTION_TAGGED = EXCEPTION_TAGGED.replace(key, value);
            EXPLOITING_KICK = EXPLOITING_KICK.replace(key, value);
        }*/
    }
}
