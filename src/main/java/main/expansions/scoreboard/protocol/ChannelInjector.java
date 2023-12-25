package main.expansions.scoreboard.protocol;

import com.google.common.collect.MapMaker;
import io.netty.channel.Channel;
import lombok.SneakyThrows;
import main.expansions.scoreboard.util.Reflection;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.Map;

public class ChannelInjector {
    public static final ChannelInjector IMP = new ChannelInjector();
    private static final MethodHandle GET_CONNECTION;
    private static final MethodHandle GET_MANAGER;
    private static final MethodHandle GET_CHANNEL;

    static {
        MethodHandles.Lookup lookup = MethodHandles.lookup();

        try {
            Class<?> entityPlayer = Reflection.getClass(
                    "net.minecraft.server.level.EntityPlayer",
                    "net.minecraft.server.level.ServerPlayer",
                    "{nms}.EntityPlayer");

            Class<?> playerConnection = Reflection.getClass(
                    "{nms}.PlayerConnection",
                    "net.minecraft.server.network.PlayerConnection",
                    "net.minecraft.server.network.ServerGamePacketListenerImpl");
            Class<?> networkManager = Reflection.getClass(
                    "{nms}.NetworkManager",
                    "net.minecraft.network.NetworkManager",
                    "net.minecraft.network.Connection");

            GET_CONNECTION = lookup.unreflectGetter(
                    Reflection.getField(entityPlayer, playerConnection, 0).handle());
            GET_MANAGER = lookup.unreflectGetter(
                    Reflection.getField(playerConnection, networkManager, 0).handle());

            GET_CHANNEL = lookup.unreflectGetter(
                    Reflection.getField(networkManager, Channel.class, 0).handle());

        } catch (Throwable throwable) {
            throw new ExceptionInInitializerError(throwable);
        }
    }

    private final Map<String, Channel> channelLookup = new MapMaker().weakValues().makeMap();

    private ChannelInjector() {
    }

    @SneakyThrows
    public Channel getChannel(Player player) {
        Channel channel = channelLookup.get(player.getName());

        if (channel == null || !channel.isOpen()) {
            Object connection = GET_CONNECTION.invoke(((CraftPlayer) player).getHandle());
            Object manager = GET_MANAGER.invoke(connection);
            channelLookup.put(player.getName(), channel = (Channel) GET_CHANNEL.invoke(manager));
        }

        return channel;
    }

    public void sendPacket(Player player, Object packet) {
        getChannel(player).writeAndFlush(packet);
    }
}
