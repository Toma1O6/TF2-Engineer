package dev.toma.engineermod.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Network packet API
 *
 * @author Toma
 * @version 1.0
 */
public interface IPacket<P> {

    /**
     * Encodes packet data into buffer
     * @param buffer Packet buffer
     */
    void encode(PacketBuffer buffer);

    /**
     * Decodes packet data into new packet instance
     * @param buffer Packet buffer
     * @return New packet instance with data from buffer
     */
    P decode(PacketBuffer buffer);

    /**
     * Processes packet data
     * @param supplier Network context supplier
     */
    void handle(Supplier<NetworkEvent.Context> supplier);
}
