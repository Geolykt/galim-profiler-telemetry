package de.geolykt.profilertelemetry.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

import me.lucko.spark.common.util.StatisticFormatter;

@Mixin(StatisticFormatter.class)
public class StatisticFormatterMixins {
    @Overwrite
    public static TextComponent formatTps(final double tps) {
        TextColor color;
        if (tps > 18.0) {
            color = (TextColor) NamedTextColor.GREEN;
        }
        else if (tps > 16.0) {
            color = (TextColor) NamedTextColor.YELLOW;
        }
        else {
            color = (TextColor) NamedTextColor.RED;
        }
        return Component.text(Math.round(tps * 100.0) / 100.0, color);
    }
}
