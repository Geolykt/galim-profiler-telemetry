package de.geolykt.profilertelemetry.ui.adventure;

import java.util.Objects;

import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.flattener.ComponentFlattener;
import net.kyori.adventure.text.flattener.FlattenerListener;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.ComponentSerializer;

public class ComponentSerializerGDX implements ComponentSerializer<Component, TextComponent, String> {
    private static class ComponentGDXFlattener implements FlattenerListener {
        @NotNull
        private final StringBuilder text = new StringBuilder();

        @Override
        public void component(@NotNull String text) {
            this.text.append(text.replace("[", "[["));
        }

        @Override
        public void popStyle(@NotNull Style style) {
            if (style.color() != null) {
                this.text.append("[]");
            }
        }

        @Override
        public void pushStyle(@NotNull Style style) {
            TextColor color = style.color();
            if (color != null) {
                this.text.append("[").append(color.asHexString()).append("]");
            }
        }
    }

    public static final ComponentSerializerGDX INSTANCE = new ComponentSerializerGDX();

    @Override
    @NotNull
    public TextComponent deserialize(@NotNull String input) {
        throw new UnsupportedOperationException("Deserialization not supported (at the moment).");
    }

    @Override
    @NotNull
    public String serialize(@NotNull Component component) {
        ComponentGDXFlattener flattener = new ComponentGDXFlattener();
        ComponentFlattener.basic().flatten(component, flattener);
        return Objects.requireNonNull(flattener.text.toString());
    }
}
