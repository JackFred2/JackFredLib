package red.jackf.jackfredlib.impl.config.migrator;

import blue.endless.jankson.JsonObject;

public class MigratorImpl {
    public static final String VERSION_KEY = "__version";

    private final String currentVersion;

    public MigratorImpl(String currentVersion) {
        this.currentVersion = currentVersion;
    }

    public String getCurrentVersion() {
        return currentVersion;
    }

    public boolean migrate(JsonObject oldConfigJson, String lastVersion) {
        boolean changed = !currentVersion.equals(lastVersion);
        return changed;
    }
}
