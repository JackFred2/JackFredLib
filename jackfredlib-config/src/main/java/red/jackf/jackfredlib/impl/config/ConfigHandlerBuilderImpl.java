package red.jackf.jackfredlib.impl.config;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonGrammar;
import red.jackf.jackfredlib.api.config.Config;
import red.jackf.jackfredlib.api.config.ConfigHandler;
import red.jackf.jackfredlib.api.config.ConfigHandlerBuilder;

import java.nio.file.Path;
import java.util.function.Consumer;

public class ConfigHandlerBuilderImpl<T extends Config> implements ConfigHandlerBuilder<T> {
    private final Class<T> configClass;
    private final Jankson.Builder jankson = Jankson.builder();
    private final Path path;
    private JsonGrammar grammar = JsonGrammar.builder()
                                             .printUnquotedKeys(true)
                                             .bareSpecialNumerics(true)
                                             .printTrailingCommas(true)
                                             .withComments(true)
                                             .build();

    public ConfigHandlerBuilderImpl(Class<T> configClass, Path path) {
        this.configClass = configClass;
        this.path = path;
    }

    public ConfigHandlerBuilderImpl<T> janksonGrammar(JsonGrammar grammar) {
        this.grammar = grammar;
        return this;
    }

    public ConfigHandlerBuilderImpl<T> modifyJankson(Consumer<Jankson.Builder> operator) {
        operator.accept(this.jankson);
        return this;
    }

    @Override
    public ConfigHandler<T> build() {
        return new ConfigHandlerImpl<>(configClass, path, jankson.build(), grammar);
    }
}
