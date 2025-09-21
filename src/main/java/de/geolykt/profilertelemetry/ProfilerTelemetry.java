package de.geolykt.profilertelemetry;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import org.jetbrains.annotations.NotNull;

import com.badlogic.gdx.Input.Keys;

import de.geolykt.profilertelemetry.ui.SparkProfilerKeybind;
import de.geolykt.starloader.StarloaderAPIExtension;
import de.geolykt.starloader.api.event.EventHandler;
import de.geolykt.starloader.api.event.EventManager;
import de.geolykt.starloader.api.event.Listener;
import de.geolykt.starloader.api.event.lifecycle.ApplicationStopEvent;
import de.geolykt.starloader.api.event.lifecycle.SignalExtensionTerminationEvent;
import de.geolykt.starloader.api.gui.KeystrokeInputHandler;
import de.geolykt.starloader.mod.Extension;

import me.lucko.spark.common.SparkPlatform;

public class ProfilerTelemetry extends Extension {

    private AtomicReference<SparkPlatform> spark = new AtomicReference<>();

    @Override
    public void preInitialize() {
        this.spark.set(new SparkPlatform(new GalimulatorSparkPlugin(this)));
        EventManager.registerListener(new Listener() {
            @EventHandler
            public void handleShutdown(ApplicationStopEvent e) {
                ProfilerTelemetry.this.initiateSparkShutdown();
            }
            @EventHandler
            public void handleShutdown(SignalExtensionTerminationEvent e) {
                if (e.getUnloadingExtension() instanceof StarloaderAPIExtension) {
                    ProfilerTelemetry.this.initiateSparkShutdown();
                }
            }
        });
        this.spark.get().enable();
    }

    @Override
    public void initialize() {
        KeystrokeInputHandler.getInstance().registerKeybind(new SparkProfilerKeybind(this), Keys.CONTROL_LEFT, Keys.P);
    }

    private void initiateSparkShutdown() {
        SparkPlatform platform;
        while ((platform = this.spark.get()) != null) {
            if (!this.spark.compareAndSet(platform, null)) {
                continue;
            }

            this.getLogger().info("Shutting down Spark. Goodbye!");
            platform.disable();
        }
    }

    @Override
    public void terminate() {
        ProfilerTelemetry.this.initiateSparkShutdown();
    }

    @NotNull
    public SparkPlatform getSpark() {
        return Objects.requireNonNull(this.spark.get());
    }
}
