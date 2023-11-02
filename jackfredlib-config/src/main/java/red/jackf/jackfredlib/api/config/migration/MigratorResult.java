package red.jackf.jackfredlib.api.config.migration;

import blue.endless.jankson.JsonObject;
import net.fabricmc.loader.api.Version;

import java.util.List;

/**
 * Represents a result of a full {@link red.jackf.jackfredlib.impl.config.migrator.MigratorImpl#migrate(JsonObject, Version)}
 * call.
 */
public sealed interface MigratorResult {
    record Success(JsonObject result, List<String> messages) implements MigratorResult {}
    record FailHard(List<String> messages) implements MigratorResult {}
    record Replace(JsonObject replacement) implements MigratorResult {}
}
