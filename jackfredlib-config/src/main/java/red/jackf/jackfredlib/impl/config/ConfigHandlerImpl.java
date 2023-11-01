package red.jackf.jackfredlib.impl.config;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonGrammar;
import red.jackf.jackfredlib.api.config.Config;
import red.jackf.jackfredlib.api.config.ConfigHandler;

import java.nio.file.Path;

public class ConfigHandlerImpl<T extends Config> implements ConfigHandler<T> {
    private final Class<T> configClass;
    private final Path path;
    private final Jankson jankson;
    private final JsonGrammar grammar;

    public ConfigHandlerImpl(
            Class<T> configClass,
            Path path,
            Jankson jankson,
            JsonGrammar grammar) {

        this.configClass = configClass;
        this.path = path;
        this.jankson = jankson;
        this.grammar = grammar;
    }

    @Override
    public T getDefault() {
        return null;
    }

    @Override
    public T getCurrent() {
        return null;
    }

    @Override
    public void reload() {

    }
}
