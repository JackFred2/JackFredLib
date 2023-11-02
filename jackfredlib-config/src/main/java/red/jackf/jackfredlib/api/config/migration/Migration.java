package red.jackf.jackfredlib.api.config.migration;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonObject;
import net.fabricmc.loader.api.Version;
import red.jackf.jackfredlib.api.config.Config;

import java.util.function.Supplier;

/**
 * <p>A migration performs an action on the raw JSON loaded from a file, with the version tag removed.</p>
 *
 * <p>Migrations are designed to be written once and not modified, so you can 'stack' them up over time.</p>
 */
public interface Migration<T extends Config<T>> {
    /**
     * Perform an operation on the config JSON. The JSON should be modified in-place, making adjustments as needed. Note
     * that modifying the Jankson comments has no effect, as they will not persist.
     *
     * @param loadedConfig Raw config JSON; the migrator should operate on this in-place.
     * @param defaultSupplier A supplier for a default instance of the config JSON.
     * @param jankson Jankson instance used in deserializing this JSON, will contain any custom [de]serializers from
     *                your handler.
     * @param oldVersion Previous mod version that this mod was saved with.
     * @param newVersion Currently loaded version of the mod.
     * @return A result describing how this migration turned out.
     * @see MigrationResult
     */
    MigrationResult migrate(JsonObject loadedConfig, Supplier<T> defaultSupplier, Jankson jankson, Version oldVersion, Version newVersion);
}
