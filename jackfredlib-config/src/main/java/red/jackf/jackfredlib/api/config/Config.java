package red.jackf.jackfredlib.api.config;

import org.jetbrains.annotations.Nullable;

/**
 * Represents a config class. Must be implemented by your config class.
 */
public interface Config<T extends Config<T>> extends Validatable {

    /**
     * Called when a config has been successfully loaded and validated.
     * @param oldInstance Previous instance of the config, or <code>null</code> if not applicable, such as on initial
     *                    load.
     */
    default void onLoad(@Nullable T oldInstance) {}
}
