package red.jackf.jackfredlib.api.config;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonGrammar;

import java.util.function.Consumer;

/**
 * A builder to create a handler for your config.
 * @param <T> Class of your config.
 */
public interface ConfigHandlerBuilder<T extends Config> {
    /**
     * Use a custom Jankson grammar.
     * @param grammar Custom grammar to use.
     * @return This config handler builder.
     */
    ConfigHandlerBuilder<T> janksonGrammar(JsonGrammar grammar);

    /**
     * Modify the Jankson builder. This is where you register custom type adapters if needed.
     * @param operator Function to perform on the builder.
     * @return This config handler builder.
     */
    ConfigHandlerBuilder<T> modifyJankson(Consumer<Jankson.Builder> operator);

    /**
     * Create a new Config Handler with the given settings.
     * @return The constructed config handler.
     */
    ConfigHandler<T> build();
}
