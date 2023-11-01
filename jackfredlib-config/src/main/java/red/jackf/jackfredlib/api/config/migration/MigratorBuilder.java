package red.jackf.jackfredlib.api.config.migration;

import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.NotNull;
import red.jackf.jackfredlib.impl.config.migrator.MigratorBuilderImpl;

import java.util.Objects;

public interface MigratorBuilder {
    static MigratorBuilder forMod(@NotNull String modid) {
        Objects.requireNonNull(modid, "Mod ID must not be null.");
        return new MigratorBuilderImpl(FabricLoader.getInstance()
                                               .getModContainer(modid)
                                               .orElseThrow(() -> new IllegalArgumentException("Mod " + modid + " not loaded."))
                                               .getMetadata()
                                               .getVersion()
                                               .getFriendlyString());
    }

    static MigratorBuilder forCustomVersionGrabbar(@NotNull String currentVersion) {
        Objects.requireNonNull(currentVersion, "Current version must not be null.");
        return new MigratorBuilderImpl(currentVersion);
    }
}
