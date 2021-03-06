package dev.toma.engineermod.client.model.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import dev.toma.engineermod.common.entity.SentryEntity;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;

/**
 * Sentry model.
 *
 * @author Model by NotDracon
 * @version 1.0
 */
public class SentryModel extends EntityModel<SentryEntity> {

    private final ModelRenderer sentryhead;
    private final ModelRenderer sentrybase;
    private final ModelRenderer cube_r1;
    private final ModelRenderer cube_r2;
    private final ModelRenderer cube_r3;

    /**
     * Constructor
     */
    public SentryModel() {
        texWidth = 32;
        texHeight = 32;

        sentryhead = new ModelRenderer(this);
        sentryhead.setPos(0.3333F, 14.2944F, -1.3278F);
        sentryhead.texOffs(10, 22).addBox(-0.9833F, -1.6444F, -5.6722F, 2.0F, 2.0F, 2.0F, 0.0F, false);
        sentryhead.texOffs(0, 11).addBox(-2.0333F, -2.5944F, -3.6722F, 4.0F, 4.0F, 5.0F, 0.0F, false);
        sentryhead.texOffs(0, 0).addBox(-3.0833F, -2.8944F, 2.3278F, 6.0F, 5.0F, 6.0F, 0.0F, false);
        sentryhead.texOffs(0, 20).addBox(-2.0333F, -2.5944F, 1.3278F, 4.0F, 4.0F, 1.0F, 0.0F, false);
        sentryhead.texOffs(10, 14).addBox(-3.4333F, -0.4944F, -0.8222F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        sentryhead.texOffs(13, 11).addBox(-3.0333F, -1.2944F, -1.2222F, 1.0F, 3.0F, 2.0F, 0.0F, false);
        sentryhead.texOffs(18, 29).addBox(-2.6333F, 1.3056F, -1.2222F, 5.0F, 1.0F, 2.0F, 0.0F, false);
        sentryhead.texOffs(6, 14).addBox(2.7667F, -0.4944F, -0.8222F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        sentryhead.texOffs(9, 13).addBox(1.9667F, -1.2944F, -1.2222F, 1.0F, 3.0F, 2.0F, 0.0F, false);

        sentrybase = new ModelRenderer(this);
        sentrybase.setPos(5.5F, 21.0F, -1.5F);
        sentrybase.texOffs(18, 2).addBox(-1.551F, 1.999F, -1.101F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        sentrybase.texOffs(18, 0).addBox(-6.501F, 1.999F, 4.649F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        sentrybase.texOffs(17, 13).addBox(-9.401F, 1.999F, -1.101F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        sentrybase.texOffs(0, 14).addBox(-4.501F, 1.999F, 4.649F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        sentrybase.texOffs(20, 15).addBox(-5.5F, -4.8F, -0.55F, 1.0F, 6.0F, 1.0F, 0.2F, false);

        cube_r1 = new ModelRenderer(this);
        cube_r1.setPos(0.0F, 0.0F, 0.0F);
        sentrybase.addChild(cube_r1);
        setRotationAngle(cube_r1, 0.0F, 0.0F, -0.7854F);
        cube_r1.texOffs(18, 22).addBox(-2.84F, -3.9101F, -1.1F, 1.0F, 5.0F, 1.0F, 0.0F, false);

        cube_r2 = new ModelRenderer(this);
        cube_r2.setPos(-7.0F, 0.0F, 0.0F);
        sentrybase.addChild(cube_r2);
        setRotationAngle(cube_r2, 0.0F, 0.0F, 0.7854F);
        cube_r2.texOffs(22, 22).addBox(-0.26F, -1.9101F, -1.1F, 1.0F, 5.0F, 1.0F, 0.0F, false);

        cube_r3 = new ModelRenderer(this);
        cube_r3.setPos(-5.0F, -4.0F, 1.5F);
        sentrybase.addChild(cube_r3);
        setRotationAngle(cube_r3, -0.3927F, 0.0F, 0.0F);
        cube_r3.texOffs(19, 11).addBox(-1.5F, 3.8675F, 2.1499F, 1.0F, 1.0F, 4.0F, 0.0F, false);
        cube_r3.texOffs(18, 0).addBox(0.5F, 3.8675F, 2.1499F, 1.0F, 1.0F, 4.0F, 0.0F, false);
        cube_r3.texOffs(14, 16).addBox(-1.5F, 2.8675F, -1.8501F, 3.0F, 2.0F, 4.0F, 0.0F, false);
    }

    @Override
    public void setupAnim(SentryEntity sentry, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
        float y = (float) (Math.PI + Math.toRadians(sentry.yHeadRot));
        float x = (float) (Math.toRadians(sentry.xRot));
        setRotationAngle(sentryhead, x, y, 0.0F);
    }

    @Override
    public void renderToBuffer(MatrixStack matrix, IVertexBuilder vertexBuilder, int light, int overlay, float r, float g, float b, float a) {
        sentryhead.render(matrix, vertexBuilder, light, overlay, r, g, b, a);
        sentrybase.render(matrix, vertexBuilder, light, overlay, r, g, b, a);
    }

    /**
     * Sets renderer rotation angle
     * @param modelRenderer The renderer to modify
     * @param x X rotation
     * @param y Y rotation
     * @param z Z rotation
     */
    private static void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
    }
}
