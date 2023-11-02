package red.jackf.jackfredlib.api.config.migration.builtin;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonObject;
import net.fabricmc.loader.api.Version;
import red.jackf.jackfredlib.api.config.Config;
import red.jackf.jackfredlib.api.config.migration.Migration;
import red.jackf.jackfredlib.api.config.migration.MigrationResult;

import java.util.function.Supplier;

public class Identity<T extends Config<T>> implements Migration<T> {

    @Override
    public MigrationResult migrate(
            JsonObject loadedConfig,
            Supplier<T> defaultSupplier,
            Jankson jankson,
            Version oldVersion,
            Version newVersion) {
        return MigrationResult.SUCCESS("passthrough");
    }
}
