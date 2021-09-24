package dev.toma.engineermod.common.block;

import dev.toma.engineermod.EngineerMod;
import dev.toma.engineermod.common.blockentity.TeleporterBlockEntity;
import dev.toma.engineermod.common.init.ModdedItems;
import dev.toma.engineermod.util.TeleporterLink;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Teleporter block teleports player between A and B locations.
 *
 * @author Toma
 * @version 1.0
 */
public class TeleporterBlock extends Block {

    /**
     * Carpet like hitbox
     */
    private static final VoxelShape COLLISION = Block.box(0.0, 0.0, 0.0, 16.0, 1.0, 16.0);

    /**
     * Slightly bigger outline for {@link BlockState#entityInside(World, BlockPos, Entity)} method to be invoked
     */
    private static final VoxelShape OUTLINE = Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);

    /**
     * Constructor
     * @param name Registry name
     * @param properties Block properties
     */
    public TeleporterBlock(String name, Properties properties) {
        super(properties);

        setRegistryName(EngineerMod.createModPath(name));
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader reader, BlockPos pos, ISelectionContext selection) {
        return OUTLINE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader reader, BlockPos pos, ISelectionContext selection) {
        return COLLISION;
    }

    @Override
    public void entityInside(BlockState state, World level, BlockPos pos, Entity entity) {
        if (!level.isClientSide && entity instanceof PlayerEntity) {
            UUID uuid = entity.getUUID();
            TileEntity tile = level.getBlockEntity(pos);
            if (tile instanceof TeleporterBlockEntity) {
                TeleporterBlockEntity teleporter = (TeleporterBlockEntity) tile;
                teleporter.requestTeleportation(uuid);
            }
        }
    }

    @Override
    public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult traceResult) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() == ModdedItems.WRENCH && !level.isClientSide) {
            TeleporterLink.IResult result = TeleporterLink.get().link(level, pos);
            if (result.isValid()) {
                linkTiles(level, result);
            }
        }
        return ActionResultType.PASS;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TeleporterBlockEntity();
    }

    /**
     * Links teleporters to their counterparts
     * @param level Level
     * @param result Linking result
     */
    private void linkTiles(World level, TeleporterLink.IResult result) {
        BlockPos a = result.getNodeA();
        BlockPos b = result.getNodeB();
        TileEntity source = level.getBlockEntity(a);
        if (source instanceof TeleporterBlockEntity) {
            TileEntity target = level.getBlockEntity(b);
            if (target instanceof TeleporterBlockEntity) {
                TeleporterBlockEntity tileA = (TeleporterBlockEntity) source;
                TeleporterBlockEntity tileB = (TeleporterBlockEntity) target;
                tileA.linkTo(b);
                tileB.linkTo(a);
            }
        }
    }
}
