package dev.toma.engineermod.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import dev.toma.engineermod.EngineerMod;
import dev.toma.engineermod.common.container.WrenchContainer;
import dev.toma.engineermod.common.item.WrenchItem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

/**
 * Wrench screen
 *
 * @author Toma
 * @version 1.0
 */
public class WrenchScreen extends ContainerScreen<WrenchContainer> {

    /**
     * Background image
     */
    private static final ResourceLocation BG = EngineerMod.createModPath("textures/screen/wrench.png");

    /**
     * Constructor
     * @param container The wrench container
     * @param inventory Player's inventory
     * @param component Display name of this screen
     */
    public WrenchScreen(WrenchContainer container, PlayerInventory inventory, ITextComponent component) {
        super(container, inventory, component);

        imageHeight = 172;
        inventoryLabelY += 6;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void renderBg(MatrixStack poseStack, float partialTicks, int mouseX, int mouseY) {
        minecraft.getTextureManager().bind(BG);
        blit(poseStack, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        renderLiquidIron(poseStack, leftPos + 134, topPos + 13, 34, 68, getAmount());
    }

    /**
     * Draws liquid iron progress bar and text information into screen.
     * @param poseStack The poseStack
     * @param left Left position
     * @param top Top position
     * @param width Width
     * @param height Height
     * @param amount Amount of liquid iron
     */
    private void renderLiquidIron(MatrixStack poseStack, int left, int top, int width, int height, int amount) {
        int h = (int) (height * getFillProgress(amount));
        fillGradient(poseStack, left, top + height - h, left + width, top + height, 0xFFDDDDDD, 0xFF888888);
        String text = amount + " / " + WrenchItem.CAPACITY + " mB";
        int tw = font.width(text);
        font.draw(poseStack, text, left - 10 - tw, top + height - 16, 0x333333);
    }

    /**
     * @return Liquid iron amount
     */
    private int getAmount() {
        ItemStack stack = minecraft.player.getMainHandItem();
        if (!(stack.getItem() instanceof WrenchItem))
            return 0;
        return WrenchItem.getIronVolume(stack);
    }

    private float getFillProgress(int amount) {
        return amount / (float) WrenchItem.CAPACITY;
    }
}
