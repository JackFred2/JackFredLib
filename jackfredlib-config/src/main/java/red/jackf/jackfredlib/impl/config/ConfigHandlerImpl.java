package red.jackf.jackfredlib.impl.config;

import blue.endless.jankson.*;
import blue.endless.jankson.api.SyntaxError;
import blue.endless.jankson.magic.TypeMagic;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import red.jackf.jackfredlib.api.config.Config;
import red.jackf.jackfredlib.api.config.ConfigHandler;
import red.jackf.jackfredlib.api.config.LoadErrorHandlingMode;
import red.jackf.jackfredlib.api.config.error.ConfigLoadException;
import red.jackf.jackfredlib.api.config.error.ConfigValidationException;
import red.jackf.jackfredlib.impl.config.migrator.MigratorImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Consumer;

public class ConfigHandlerImpl<T extends Config<T>> implements ConfigHandler<T> {
    private final Class<T> configClass;
    private final Path path;
    private final Jankson jankson;
    private JsonGrammar grammar;
    private final Logger logger;
    private final @Nullable MigratorImpl migrator;
    private final LoadErrorHandlingMode loadErrorHandlingMode;
    private final Consumer<Exception> loadExceptionCallback;

    private T instance = null;

    public ConfigHandlerImpl(
            Class<T> configClass,
            Path path,
            Jankson jankson,
            JsonGrammar grammar,
            Logger logger,
            @Nullable MigratorImpl migrator,
            LoadErrorHandlingMode loadErrorHandlingMode,
            Consumer<Exception> loadExceptionCallback) {

        this.configClass = configClass;
        this.path = path;
        this.jankson = jankson;
        this.grammar = grammar;
        this.logger = logger;
        this.migrator = migrator;
        this.loadErrorHandlingMode = loadErrorHandlingMode;
        this.loadExceptionCallback = loadExceptionCallback;
    }

    @Override
    public T getDefault() {
        return TypeMagic.createAndCast(this.configClass);
    }

    @Override
    public T instance() {
        if (this.instance == null) this.load();
        return this.instance;
    }

    @Override
    public void load() {
        T oldInstance = this.instance;

        if (Files.exists(this.path)) {
            this.logger.info("Loading config file {}", this.path.getFileName());

            try {

                JsonObject json = this.jankson.load(path.toFile());

                // Config Migration
                JsonElement lastVersion = json.remove(MigratorImpl.VERSION_KEY);
                boolean hasChangedDueToUpdate = false;

                if (this.migrator != null
                        && lastVersion instanceof JsonPrimitive primitive
                        && primitive.getValue() instanceof String versionStr)
                    hasChangedDueToUpdate = this.migrator.migrate(json, versionStr);

                this.instance = this.jankson.fromJson(json, configClass);

                this.instance.validate();

                // Update checking
                JsonElement copy = this.jankson.toJson(instance);
                if (hasChangedDueToUpdate || copy instanceof JsonObject copyObj && !copyObj.getDelta(json).isEmpty()) {
                    this.logger.info("Saving updated config");
                    this.save();
                }

            } catch (IOException | SyntaxError | ConfigValidationException err) {

                this.loadExceptionCallback.accept(err);

                // Log appropriate message
                if (this.loadErrorHandlingMode == LoadErrorHandlingMode.LOG) {
                    if (err instanceof IOException ioEx) {
                        this.logger.error("IO error when loading config file " + path.getFileName(), ioEx);
                    } else if (err instanceof SyntaxError syntaxErr) {
                        this.logger.error("Syntax error in {}: {}", path.getFileName(), syntaxErr.getMessage());
                        this.logger.error(syntaxErr.getLineMessage());
                    } else {
                        ConfigValidationException validationEx = (ConfigValidationException) err;
                        this.logger.error("Error validating config file {}", path.getFileName());
                        this.logger.error(validationEx.getMessage());
                    }
                }

                // keep last or load default
                if (this.instance == null) {
                    if (this.loadErrorHandlingMode == LoadErrorHandlingMode.LOG)
                        this.logger.error("Temporarily using defaults for {}", path.getFileName());

                    this.instance = getDefault();
                }

                if (this.loadErrorHandlingMode == LoadErrorHandlingMode.RETHROW)
                    throw new ConfigLoadException(err);

            }
        } else {
            this.logger.info("Creating default config file {}", this.path.getFileName());

            this.instance = this.getDefault();
            this.save();
        }

        if (this.instance != oldInstance) this.instance.onLoad(oldInstance);
    }

    @Override
    public void save() {
        T instance = instance();
        JsonObject json = (JsonObject) this.jankson.toJson(instance);

        if (this.migrator != null)
            json.put(MigratorImpl.VERSION_KEY,
                     JsonPrimitive.of(this.migrator.getCurrentVersion()),
                     "Users: do not edit manually! Last saved mod version");

        // TODO proper error handling for saving
        try {
            this.logger.info("Saving config {}", this.path.getFileName());
            Files.writeString(this.path, json.toJson(this.grammar));
        } catch (IOException ex) {
            this.logger.error("Error saving config " + this.path.getFileName(), ex);
        }
    }

    @Override
    public void changeGrammar(@NotNull JsonGrammar newGrammar) {
        Objects.requireNonNull(newGrammar, "New Jankson Grammar must not be null.");
        this.grammar = newGrammar;
    }
}
