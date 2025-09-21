package de.geolykt.profilertelemetry;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.logging.Level;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;

import net.minestom.server.extras.selfmodification.MinestomRootClassLoader;

import de.geolykt.profilertelemetry.ui.GalimProfilerTelemetryConsoleLog;
import de.geolykt.starloader.Starloader;
import de.geolykt.starloader.api.Galimulator;
import de.geolykt.starloader.mod.DiscoveredExtension.ExternalDependencyArtifact;
import de.geolykt.starloader.mod.Extension;

import me.lucko.spark.common.SparkPlugin;
import me.lucko.spark.common.command.sender.CommandSender;
import me.lucko.spark.common.monitor.tick.TickStatistics;
import me.lucko.spark.common.platform.PlatformInfo;
import me.lucko.spark.common.sampler.source.ClassSourceLookup;
import me.lucko.spark.common.sampler.source.SourceMetadata;
import me.lucko.spark.common.tick.TickHook;
import me.lucko.spark.common.tick.TickReporter;
import me.lucko.spark.common.util.classfinder.ClassFinder;

public class GalimulatorSparkPlugin implements SparkPlugin {
    @NotNull
    private final SLLPlatformInfo platformInfo = new SLLPlatformInfo();
    private final Set<CommandSender> senders = ConcurrentHashMap.newKeySet();
    @NotNull
    private final Extension sllExtension;
    @NotNull
    private final String sparkVersion;

    public GalimulatorSparkPlugin(@NotNull Extension extension) {
        this.sllExtension = extension;

        String sparkversion = "unknown";
        for (ExternalDependencyArtifact artifact : this.sllExtension.getDescription().getOrigin().getExternalDependencies().getArtifacts()) {
            if (artifact.getGroup().equals("me.lucko") && artifact.getArtifact().equals("spark-common")) {
                sparkversion = Objects.requireNonNull(artifact.getVersion().toString());
            }
        }

        this.sparkVersion = sparkversion;
        this.addSender(GalimProfilerTelemetryConsoleLog.INSTANCE);
    }

    public boolean addSender(@NotNull CommandSender sender) {
        return this.senders.add(Objects.requireNonNull(sender, "sender may not be null"));
    }

    @Override
    public ClassFinder createClassFinder() {
        return (p0) -> {
            try {
                return MinestomRootClassLoader.getInstance().loadClass(p0);
            } catch (ClassNotFoundException e) {
                this.sllExtension.getLogger().warn("Failed to find class '{}' via the root classloader.", p0, e);
                return null;
            }
        };
    }

    @Override
    public ClassSourceLookup createClassSourceLookup() {
        return new SLLCodeSourceURILookup();
    }

    @Override
    public TickHook createTickHook() {
        return SLAPILogicalTickManager.INSTANCE;
    }

    @Override
    public TickReporter createTickReporter() {
        return SLAPILogicalTickManager.INSTANCE;
    }

    @Override
    public TickStatistics createTickStatistics() {
        return SLAPILogicalTickManager.INSTANCE.getTickStatistics();
    }

    @Override
    public void executeAsync(Runnable p0) {
        ForkJoinPool.commonPool().execute(p0);
    }

    @Override
    public void executeSync(Runnable task) {
        try {
            Galimulator.runTaskOnNextTick(Objects.requireNonNull(task, "'task' may not be null"));
        } catch (NoClassDefFoundError cnfe) {
            throw new UnsupportedOperationException("Sync execution not implemented/supported by default", cnfe);
        }
    }

    @Override
    public String getCommandName() {
        return "spark";
    }

    @Override
    public Stream<? extends CommandSender> getCommandSenders() {
        return this.senders.stream();
    }

    @Override
    public PlatformInfo getPlatformInfo() {
        return this.platformInfo;
    }

    @Override
    public Path getPluginDirectory() {
        return Starloader.getInstance().getModDirectory();
    }

    @Override
    public String getVersion() {
        return this.sllExtension.getDescription().getVersion() + "+" + this.sparkVersion;
    }

    @Override
    public Collection<SourceMetadata> getKnownSources() {
        return SourceMetadata.gather(
                Starloader.getExtensionManager().getExtensions(),
                extension -> extension.getDescription().getName(),
                extension -> extension.getDescription().getVersion(),
                extension -> String.join(", ", extension.getDescription().getAuthors()),
                extension -> null
        );
    }

    @Override
    public void log(Level p0, String p1) {
        if (p0.intValue() <= Level.FINEST.intValue()) {
            this.sllExtension.getLogger().trace(p1);
        } else if (p0.intValue() <= Level.FINER.intValue()) {
            this.sllExtension.getLogger().trace(p1);
        } else if (p0.intValue() <= Level.FINE.intValue()) {
            this.sllExtension.getLogger().debug(p1);
        } else if (p0.intValue() <= Level.INFO.intValue()) {
            this.sllExtension.getLogger().info(p1);
        } else if (p0.intValue() <= Level.WARNING.intValue()) {
            this.sllExtension.getLogger().warn(p1);
        } else if (p0.intValue() <= Level.SEVERE.intValue()) {
            this.sllExtension.getLogger().error(p1);
        } else {
            this.sllExtension.getLogger().error(p1);
        }
    }

    @Override
    public void log(Level p0, String p1, Throwable p2) {
        if (p0.intValue() <= Level.FINEST.intValue()) {
            this.sllExtension.getLogger().trace(p1, p2);
        } else if (p0.intValue() <= Level.FINER.intValue()) {
            this.sllExtension.getLogger().trace(p1, p2);
        } else if (p0.intValue() <= Level.FINE.intValue()) {
            this.sllExtension.getLogger().debug(p1, p2);
        } else if (p0.intValue() <= Level.INFO.intValue()) {
            this.sllExtension.getLogger().info(p1, p2);
        } else if (p0.intValue() <= Level.WARNING.intValue()) {
            this.sllExtension.getLogger().warn(p1, p2);
        } else if (p0.intValue() <= Level.SEVERE.intValue()) {
            this.sllExtension.getLogger().error(p1, p2);
        } else {
            this.sllExtension.getLogger().error(p1, p2);
        }
    }

    public boolean removeSender(@NotNull CommandSender sender) {
        return this.senders.remove(sender);
    }
}
