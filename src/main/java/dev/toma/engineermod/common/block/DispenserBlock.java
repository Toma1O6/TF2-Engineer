package dev.toma.engineermod.common.block;

import dev.toma.engineermod.EngineerMod;
import dev.toma.engineermod.common.ILiquidIronStorage;
import dev.toma.engineermod.common.blockentity.DispenserBlockEntity;
import dev.toma.engineermod.common.init.ModdedItems;
import dev.toma.engineermod.common.init.Sounds;
import dev.toma.engineermod.common.item.WrenchItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Dispenser block class
 *
 * @author Toma
 * @version 1.0
 */
public class DispenserBlock extends Block {

    /**
     * Collision shape
     */
    private static final VoxelShape COLLISION_SHAPE = VoxelShapes.box(0.0, 0.0, 0.0, 1.0, 1.5, 1.0);

    /**
     * Constructor
     * @param name Block registry name
     * @param properties Block properties
     */
    public DispenserBlock(String name, Properties properties) {
        super(properties);
        setRegistryName(EngineerMod.createModPath(name));
    }

    @Override
    public void attack(BlockState state, World level, BlockPos pos, PlayerEntity player) {
        ItemStack stack = player.getMainHandItem();
        if (stack.getItem() == ModdedItems.WRENCH) {
            TileEntity tile = level.getBlockEntity(pos);
            if (tile instanceof ILiquidIronStorage) {
                ILiquidIronStorage storage = (ILiquidIronStorage) tile;
                int total = WrenchItem.getIronVolume(stack);
                int insertTarget = Math.min(total, storage.getRequestAmount());
                int extra = storage.insertIron(insertTarget);
                int consumed = insertTarget - extra;
                WrenchItem.consumeIronVolume(stack, consumed);
                player.playSound(Sounds.WRENCH_HIT_BUILD_SUCCESS, 1.0F, 1.0F);
            }
        }
    }

    @Override
    public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result) {
        ItemStack stack = player.getItemInHand(hand);
        TileEntity tile = level.getBlockEntity(pos);
        if (tile instanceof DispenserBlockEntity) {
            DispenserBlockEntity dispenser = (DispenserBlockEntity) tile;
            if (stack.getItem() != ModdedItems.WRENCH) {
                dispenser.transferArrows(player);
            }
        }
        return ActionResultType.PASS;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader reader, BlockPos pos, ISelectionContext context) {
        return COLLISION_SHAPE;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new DispenserBlockEntity();
    }
}
