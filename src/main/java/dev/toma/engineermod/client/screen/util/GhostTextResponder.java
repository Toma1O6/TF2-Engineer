package dev.toma.engineermod.client.screen.util;

import net.minecraft.client.gui.widget.TextFieldWidget;

import java.util.function.Consumer;

/**
 * Text responder for text input widgets which automatically displays/hides the ghost text based on widget properties.
 *
 * @author Toma
 * @version 1.0
 */
public final class GhostTextResponder implements Consumer<String> {

    private final String textEntry;
    private final TextFieldWidget target;
    private final Consumer<String> responder;

    public GhostTextResponder(String textEntry, TextFieldWidget target) {
        this(textEntry, target, s -> {});
    }

    public GhostTextResponder(String textEntry, TextFieldWidget target, Consumer<String> responder) {
        this.textEntry = textEntry;
        this.target = target;
        this.responder = responder;
        if (target.getValue().isEmpty()) {
            target.setSuggestion(textEntry);
        }
    }

    @Override
    public void accept(String s) {
        if (s.isEmpty())
            target.setSuggestion(textEntry);
        else
            target.setSuggestion(null);
        responder.accept(s);
    }
}
