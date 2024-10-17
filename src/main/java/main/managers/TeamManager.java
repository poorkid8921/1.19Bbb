package main.managers;

import main.managers.instances.PlayerDataHolder;
import main.utils.Utils;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_19_R3.util.CraftChatMessage;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import static main.Economy.databaseManager;

public class TeamManager {
    public String CATTO_LOVES = "§dᴄᴀᴛᴛᴏ ʟᴏᴠᴇs §r";
    public String CATTO_HATES = Utils.translateA("#2e2e2e") + "ᴄᴀᴛᴛᴏ ʜᴀᴛᴇs §r";
    public String GAY = Utils.translateA("#fb0000ɢ#56fa35ᴀ#ff00deʏ") + " §r";
    public String VIP = Utils.translateA("#faf739") + "ᴠɪᴘ §r";
    public String BOOSTER = Utils.translateA("#e900ff") + "ʙᴏᴏꜱᴛᴇʀ §r";
    public String MEDIA = Utils.translateA("#ffc2c2") + "ᴍᴇᴅɪᴀ §r";
    public String T_HELPER = Utils.translateA("#06dce4") + "ᴛ. ʜᴇʟᴘᴇʀ §r";
    public String HELPER = Utils.translateA("#00dd04") + "ʜᴇʟᴘᴇʀ §r";
    public String JRMOD = Utils.translateA("#ff7e13") + "ᴊʀ. ᴍᴏᴅ §r";
    public String MOD = Utils.translateA("#d10000") + "ᴍᴏᴅ §r";
    public String ADMIN = Utils.translateA("#47aeee") + "ᴀᴅᴍɪɴ §r";
    public String MANAGER = Utils.translateA("#d10000") + "ᴍᴀɴᴀɢᴇʀ §r";
    public String EXECUTIVE = Utils.translateA("#2494fb") + "ᴏᴡɴᴇʀ §r";

    public Scoreboard scoreboard;
    public Team ownerTeam;
    public Team managerTeam;
    public Team adminTeam;
    public Team modTeam;
    public Team jrmodTeam;
    public Team helperTeam;
    public Team trialHelperTeam;
    public Team mediaTeam;
    public Team boosterTeam;
    public Team vipTeam;
    public Team cattoLovesTeam;
    public Team cattoHatesTeam;
    public Team gayTeam;

    public TeamManager() {
        scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        try {
            ownerTeam = scoreboard.registerNewTeam("a");
            ownerTeam.setPrefix(EXECUTIVE);
            managerTeam = scoreboard.registerNewTeam("b");
            managerTeam.setPrefix(MANAGER);
            adminTeam = scoreboard.registerNewTeam("c");
            adminTeam.setPrefix(ADMIN);
            modTeam = scoreboard.registerNewTeam("d");
            modTeam.setPrefix(MOD);
            jrmodTeam = scoreboard.registerNewTeam("e");
            jrmodTeam.setPrefix(JRMOD);
            helperTeam = scoreboard.registerNewTeam("f");
            helperTeam.setPrefix(HELPER);
            trialHelperTeam = scoreboard.registerNewTeam("g");
            trialHelperTeam.setPrefix(T_HELPER);
            mediaTeam = scoreboard.registerNewTeam("h");
            mediaTeam.setPrefix(MEDIA);
            boosterTeam = scoreboard.registerNewTeam("i");
            boosterTeam.setPrefix(BOOSTER);
            vipTeam = scoreboard.registerNewTeam("j");
            vipTeam.setPrefix(VIP);
            cattoLovesTeam = scoreboard.registerNewTeam("k");
            cattoLovesTeam.setPrefix(CATTO_LOVES);
            cattoHatesTeam = scoreboard.registerNewTeam("l");
            cattoHatesTeam.setPrefix(CATTO_HATES);
            gayTeam = scoreboard.registerNewTeam("m");
            gayTeam.setPrefix(GAY);
        } catch (Exception e) {
            ownerTeam = scoreboard.getTeam("a");
            ownerTeam.removeEntries(ownerTeam.getEntries());
            managerTeam = scoreboard.getTeam("b");
            managerTeam.removeEntries(managerTeam.getEntries());
            adminTeam = scoreboard.getTeam("c");
            adminTeam.removeEntries(adminTeam.getEntries());
            modTeam = scoreboard.getTeam("d");
            modTeam.removeEntries(modTeam.getEntries());
            jrmodTeam = scoreboard.getTeam("e");
            jrmodTeam.removeEntries(jrmodTeam.getEntries());
            helperTeam = scoreboard.getTeam("f");
            helperTeam.removeEntries(helperTeam.getEntries());
            trialHelperTeam = scoreboard.getTeam("g");
            trialHelperTeam.removeEntries(trialHelperTeam.getEntries());
            mediaTeam = scoreboard.getTeam("h");
            mediaTeam.removeEntries(mediaTeam.getEntries());
            boosterTeam = scoreboard.getTeam("i");
            boosterTeam.removeEntries(boosterTeam.getEntries());
            vipTeam = scoreboard.getTeam("j");
            vipTeam.removeEntries(vipTeam.getEntries());
            cattoLovesTeam = scoreboard.getTeam("k");
            cattoLovesTeam.removeEntries(cattoLovesTeam.getEntries());
            cattoHatesTeam = scoreboard.getTeam("l");
            cattoHatesTeam.removeEntries(cattoHatesTeam.getEntries());
            gayTeam = scoreboard.getTeam("m");
            gayTeam.removeEntries(gayTeam.getEntries());
        }
    }

    public void addPlayerToTeam(PlayerDataHolder holder, String name, Player player) {
        switch (databaseManager.setData(name, holder)) {
            case 1 -> cattoLovesTeam.addPlayer(player);
            case 2 -> cattoHatesTeam.addPlayer(player);
            case 3 -> gayTeam.addPlayer(player);
            case 4 -> vipTeam.addPlayer(player);
            case 5 -> boosterTeam.addPlayer(player);
            case 6 -> mediaTeam.addPlayer(player);
            case 7 -> trialHelperTeam.addPlayer(player);
            case 8 -> helperTeam.addPlayer(player);
            case 9 -> jrmodTeam.addPlayer(player);
            case 10 -> modTeam.addPlayer(player);
            case 11 -> adminTeam.addPlayer(player);
            case 12 -> managerTeam.addPlayer(player);
            case 13 -> ownerTeam.addPlayer(player);
        }

        final ServerPlayer craftPlayer = ((CraftPlayer) player).getHandle();
        craftPlayer.listName = CraftChatMessage.fromString(holder.getFRank(name))[0];
        for (final ServerPlayer serverPlayer : MinecraftServer.getServer().getPlayerList().players) {
            serverPlayer.connection.send(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_DISPLAY_NAME, craftPlayer));
        }
    }
}
