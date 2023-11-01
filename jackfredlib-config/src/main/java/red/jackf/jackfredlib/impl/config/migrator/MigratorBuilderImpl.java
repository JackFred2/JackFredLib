package red.jackf.jackfredlib.impl.config.migrator;

import red.jackf.jackfredlib.api.config.migration.MigratorBuilder;

public class MigratorBuilderImpl implements MigratorBuilder {
    private final String currentVersion;

    public MigratorBuilderImpl(String currentVersion) {
        this.currentVersion = currentVersion;
    }

    public MigratorImpl build() {
        return new MigratorImpl(currentVersion);
    }
}
