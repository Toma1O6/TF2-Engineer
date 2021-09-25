package dev.toma.engineermod.client;

import dev.toma.engineermod.client.render.entity.SentryBulletRenderer;
import dev.toma.engineermod.client.render.entity.SentryRenderer;
import dev.toma.engineermod.client.screen.SentryScreen;
import dev.toma.engineermod.client.screen.WrenchScreen;
import dev.toma.engineermod.common.entity.SentryEntity;
import dev.toma.engineermod.common.init.Containers;
import dev.toma.engineermod.common.init.Entities;
import dev.toma.engineermod.common.init.ModdedBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * Client side manager
 *
 * @author Toma
 * @version 1.0
 */
public final class EngineerModClient {

    /**
     * Singleton instance
     */
    private static final EngineerModClient INSTANCE = new EngineerModClient();

    /**
     * @return Single instance of this class
     */
    public static EngineerModClient instance() {
        return INSTANCE;
    }

    /**
     * Client side setup callback
     * @param event The event
     */
    public void setup(FMLClientSetupEvent event) {
        assignRenderLayers();
        assignEntityRenderers();
        event.enqueueWork(() -> {
            ScreenManager.register(Containers.WRENCH_CONTAINER.get(), WrenchScreen::new);
        });
    }

    /**
     * Opens sentry screen.
     * @param sentry The sentry obj
     */
    public void openSentryScreen(SentryEntity sentry) {
        Minecraft mc = Minecraft.getInstance();
        mc.setScreen(new SentryScreen(sentry));
    }

    /**
     * Assigns render layers to blocks which require specific rendering
     */
    private void assignRenderLayers() {
        RenderTypeLookup.setRenderLayer(ModdedBlocks.TELEPORTER, RenderType.translucent());
        RenderTypeLookup.setRenderLayer(ModdedBlocks.DISPENSER, RenderType.cutout());
    }

    /**
     * Assigns entity renderers to their types
     */
    private void assignEntityRenderers() {
        RenderingRegistry.registerEntityRenderingHandler(Entities.SENTRY.get(), SentryRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(Entities.SENTRY_BULLET.get(), SentryBulletRenderer::new);
    }

    /**
     * Private constructor
     */
    private EngineerModClient() {}
}
