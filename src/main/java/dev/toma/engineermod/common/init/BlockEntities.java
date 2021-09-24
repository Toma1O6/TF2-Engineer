package dev.toma.engineermod.common.init;

import dev.toma.engineermod.EngineerMod;
import dev.toma.engineermod.common.blockentity.DispenserBlockEntity;
import dev.toma.engineermod.common.blockentity.TeleporterBlockEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Block entity registry.
 *
 * @author Toma
 * @version 1.0
 */
public final class BlockEntities {

    /**
     * Deferred registry for block entities
     */
    private static final DeferredRegister<TileEntityType<?>> TYPES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, EngineerMod.MODID);

    // ------------ ENTRIES

    /**
     * Dispenser block entity type
     */
    public static final RegistryObject<TileEntityType<?>> DISPENSER_TYPE = TYPES.register("dispenser", () -> TileEntityType.Builder.of(DispenserBlockEntity::new, ModdedBlocks.DISPENSER).build(null));

    /**
     * Teleporter block entity type
     */
    public static final RegistryObject<TileEntityType<?>> TELEPORTER_TYPE = TYPES.register("teleporter", () -> TileEntityType.Builder.of(TeleporterBlockEntity::new, ModdedBlocks.TELEPORTER).build(null));

    // ------------ ENTRIES END

    /**
     * Registers this registry to provider event bus.
     * @param eventBus Event bus
     */
    public static void subscribe(IEventBus eventBus) {
        TYPES.register(eventBus);
    }

    /**
     * Private constructor
     */
    private BlockEntities() {}
}
