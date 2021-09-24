package dev.toma.engineermod.common.entity.task;

import dev.toma.engineermod.common.entity.SentryBullet;
import dev.toma.engineermod.common.entity.SentryEntity;
import dev.toma.engineermod.common.init.Sounds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.SoundCategory;

import java.awt.datatransfer.FlavorEvent;

/**
 * @author Toma
 * @version 1.0
 */
public class SentryShootTask extends Goal {

    private final SentryEntity entity;
    private int shootDelay;

    public SentryShootTask(SentryEntity entity) {
        this.entity = entity;
    }

    @Override
    public void start() {
        shootDelay = 10;
    }

    @Override
    public void tick() {
        if (--shootDelay <= 0) {
            shoot();
            shootDelay = 10;
        }
        LivingEntity target = entity.getTarget();
        if (target == null)
            return;
        entity.getLookControl().setLookAt(target.getX(), target.getEyeY() - 0.15, target.getZ());
    }

    @Override
    public boolean canUse() {
        return entity.hasAmmo() && entity.getTarget() != null && entity.getTargetType().isValidTargetFor(entity, entity.getTarget());
    }

    @Override
    public boolean canContinueToUse() {
        return entity.hasAmmo() && entity.getTarget() != null;
    }

    private void shoot() {
        if (!entity.level.isClientSide) {
            SentryBullet bullet = new SentryBullet(entity.level, entity);
            bullet.shoot(entity.xRot, entity.yHeadRot, 6.0F, 0.5F);
            entity.level.addFreshEntity(bullet);
            entity.consumeAmmo();
            entity.level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), Sounds.SENTRY_SHOOT, SoundCategory.MASTER, 0.4F, 1.0F);
        }
    }
}
