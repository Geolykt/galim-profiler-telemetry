package de.geolykt.profilertelemetry.ui;

import java.util.Objects;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.geolykt.starloader.api.gui.Drawing;
import de.geolykt.starloader.api.gui.canvas.prefab.AbstractConsoleCanvasContext;

import me.lucko.spark.common.SparkPlatform;

public class SparkConsoleCanvasContext extends AbstractConsoleCanvasContext {
    @NotNull
    private final SparkPlatform spark;

    public SparkConsoleCanvasContext(int width, int height, @NotNull SparkPlatform spark) {
        super(width, height, Drawing.getSpaceFont());

        GalimProfilerTelemetryConsoleLog.INSTANCE.addLog("[RED]Spark command console. Press [ESCAPE] to exit, 'spark' for help.");
        this.spark = Objects.requireNonNull(spark);
    }

    @Override
    @Nullable
    public String getLine(int lineIndex) {
        return GalimProfilerTelemetryConsoleLog.INSTANCE.getLine(lineIndex);
    }

    @Override
    public void processInput(@NotNull String entered) {
        if (entered.equals("spark")) {
            this.spark.executeCommand(GalimProfilerTelemetryConsoleLog.INSTANCE, new String[0]);
        } else if (entered.startsWith("spark ")) {
            this.spark.executeCommand(GalimProfilerTelemetryConsoleLog.INSTANCE, entered.substring("spark ".length()).split(" "));
        } else if (entered.equals("clear") || entered.startsWith("clear ")) {
            GalimProfilerTelemetryConsoleLog.INSTANCE.clearLog();
            GalimProfilerTelemetryConsoleLog.INSTANCE.addLog("[RED]Spark command console. Press [ESCAPE] to exit, 'spark' for help.");
        } else {
            GalimProfilerTelemetryConsoleLog.INSTANCE.addLog("[RED]Unknown command: []'" + entered + "'");
        }
    }
}
