package dev.toma.engineermod.network.packet;

import dev.toma.engineermod.common.entity.SentryEntity;
import dev.toma.engineermod.common.entity.SentryTargetType;
import dev.toma.engineermod.network.AbstractPacket;
import dev.toma.engineermod.network.NetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;

/**
 * Client -> Server packet (CS prefix).
 * Handles sentry target update from UI.
 *
 * @author Toma
 * @version 1.0
 */
public class CS_SetSentryTargetting extends AbstractPacket<CS_SetSentryTargetting> {

    private final int sentryNetworkId;
    private final SentryTargetType targetType;

    public CS_SetSentryTargetting() {
        this(0, null);
    }

    public CS_SetSentryTargetting(int sentryNetworkId, SentryTargetType targetType) {
        this.sentryNetworkId = sentryNetworkId;
        this.targetType = targetType;
    }

    @Override
    public void encode(PacketBuffer buffer) {
        buffer.writeInt(sentryNetworkId);
        buffer.writeEnum(targetType);
    }

    @Override
    public CS_SetSentryTargetting decode(PacketBuffer buffer) {
        return new CS_SetSentryTargetting(buffer.readInt(), buffer.readEnum(SentryTargetType.class));
    }

    @Override
    protected void process(NetworkEvent.Context context) {
        ServerPlayerEntity player = context.getSender();
        ServerWorld level = player.getLevel();
        Entity entity = level.getEntity(sentryNetworkId);
        if (entity instanceof SentryEntity) {
            SentryEntity sentry = (SentryEntity) entity;
            if (sentry.isOwner(player.getUUID())) {
                sentry.setTargetType(targetType);
                CompoundNBT cnbt = new CompoundNBT();
                sentry.saveInternalDataTo(cnbt);
                NetworkHandler.SC_sendClientPacket(player, new SC_UpdateSentryData(sentryNetworkId, cnbt));
            }
        }
    }
}
