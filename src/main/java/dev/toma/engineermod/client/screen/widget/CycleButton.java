package dev.toma.engineermod.client.screen.widget;

import dev.toma.engineermod.util.ITextFormatter;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.Objects;

/**
 * Button which cycles through predefined values on click.
 *
 * @author Toma
 * @version 1.0
 */
public class CycleButton<T> extends Button {

    /**
     * Valid values for iteration
     */
    private final T[] values;

    /**
     * The object to text formatter
     */
    private ITextFormatter<T> formatter = t -> StringTextComponent.EMPTY;

    /**
     * Selection change listener
     */
    private ISelectionChange<T> selectionChange;

    /**
     * Current selection index
     */
    private int selectionIndex;

    /**
     * Constructor
     * @param values Valid values for iteration
     * @param x X widget position
     * @param y Y widget position
     * @param width Widget width
     * @param height Widget height
     */
    public CycleButton(T[] values, int x, int y, int width, int height) {
        super(x, y, width, height, StringTextComponent.EMPTY, null);
        this.values = values;
    }

    /**
     * Sets text formatter
     * @param formatter The {@code nonnull} formatter
     */
    public void setFormatter(ITextFormatter<T> formatter) {
        this.formatter = Objects.requireNonNull(formatter);
        updateMessage();
    }

    /**
     * Sets selection change listener
     * @param selectionChange The listener
     */
    public void onChanged(ISelectionChange<T> selectionChange) {
        this.selectionChange = selectionChange;
    }

    /**
     * Forces index change
     * @param index New index value
     */
    public void setSelectionIndex(int index) {
        this.selectionIndex = index % values.length;
        updateMessage();
        notifyChangeListener();
    }

    /**
     * @return Currently selected object
     */
    public T getValue() {
        return values[selectionIndex];
    }

    @Override
    public void onPress() {
        setSelectionIndex(selectionIndex + 1);
    }

    /**
     * Updates message based on currently selected object
     */
    private void updateMessage() {
        T t = getValue();
        ITextComponent component = formatter.getFormattedComponent(t);
        setMessage(component);
    }

    /**
     * Notifies selection change listener (if any exists).
     */
    private void notifyChangeListener() {
        if (selectionChange != null) {
            T t = getValue();
            selectionChange.onSelectionChanged(t);
        }
    }

    /**
     * Selection change callback
     * @param <T> Type of object which is used by widget
     */
    @FunctionalInterface
    public interface ISelectionChange<T> {

        /**
         * Called when selection changes
         * @param newValue New value
         */
        void onSelectionChanged(T newValue);
    }
}
