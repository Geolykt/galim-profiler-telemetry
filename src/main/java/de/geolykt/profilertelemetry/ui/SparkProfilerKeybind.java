package de.geolykt.profilertelemetry.ui;

import org.jetbrains.annotations.NotNull;

import de.geolykt.profilertelemetry.ProfilerTelemetry;
import de.geolykt.starloader.api.NamespacedKey;
import de.geolykt.starloader.api.gui.Drawing;
import de.geolykt.starloader.api.gui.Keybind;
import de.geolykt.starloader.api.gui.canvas.Canvas;
import de.geolykt.starloader.api.gui.canvas.CanvasManager;
import de.geolykt.starloader.api.gui.canvas.CanvasPosition;
import de.geolykt.starloader.api.gui.canvas.CanvasSettings;

import me.lucko.spark.common.SparkPlatform;

public class SparkProfilerKeybind implements Keybind {

    @NotNull
    private final NamespacedKey keybindId;

    @NotNull
    private final SparkPlatform spark;

    public SparkProfilerKeybind(@NotNull ProfilerTelemetry owner) {
        this.keybindId = new NamespacedKey(owner, "spark_console_open");
        this.spark = owner.getSpark();
    }

    @Override
    @NotNull
    public String getDescription() {
        return "Open the Spark profiler console";
    }

    @Override
    @NotNull
    public NamespacedKey getID() {
        return this.keybindId;
    }

    @Override
    public void executeAction() {
        CanvasManager canvasManager = Drawing.getCanvasManager();
        Canvas consoleLog = canvasManager.childCanvas(new SparkConsoleCanvasContext(1000, 900, this.spark));
        Canvas canvas = canvasManager.withMargins(15, 15, 15, 15, consoleLog, new CanvasSettings("Spark profiler console"));
        canvasManager.openCanvas(canvas, CanvasPosition.CENTER);
    }
}
