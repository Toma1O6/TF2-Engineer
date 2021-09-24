package dev.toma.engineermod.network;

import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Class abstracting some packet formalities.
 *
 * @author Toma
 * @version 1.0
 */
public abstract class AbstractPacket<P> implements IPacket<P> {

    /**
     * Processes packet data
     * @param context Network context
     */
    protected abstract void process(NetworkEvent.Context context);

    @Override
    public final void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> process(ctx));
        ctx.setPacketHandled(true);
    }
}
