package dev.toma.engineermod.client.screen.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import dev.toma.engineermod.util.ITextFormatter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Allows you to display elements from collection. Default implementation doesn't support scrolling (to be implemented once required).
 * You must set custom text formatter using {@link ListView#setTextFormatter(ITextFormatter)} otherwise no values will be displayed.
 *
 * @author Toma
 * @version 1.0
 */
public class ListView<T> extends Widget {

    /**
     * The font renderer
     */
    private final FontRenderer font;

    /**
     * Internal collection of elements
     */
    private final List<T> list;

    /**
     * Amount of entries which can be displayed at the same time
     */
    private final int entryDisplayLimit;

    /**
     * Minecraft instance
     */
    private final Minecraft mc;

    /**
     * Text formatter.
     * @see ITextFormatter
     */
    private ITextFormatter<T> textFormatter = t -> StringTextComponent.EMPTY;

    /**
     * Click responder, called when element is clicked.
     * @see IClickResponder
     */
    private IClickResponder<T> clickResponder;

    /**
     * Scrolling offset
     */
    private int offset;

    /**
     * Selected element. Can be {@code null}
     */
    private T selected;

    /**
     * Constructor
     * @param font Font renderer
     * @param x X position
     * @param y Y position
     * @param width Width
     * @param height Height
     * @param values Values to be displayed
     */
    public ListView(FontRenderer font, int x, int y, int width, int height, Iterable<T> values) {
        super(x, y, width, height, StringTextComponent.EMPTY);
        this.mc = Minecraft.getInstance();
        this.font = font;
        this.list = convertToList(values);
        this.entryDisplayLimit = height / 12;
    }

    /**
     * Sets custom text formatter.
     * @param textFormatter The {@code nonnull} text formatter to be used by this list display.
     * @see ITextFormatter
     */
    public void setTextFormatter(ITextFormatter<T> textFormatter) {
        this.textFormatter = Objects.requireNonNull(textFormatter);
    }

    /**
     * Sets custom click responder.
     * @param clickResponder The click responder
     * @see IClickResponder
     */
    public void setClickResponder(IClickResponder<T> clickResponder) {
        this.clickResponder = clickResponder;
    }

    @Override
    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        int black = 0xFF << 24;
        fillGradient(matrixStack, x, y, x + width, y + height, black, black);
        for (int i = offset; i < offset + entryDisplayLimit; i++) {
            if (i >= list.size()) break;
            T t = list.get(i);
            boolean selected = isSelected(t);
            boolean hovered = isHovered(i, mouseX, mouseY);
            int textColor = 0xFFFFFF;
            int y1 = y + i * 12;
            int y2 = y + (i + 1) * 12 - 1;
            if (selected) {
                int white = 0x99FFFFFF;
                textColor = 0x333333;
                fillGradient(matrixStack, x, y1, x + width, y2, white, white);
            } else if (hovered) {
                int white = 0x44FFFFFF;
                textColor = 0x666666;
                fillGradient(matrixStack, x, y1, x + width, y2, white, white);
            }
            font.draw(matrixStack, textFormatter.getFormattedComponent(t), x + 3, y1 + 3, textColor);
        }
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        T t = getClickedElement((int) mouseY);
        this.selected = t;
        if (clickResponder != null) {
            clickResponder.onClick(t);
        }
    }

    /**
     * Returns whether element index is hovered.
     * @param index The element index
     * @param mouseX Mouse X
     * @param mouseY Mouse Y
     * @return If element at specified index is hovered.
     */
    public boolean isHovered(int index, int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y + index * 12 && mouseY < y + (index + 1) * 12;
    }

    /**
     * @param t The element to check
     * @return Checks if provided element is selected.
     */
    public boolean isSelected(T t) {
        return Objects.equals(t, selected);
    }

    /**
     * @return If this list's display capacity is full
     */
    public boolean isFull() {
        return list.size() >= entryDisplayLimit;
    }

    /**
     * @return Selected element. Can be {@code null}
     */
    public T getSelected() {
        return selected;
    }

    @Nullable
    private T getClickedElement(int mouseY) {
        int absoluteY = mouseY - y;
        int index = absoluteY / 12;
        T t = null;
        if (index >= 0 && index < list.size()) {
            t = list.get(index);
        }
        return t;
    }

    /**
     * Adds all element's from iterable into newly constructed array list.
     * @param iterable The iterable to convert
     * @param <T> Element type
     * @return Array list with all elements from supplied iterable.
     */
    private static <T> List<T> convertToList(Iterable<T> iterable) {
        List<T> list = new ArrayList<>();
        iterable.forEach(list::add);
        return list;
    }

    /**
     * Simple click callback. Called anytime player clicks on this widget.
     * @param <T> The element type
     */
    @FunctionalInterface
    public interface IClickResponder<T> {

        /**
         * Called when player clicks this list display.
         * @param element The clicked element. {@code Null} when no element was clicked.
         */
        void onClick(@Nullable T element);
    }
}
