package dev.toma.engineermod.common.entity.task;

import dev.toma.engineermod.common.entity.SentryEntity;
import dev.toma.engineermod.common.entity.SentryTargetType;
import dev.toma.engineermod.common.init.Entities;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.function.Predicate;

/**
 * Sentry target selector.
 *
 * @author Toma
 * @version 1.0
 */
public class SentryFindTargetTask extends NearestAttackableTargetGoal<LivingEntity> {

    /**
     * Current target type configuration used by this task
     */
    private SentryTargetType usingTargetType;

    /**
     * If target conditions must be updated
     */
    private boolean needsUpdate = true;

    /**
     * Constructor
     * @param sentry The sentry
     * @param checkSight If entity must be seen
     */
    public SentryFindTargetTask(SentryEntity sentry, boolean checkSight) {
        super(sentry, LivingEntity.class, 0, checkSight, false, entity -> entity.getType() != Entities.SENTRY.get());
        updateConditions();
    }

    @Override
    public void tick() {
        SentryEntity entity = (SentryEntity) mob;
        SentryTargetType targetType = entity.getTargetType();
        if (targetType != usingTargetType || needsUpdate) {
            updateConditions();
        }
    }

    @Override
    protected AxisAlignedBB getTargetSearchArea(double range) {
        return mob.getBoundingBox().inflate(range);
    }

    /**
     * Updates target conditions based on selector config
     */
    private void updateConditions() {
        SentryEntity entity = (SentryEntity) mob;
        SentryTargetType targetType = entity.getTargetType();
        if (targetType == null)
            return;
        this.usingTargetType = targetType;
        entity.setTarget(null);
        Predicate<LivingEntity> selector = e -> e.getType() != Entities.SENTRY.get() && targetType.isValidTargetFor(entity, e);
        this.targetConditions = new EntityPredicate().range(getFollowDistance()).selector(selector);
        this.needsUpdate = false;
    }
}
