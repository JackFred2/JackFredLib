package red.jackf.jackfredlib.api.config;

import blue.endless.jankson.JsonGrammar;
import org.jetbrains.annotations.NotNull;
import red.jackf.jackfredlib.impl.config.ConfigHandlerBuilderImpl;

import java.nio.file.Path;

/**
 * A ConfigHandler is your interface point to access your config. Create a new instance using {@link #builder(Class)}.
 *
 * @param <T> Type of your config file.
 */
public interface ConfigHandler<T extends Config<T>> {
    /**
     * Get a default instance of the config. A new instance is generated each call.
     *
     * @return A new default instance of your config.
     */
    T getDefault();

    /**
     * <p>Get the current instance of your config. If this is the first time calling, load it from a file.</p>
     * <p>If an error occurs during loading of the file, then a temporary default instance will be used.
     *
     * @return Current instance of the config.
     */
    T instance();

    /**
     * Load the config from the given file. If an error occurs during loading, the config handler will react as defined
     * by {@link ConfigHandlerBuilder#loadErrorHandling(LoadErrorHandlingMode)}.
     */
    void load();

    /**
     * Save the current config instance to the given file.
     */
    void save();

    /**
     * Get the path of the config file used for this handler.
     * @return Path of the config file for this handler.
     */
    Path getFilePath();

    /**
     * Update the Jankson grammar for this handler.
     *
     * @apiNote Not recommended to update to a new grammar with a different <code>allowBareRootObject</code> setting.
     * @param newGrammar New Jankson grammar to use.
     */
    void changeGrammar(@NotNull JsonGrammar newGrammar);

    /**
     * Create a new builder for a config handler.
     *
     * @param configClass Class of your config file.
     * @param <T>         Class of your config file.
     * @return A new config handler builder.
     */
    static <T extends Config<T>> ConfigHandlerBuilder<T> builder(@NotNull Class<T> configClass) {
        return new ConfigHandlerBuilderImpl<>(configClass);
    }
}
