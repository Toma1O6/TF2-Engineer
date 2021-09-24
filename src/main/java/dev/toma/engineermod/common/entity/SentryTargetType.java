package dev.toma.engineermod.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Effects;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.UUID;

/**
 * All sentry target types
 *
 * @author Toma
 * @version 1.0
 */
public enum SentryTargetType {

    ALL(ITargetPredicate.BASIC_PREDICATE),
    HOSTILE(ITargetPredicate.and(ITargetPredicate.BASIC_PREDICATE, SentryTargetType::isHostile)),
    PLAYER(ITargetPredicate.and(ITargetPredicate.BASIC_PREDICATE, SentryTargetType::nonWhitelistedPlayer));

    private final ITargetPredicate predicate;
    private final ITextComponent displayComponent;

    SentryTargetType(ITargetPredicate predicate) {
        this.predicate = predicate;
        this.displayComponent = new TranslationTextComponent("sentry.target." + name().toLowerCase());
    }

    public ITextComponent getDisplayComponent() {
        return displayComponent;
    }

    public boolean isValidTargetFor(SentryEntity sentry, Entity target) {
        return predicate.canAttack(sentry, target);
    }

    public static SentryTargetType fromNbt(CompoundNBT nbt) {
        SentryTargetType[] vals = values();
        int len = vals.length;
        byte id = (byte) (nbt.getByte("targetType") % len);
        return vals[id];
    }

    public static boolean ignoreOwner(SentryEntity sentry, Entity target) {
        UUID uuid = target.getUUID();
        return !sentry.isOwner(uuid);
    }

    public static boolean ignoreInvisible(SentryEntity sentry, Entity target) {
        if (target instanceof LivingEntity) {
            LivingEntity entity = (LivingEntity) target;
            return !entity.hasEffect(Effects.INVISIBILITY);
        }
        return false;
    }

    private static boolean isHostile(SentryEntity sentry, Entity target) {
        return target.getClassification(false) == EntityClassification.MONSTER;
    }

    private static boolean nonWhitelistedPlayer(SentryEntity entity, Entity target) {
        if (target instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) target;
            UUID uuid = player.getUUID();
            return !entity.isWhitelisted(uuid);
        }
        return false;
    }
}
