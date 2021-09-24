package dev.toma.engineermod.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

/**
 * Centered screen with optional image background.
 *
 * @author Toma
 * @version 1.0
 */
public abstract class CenteredScreen extends Screen {

    protected int leftPos;
    protected int topPos;
    protected int imageWidth = 176;
    protected int imageHeight = 166;

    /**
     * Constuctor
     * @param title The screen title
     */
    public CenteredScreen(ITextComponent title) {
        super(title);
    }

    /**
     * @return Background texture path
     */
    public abstract ResourceLocation getBackgroundTexture();

    @Override
    protected <T extends Widget> T addButton(T button) {
        button.x += leftPos;
        button.y += topPos;
        return super.addButton(button);
    }

    @Override
    protected void init() {
        leftPos = (width - imageWidth) / 2;
        topPos = (height - imageHeight) / 2;
    }

    /**
     * Renders background image
     * @param poseStack The pose stack
     */
    public void drawBackgroundImage(MatrixStack poseStack) {
        minecraft.getTextureManager().bind(this.getBackgroundTexture());
        blit(poseStack, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }
}
