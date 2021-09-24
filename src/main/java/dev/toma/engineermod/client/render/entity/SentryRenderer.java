package dev.toma.engineermod.client.render.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import dev.toma.engineermod.EngineerMod;
import dev.toma.engineermod.client.model.entity.SentryModel;
import dev.toma.engineermod.common.entity.SentryEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

/**
 * Sentry renderer.
 *
 * @author Toma
 * @version 1.0
 */
public class SentryRenderer extends MobRenderer<SentryEntity, SentryModel> {

    /**
     * Sentry texture path
     */
    private static final ResourceLocation TEXTURE = EngineerMod.createModPath("textures/entity/sentry1.png");

    /**
     * Constructor
     * @param manager Entity render manager
     */
    public SentryRenderer(EntityRendererManager manager) {
        super(manager, new SentryModel(), 0.4F);
    }

    @Override
    public ResourceLocation getTextureLocation(SentryEntity entity) {
        return TEXTURE;
    }

    @Override
    protected void setupRotations(SentryEntity p_225621_1_, MatrixStack p_225621_2_, float p_225621_3_, float p_225621_4_, float p_225621_5_) {
    }

    @Override
    protected void scale(SentryEntity sentry, MatrixStack stack, float partialTicks) {
        stack.scale(1.5F, 1.5F, 1.5F);
    }
}
