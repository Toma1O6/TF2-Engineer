package dev.toma.engineermod.common.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Interface is supposed to be implemented on items, which are capable of spawning
 * custom entities.
 * @param <E> The entity type.
 *
 * @author Toma
 * @version 1.0
 */
@FunctionalInterface
public interface IEntityCreator<E extends Entity> {

    /**
     * Used to create new entity instance for adding to level
     * @param level The level where entity will be created
     * @param placer The player which placed this entity
     * @param stack The item used to create entity
     * @return New entity instance
     */
    E createEntityInstance(World level, PlayerEntity placer, ItemStack stack);

    /**
     * Called once entity is added to level to init additional parameters
     * @param entity The spawned entity
     * @param level The level where entity spawned
     * @param player The spawn source
     */
    default void initPostSpawn(E entity, World level, PlayerEntity player) {}
}
