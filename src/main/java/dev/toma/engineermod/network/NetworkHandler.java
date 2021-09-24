package dev.toma.engineermod.network;

import dev.toma.engineermod.EngineerMod;
import dev.toma.engineermod.network.packet.CS_SentryWhitelistAction;
import dev.toma.engineermod.network.packet.CS_SetSentryTargetting;
import dev.toma.engineermod.network.packet.SC_UpdateSentryData;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Predicate;

/**
 * Packet manager/dispatcher.
 *
 * @author Toma
 * @version 1.0
 */
public final class NetworkHandler {

    private static final String PROTOCOL_ID = "tf2engineer1";
    private static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
            .named(EngineerMod.createModPath("network"))
            .networkProtocolVersion(() -> PROTOCOL_ID)
            .clientAcceptedVersions(PROTOCOL_ID::equals)
            .serverAcceptedVersions(PROTOCOL_ID::equals)
            .simpleChannel();
    private static byte packetId;

    /**
     * Sends packet to server.
     * This is Client -> Server operation.
     * @param packet The packet to send
     */
    public static void CS_sendServerPacket(IPacket<?> packet) {
        CHANNEL.sendToServer(packet);
    }

    /**
     * Sends packet to world.
     * This is Server -> Client operation.
     * @param world The world where packet is being sent
     * @param packet The packet
     */
    public static void SC_sendWorldPacket(World world, IPacket<?> packet) {
        SC_sendWorldPacket(world, packet, player -> true);
    }

    /**
     * Sends packet to world, but only to players who fulfil condition.
     * This is Server -> Client operation.
     * @param world The world where packet is being sent
     * @param packet The packet
     * @param condition The condition
     */
    public static void SC_sendWorldPacket(World world, IPacket<?> packet, Predicate<ServerPlayerEntity> condition) {
        if (!(world instanceof ServerWorld)) {
            throw new UnsupportedOperationException("Cannot send world packet from client!");
        }
        world.players().stream()
                .map(pl -> (ServerPlayerEntity) pl)
                .filter(condition)
                .forEach(serverPlayerEntity -> SC_sendClientPacket(serverPlayerEntity, packet));
    }

    /**
     * Sends packet to specific player.
     * This is Server -> Client operation.
     * @param user The user who receives the packet
     * @param packet The packet
     */
    public static void SC_sendClientPacket(ServerPlayerEntity user, IPacket<?> packet) {
        CHANNEL.sendTo(packet, user.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    /**
     * Registers mod network packets
     */
    public static void registerPackets() {
        register(CS_SentryWhitelistAction.class);
        register(CS_SetSentryTargetting.class);
        register(SC_UpdateSentryData.class);
    }

    /**
     * Registers packet to this network handler. Every packet must provide <b>public Constructor() with no parameters</b>
     * @param packetClass The packet class
     * @param <P> Packet type
     */
    private static <P extends IPacket<P>> void register(Class<P> packetClass) {
        P packet;
        try {
            packet = packetClass.getDeclaredConstructor().newInstance();
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new ReportedException(CrashReport.forThrowable(e, "Couldn't instantiate packet for registraction. Make sure your packet class provides public constructor with no parameters!"));
        }
        CHANNEL.registerMessage(packetId++, packetClass, IPacket::encode, packet::decode, IPacket::handle);
    }

    /**
     * Private constructor
     */
    private NetworkHandler() {}
}
