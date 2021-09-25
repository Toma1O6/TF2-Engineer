package dev.toma.engineermod.common.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import dev.toma.engineermod.EngineerGroups;
import dev.toma.engineermod.EngineerMod;
import dev.toma.engineermod.common.ILiquidIronStorage;
import dev.toma.engineermod.common.container.WrenchContainer;
import dev.toma.engineermod.common.init.Containers;
import dev.toma.engineermod.common.init.ModdedBlocks;
import dev.toma.engineermod.common.init.Sounds;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

/**
 * Wrench is item responsible for machine repairs and modifications.
 * It internally stores liquid iron which is used for features listed above.
 *
 * @author Toma
 * @version 1.0
 */
public class WrenchItem extends Item {

    /**
     * Liquid iron capacity
     */
    public static final int CAPACITY = 2000;

    /**
     * Wrench container text component
     */
    private static final ITextComponent TITLE = new TranslationTextComponent("container.wrench");

    /**
     * Wrench attribute modifiers, such as attack damage and cooldown
     */
    private final Multimap<Attribute, AttributeModifier> modifiers;

    /**
     * Constructor
     * @param name Registry name
     */
    public WrenchItem(String name) {
        super(new Properties().tab(EngineerGroups.ENGINEER).stacksTo(1));
        setRegistryName(EngineerMod.createModPath(name));

        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", 6.0, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", -2.4, AttributeModifier.Operation.ADDITION));
        modifiers = builder.build();
    }

    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
        entity.playSound(Sounds.WRENCH_SWING, 1.0F, 1.0F);
        return super.onEntitySwing(stack, entity);
    }

    @Override
    public ActionResult<ItemStack> use(World level, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide && player.isCrouching()) {
            NetworkHooks.openGui((ServerPlayerEntity) player, new SimpleNamedContainerProvider((id, inv, entity) -> new WrenchContainer(Containers.WRENCH_CONTAINER.get(), id, inv, stack), TITLE));
        }
        return ActionResult.consume(stack);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        if (state.is(ModdedBlocks.DISPENSER) || state.is(ModdedBlocks.TELEPORTER)) {
            return 15.0F;
        }
        return super.getDestroySpeed(stack, state);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, PlayerEntity player, Entity entity) {
        if (entity instanceof ILiquidIronStorage) {
            ILiquidIronStorage storage = (ILiquidIronStorage) entity;
            int total = getIronVolume(stack);
            int insertTarget = Math.min(total, storage.getRequestAmount());
            int extra = storage.insertIron(insertTarget);
            int consumed = insertTarget - extra;
            consumeIronVolume(stack, consumed);
            player.playSound(Sounds.WRENCH_HIT_BUILD_SUCCESS, 1.0F, 1.0F);
            return true;
        }
        return false;
    }

    @Override
    public boolean isCorrectToolForDrops(BlockState state) {
        return state.is(ModdedBlocks.DISPENSER);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlotType slotType) {
        return slotType == EquipmentSlotType.MAINHAND ? modifiers : super.getDefaultAttributeModifiers(slotType);
    }

    /**
     * Removes specified amount of iron liquid from the stack
     * @param stack Modified stack
     * @param consumeAmount Amount to consume
     */
    public static void consumeIronVolume(ItemStack stack, int consumeAmount) {
        growIronVolume(stack, -consumeAmount);
    }

    /**
     * Inserts specified amount of iron liquid into the stack
     * @param stack Modified stack
     * @param growAmount Amount to insert
     */
    public static void growIronVolume(ItemStack stack, int growAmount) {
        setIronVolume(stack, getIronVolume(stack) + growAmount);
    }

    /**
     * Sets specified amount of iron liquid in the stack
     * @param stack Modified stack
     * @param value Iron liquid value
     */
    public static void setIronVolume(ItemStack stack, int value) {
        CompoundNBT nbt = getOrCreateNbt(stack);
        nbt.putInt("liquidIron", MathHelper.clamp(value, 0, CAPACITY));
    }

    /**
     * Retrieves amount of iron liquid inside the stack
     * @param stack ItemStack
     * @return Iron liquid amount
     */
    public static int getIronVolume(ItemStack stack) {
        CompoundNBT nbt = getOrCreateNbt(stack);
        return nbt.getInt("liquidIron");
    }

    /**
     * Retrieves NBT Tag from specified tag or generates new when no tag exists.
     * @param stack ItemStack
     * @return Either NBT Tag which was assigned to ItemStack or new NBT Compound instance
     */
    private static CompoundNBT getOrCreateNbt(ItemStack stack) {
        CompoundNBT nbt = stack.getTag();
        if (nbt != null)
            return nbt;
        CompoundNBT tag = new CompoundNBT();
        tag.putInt("liquidIron", 0);
        stack.setTag(tag);
        return tag;
    }
}
