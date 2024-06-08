package main.utils.modules.npcs;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import main.utils.Initializer;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_19_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_19_R3.util.CraftChatMessage;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Team;
import org.json.simple.JSONObject;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.List;
import java.util.UUID;

import static main.utils.Utils.translateA;
import static main.utils.modules.arenas.Utils.nmsOverworld;
import static main.utils.modules.holos.Utils.tickableHolos;
import static net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER;

public class Utils {
    public static final LoopableNPCHolder[] NPCs = new LoopableNPCHolder[3];
    public static final ServerPlayer[] moveNPCs = new ServerPlayer[3];
    private static final ObjectArrayList<UUID> UUIDs = ObjectArrayList.of();

    private static String[] fetchSkinData(String uuid) {
        try {
            String sURL = "https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false";
            URL url = new URL(sURL);
            URLConnection request = url.openConnection();
            request.connect();
            JsonParser parser = new JsonParser();
            JsonElement root = parser.parse(new InputStreamReader((InputStream) request.getContent()));
            JsonObject obj = root.getAsJsonObject();
            String value = obj.get("properties").getAsJsonArray().get(0).getAsString();
            String signature = obj.get("properties").getAsJsonArray().get(1).getAsString();
            return new String[] { value, signature };
        } catch (Exception ignored) {
            return new String[] { "", "" };
        }
    }

