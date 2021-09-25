package dev.toma.engineermod.common.entity;

import dev.toma.engineermod.common.init.Entities;
import dev.toma.engineermod.util.Mth;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.network.IPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.Random;

/**
 * @author Toma
 * @version 1.0
 */
public class SentryBullet extends ProjectileEntity {

    /**
     * Constructor
     * @param type Entity type
     * @param level Entity level
     */
    public SentryBullet(EntityType<? extends SentryBullet> type, World level) {
        super(type, level);
        noPhysics = true;
    }

    public SentryBullet(World level, SentryEntity owner) {
        this(Entities.SENTRY_BULLET.get(), level);
        setPos(owner.getX(), owner.getEyeY(), owner.getZ());
    }

    public void shoot(float xRot, float yRot, float velocity, float inaccuracy) {
        Random random = this.random;
        float scale = 0.2F;
        float xx = inaccuracy * (random.nextFloat() - random.nextFloat());
        float yy = inaccuracy * (random.nextFloat() - random.nextFloat());
        float zz = inaccuracy * (random.nextFloat() - random.nextFloat());
        Vector3d offsetVec = new Vector3d(xx, yy, zz).multiply(scale, scale, scale);
        Vector3d vec = Vector3d.directionFromRotation(xRot, yRot);
        setDeltaMovement(vec.add(offsetVec).multiply(velocity, velocity, velocity));
        updateHeading();
    }

    @Override
    protected void onHitBlock(BlockRayTraceResult result) {
        BlockPos pos = result.getBlockPos();
        BlockState state = level.getBlockState(pos);
        state.onProjectileHit(level, state, result, this);
        if (!state.isAir(level, pos)) {
            remove();
        }
    }

    @Override
    protected void onHitEntity(EntityRayTraceResult result) {
        Entity entity = result.getEntity();
        if (entity != null) {
            entity.hurt(DamageSource.GENERIC, 3.0F);
            entity.invulnerableTime = 0;
            remove();
        }
    }

    @Nullable
    protected EntityRayTraceResult findHitEntity(Vector3d vec1, Vector3d vec2) {
        return ProjectileHelper.getEntityHitResult(this.level, this, vec1, vec2, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0D), this::canHitEntity);
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        return !entity.isSpectator() && entity.isAlive() && entity.getType() != Entities.SENTRY.get();
    }

    @Override
    public void tick() {
        super.tick();
        updateHeading();

        Vector3d v1 = position();
        Vector3d v2 = v1.add(getDeltaMovement());
        checkCollisions(v1, v2);

        if (tickCount > 20)
            remove();

        move(MoverType.SELF, getDeltaMovement());
    }

    @OnlyIn(Dist.CLIENT)
    public void lerpTo(double x, double y, double z, float xRot, float yRot, int p_180426_9_, boolean p_180426_10_) {
        this.setPos(x, y, z);
        this.setRot(yRot, xRot);
    }

    @Override
    protected void defineSynchedData() {}

    @Override
    public boolean shouldRenderAtSqrDistance(double dist) {
        double size = this.getBoundingBox().getSize() * 10.0D;
        if (Double.isNaN(size)) {
            size = 1.0D;
        }

        size = size * 64.0D * getViewScale();
        return dist < size * size;
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    private void updateHeading() {
        Vector3d delta = getDeltaMovement();
        float f = MathHelper.sqrt(Mth.sqr(delta.x) + Mth.sqr(delta.z));
        yRot = (float) (MathHelper.atan2(delta.x, delta.z) * (180.0D / Math.PI));
        xRot = (float) (MathHelper.atan2(delta.y, f) * (180.0D / Math.PI));
        yRotO = yRot;
        xRotO = xRot;
    }

    private void checkCollisions(Vector3d v1, Vector3d v2) {
        RayTraceResult result = level.clip(new RayTraceContext(v1, v2, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, this));
        if (result.getType() != RayTraceResult.Type.MISS) {
            v2 = result.getLocation();
        }
        EntityRayTraceResult entityResult = findHitEntity(v1, v2);
        if (entityResult != null) {
            result = entityResult;
        }
        if (result != null) {
            if (result.getType() == RayTraceResult.Type.ENTITY) {
                onHitEntity((EntityRayTraceResult) result);
            } else {
                onHitBlock((BlockRayTraceResult) result);
            }
        }
    }
}
