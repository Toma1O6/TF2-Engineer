package dev.toma.engineermod.network.packet;

import dev.toma.engineermod.common.entity.SentryEntity;
import dev.toma.engineermod.network.AbstractPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

/**
 * Server -> Client packet (SC prefix).
 * Handles sentry internal updates.
 *
 * @author Toma
 * @version 1.0
 */
public class SC_UpdateSentryData extends AbstractPacket<SC_UpdateSentryData> {

    private final int sentryNetworkId;
    private final CompoundNBT data;

    public SC_UpdateSentryData() {
        this(0, null);
    }

    public SC_UpdateSentryData(int sentryNetworkId, CompoundNBT data) {
        this.sentryNetworkId = sentryNetworkId;
        this.data = data;
    }

    @Override
    public void encode(PacketBuffer buffer) {
        buffer.writeInt(sentryNetworkId);
        buffer.writeNbt(data);
    }

    @Override
    public SC_UpdateSentryData decode(PacketBuffer buffer) {
        return new SC_UpdateSentryData(buffer.readInt(), buffer.readNbt());
    }

    @Override
    protected void process(NetworkEvent.Context context) {
        handleClientSide();
    }

    private void handleClientSide() {
        Minecraft mc = Minecraft.getInstance();
        ClientWorld level = mc.level;
        Entity entity = level.getEntity(sentryNetworkId);
        if (entity instanceof SentryEntity) {
            SentryEntity sentry = (SentryEntity) entity;
            sentry.loadInternalDataFrom(data);
        }
    }
}
