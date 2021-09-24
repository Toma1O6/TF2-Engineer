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
 * @author Toma
 * @version 1.0
 */
public class ListView<T> extends Widget {

    private final FontRenderer font;
    private final List<T> list;
    private final int entryDisplayLimit;
    private final Minecraft mc;
    private ITextFormatter<T> textFormatter = t -> StringTextComponent.EMPTY;
    private IClickResponder<T> clickResponder;
    private int offset;
    private T selected;

    public ListView(FontRenderer font, int x, int y, int width, int height, Iterable<T> values) {
        super(x, y, width, height, StringTextComponent.EMPTY);
        this.mc = Minecraft.getInstance();
        this.font = font;
        this.list = convertToList(values);
        this.entryDisplayLimit = height / 12;
    }

    public void setTextFormatter(ITextFormatter<T> textFormatter) {
        this.textFormatter = Objects.requireNonNull(textFormatter);
    }

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

    public boolean isHovered(int index, int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y + index * 12 && mouseY < y + (index + 1) * 12;
    }

    public boolean isSelected(T t) {
        return Objects.equals(t, selected);
    }

    public boolean isFull() {
        return list.size() >= entryDisplayLimit;
    }

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

    private static <T> List<T> convertToList(Iterable<T> iterable) {
        List<T> list = new ArrayList<>();
        iterable.forEach(list::add);
        return list;
    }

    public interface IClickResponder<T> {
        void onClick(@Nullable T element);
    }
}
