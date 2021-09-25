package dev.toma.engineermod.common;

import dev.toma.engineermod.EngineerMod;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages teleport link creation.
 *
 * @author Toma
 * @version 1.0
 */
@Mod.EventBusSubscriber(modid = EngineerMod.MODID)
public final class TeleporterLinkManager {

    private static final Map<UUID, LinkContext> LINKS = new HashMap<>();
    private static final Marker MARKER = MarkerManager.getMarker("TeleporterLinks");

    public static ILinkResult addLinkNode(PlayerEntity player, BlockPos pos) {
        UUID uid = player.getUUID();
        LinkContext context = LINKS.get(uid);
        if (context == null) {
            LINKS.put(uid, new LinkContext(pos));
            return ILinkResult.NO_RESULT;
        } else {
            ILinkResult result = context.dest(pos);
            LINKS.remove(uid);
            EngineerMod.LOGGER.info(MARKER, "Created new teleporter link between nodes [A: {}, B: {}] by {} [UID: {}]", result.getNodeA().immutable(), result.getNodeB().immutable(), player.getDisplayName().getString(), uid);
            return result;
        }
    }

    @SubscribeEvent
    public static void loseLinkTrack(PlayerEvent.PlayerLoggedOutEvent event) {
        PlayerEntity player = event.getPlayer();
        if (player != null) {
            UUID uuid = player.getUUID();
            LINKS.remove(uuid);
        }
    }

    private static class LinkContext {

        private final BlockPos head;

        public LinkContext(BlockPos head) {
            this.head = head;
        }

        public ILinkResult dest(BlockPos endNode) {
            if (endNode.equals(head))
                return ILinkResult.NO_RESULT;
            return ILinkResult.of(head, endNode);
        }
    }

    /**
     * Simple node provider.
     */
    public interface ILinkResult {

        ILinkResult NO_RESULT = of(null, null);

        /**
         * @return First node of link
         */
        BlockPos getNodeA();

        /**
         * @return Second node of link
         */
        BlockPos getNodeB();

        /**
         * @return Whether result contains both nodes.
         */
        default boolean isValid() {
            return getNodeA() != null && getNodeB() != null;
        }

        static ILinkResult of(BlockPos nodeA, BlockPos nodeB) {
            return new ILinkResult() {
                @Override
                public BlockPos getNodeA() {
                    return nodeA;
                }

                @Override
                public BlockPos getNodeB() {
                    return nodeB;
                }
            };
        }
    }
}
