package de.geolykt.profilertelemetry.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.kyori.adventure.text.Component;

import de.geolykt.profilertelemetry.ui.adventure.ComponentSerializerGDX;

import me.lucko.spark.common.command.sender.CommandSender;

public class GalimProfilerTelemetryConsoleLog implements CommandSender {

    @NotNull
    public static final GalimProfilerTelemetryConsoleLog INSTANCE = new GalimProfilerTelemetryConsoleLog();

    @NotNull
    private final List<@NotNull String> log = new ArrayList<>();

    private GalimProfilerTelemetryConsoleLog() { }

    public synchronized void addLog(@NotNull String logLine) {
        this.log.add(0, logLine);
        if (this.log.size() > 60) {
            this.log.remove(this.log.size() - 1);
        }
    }

    public synchronized void clearLog() {
        this.log.clear();
    }

    @Nullable
    public synchronized String getLine(int lineIndex) {
        if (this.log.size() <= lineIndex) {
            return null;
        } else {
            return this.log.get(lineIndex);
        }
    }

    @Override
    public String getName() {
        return "GalimProfilerTelemetryConsoleLog";
    }

    @Override
    public UUID getUniqueId() {
        return null;
    }

    @Override
    public boolean hasPermission(String arg0) {
        return true;
    }

    @Override
    public void sendMessage(Component arg0) {
        this.addLog(ComponentSerializerGDX.INSTANCE.serialize(Objects.requireNonNull(arg0)));
    }
}
