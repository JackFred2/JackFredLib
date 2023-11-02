package red.jackf.jackfredlib.impl.config.migrator;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonObject;
import com.google.common.collect.ImmutableSortedMap;
import net.fabricmc.loader.api.Version;
import org.apache.commons.lang3.Validate;
import red.jackf.jackfredlib.api.config.Config;
import red.jackf.jackfredlib.api.config.migration.Migration;
import red.jackf.jackfredlib.api.config.migration.MigrationResult;
import red.jackf.jackfredlib.impl.config.ConfigHandlerImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NavigableMap;

public class MigratorImpl<T extends Config<T>> {
    public static final String VERSION_KEY = "__version";

    private final Version currentVersion;
    private final NavigableMap<Version, List<Migration<T>>> migrations;
    private final ConfigHandlerImpl<T> configHandler;

    public MigratorImpl(Version currentVersion, NavigableMap<Version, List<Migration<T>>> migrations, ConfigHandlerImpl<T> configHandler) {
        this.currentVersion = currentVersion;
        this.migrations = ImmutableSortedMap.copyOf(migrations);
        this.configHandler = configHandler;
    }

    public Version getCurrentVersion() {
        return currentVersion;
    }

    public MigratorResult migrate(JsonObject oldConfigJson, Version oldVersion) {
        Validate.isTrue(currentVersion.compareTo(oldVersion) > 0, "Tried to use migrator when the versions are identical or down-dating.");
        Jankson jankson = this.configHandler.getJankson();
        List<String> messages = new ArrayList<>();

        List<Migration<T>> migrations = this.migrations.subMap(oldVersion, false, this.currentVersion, true)
                                                    .values()
                                                    .stream()
                                                    .flatMap(Collection::stream)
                                                    .toList();

        JsonObject json = oldConfigJson;

        for (Migration<T> migration : migrations) {
            JsonObject copy = json.clone();

            MigrationResult result = migration.migrate(copy,
                                                       this.configHandler::getDefault,
                                                       jankson,
                                                       oldVersion,
                                                       this.currentVersion);

            switch (result.type()) {
                case REPLACE -> {
                    // We don't need other messages due to intended replacement
                    return new MigratorResult.Replace(((MigrationResult.Replacement) result).replacement());
                }
                case FAIL_HARD -> {
                    return new MigratorResult.FailHard(messages);
                }
                case FAIL_SOFT -> {
                    if (result instanceof MigrationResult.WithMessage withMessage)
                        messages.add(withMessage.message());
                }
                case SUCCESS -> {
                    json = copy;
                    if (result instanceof MigrationResult.WithMessage withMessage)
                        messages.add(withMessage.message());
                }
            }
        }

        return new MigratorResult.Success(json, messages);
    }
}
