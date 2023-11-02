package red.jackf.jackfredlib.impl.config.migrator;

import net.fabricmc.loader.api.Version;
import org.jetbrains.annotations.NotNull;
import red.jackf.jackfredlib.api.config.Config;
import red.jackf.jackfredlib.api.config.migration.Migration;
import red.jackf.jackfredlib.api.config.migration.MigratorBuilder;
import red.jackf.jackfredlib.api.config.migration.MigratorUtil;
import red.jackf.jackfredlib.impl.config.ConfigHandlerImpl;

import java.util.*;

public class MigratorBuilderImpl<T extends Config<T>> implements MigratorBuilder<T> {
    private final Version currentVersion;
    private final NavigableMap<Version, List<Migration<T>>> migrations = new TreeMap<>();

    public MigratorBuilderImpl(Version currentVersion) {
        this.currentVersion = currentVersion;
    }

    @Override
    public MigratorBuilder<T> addMigration(@NotNull Version targetVersion, @NotNull Migration<T> migration) {
        Objects.requireNonNull(targetVersion, "Target version must not be null");
        Objects.requireNonNull(migration, "Migration must not be null");
        this.migrations.computeIfAbsent(targetVersion, k -> new ArrayList<>()).add(migration);
        return this;
    }

    @Override
    public MigratorBuilder<T> addMigration(@NotNull String targetVersionString, @NotNull Migration<T> migration) {
        return addMigration(MigratorUtil.unsafeParse(targetVersionString), migration);
    }

    public MigratorImpl<T> build(ConfigHandlerImpl<T> configHandler) {
        return new MigratorImpl<>(currentVersion, migrations, configHandler);
    }
}
