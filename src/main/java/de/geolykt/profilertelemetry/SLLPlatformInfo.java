package de.geolykt.profilertelemetry;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;
import org.stianloader.sll.SLLEnvironment;

import de.geolykt.starloader.api.resource.DataFolderProvider;

import me.lucko.spark.common.platform.PlatformInfo;

public class SLLPlatformInfo implements PlatformInfo {

    @NotNull
    private final String applicationName;

    @NotNull
    private final String applicationVersion;

    public SLLPlatformInfo() {
        String galimVersion = "unknown";
        String applicationName = "Generic SLL Application";

        try {
            if (DataFolderProvider.isRegistered()) {
                applicationName = "Galimulator"; // If SLAPI is installed it's reasonable to presume that we are running Galimulator
                Path versionFile = DataFolderProvider.getProvider().provideAsPath().resolve("version.txt");
                if (Files.exists(versionFile)) {
                    try {
                        galimVersion = new String(Files.readAllBytes(versionFile), StandardCharsets.UTF_8);
                    } catch (IOException e) {
                        LoggerFactory.getLogger(SLLPlatformInfo.class).warn("Cannot determine Galimulator version (IO Failure; Is the filesystem working?)", e);
                    }
                } else {
                    LoggerFactory.getLogger(SLLPlatformInfo.class).warn("Cannot determine Galimulator version (version.txt file missing - improper Galimulator installation?)");
                }
            } else {
                LoggerFactory.getLogger(SLLPlatformInfo.class).warn("Cannot determine Galimulator version (DataFolderProvider not registered; report this as a bug!)");
            }
        } catch (NoClassDefFoundError e) {
            LoggerFactory.getLogger(SLLPlatformInfo.class).warn("Cannot determine Galimulator version (SLAPI not installed?)");
        }

        this.applicationName = applicationName;
        this.applicationVersion = galimVersion;
    }

    public SLLPlatformInfo(@NotNull String applicationVersion, @NotNull String applicationName) {
        this.applicationVersion = applicationVersion;
        this.applicationName = applicationName;
    }

    @Override
    public String getBrand() {
        return "SLL (" + SLLEnvironment.getSLLGroupId() + ":" + SLLEnvironment.getSLLArtifactId() + " using " + SLLEnvironment.getMixinCompiledGroupId() + ":" + SLLEnvironment.getMixinCompiledArtifactId() + ")";
    }

    @Override
    public String getMinecraftVersion() {
        return this.applicationVersion;
    }

    @Override
    public String getName() {
        return this.applicationName;
    }

    @Override
    public Type getType() {
        return Type.APPLICATION;
    }

    @Override
    public String getVersion() {
        return SLLEnvironment.getSLLVersion() + " with mixin version " + SLLEnvironment.getMixinCompiledVersion();
    }
}
