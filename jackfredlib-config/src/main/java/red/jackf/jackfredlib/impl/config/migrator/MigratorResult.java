package red.jackf.jackfredlib.impl.config.migrator;

import blue.endless.jankson.JsonObject;

import java.util.List;

public sealed interface MigratorResult {
    record Success(JsonObject result, List<String> messages) implements MigratorResult {}
    record FailHard(List<String> messages) implements MigratorResult {}
    record Replace(JsonObject replacement) implements MigratorResult {}
}
