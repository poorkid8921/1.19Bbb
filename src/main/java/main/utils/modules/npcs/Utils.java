package main.utils.modules.npcs;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import main.Economy;
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

import java.util.List;
import java.util.UUID;

import static main.Economy.arenaManager;
import static main.Economy.scheduleManager;
import static main.utils.Utils.translateA;
import static main.utils.modules.holos.Utils.tickableHolos;
import static net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER;

public class Utils {
    public static final LoopableNPCHolder[] NPCs = new LoopableNPCHolder[3];
    public static final ServerPlayer[] moveNPCs = new ServerPlayer[3];
    private static final ObjectArrayList<UUID> UUIDs = ObjectArrayList.of();

    public static void init() {
        Team team = Bukkit.getScoreboardManager().getMainScoreboard().getTeam("K");
        if (Bukkit.getScoreboardManager().getMainScoreboard().getTeam("K") == null) {
            team = Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam("K");
            team.setNameTagVisibility(NameTagVisibility.NEVER);
        }
        final String CLICK_NPC = translateA("#e52d27§l↓ ᴄʟɪᴄᴋ ɴᴘᴄ ↓");
        final Utils.NPCHolder Discord = new Utils.NPCHolder(new String[]{translateA("#27bbf9"), translateA("#27bbf9§l▪ ᴅɪꜱᴄᴏʀᴅ ▪")}, "ewogICJ0aW1lc3RhbXAiIDogMTcxMDMyMzk5MzU3NCwKICAicHJvZmlsZUlkIiA6ICI4NGMyNzcwOGM1MGQ0OTk4YjM1NDhlNTNjM2JlYjczZiIsCiAgInByb2ZpbGVOYW1lIiA6ICJMZWFkZXJYRHJhZ29uVXdVIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2U3YTM5NzYwMjE5ZDA2NWM3YTQwOTBhZjNlYzU1YzQxMTgwNTg4MTlmZWJjZGM3NDljNDg1OTUwYzEwYjBhOGEiCiAgICB9CiAgfQp9", "A9OWdY1IXokxY4PgyrRnm8KavtByc6PfyuPutlFIlnmfALS3ane1MeCr5D1Lu8eCAvH+0uQzleBmSwIFS01G15CENdMYsOQ/7YP1UDHJ1y9Vr7uf8I6kcULl4enZ6wvmwO0OXTLrM0ehzmDyLMDsjBAPA1UEmp5CMfy69/2n1rmBeFJK5ygQxNA2LZju3RVe5psdTClVpLYYyiY1rwsFP+qP0Cb51cuJa4w3weQYDUyv/LDoCR5EFgK/ogVhazTyaGJZ8GBN48e3DJFPNuIbKKBM6pukDYmv6xl2VCYeJTcUGTKbt6qHpdU29iL5rZtLFHx2XJ/c6UojzY7sFA68O4Rddq/OyAdWp71rDL6OsjXyuOy515WCJk3ueM8xDayr4dUTGCU3dlVPn98LlvcWFnyC5igVAz7xiM0eUoO2DF2N/1P/Q2/v1khmZTOXuse1n29jQdq6gdF6rW9UK9lRxvsicBvW8pEd//6D14/NeFMN3Qlm/nJCySzwuDDn45v42owAkY255Iyytj+O5FEA+d+Y3n2aIpM+YEx8rxH7zZTzt/E/9HbEh05ZcSwIytyny/8YWk2i0Wgg57wvRo25fPi37C6oU+mqMjG+u6LQ+1FVl+FQJ71/jUBdnFdFBsvI8zEc8pj/DFeLcxddVwdvvs00zTkYys9zyO9qK8Z0VxY=", -19.5D, 139D, 16.5D, new ItemStack(Material.RESPAWN_ANCHOR), new ItemStack(Material.GLOWSTONE));
        final Utils.NPCHolder RTP = new Utils.NPCHolder(new String[]{CLICK_NPC, translateA("#e52d27§l▪ ʀᴛᴘ ▪")}, "ewogICJ0aW1lc3RhbXAiIDogMTcwOTQyMTk3NDI1NywKICAicHJvZmlsZUlkIiA6ICJjZjQxNmViZDkyNWU0ZmI0YjQxNTA5YTBkMThlNmZiMyIsCiAgInByb2ZpbGVOYW1lIiA6ICJSVFAiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzhlMDQwMGVhMzMxNDgwZDhkYTA1MTBiNjQyYmE3MzExOWVhZTg4OTFmOGEwMTlkNjVkYjJjYWQyNDkyZGM2NSIKICAgIH0KICB9Cn0=", "SeAZhv07Yj+s+evr8AT7sWinEoDYWHq1HJGAsC+1PaQ+8vPvJwgxDZmoHUTkx+b4MIwk+pf3/yv0+5LwQdss8GgUBrTIh9pKvHUfUGw8S5MHu6mDOpxtE5mlUG4Ws61L6Vents5a7BJaGDbqNOh15k03q9N1bU01jKt5PgC5ULzMjRzL/MjR5/z/IdLzVCdRYA52XlV2f6/ziZ1yPLGFxFs0t9+S1lxJSozHp//ILISIuCPZ0NBs5sKE8PHtTv4euybi79q3gM7rCNxKSCO5/eIt7g4CE1OgnNjwFy8dPeioQr9VgWUWYyeBdxCGItzGI+t/gyQPlP4tAwt9T9DeeH2QnltMokQuEYSpuyD8A8amq6ilNXt0ZXthlsIXioIZpCaRCznByefQT5hBwEymQom5N1zu8trvqjjqC7uY63oZKcJoyoa+BO02n9pb/Rk8teJwU+bJsgxHd9K4fCHwDmnfg68rTasrtt761IEtgKJonQCtkV8ok8OsQEkfD/nSFr8CM13ZRNb1DWFYcCOS0Vn6lLNvaY+rLQye8YQGkpllu9SLtntSTXCAuFeZub2/RuJWLltNz0g5nuL7P5n2SZKoxEQWIyZG7gKTmKr+VrdAPhjqydeibsgc+yJ823Bm0aH6dN9IU8+aP/0sefmX2xSrNihppGEYUGdhodCHjyY=", -15.5D, 139D, 20.5D, new ItemStack(Material.OBSIDIAN), new ItemStack(Material.END_CRYSTAL));
        final Utils.NPCHolder Auctionhouse = new Utils.NPCHolder(new String[]{translateA("#8e2de2↓ ᴄʟɪᴄᴋ ɴᴘᴄ ↓"), translateA("#8e2de2§l▪ ᴀᴜᴄᴛɪᴏɴ ʜᴏᴜꜱᴇ ▪")},
                "ewogICJ0aW1lc3RhbXAiIDogMTcxMTg3MDI3NDc2NywKICAicHJvZmlsZUlkIiA6ICI4MDJmMWQyNTRkZGE0NzUyOWM5YzBlZGE2YjNjNTM4ZCIsCiAgInByb2ZpbGVOYW1lIiA6ICJsYXU0RE9BIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzJjNjdjZjg3OTcwNDIyMTM1Nzc0MjAxNzI1YWFiZjc4MjU4NjAwY2QwMmQ5NjBmMzRjZTliZmUxZmYwNTczZjkiCiAgICB9CiAgfQp9",
                "kxvdm5Z8xWG/Bzd7Vr+8zXEbfR/W/g7zbF2Ub+zYbJnDwGLjhzgNWFuQB5XWcGrku4rjkesoIPcHeWU093f9cojR9cgqHzOoI9l0evOHJ/IZWtO+tXfB0QbpvrcqTKXLxQqdsd87Yg0LK4iaugRsbs4UZR7PCGE2mRmqs9KAqwa9T3OgMg/W1bs2XAAvK+IJKFH2sASFAOJ+laowk/uvop+kUo3dl2GQBItGx7t2Slsh5hDqnHqA1ID5NFoELPdeHEVXOyMgsXyUJAhEL1utJaugC1CmQggqkHVD/LhQnNmDwEdQMPdGFJJHZf9MOUsRCqNR8AZ5Asz67nOyzj/P5cRHTdEhHb/5MmA8aFPDubNNueXEVbgpMDMh2XGt/maynoaCJPK1XT/HovGRZ4iL/dR2++8SUhiSJ+17i/oM4BneZ/oYXSXITBsLZ5jKR72qEZhLWiNhIhUQxNDYZOZm4pB6WaIFliQnyZi+5er2kCjt3yTTw/vTinZuerzJCzGnEIpMdE+zFhhNak+TZJebOD00edl+9XdjniFJjIzQfzqDP9DzR907qUz4zvbyAXP4DqlKdC/ysMSC0CvyDE7s6fSBaqtg17okhoAgs2/OIqs3CxrC7R0zZ+EcNI74JjDOMPZT1DGti9MIzwRbiYo0h0EKhdEqI0OfhZyhp6MZxUw=", -19.5D, 139D, 20.5D, new ItemStack(Material.OBSIDIAN), new ItemStack(Material.END_CRYSTAL));
        int count = -1;
        int i = -1;
        for (Utils.NPCHolder k : new NPCHolder[]{Discord, Auctionhouse, RTP}) {
            final UUID ID = UUID.randomUUID();
            final String name = "K" + ++count;
            final GameProfile gameProfile = new GameProfile(ID, name);
            gameProfile.getProperties().put("textures", new Property("textures", k.getUnsigned(), k.getSignature()));
            final ServerPlayer NPC = new ServerPlayer(MinecraftServer.getServer(), arenaManager.nmsOverworld, gameProfile);
            team.addEntry(name);
            NPC.setPos(k.getX(), k.getY(), k.getZ());
            NPC.setId(count);
            final SynchedEntityData data = NPC.getEntityData();
            data.set(new EntityDataAccessor<>(17, EntityDataSerializers.BYTE), (byte) (0x01 | 0x04 | 0x08 | 0x10 | 0x20 | 0x40));
            NPCs[count] = new Utils.LoopableNPCHolder(NPC, k.getLines(), data, new ClientboundPlayerInfoUpdatePacket(ADD_PLAYER, NPC), new ClientboundAddPlayerPacket(NPC), new ClientboundSetEntityDataPacket(count, data.getNonDefaultValues()), new ClientboundSetEquipmentPacket(count, List.of(com.mojang.datafixers.util.Pair.of(EquipmentSlot.MAINHAND, CraftItemStack.asNMSCopy(k.getFirst())))), new ClientboundSetEquipmentPacket(count, List.of(com.mojang.datafixers.util.Pair.of(EquipmentSlot.OFFHAND, CraftItemStack.asNMSCopy(k.getSecond())))));
            moveNPCs[count] = NPC;
            UUIDs.add(ID);
            double y = k.getY() + 1.8D;
            for (final String c : k.getLines()) {
                final ArmorStand holo = new ArmorStand(arenaManager.nmsOverworld, k.getX(), y, k.getZ());
                holo.setInvisible(true);
                holo.setCustomNameVisible(true);
                holo.setMarker(true);
                holo.setCustomName(CraftChatMessage.fromString(c, false, false)[0]);
                tickableHolos[++i] = new main.utils.modules.holos.Utils.LoopableHologramHolder(holo, new ClientboundAddEntityPacket(holo), new ClientboundSetEntityDataPacket(holo.getId(), holo.getEntityData().getNonDefaultValues()));
                y += 0.3D;
            }
        }
    }

    public static void showForPlayer(final ServerGamePacketListenerImpl connection) {
        for (final LoopableNPCHolder k : NPCs) {
            connection.send(k.ADD_PLAYER);
            connection.send(k.ADD_PLAYER2);
            connection.send(k.METADATA);
            connection.send(k.EQUIPMENT1);
            connection.send(k.EQUIPMENT2);
        }
        scheduleManager.later(() -> connection.send(new ClientboundPlayerInfoRemovePacket(UUIDs)), 15L);
    }

    public record LoopableNPCHolder(ServerPlayer NPC, String[] lines, SynchedEntityData data,
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
