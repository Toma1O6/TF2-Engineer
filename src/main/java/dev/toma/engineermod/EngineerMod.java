package dev.toma.engineermod;

import dev.toma.engineermod.client.EngineerModClient;
import dev.toma.engineermod.common.init.BlockEntities;
import dev.toma.engineermod.common.init.Containers;
import dev.toma.engineermod.common.init.Entities;
import dev.toma.engineermod.network.NetworkHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This mod is supposed to add few features from TF2 character - Engineer.
 * Entities:
 *  Sentry - turret with configurable targets (HOSTILE, ALL, PLAYER)
 *  Dispenser - Passively adds regeneration I to all nearby entities. Passively generates arrows which can then be dispensed
 *              with a right click
 *  Teleporter - Teleports players from A to B and vice versa
 *
 * Items:
 *  Wrench - Iron liquid storage/processor (2000mb cap). Used to upgrade and heal dispenser and sentry
 *
 * @author Toma
 * @version 1.0
 */
@Mod(EngineerMod.MODID)
public class EngineerMod {

    /**
     * Unique ID for mod identification
     */
    public static final String MODID = "tf2engineer";

    /**
     * The mod's logger
     */
    public static final Logger LOGGER = LogManager.getLogger("TF2-Engineer-Buildings-Mod");

    /**
     * Mod constructor
     */
    public EngineerMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        // event listeners
        modEventBus.addListener(this::onClientSetup);
        modEventBus.addListener(this::onCommonSetup);
        // deferred registries
        Entities.subscribe(modEventBus);
        BlockEntities.subscribe(modEventBus);
        Containers.subscribe(modEventBus);
    }

    /**
     * Creates new resource location with this mod id as namespace
     * @param path Resource path
     * @return New resource location
     */
    public static ResourceLocation createModPath(String path) {
        return new ResourceLocation(MODID, path);
    }

    /**
     * Called client-side for client only stuff initialization
     * @param event The event
     */
    private void onClientSetup(FMLClientSetupEvent event) {
        EngineerModClient.instance().setup(event);
    }

    /**
     * Called on both sides for common stuff initialization
     * @param event The event
     */
    private void onCommonSetup(FMLCommonSetupEvent event) {
        NetworkHandler.registerPackets();
    }
}
