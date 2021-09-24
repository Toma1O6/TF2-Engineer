package dev.toma.engineermod.common.init;

import dev.toma.engineermod.EngineerMod;
import dev.toma.engineermod.common.container.WrenchContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Container type registry.
 *
 * @author Toma
 * @version 1.0
 */
public final class Containers {

    /**
     * Container type deferred registry.
     */
    private static final DeferredRegister<ContainerType<?>> TYPES = DeferredRegister.create(ForgeRegistries.CONTAINERS, EngineerMod.MODID);

    /**
     * Wrench container type.
     */
    public static final RegistryObject<ContainerType<WrenchContainer>> WRENCH_CONTAINER = register("wrench", WrenchContainer::new);

    /**
     * Subscribes this registry to specified event bus.
     * @param bus The event bus
     */
    public static void subscribe(IEventBus bus) {
        TYPES.register(bus);
    }

    /**
     * Type safe container type creation
     * @param name Registry name
     * @param factory Container factory
     * @param <C> Container type
     * @return Registry object containg new conteiner type
     */
    private static <C extends Container> RegistryObject<ContainerType<C>> register(String name, IContainerFactory<C> factory) {
        return TYPES.register(name, () -> IForgeContainerType.create(factory));
    }

    /**
     * Private constructor.
     */
    private Containers() {}
}
