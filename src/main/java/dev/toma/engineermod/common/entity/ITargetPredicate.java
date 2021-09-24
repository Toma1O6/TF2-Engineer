package dev.toma.engineermod.common.entity;

import net.minecraft.entity.Entity;

/**
 * Sentry target predicate
 *
 * @author Toma
 * @version 1.0
 */
public interface ITargetPredicate {

    static ITargetPredicate BASIC_PREDICATE = ITargetPredicate.and(SentryTargetType::ignoreOwner, SentryTargetType::ignoreInvisible);

    /**
     * Checks if sentry can attack the target based on it's targetting options.
     * @param entity The sentry
     * @param target The target
     * @return If target can be attacked by this entity
     */
    boolean canAttack(SentryEntity entity, Entity target);

    /**
     * AND operation on 2 predicates.
     * @param predicate1 First predicate
     * @param predicate2 Second predicate
     * @return {@code True} if both predicates are valid, else returns {@code False}
     */
    static ITargetPredicate and(ITargetPredicate predicate1, ITargetPredicate predicate2) {
        return (entity, target) -> predicate1.canAttack(entity, target) && predicate2.canAttack(entity, target);
    }
}
