package dev.toma.engineermod.network.packet;

import dev.toma.engineermod.common.entity.SentryEntity;
import dev.toma.engineermod.network.AbstractPacket;
import dev.toma.engineermod.network.NetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;

/**
 * Client -> Server packet (CS prefix).
 * Handles modification of sentry whitelist.
 *
 * @author Toma
 * @version 1.0
 */
public class CS_SentryWhitelistAction extends AbstractPacket<CS_SentryWhitelistAction> {

    private final ActionType actionType;
    private final int sentryNetworkId;
    private final UUID whitelistId;

    public CS_SentryWhitelistAction() {
        this(null, 0, null);
    }

    public CS_SentryWhitelistAction(ActionType type, int sentryNetworkId, UUID uuid) {
        this.actionType = type;
        this.sentryNetworkId = sentryNetworkId;
        this.whitelistId = uuid;
    }

    @Override
    public void encode(PacketBuffer buffer) {
        buffer.writeEnum(actionType);
        buffer.writeInt(sentryNetworkId);
        buffer.writeUUID(whitelistId);
    }

    @Override
    public CS_SentryWhitelistAction decode(PacketBuffer buffer) {
        return new CS_SentryWhitelistAction(buffer.readEnum(ActionType.class), buffer.readInt(), buffer.readUUID());
    }

    @Override
    protected void process(NetworkEvent.Context context) {
        ServerPlayerEntity player = context.getSender();
        ServerWorld world = player.getLevel();
        Entity entity = world.getEntity(sentryNetworkId);
        if (entity instanceof SentryEntity) {
            SentryEntity sentry = (SentryEntity) entity;
            if (sentry.isOwner(player.getUUID())) { // owner validation
                actionType.handle(sentry, whitelistId);
                CompoundNBT cnbt = new CompoundNBT();
                sentry.saveInternalDataTo(cnbt);
                NetworkHandler.SC_sendWorldPacket(world, new SC_UpdateSentryData(sentryNetworkId, cnbt));
            }
        }
    }

    public enum ActionType {

        ADD(SentryEntity::addToWhitelist),
        REMOVE(SentryEntity::removeFromWhitelist);

        private final IActionHandler handler;

        ActionType(IActionHandler handler) {
            this.handler = handler;
        }

        public void handle(SentryEntity entity, UUID uuid) {
            handler.handleAction(entity, uuid);
        }
    }

    private interface IActionHandler {
        void handleAction(SentryEntity entity, UUID uuid);
    }
}
