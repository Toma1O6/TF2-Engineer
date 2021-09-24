package dev.toma.engineermod.common.item;

import dev.toma.engineermod.EngineerMod;
import dev.toma.engineermod.common.init.Sounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Handles machine spawning
 * @param <E> Entity type
 *
 * @author Toma
 * @version 1.0
 */
public class ItemMachineCreator<E extends Entity> extends Item {

    /**
     * The entity instance creator
     */
    private final IEntityCreator<E> creator;

    /**
     * Constructor
     * @param name Registry name - just the path
     * @param properties Item properties such as creative tab etc.
     * @param creator Entity instance creator
     */
    public ItemMachineCreator(String name, Properties properties, IEntityCreator<E> creator) {
        super(properties);
        this.creator = creator;
        setRegistryName(EngineerMod.createModPath(name));
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        World level = context.getLevel();
        if (level.isClientSide)
            return ActionResultType.CONSUME;
        BlockPos pos = context.getClickedPos();
        PlayerEntity player = context.getPlayer();
        ItemStack stack = context.getItemInHand();
        E entity = creator.createEntityInstance(level, player, stack);
        entity.setPos(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
        if (level.addFreshEntity(entity)) {
            player.playSound(Sounds.SENTRY_DEPLOY, 1.0F, 1.0F);
            creator.initPostSpawn(entity, level, player);
        }
        if (!player.isCreative()) {
            stack.shrink(1);
        }
        return ActionResultType.SUCCESS;
    }
}
