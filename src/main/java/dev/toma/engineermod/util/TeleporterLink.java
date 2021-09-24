package dev.toma.engineermod.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Singleton class handling teleporter links.
 *
 * @author Toma
 * @version 1.0
 */
public final class TeleporterLink {

    /**
     * The single instance
     */
    private static final TeleporterLink CONTEXT = new TeleporterLink();

    /**
     * Level used for linking. Used to validate that links are happening in the same world. Not really multiplayer friendly.
     * Could be possibly moved to players to manage their own links.
     */
    private World level;

    /**
     * First node
     */
    private BlockPos nodeA;

    /**
     * Second node
     */
    private BlockPos nodeB;

    /**
     * @return The single instance of this class
     */
    public static TeleporterLink get() {
        return CONTEXT;
    }

    /**
     * Links provided node and returns result based on existing data. Result can be either valid or invalid.
     * @param level The level where link is happening
     * @param pos Node position
     * @return Link result. If result is valid, it will clear internal references to nodes and return result with these nodes.
     */
    public IResult link(World level, BlockPos pos) {
        if (level != this.level) {
            reset(level);
        }
        addNode(pos);
        IResult result = construct();
        if (result.isValid()) {
            clearNodes();
        }
        return result;
    }

    private void reset(World level) {
        this.level = level;
        clearNodes();
    }

    private void clearNodes() {
        this.nodeA = null;
        this.nodeB = null;
    }

    private void addNode(BlockPos node) {
        if (nodeA == null) {
            nodeA = node;
        } else if (!node.equals(nodeA)) {
            nodeB = node;
        }
    }

    private IResult construct() {
        return nodeA != null && nodeB != null ? IResult.of(nodeA, nodeB) : IResult.NO_RESULT;
    }

    private TeleporterLink() {}

    /**
     * Simple node provider. Use {@link IResult#isValid()} to validate that result is finalized and ready to be linked in world.
     */
    public interface IResult {

        IResult NO_RESULT = of(null, null);

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

        static IResult of(BlockPos nodeA, BlockPos nodeB) {
            return new IResult() {
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
