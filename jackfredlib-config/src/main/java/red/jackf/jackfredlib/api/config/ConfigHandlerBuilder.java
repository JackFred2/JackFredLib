package red.jackf.jackfredlib.api.config;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonGrammar;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.function.Consumer;

/**
 * A builder to create a handler for your config.
 *
 * @param <T> Class of your config.
 */
public interface ConfigHandlerBuilder<T extends Config<T>> {
    /**
     * Create a new Config Handler with the given settings.
     *
     * @return The constructed config handler.
     */
    ConfigHandler<T> build();

    // REQUIRED

    /**
     * Set the file path of this config file.
     *
     * @param filePath Path to save the file at.
     * @return This config handler builder.
     */
    ConfigHandlerBuilder<T> path(@NotNull Path filePath);

    /**
     * Set the path of this config file by placing it in the default config directory, under <code>config/{name}.json5</code>.
     *
     * @param name Name of the used JSON file.
     * @return This config handler builder.
     */
    ConfigHandlerBuilder<T> fileName(@NotNull String name);

    // OPTIONAL

    /**
     * Use a custom Jankson grammar.
     *
     * @param grammar Custom grammar to use.
     * @return This config handler builder.
     */
    ConfigHandlerBuilder<T> janksonGrammar(@NotNull JsonGrammar grammar);

    /**
     * Modify the Jankson builder. This is where you register custom type adapters if needed.
     *
     * @param operator Function to perform on the builder.
     * @return This config handler builder.
     */
    ConfigHandlerBuilder<T> modifyJankson(@NotNull Consumer<Jankson.Builder> operator);

    /**
     * Guides the config handler on how to deal with a config it can't load. Default: LOG_AND_TEMPORARY_DEFAULT
     * @see #errorCallback(Consumer)
     * @see LoadErrorHandlingMode
     * @param loadErrorHandlingMode How to handle errored config files.
     * @return This config handler builder.
     */
    ConfigHandlerBuilder<T> loadErrorHandling(@NotNull LoadErrorHandlingMode loadErrorHandlingMode);

    /**
     * Adds a callback that is ran when there is an error loading the config file. This is always ran before the given
     * {@link LoadErrorHandlingMode} is handled. Multiple callbacks can be added.
     * @param errorCallback Callback to be ran with the thrown exception.
     * @return This config handler builder
     */
    ConfigHandlerBuilder<T> errorCallback(@NotNull Consumer<Exception> errorCallback);
}
