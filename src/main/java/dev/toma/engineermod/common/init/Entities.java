package dev.toma.engineermod.common.init;

import dev.toma.engineermod.EngineerMod;
import dev.toma.engineermod.common.entity.SentryBullet;
import dev.toma.engineermod.common.entity.SentryEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Consumer;

/**
 * Mod registry for all entities.
 *
 * @author Toma
 * @version 1.0
 */
public final class Entities {

    /**
     * Deferred registry
     */
    private static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, EngineerMod.MODID);

    /**
     * Sentry entity type
     */
    public static final RegistryObject<EntityType<SentryEntity>> SENTRY = register("sentry", SentryEntity::new, EntityClassification.MISC, Entities::setupStationaryEntity);

    /**
     * Sentry bullet type
     */
    public static final RegistryObject<EntityType<SentryBullet>> SENTRY_BULLET = register("sentry_bullet", SentryBullet::new, EntityClassification.MISC, builder -> builder.sized(0.1F, 0.1F).clientTrackingRange(4).updateInterval(10));

    /**
     * Subscribes registry to specified event bus.
     * @param bus The event bus. Should be {@link net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus#MOD}
     */
    public static void subscribe(IEventBus bus) {
        TYPES.register(bus);
    }

    /**
     * Registers entity type with provided parameters and returns registry object for future references.
     * @param name Registry name of your entity type
     * @param factory Entity instance factory
     * @param classification Entity classification
     * @param builderConsumer Consumer which allows you to further modify entity parameters
     * @param <T> Type of entity
     * @return New registry object
     */
    private static <T extends Entity> RegistryObject<EntityType<T>> register(
            String name,
            EntityType.IFactory<T> factory,
            EntityClassification classification,
            Consumer<EntityType.Builder<T>> builderConsumer
    ) {
        EntityType.Builder<T> builder = EntityType.Builder.of(factory, classification);
        builderConsumer.accept(builder);
        return TYPES.register(name, () -> builder.build(name));
    }

    /**
     * Sets default values to provided builder, such as tracking range and size
     * @param builder The builder to modify
     */
    private static void setupStationaryEntity(EntityType.Builder<?> builder) {
        builder.setTrackingRange(64).fireImmune().sized(0.8F, 1.4F);
    }

    // Utility class, no instances needed
    private Entities() {}
}
