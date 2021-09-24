package dev.toma.engineermod.common.blockentity;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

/**
 * Class containing abstract behaviour of inventory block entities. Inventory is automatically saved to NBT
 * and is accessible to all children via direct reference.
 *
 * @author Toma
 * @version 1.0
 */
@SuppressWarnings("unchecked")
public abstract class InventoryHandlerBlockEntity extends TileEntity {

    /**
     * The item handler
     */
    protected IItemHandlerModifiable itemHandler;

    /**
     * Lazy optional for capabilities.
     */
    private final LazyOptional<IItemHandlerModifiable> optional;

    /**
     * Constructor
     * @param type Blockentity type
     */
    public InventoryHandlerBlockEntity(TileEntityType<?> type) {
        super(type);
        this.itemHandler = createItemHandler();
        this.optional = LazyOptional.of(() -> itemHandler);
    }

    /**
     * @return New instance of item handler to be used by this block entity
     */
    public abstract IItemHandlerModifiable createItemHandler();

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        super.save(nbt);
        optional.ifPresent(handler -> {
            if (handler instanceof INBTSerializable) {
                CompoundNBT inv = ((INBTSerializable<CompoundNBT>) handler).serializeNBT();
                nbt.put("inventory", inv);
            }
        });
        write(nbt);
        return nbt;
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt) {
        super.load(state, nbt);
        optional.ifPresent(handler -> {
            if (handler instanceof INBTSerializable) {
                CompoundNBT inv = nbt.contains("inventory", Constants.NBT.TAG_COMPOUND) ? nbt.getCompound("inventory") : new CompoundNBT();
                ((INBTSerializable<CompoundNBT>) handler).deserializeNBT(inv);
            }
        });
        read(nbt);
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return save(new CompoundNBT());
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return optional.cast();
        }
        return super.getCapability(cap, side);
    }

    /**
     * Allows you to write additional data into NBT
     * @param nbt NBT object
     */
    protected void write(CompoundNBT nbt) {
    }

    /**
     * Allows you to read additional data from NBT
     * @param nbt NBT object
     */
    protected void read(CompoundNBT nbt) {
    }
}