    public static void init() {
        Team team = Bukkit.getScoreboardManager().getMainScoreboard().getTeam("K");
        if (Bukkit.getScoreboardManager().getMainScoreboard().getTeam("K") == null) {
            team = Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam("K");
            team.setNameTagVisibility(NameTagVisibility.NEVER);
        }
        final String CLICK_NPC = translateA("#e52d27§l↓ ᴄʟɪᴄᴋ ɴᴘᴄ ↓");
        final Utils.NPCHolder FFA = new Utils.NPCHolder(new String[]{CLICK_NPC, translateA("#e52d27§l▪ ꜰꜰᴀ ▪")}, fetchSkinData("84c27708-c50d-4998-b354-8e53c3beb73f")[0], fetchSkinData("84c27708-c50d-4998-b354-8e53c3beb73f")[1], -10.5D, 86D, -3.5D, new ItemStack(Material.RESPAWN_ANCHOR), new ItemStack(Material.GLOWSTONE));
        final Utils.NPCHolder Flat = new Utils.NPCHolder(new String[]{CLICK_NPC, translateA("#e52d27§l▪ ꜰʟᴀᴛ ▪")}, "ewogICJ0aW1lc3RhbXAiIDogMTcwOTY2ODEyNTY2OSwKICAicHJvZmlsZUlkIiA6ICJhNDAwY2IyMzI1NjY0NDgzOThjNDM1NjQ4N2ZjYmNjZiIsCiAgInByb2ZpbGVOYW1lIiA6ICJQM29zdCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8yMDc2NGU0NjY5YjI5ZTE3OTgxOWQ5MWNlMzRkN2UxM2ZiZTEwZDMxYWNmMDU3NjhlNGYwNjE0ODhiODg1YTViIgogICAgfSwKICAgICJDQVBFIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8yMzQwYzBlMDNkZDI0YTExYjE1YThiMzNjMmE3ZTllMzJhYmIyMDUxYjI0ODFkMGJhN2RlZmQ2MzVjYTdhOTMzIgogICAgfQogIH0KfQ==", "TdPHZYgipH3uObe7TVwcBo6htkSuPx1e53W92U+asjzAEFK1Ed1DGGq0Zddc+eBV5eQRO858JoKzQ+dmieZ9vU2WaCaie2VAj0sGS5+4NnyYsNLItFFUyt3xfwaxDmG62sCsaOod+P/HRSSZok0oGXuA3H1F+KCtSY4PzPxUPxhlExNxJzWk82Lxq5MInKWP8TTeZbdNOUF/zRl8ioj8qtnQN1+Mm6ySzGIjRk7d+ySKolRhodkTJHd6mO/VoNi7CqPhyWWmImdbk4boiK5RmfFHyWlw97lhG5jdAQI92vTvZNTy4qI3YYq3Z8kGGCNjXHSejf9VMBDRt+6NI4Jx2D++cVzna3d/J9hOX4bXYa8sMvcX2ahrFyzTDHT2UHZET2mfFec3fNOZxU2EMqMmDEAMX4EzX1jX9jK21n0sDjVwkqXy4r035Hu22QNzHYjnaXMjp0XGQzjlLG90snX/lYmWMJfKpdLMKYa8DcnO5WPiqpdfLOWwWvJidZLGI6KvzbvscZY47kW18ch99TR7CLmWF+90VUhgvXFuWXdW7/wXav56aq3SsKyreS8A3BNlJs7hf16RtkF46e++6wNXZuci5B/puuGUa83neyrJ1WNX38g3y8TBnxJ7iCUqcp3SZ98zpOrw1oO8JCuHIoCIJV1ce9LmXXZOYotFLgrY6lM=", -14.5D, 86D, 0.5D, new ItemStack(Material.OBSIDIAN), new ItemStack(Material.END_CRYSTAL));
        final Utils.NPCHolder RTP = new Utils.NPCHolder(new String[]{CLICK_NPC, translateA("#e52d27§l▪ ʀᴛᴘ ▪")}, "ewogICJ0aW1lc3RhbXAiIDogMTcwOTQyMTk3NDI1NywKICAicHJvZmlsZUlkIiA6ICJjZjQxNmViZDkyNWU0ZmI0YjQxNTA5YTBkMThlNmZiMyIsCiAgInByb2ZpbGVOYW1lIiA6ICJSVFAiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzhlMDQwMGVhMzMxNDgwZDhkYTA1MTBiNjQyYmE3MzExOWVhZTg4OTFmOGEwMTlkNjVkYjJjYWQyNDkyZGM2NSIKICAgIH0KICB9Cn0=", "SeAZhv07Yj+s+evr8AT7sWinEoDYWHq1HJGAsC+1PaQ+8vPvJwgxDZmoHUTkx+b4MIwk+pf3/yv0+5LwQdss8GgUBrTIh9pKvHUfUGw8S5MHu6mDOpxtE5mlUG4Ws61L6Vents5a7BJaGDbqNOh15k03q9N1bU01jKt5PgC5ULzMjRzL/MjR5/z/IdLzVCdRYA52XlV2f6/ziZ1yPLGFxFs0t9+S1lxJSozHp//ILISIuCPZ0NBs5sKE8PHtTv4euybi79q3gM7rCNxKSCO5/eIt7g4CE1OgnNjwFy8dPeioQr9VgWUWYyeBdxCGItzGI+t/gyQPlP4tAwt9T9DeeH2QnltMokQuEYSpuyD8A8amq6ilNXt0ZXthlsIXioIZpCaRCznByefQT5hBwEymQom5N1zu8trvqjjqC7uY63oZKcJoyoa+BO02n9pb/Rk8teJwU+bJsgxHd9K4fCHwDmnfg68rTasrtt761IEtgKJonQCtkV8ok8OsQEkfD/nSFr8CM13ZRNb1DWFYcCOS0Vn6lLNvaY+rLQye8YQGkpllu9SLtntSTXCAuFeZub2/RuJWLltNz0g5nuL7P5n2SZKoxEQWIyZG7gKTmKr+VrdAPhjqydeibsgc+yJ823Bm0aH6dN9IU8+aP/0sefmX2xSrNihppGEYUGdhodCHjyY=", -10.5D, 86D, 4.5D, new ItemStack(Material.OBSIDIAN), new ItemStack(Material.END_CRYSTAL));
        int count = -1;
        int i = -1;
        for (final Utils.NPCHolder k : new NPCHolder[]{FFA, Flat, RTP}) {
            final UUID ID = UUID.randomUUID();
            final String name = "K" + ++count;
            final GameProfile gameProfile = new GameProfile(ID, name);
            gameProfile.getProperties().put("textures", new Property("textures", k.getUnsigned(), k.getSignature()));
            final ServerPlayer NPC = new ServerPlayer(MinecraftServer.getServer(), nmsOverworld, gameProfile);
            team.addEntry(name);
            NPC.setId(count);
            final SynchedEntityData data = NPC.getEntityData();
            data.set(new EntityDataAccessor<>(17, EntityDataSerializers.BYTE), (byte) (0x01 | 0x04 | 0x08 | 0x10 | 0x20 | 0x40));
            NPCs[count] = new Utils.LoopableNPCHolder(NPC, k.x, k.y, k.z, new ClientboundPlayerInfoUpdatePacket(ADD_PLAYER, NPC), new ClientboundAddPlayerPacket(NPC), new ClientboundSetEntityDataPacket(count, data.getNonDefaultValues()), new ClientboundSetEquipmentPacket(count, List.of(com.mojang.datafixers.util.Pair.of(EquipmentSlot.MAINHAND, CraftItemStack.asNMSCopy(k.getFirst())))), new ClientboundSetEquipmentPacket(count, List.of(com.mojang.datafixers.util.Pair.of(EquipmentSlot.OFFHAND, CraftItemStack.asNMSCopy(k.getSecond())))));
            moveNPCs[count] = NPC;
            UUIDs.add(ID);
            double y = k.getY() + 1.8D;
            for (String c : k.getLines()) {
                final ArmorStand holo = new ArmorStand(nmsOverworld, k.getX(), y, k.getZ());
                holo.setInvisible(true);
                holo.setCustomNameVisible(true);
                holo.setMarker(true);
                holo.setCustomName(CraftChatMessage.fromString(c, false, false)[0]);
                tickableHolos[++i] = new main.utils.modules.holos.Utils.LoopableHologramHolder(holo, new ClientboundAddEntityPacket(holo), new ClientboundSetEntityDataPacket(holo.getId(), holo.getEntityData().getNonDefaultValues()));
                y += 0.3D;
            }
        }
    }

