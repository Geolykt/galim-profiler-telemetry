package de.geolykt.profilertelemetry;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.LoggerFactory;

import de.geolykt.starloader.Starloader;
import de.geolykt.starloader.api.resource.DataFolderProvider;

import me.lucko.spark.common.platform.PlatformInfo;

public class SLLPlatformInfo implements PlatformInfo {

    @Nullable
    private static Map.Entry<@NotNull String, @NotNull String> determineLauncherMeta() {
        for (String variant : new @NotNull String[] {
                "launcher-micromixin",
                "launcher-sponge",
                "launcher-mixin-fabric"
        }) {
            try (InputStream is = Starloader.class.getClassLoader().getResourceAsStream("META-INF/maven/org.stianloader/" + variant + "/pom.properties")) {
                if (is == null) {
                    continue;
                }
                Properties properties = new Properties();
                properties.load(is);
                return new AbstractMap.SimpleImmutableEntry<>(variant, Objects.requireNonNull(properties.getProperty("version"), "property file should contain version field"));
            } catch (IOException ignored) { }
        }

        LoggerFactory.getLogger(SLLPlatformInfo.class).warn("Unable to determine SLL brand information!");
        return null;
    }

    @NotNull
    private final String applicationName;

    @NotNull
    private final String applicationVersion;

    @NotNull
    private final String sllVariant;

    @NotNull
    private final String sllVersion;

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

        Map.Entry<@NotNull String, @NotNull String> sllMeta = SLLPlatformInfo.determineLauncherMeta();
        if (sllMeta == null) {
            this.sllVariant = "unknown";
            this.sllVersion = "unknown";
        } else {
            this.sllVariant = sllMeta.getKey();
            this.sllVersion = sllMeta.getValue();
        }
    }

    public SLLPlatformInfo(@NotNull String applicationVersion, @NotNull String applicationName) {
        this.applicationVersion = applicationVersion;
        this.applicationName = applicationName;
        Map.Entry<@NotNull String, @NotNull String> sllMeta = SLLPlatformInfo.determineLauncherMeta();
        if (sllMeta == null) {
            this.sllVariant = "unknown";
            this.sllVersion = "unknown";
        } else {
            this.sllVariant = sllMeta.getKey();
            this.sllVersion = sllMeta.getValue();
        }
    }

    @Override
    public String getBrand() {
        return "SLL (" + this.sllVariant + ")";
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
        return this.sllVersion;
    }
}
