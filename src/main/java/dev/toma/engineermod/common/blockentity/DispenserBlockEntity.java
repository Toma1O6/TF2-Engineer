package dev.toma.engineermod.common.blockentity;

import dev.toma.engineermod.common.ILiquidIronStorage;
import dev.toma.engineermod.common.init.BlockEntities;
import dev.toma.engineermod.common.init.Entities;
import dev.toma.engineermod.util.Mth;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Predicate;

/**
 * Block entity defining behaviour of dispensers. Adds Regeneration I effect to all nearby entities
 * and passively generates arrows which can be obtained by right-clicking the block.
 *
 * @author Toma
 * @version 1.0
 */
public class DispenserBlockEntity extends InventoryHandlerBlockEntity implements ITickableTileEntity, ILiquidIronStorage {

    /**
     * Range for Regeneration I effect
     */
    private static final int RANGE = 4;

    /**
     * Squared {@link DispenserBlockEntity#RANGE}
     */
    private static final int SQR_RANGE = RANGE * RANGE;

    /**
     * Arrow generation interval (ticks)
     */
    private static final int ARROW_GEN_INTERVAL = 160;

    /**
     * Maximum amount of arrows which can be inside this block entity
     */
    private static final int ARROW_THRESHOLD = 64;

    /**
     * Liquid iron storage capacity
     */
    private static final int IRON_CAPACITY = 2000;

    /**
     * Stored iron
     */
    private int liquidIron;

    /**
     * Constructor
     * @param type BlockEntity Type
     */
    public DispenserBlockEntity(TileEntityType<?> type) {
        super(type);
    }

    /**
     * Constructor
     */
    public DispenserBlockEntity() {
        this(BlockEntities.DISPENSER_TYPE.get());
    }

    /**
     * Transfers arrows from internal inventory to player
     * @param player Player who should receive the arrows
     */
    public void transferArrows(PlayerEntity player) {
        if (!level.isClientSide) {
            player.addItem(itemHandler.getStackInSlot(0).copy());
            itemHandler.setStackInSlot(0, ItemStack.EMPTY);
        }
    }

    @Override
    public void tick() {
        addEffectToAllAround();
        generateArrows();
    }

    @Override
    public IItemHandlerModifiable createItemHandler() {
        return new ItemStackHandler(1) {
            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return stack.getItem() == Items.ARROW;
            }
        };
    }

    @Override
    public int getFluidVolume() {
        return liquidIron;
    }

    @Override
    public void setIronVolume(int volume) {
        liquidIron = MathHelper.clamp(volume, 0, IRON_CAPACITY);
    }

    @Override
    public int insertIron(int amount) {
        int over = Math.max(0, liquidIron + amount - IRON_CAPACITY);
        setIronVolume(liquidIron + amount);
        setChanged();
        return over;
    }

    @Override
    public int extractIron(int amount) {
        int extract = Math.min(amount, liquidIron);
        setIronVolume(liquidIron - amount);
        setChanged();
        return extract;
    }

    @Override
    public int getRequestAmount() {
        return 250;
    }

    @Override
    protected void write(CompoundNBT nbt) {
        nbt.putInt("liquidIron", liquidIron);
    }

    @Override
    protected void read(CompoundNBT nbt) {
        liquidIron = nbt.getInt("liquidIron");
    }

    /**
     * Generates arrow every few seconds based on interval until specific threshold is reached
     */
    private void generateArrows() {
        if (level.isClientSide)
            return;
        ItemStack stack = itemHandler.getStackInSlot(0);
        long time = level.getDayTime();
        if ((stack.isEmpty() || stack.getCount() < ARROW_THRESHOLD) && time % ARROW_GEN_INTERVAL == 0L) {
            itemHandler.insertItem(0, new ItemStack(Items.ARROW), false);
        }
    }

    /**
     * Adds effect to all living entities whose are in range.
     * Applied every 3 seconds server-side only
     */
    private void addEffectToAllAround() {
        if (!level.isClientSide && level.getDayTime() % 60L == 0L) {
            Predicate<LivingEntity> predicate = entity -> !entity.isSpectator() && entity.getType() != Entities.SENTRY.get();
            List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, VoxelShapes.block().move(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ()).bounds().inflate(RANGE), predicate);
            list.forEach(this::addRegenerationEffect);
        }
    }

    /**
     * Adds regeneration effect to specified if entity is within range.
     * @param entity Entity
     */
    private void addRegenerationEffect(LivingEntity entity) {
        BlockPos pos1 = worldPosition;
        Vector3d pos2 = entity.position();
        double x = Mth.sqr(pos1.getX() - pos2.x());
        double y = Mth.sqr(pos1.getY() - pos2.y());
        double z = Mth.sqr(pos1.getZ() - pos2.z());
        double distance = x + y + z;
        // additional range check, because first check provided by level is by AABB which is square instead of circle
        if (distance <= SQR_RANGE) {
            entity.addEffect(new EffectInstance(Effects.REGENERATION, 65, 0, true, true));
        }
    }
}