    public static void showForPlayer(ServerGamePacketListenerImpl connection) {
        for (final LoopableNPCHolder k : NPCs) {
            connection.send(k.ADD_PLAYER);
            connection.send(k.ADD_PLAYER2);
            connection.send(k.METADATA);
            connection.send(k.EQUIPMENT1);
            connection.send(k.EQUIPMENT2);
        }
        Bukkit.getScheduler().runTaskLater(Initializer.p, () -> connection.send(new ClientboundPlayerInfoRemovePacket(UUIDs)), 15L);
    }

    public static void showForPlayerFirstTime(ServerGamePacketListenerImpl connection) {
        for (final LoopableNPCHolder k : NPCs) {
            connection.send(k.ADD_PLAYER);
            connection.send(k.ADD_PLAYER2);
            connection.send(k.METADATA);
            connection.send(k.EQUIPMENT1);
            connection.send(k.EQUIPMENT2);
        }
        Bukkit.getScheduler().runTaskLater(Initializer.p, () -> {
            for (final LoopableNPCHolder k : NPCs) {
                k.NPC().setPos(k.x(), k.y(), k.z());
                connection.send(new ClientboundTeleportEntityPacket(k.NPC()));
            }
        }, 1L);
        Bukkit.getScheduler().runTaskLater(Initializer.p, () -> connection.send(new ClientboundPlayerInfoRemovePacket(UUIDs)), 15L);
    }

    public record LoopableNPCHolder(ServerPlayer NPC,
                                    double x,
                                    double y,
                                    double z,
                                    ClientboundPlayerInfoUpdatePacket ADD_PLAYER,
                                    ClientboundAddPlayerPacket ADD_PLAYER2, ClientboundSetEntityDataPacket METADATA,
                                    ClientboundSetEquipmentPacket EQUIPMENT1,
                                    ClientboundSetEquipmentPacket EQUIPMENT2) {
    }

    @Getter
    static class NPCHolder {
        private final String[] lines;
        private final String unsigned;
        private final String signature;
        private final double x;
        private final double y;
        private final double z;
        private final ItemStack first;
        private final ItemStack second;

        public NPCHolder(final String[] lines, final String unsigned, final String signature, final double x, final double y, final double z, final ItemStack first, final ItemStack second) {
            this.lines = lines;
            this.unsigned = unsigned;
            this.signature = signature;
            this.x = x;
            this.y = y;
            this.z = z;
            this.first = first;
            this.second = second;
        }
    }
}
