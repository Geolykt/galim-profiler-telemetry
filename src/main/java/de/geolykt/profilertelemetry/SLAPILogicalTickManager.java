package de.geolykt.profilertelemetry;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

import de.geolykt.starloader.api.event.EventHandler;
import de.geolykt.starloader.api.event.EventManager;
import de.geolykt.starloader.api.event.EventPriority;
import de.geolykt.starloader.api.event.Listener;
import de.geolykt.starloader.api.event.lifecycle.LogicalTickEvent;
import de.geolykt.starloader.api.event.lifecycle.LogicalTickEvent.Phase;

import me.lucko.spark.common.monitor.tick.SparkTickStatistics;
import me.lucko.spark.common.monitor.tick.TickStatistics;
import me.lucko.spark.common.tick.TickHook;
import me.lucko.spark.common.tick.TickReporter;

public class SLAPILogicalTickManager implements TickHook, TickReporter, Listener {

    public static final SLAPILogicalTickManager INSTANCE = new SLAPILogicalTickManager();

    @NotNull
    private final Set<TickHook.@NotNull Callback> hookCallbacks;
    @NotNull
    private final Set<TickReporter.@NotNull Callback> reporterCallbacks;
    private int tickId = 0;
    private long tickStartNanos;
    @NotNull
    private final TickStatistics tickStatistics;
    private int startCount = 0;
    private int stopCount = 0;

    private SLAPILogicalTickManager() {
        this.hookCallbacks = new CopyOnWriteArraySet<>();
        this.reporterCallbacks = new CopyOnWriteArraySet<>();
        EventManager.registerListener(this);
        this.tickStatistics = new SparkTickStatistics();
        this.addCallback((TickHook.Callback) this.tickStatistics);
        this.addCallback((TickReporter.Callback) this.tickStatistics);
    }

    @Override
    public void addCallback(TickHook.Callback p0) {
        this.hookCallbacks.add(Objects.requireNonNull(p0, "'p0' may not be null."));
    }

    @Override
    public void addCallback(TickReporter.Callback p0) {
        this.reporterCallbacks.add(Objects.requireNonNull(p0));
    }

    @Override
    public void close() {
        if (this.stopCount++ >= 2) {
            LoggerFactory.getLogger(SLAPILogicalTickManager.class).warn("Tried closing instance too many times.");
            throw new UnsupportedOperationException("Already closed more than twice.");
        }
    }

    @Override
    public int getCurrentTick() {
        return this.tickId;
    }

    @NotNull
    @Contract(pure = true)
    public TickStatistics getTickStatistics() {
        return this.tickStatistics;
    }

    @EventHandler(EventPriority.MONITOR)
    public void onTickEvent(@NotNull LogicalTickEvent tick) {
        if (tick.getPhase() == Phase.PRE_GRAPHICAL) {
            for (TickHook.Callback callback : this.hookCallbacks) {
                callback.onTick(this.tickId);
            }
            this.tickStartNanos = System.nanoTime();
        } else if (tick.getPhase() == Phase.POST) {
            this.tickId++;
            double durationMillis = (System.nanoTime() - this.tickStartNanos) / 1_000_000.0D;
            for (TickReporter.Callback callback : this.reporterCallbacks) {
                callback.onTick(durationMillis);
            }
        }
    }

    @Override
    public void removeCallback(TickHook.Callback p0) {
        this.hookCallbacks.remove(p0);
    }

    @Override
    public void removeCallback(TickReporter.Callback p0) {
        this.reporterCallbacks.remove(p0);
    }

    @Override
    public void start() {
        if (this.startCount++ >= 2) {
            LoggerFactory.getLogger(SLAPILogicalTickManager.class).warn("Tried starting instance too many times.");
            throw new UnsupportedOperationException("Already started more than twice.");
        }
    }
}
