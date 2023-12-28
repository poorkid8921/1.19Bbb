package main.utils;

import io.netty.buffer.ByteBuf;
import lombok.NonNull;
import lombok.SneakyThrows;
import main.expansions.scoreboard.ChannelInjector;
import main.expansions.scoreboard.util.buffer.ByteBufNetOutput;
import main.expansions.scoreboard.util.buffer.NetOutput;
import main.utils.Instances.CustomPlayerDataHolder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static main.utils.Initializer.playerData;
import static main.utils.Initializer.*;
import static org.bukkit.ChatColor.COLOR_CHAR;

@SuppressWarnings("deprecation")
public class Utils {
    static Pattern HEX_PATTERN = Pattern.compile("#([A-Fa-f0-9]{6})");

    public static String translate(String text) {
        Matcher matcher = HEX_PATTERN.matcher(text);
        StringBuilder buffer = new StringBuilder(text.length() + 32);
        while (matcher.find()) {
            String group = matcher.group(1);
            matcher.appendReplacement(buffer, COLOR_CHAR + "x" + COLOR_CHAR + group.charAt(0) + COLOR_CHAR + group.charAt(1) + COLOR_CHAR + group.charAt(2) + COLOR_CHAR + group.charAt(3) + COLOR_CHAR + group.charAt(4) + COLOR_CHAR + group.charAt(5));
        }
        return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());
    }

    public static String translateA(String text) {
        Matcher matcher = HEX_PATTERN.matcher(text);
        StringBuilder buffer = new StringBuilder(text.length() + 32);
        while (matcher.find()) {
            String group = matcher.group(1);
            matcher.appendReplacement(buffer, COLOR_CHAR + "x" + COLOR_CHAR + group.charAt(0) + COLOR_CHAR + group.charAt(1) + COLOR_CHAR + group.charAt(2) + COLOR_CHAR + group.charAt(3) + COLOR_CHAR + group.charAt(4) + COLOR_CHAR + group.charAt(5));
        }
        return matcher.appendTail(buffer).toString();
    }

    public static void killeffect(Player p, int toset, String fancy, int money) {
        p.closeInventory();
        String pn = p.getName();
        CustomPlayerDataHolder D = playerData.get(pn);
        if (money > D.getMoney()) {
            p.sendMessage(SECOND_COLOR + "ꜱʜᴏᴘ » " + MAIN_COLOR + "ʏᴏᴜ ᴅᴏɴ'ᴛ ʜᴀᴠᴇ ᴇɴᴏᴜɢʜ ᴍᴏɴᴇʏ");
            return;
        }
        D.decrementMoney(money);
        D.setC(toset);
        p.sendMessage(SECOND_COLOR + "ꜱʜᴏᴘ » §f" + (toset == -1 ? "ʏᴏᴜʀ ᴋɪʟʟ ᴇꜰꜰᴇᴄᴛ ʜᴀs ʙᴇᴇɴ ʀᴇᴍᴏᴠᴇᴅ" :
                "ʏᴏᴜ ʜᴀᴠᴇ ꜱᴜᴄᴄᴇꜱꜱꜰᴜʟʟʏ ᴘᴜʀᴄʜᴀꜱᴇ ᴛʜᴇ " + MAIN_COLOR + fancy + " §fꜰᴏʀ " + SECOND_COLOR + "$" + money));
    }

    public static void report(Player pp, String report, String reason) {
        String d = pp.getDisplayName();
        Bukkit.getOnlinePlayers().stream().filter(r -> r.hasPermission("has.staff")).forEach(r -> r.sendMessage(MAIN_COLOR + d + " §7has submitted a report against " + MAIN_COLOR +
                report + (reason == null ? "" : " §7with the reason of " + MAIN_COLOR + reason)));
        pp.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
        Bukkit.getScheduler().runTaskAsynchronously(Initializer.p, () -> {
            String avturl = "https://mc-heads.net/avatar/" + pp.getName() + "/100";
            DiscordWebhook webhook = new DiscordWebhook("https://discord.com/api/webhooks/1188919657088946186/ZV0kpZI_P6KLzz_d_LVbGmVgj94DLwOJBNQylbayYUJo0zz0L8xVZzG7tPP9BOlt4Bip");
            webhook.setAvatarUrl(avturl);
            webhook.setUsername("Report");
            webhook.addEmbed(reason == null ?
                    new DiscordWebhook.EmbedObject().setTitle("Report").addField("Server", "Practice", true).addField("Sender", pp.getName(), true).addField("Reason", report, true) :
                    new DiscordWebhook.EmbedObject().setTitle("Report").addField("Server", "Practice", true).addField("Sender", pp.getName(), true).addField("Target", report, true).addField("Reason", reason, true).setThumbnail(avturl).setColor(java.awt.Color.ORANGE));

            try {
                webhook.execute();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        pp.sendMessage("§7Successfully submitted the report.");
    }

    public static ItemStack createItemStack(Material mat, String display, List<String> lore) {
        ItemStack ie = new ItemStack(mat, 1);
        ItemMeta iem = ie.getItemMeta();
        iem.setDisplayName(display);
        iem.setLore(lore);
        ie.setItemMeta(iem);
        return ie;
    }

    public static ItemStack createItemStack(ItemStack ie, String display, List<String> lore) {
        ItemMeta iem = ie.getItemMeta();
        iem.setDisplayName(display);
        iem.setLore(lore);
        ie.setItemMeta(iem);
        return ie;
    }

    public static ItemStack getHead(String player) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1, (short) 3);
        SkullMeta skull = (SkullMeta) item.getItemMeta();
        skull.setDisplayName(player);
        skull.setOwner(player);
        item.setItemMeta(skull);
        return item;
    }

    public static ByteBuf getPacket(Player player,
                                    int mode,
                                    String a) {
        ByteBuf buf = ChannelInjector.getChannel(player).alloc().buffer();

        NetOutput output = new ByteBufNetOutput(buf);
        output.writeVarInt(0x58);
        output.writeString(a);
        output.writeByte(mode);

        output.writeString(TITLE);
        output.writeVarInt(0);
        return buf;
    }

    @SneakyThrows
    public static void sendPacket(Player player, ByteBuf packet) {
        ChannelInjector.sendPacket(player, packet);
    }

    public static ByteBuf createScorePacket(@NonNull Player player, int action, String objectiveName, int index) {
        ByteBuf buf = ChannelInjector.getChannel(player).alloc().buffer();
        NetOutput output = new ByteBufNetOutput(buf);

        output.writeVarInt(0x5B);
        output.writeString(ChatColor.values()[index].toString());
        output.writeVarInt(action);
        output.writeString(objectiveName);

        if (action != 1) {
            output.writeVarInt(0);
        }
        return buf;
    }

    @SneakyThrows
    public static ByteBuf createTeamPacket(int mode, int index,
                                           @NonNull String teamName,
                                           Player player,
                                           String text) {
        String teamEntry = ChatColor.values()[index].toString();
        ByteBuf buf = ChannelInjector.getChannel(player).alloc().buffer();
        NetOutput packet = new ByteBufNetOutput(buf);
        packet.writeVarInt(0x5A);

        packet.writeString(teamName);
        packet.writeByte(mode);

        packet.writeString("{\"text\":\"\"}");
        packet.writeByte(10);
        packet.writeString("always");
        packet.writeString("always");
        packet.writeVarInt(21);
        packet.writeString(text);
        packet.writeString("{\"text\":\"\"}");

        if (mode == 0) {
            packet.writeVarInt(1);
            packet.writeString(teamEntry);
        }
        return buf;
    }
}