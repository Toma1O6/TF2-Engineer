package dev.toma.engineermod.client.render.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import dev.toma.engineermod.common.entity.SentryBullet;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

/**
 * @author Toma
 * @version 1.0
 */
public class SentryBulletRenderer extends EntityRenderer<SentryBullet> {

    public static final ResourceLocation NORMAL_ARROW_LOCATION = new ResourceLocation("tf2engineer:textures/entity/projectile.png");

    public SentryBulletRenderer(EntityRendererManager manager) {
        super(manager);
    }

    @Override
    public ResourceLocation getTextureLocation(SentryBullet bullet) {
        return NORMAL_ARROW_LOCATION;
    }

    @Override
    public void render(SentryBullet bullet, float yaw, float partial, MatrixStack poseStack, IRenderTypeBuffer renderBuffer, int light) {
        poseStack.pushPose();
        poseStack.mulPose(Vector3f.YP.rotationDegrees(MathHelper.lerp(partial, bullet.yRotO, bullet.yRot) - 90.0F));
        poseStack.mulPose(Vector3f.ZP.rotationDegrees(MathHelper.lerp(partial, bullet.xRotO, bullet.xRot)));
        poseStack.mulPose(Vector3f.XP.rotationDegrees(45.0F));
        poseStack.scale(0.05625F, 0.05625F, 0.05625F);
        poseStack.translate(-4.0D, 0.0D, 0.0D);
        IVertexBuilder ivertexbuilder = renderBuffer.getBuffer(RenderType.entityCutout(this.getTextureLocation(bullet)));
        MatrixStack.Entry matrixstack$entry = poseStack.last();
        Matrix4f matrix4f = matrixstack$entry.pose();
        Matrix3f matrix3f = matrixstack$entry.normal();
        for(int j = 0; j < 4; ++j) {
            poseStack.mulPose(Vector3f.XP.rotationDegrees(90.0F));
            this.vertex(matrix4f, matrix3f, ivertexbuilder, -8, -2, 0, 0.0F, 0.0F, 0, 1, 0, light);
            this.vertex(matrix4f, matrix3f, ivertexbuilder, 8, -2, 0, 1.0F, 0.0F, 0, 1, 0, light);
            this.vertex(matrix4f, matrix3f, ivertexbuilder, 8, 2, 0, 1.0F, 0.3125F, 0, 1, 0, light);
            this.vertex(matrix4f, matrix3f, ivertexbuilder, -8, 2, 0, 0.0F, 0.3125F, 0, 1, 0, light);
        }

        poseStack.popPose();
        super.render(bullet, yaw, partial, poseStack, renderBuffer, light);
    }

    public void vertex(Matrix4f pose, Matrix3f normal, IVertexBuilder builder, int x, int y, int z, float u, float v, int normalX, int normalZ, int normalY, int uv2) {
        builder.vertex(pose, (float)x, (float)y, (float)z).color(255, 255, 255, 255).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(uv2).normal(normal, (float)normalX, (float)normalY, (float)normalZ).endVertex();
    }
}
