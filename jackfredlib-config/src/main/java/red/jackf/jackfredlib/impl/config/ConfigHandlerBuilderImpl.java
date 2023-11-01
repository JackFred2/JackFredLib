package red.jackf.jackfredlib.impl.config;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonGrammar;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import red.jackf.jackfredlib.api.config.Config;
import red.jackf.jackfredlib.api.config.ConfigHandler;
import red.jackf.jackfredlib.api.config.ConfigHandlerBuilder;
import red.jackf.jackfredlib.api.config.LoadErrorHandlingMode;
import red.jackf.jackfredlib.api.config.migration.MigratorBuilder;
import red.jackf.jackfredlib.impl.config.migrator.MigratorBuilderImpl;
import red.jackf.jackfredlib.impl.config.migrator.MigratorImpl;

import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Consumer;

public class ConfigHandlerBuilderImpl<T extends Config<T>> implements ConfigHandlerBuilder<T> {
    private final Class<T> configClass;
    private final Jankson.Builder jankson = Jankson.builder();
    private Path path = null;
    private JsonGrammar grammar = JsonGrammar.builder().printUnquotedKeys(true).bareSpecialNumerics(true)
                                             .printTrailingCommas(true).withComments(true).build();
    private LoadErrorHandlingMode loadErrorHandling = LoadErrorHandlingMode.LOG;
    private Consumer<Exception> loadExceptionCallback = e -> {};
    private Logger logger = JFLibConfig.LOGGER;
    @Nullable
    private MigratorImpl migrator = null;

    public ConfigHandlerBuilderImpl(@NotNull Class<T> configClass) {
        this.configClass = configClass;
    }

    @Override
    public ConfigHandlerBuilder<T> path(@NotNull Path filePath) {
        Objects.requireNonNull(filePath, "Path must not be null.");
        this.path = filePath;
        return this;
    }

    @Override
    public ConfigHandlerBuilder<T> fileName(@NotNull String name) {
        Objects.requireNonNull(name, "Filename must not be null.");
        this.path = FabricLoader.getInstance().getConfigDir().resolve(name + ".json5");
        return this;
    }

    @Override
    public ConfigHandlerBuilder<T> janksonGrammar(@NotNull JsonGrammar grammar) {
        Objects.requireNonNull(grammar, "Grammar must not be null.");
        this.grammar = grammar;
        return this;
    }

    @Override
    public ConfigHandlerBuilder<T> modifyJankson(@NotNull Consumer<Jankson.Builder> operator) {
        Objects.requireNonNull(operator, "Jankson operator must not be null.");
        operator.accept(this.jankson);
        return this;
    }

    @Override
    public ConfigHandlerBuilder<T> loadErrorHandling(@NotNull LoadErrorHandlingMode loadErrorHandling) {
        Objects.requireNonNull(loadErrorHandling, "Error handling mode must not be null.");
        this.loadErrorHandling = loadErrorHandling;
        return this;
    }

    @Override
    public ConfigHandlerBuilder<T> errorCallback(@NotNull Consumer<Exception> errorCallback) {
        Objects.requireNonNull(errorCallback, "Error callback must not be null.");
        // chain callbacks
        Consumer<Exception> oldCallback = this.loadExceptionCallback;
        this.loadExceptionCallback = e -> {
            errorCallback.accept(e);
            oldCallback.accept(e);
        };
        return this;
    }

    @Override
    public ConfigHandlerBuilder<T> withLogger(@NotNull Logger customLogger) {
        Objects.requireNonNull(customLogger, "Custom logger must not be null.");
        this.logger = customLogger;
        return this;
    }

    @Override
    public ConfigHandlerBuilder<T> withMigrator(@NotNull MigratorBuilder builder) {
        Objects.requireNonNull(builder, "Migrator builder must not be null.");
        this.migrator = ((MigratorBuilderImpl) builder).build();
        return this;
    }

    @Override
    public ConfigHandler<T> build() {
        Objects.requireNonNull(path, "Must specify a path or name for the config file.");
        return new ConfigHandlerImpl<>(configClass, path, jankson.build(), grammar, logger, migrator, loadErrorHandling, loadExceptionCallback);
    }
}
