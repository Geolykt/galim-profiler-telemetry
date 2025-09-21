package de.geolykt.profilertelemetry;

import java.net.URI;
import java.nio.file.Paths;
import java.security.CodeSource;

import org.jetbrains.annotations.Nullable;

import net.minestom.server.extras.selfmodification.MinestomRootClassLoader;

import me.lucko.spark.common.sampler.source.ClassSourceLookup;

public class SLLCodeSourceURILookup implements ClassSourceLookup, ClassSourceLookup.ByUrl {

    // TODO hook this into transformer code. Maybe basing it off the SMAPs?

    @Override
    @Nullable
    public String identify(Class<?> p0) throws Exception {
        if (p0.getClassLoader() == MinestomRootClassLoader.getInstance()) {
            URI uri = MinestomRootClassLoader.getInstance().getClassCodeSourceURI(p0.getName().replace('.', '/'));
            if (uri != null) {
                return this.identifyFile(Paths.get(uri));
            }
        }

        CodeSource codeSource = p0.getProtectionDomain().getCodeSource();
        if (codeSource != null) {
            return this.identifyUrl(codeSource.getLocation());
        }

        return null;
    }
}
