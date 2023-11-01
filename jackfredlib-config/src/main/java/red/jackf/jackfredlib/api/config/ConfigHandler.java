package red.jackf.jackfredlib.api.config;

import red.jackf.jackfredlib.impl.config.ConfigHandlerBuilderImpl;

import java.nio.file.Path;

public interface ConfigHandler<T extends Config> {
    T getDefault();

    T getCurrent();

    void reload();

    static <T extends Config> ConfigHandlerBuilder<T> builder(Class<T> configClass, Path path) {
        return new ConfigHandlerBuilderImpl<>(configClass, path);
    }
}
