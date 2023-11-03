package red.jackf.jackfredlib.api.config;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonGrammar;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import red.jackf.jackfredlib.api.config.migration.MigratorBuilder;

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
     * Enables file watcher for this config, which checks for changes on-disk every second, and call {@link ConfigHandler#load()}
     * if changes are detected. The config handler can also opt in / out during runtime.
     *
     * @return This config handler builder.
     * @throws IllegalArgumentException If a file watcher was requested, but the config file is not in the default
     *                                  Config directory.
     */
    ConfigHandlerBuilder<T> withFileWatcher();

    /**
     * Use a custom Jankson grammar.
     *
     * @param grammar Custom grammar to use.
     * @return This config handler builder.
     * @apiNote Default: JsonGrammar.builder().printUnquotedKeys(true).bareSpecialNumerics(true).printTrailingCommas(true).withComments(true).build();
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
     * By default, some adapters are added to Jankson in order to ease common usecases, such as {@link net.minecraft.resources.ResourceLocation}s.
     * This method prevents them from being registered.
     *
     * @return This config handler builder.
     */
    ConfigHandlerBuilder<T> skipDefaultAdapters();

    /**
     * Guides the config handler on how to deal with a config it can't load.
     *
     * @param loadErrorHandlingMode How to handle errored config files.
     * @return This config handler builder.
     * @apiNote Default: {@link LoadErrorHandlingMode#LOG}
     * @see #errorCallback(Consumer)
     * @see LoadErrorHandlingMode
     */
    ConfigHandlerBuilder<T> loadErrorHandling(@NotNull LoadErrorHandlingMode loadErrorHandlingMode);

    /**
     * Adds a callback that is ran when there is an error loading the config file. This is always ran before the given
     * {@link LoadErrorHandlingMode} is handled. Multiple callbacks can be added.
     *
     * @param errorCallback Callback to be ran with the thrown exception.
     * @return This config handler builder
     * @apiNote Default: none
     */
    ConfigHandlerBuilder<T> errorCallback(@NotNull Consumer<Exception> errorCallback);

    /**
     * Use a custom logger instance of JFLib-Config's.
     *
     * @param customLogger Custom logger to use.
     * @return This config handler builder.
     * @apiNote Default: JFLib's Internal Logger.
     */
    ConfigHandlerBuilder<T> withLogger(@NotNull Logger customLogger);

    /**
     * Assign a new Migrator to this config handler. Migrators are what is used to update the config between versions, if
     * needed.
     *
     * @param builder Migrator builder to use for this config handler.
     * @return This config handler builder.
     * @apiNote Default: no Migrator
     */
    ConfigHandlerBuilder<T> withMigrator(@NotNull MigratorBuilder<T> builder);
}
