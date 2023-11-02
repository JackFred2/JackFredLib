package red.jackf.jackfredlib.api.config.migration;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;
import org.jetbrains.annotations.NotNull;
import red.jackf.jackfredlib.api.config.Config;
import red.jackf.jackfredlib.impl.config.migrator.MigratorBuilderImpl;

import java.util.Objects;

/**
 * <p>Migrators let you handle changes in your config schema between mod versions. A migrator consists of a number of
 * {@link Migration}s, that each are applied to the raw config JSON before parsing into the given class file. It is
 * recommended to always use a migrator even if you don't have any plans for migrations in your config, just to add the
 * mod version to your config, in case your mind changes.</p>
 *
 * <p>Each migrator is initialized with the current version - normally your mod's version - which is prepended to the
 * config JSON. Each load, every registeredMigration above the old version up to and including the loaded version with
 * be applied in order. If the config JSON did not have a logged old version, then every migration up to and including
 * the loaded version will be applied.</p>
 *
 * <p>If an unrecoverable error occurs during migration, then a default instance will be restored, and the errored config
 * file will be suffixed with .error</p>
 *
 * <p>For example, in a scenario with:</p>
 * <ul>
 *     <li>Config last saved with mod version 0.2.7</li>
 *     <li>Current mod version is 0.2.10</li>
 *     <li>Three migrations registerd, for versions 0.2.3, 0.2.9, and 0.2.10</li>
 * </ul>
 * <p>The migrations for versions 0.2.9 and 0.2.10 will be applied, in that order.</p>
 *
 * <h2>A note on pre-release and build info tags (1.2.3-beta+1.20.2)</h2>
 *
 * <p>Versions are ordered using <a href="https://semver.org/">SemVer</a>, that is compared component-wise (in the above
 * example, '1.2.3'), then by pre-release information (after a dash '-beta'). Build metadata (+1.20.2) is ignored when
 * comparing versions; 2.3.1+1.20.2 will not trigger migrations when moving to 2.3.1+1.20.3.</p>
 */
public interface MigratorBuilder<T extends Config<T>> {
    /**
     * Add a migration to this migrator.
     * @param targetVersion Version that this migration should take place at.
     * @param migration Migration to add at the given version.
     * @return This migrator builder.
     */
    MigratorBuilder<T> addMigration(@NotNull Version targetVersion, @NotNull Migration<T> migration);
    /**
     * Add a migration to this migrator.
     * @param targetVersionString Version string that this migration should take place at.
     * @param migration Migration to add at the given version.
     * @return This migrator builder.
     */
    MigratorBuilder<T> addMigration(@NotNull String targetVersionString, @NotNull Migration<T> migration);

    /**
     * Create a new migration builder, using the given mod's version as the current version.
     * @param modid Mod ID to pull the version from.
     * @return This migrator builder.
     * @param <T> Class file of the config.
     */
    static <T extends Config<T>> MigratorBuilder<T> forMod(@NotNull String modid) {
        Objects.requireNonNull(modid, "Mod ID must not be null.");
        return forVersion(FabricLoader.getInstance()
                                               .getModContainer(modid)
                                               .orElseThrow(() -> new IllegalArgumentException("Mod " + modid + " not loaded."))
                                               .getMetadata()
                                               .getVersion());
    }

    /**
     * Create a new migration builder, using the version string as the current. This will be converted to a {@link Version}.
     * @param versionStr Version string to parse and use.
     * @return This migrator builder.
     * @param <T> Class file of the config.
     */
    static <T extends Config<T>> MigratorBuilder<T> forVersion(@NotNull String versionStr) {
        Objects.requireNonNull(versionStr, "Version string must not be null.");
        try {
            return forVersion(Version.parse(versionStr));
        } catch (VersionParsingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Create a new migration builder, using the given version as the current.
     * @param currentVersion Version to use as the current.
     * @return This migrator builder.
     * @param <T> Class file of the config.
     */
    static <T extends Config<T>> MigratorBuilder<T> forVersion(@NotNull Version currentVersion) {
        Objects.requireNonNull(currentVersion, "Current version must not be null.");
        return new MigratorBuilderImpl<>(currentVersion);
    }
}
