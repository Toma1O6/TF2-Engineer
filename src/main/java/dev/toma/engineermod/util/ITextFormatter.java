package dev.toma.engineermod.util;

import net.minecraft.util.text.ITextComponent;

/**
 * Simple interface for text formatting.
 *
 * @author Toma
 * @version 1.0
 */
@FunctionalInterface
public interface ITextFormatter<T> {

    /**
     * Returns text component from provided object.
     * @param t The object to format
     * @return Formatted text component
     */
    ITextComponent getFormattedComponent(T t);
}
