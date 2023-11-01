package red.jackf.jackfredlib.impl.config.migrator;

import blue.endless.jankson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class MigratorImpl {
    public static final String VERSION_KEY = "__version";

    private final String currentVersion;

    public MigratorImpl(String currentVersion) {
        this.currentVersion = currentVersion;
    }

    public String getCurrentVersion() {
        return currentVersion;
    }

    public List<String> migrate(JsonObject oldConfigJson, String lastVersion) {
        //noinspection UnnecessaryLocalVariable
        List<String> messages = new ArrayList<>();

        return messages;
    }
}
