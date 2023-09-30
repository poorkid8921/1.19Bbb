package commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static main.utils.Utils.translateo;

@SuppressWarnings("deprecation")
public class Discord implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player pp) {
            TextComponent a = new TextComponent(translateo("&7ᴊᴏɪɴ ᴏᴜʀ ᴅɪsᴄᴏʀᴅ sᴇʀᴠᴇʀ ᴜsɪɴɢ "));
            TextComponent hi = new TextComponent("ᴅɪsᴄᴏʀᴅ.ɢɢ/ᴀᴇsᴛʜᴇᴛɪᴄɴᴇᴛᴡᴏʀᴋ");
            hi.setColor(ChatColor.of("#fc282f"));
            hi.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/aestheticnetwork"));
            pp.sendMessage(a, hi);
        }

        return true;
    }
}