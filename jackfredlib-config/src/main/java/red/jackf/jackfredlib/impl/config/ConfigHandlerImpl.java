package red.jackf.jackfredlib.impl.config;

import blue.endless.jankson.*;
import blue.endless.jankson.api.SyntaxError;
import blue.endless.jankson.magic.TypeMagic;
import net.fabricmc.loader.api.Version;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import red.jackf.jackfredlib.api.config.Config;
import red.jackf.jackfredlib.api.config.ConfigHandler;
import red.jackf.jackfredlib.api.config.LoadErrorHandlingMode;
import red.jackf.jackfredlib.api.config.error.ConfigLoadException;
import red.jackf.jackfredlib.api.config.error.ConfigValidationException;
import red.jackf.jackfredlib.impl.config.migrator.MigratorResult;
import red.jackf.jackfredlib.impl.config.migrator.MigratorBuilderImpl;
import red.jackf.jackfredlib.impl.config.migrator.MigratorImpl;
import red.jackf.jackfredlib.api.config.migration.MigratorUtil;

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
    private final @Nullable MigratorImpl<T> migrator;
    private final LoadErrorHandlingMode loadErrorHandlingMode;
    private final Consumer<Exception> loadExceptionCallback;

    private T instance = null;

    public ConfigHandlerImpl(
            Class<T> configClass,
            Path path,
            Jankson jankson,
            JsonGrammar grammar,
            boolean useFileWatcher,
            Logger logger,
            @Nullable MigratorBuilderImpl<T> migratorBuilder,
            LoadErrorHandlingMode loadErrorHandlingMode,
            Consumer<Exception> loadExceptionCallback) {

        this.configClass = configClass;
        this.path = path;
        this.jankson = jankson;
        this.grammar = grammar;
        this.logger = logger;
        this.migrator = migratorBuilder != null ? migratorBuilder.build(this) : null;
        this.loadErrorHandlingMode = loadErrorHandlingMode;
        this.loadExceptionCallback = loadExceptionCallback;

        if (useFileWatcher) this.useFileWatcher(true);
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
                boolean hasVersionChanged = false;
                T loadedInstance = null;

                if (this.migrator != null
                        && lastVersion instanceof JsonPrimitive primitive
                        && primitive.getValue() instanceof String versionStr) {

                    Version parsed = MigratorUtil.unsafeParse(versionStr);

                    int versionComparison = this.migrator.getCurrentVersion().compareTo(parsed);

                    if (versionComparison != 0) {
                        hasVersionChanged = true;
                        if (versionComparison > 0) {
                            this.logger.info("Config migration: {} -> {}",
                                             parsed.getFriendlyString(),
                                             this.migrator.getCurrentVersion().getFriendlyString());


                            MigratorResult result = this.migrator.migrate(json, parsed);

                            if (result instanceof MigratorResult.Replace replace) {

                                this.logger.debug("Replacing config");
                                json = replace.replacement();

                            } else if (result instanceof MigratorResult.FailHard failHard) {

                                this.logger.error("Error migrating config");
                                // TODO move errored file
                                failHard.messages().forEach(this.logger::info);
                                loadedInstance = getDefault();
                                json = (JsonObject) this.jankson.toJson(this.instance);

                            } else if (result instanceof MigratorResult.Success success) {

                                success.messages().forEach(this.logger::info);
                                json = success.result();

                            }
                        } else { // mod downdate, don't migrate and hope for the best
                            this.logger.info("Mod downdate, not migrating: {} -> {}",
                                             parsed.getFriendlyString(),
                                             this.migrator.getCurrentVersion().getFriendlyString());
                        }
                    }
                }

                this.instance = loadedInstance != null ? loadedInstance : this.jankson.fromJson(json, configClass);

                this.instance.validate();

                // Re-save if updated
                JsonElement copy = this.jankson.toJson(instance);
                if (hasVersionChanged || copy instanceof JsonObject copyObj && !copyObj.getDelta(json).isEmpty()) {
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
                     JsonPrimitive.of(this.migrator.getCurrentVersion().getFriendlyString()),
                     "Users: do not edit manually! Last saved mod version");

        // TODO proper error handling for saving
        try {
            this.logger.info("Saving config {}", this.path.getFileName());
            Files.writeString(this.path, json.toJson(this.grammar));
            FileWatcher.skip(this.path);
        } catch (IOException ex) {
            this.logger.error("Error saving config " + this.path.getFileName(), ex);
        }
    }

    @Override
    public Path getFilePath() {
        return this.path;
    }

    @Override
    public void changeGrammar(@NotNull JsonGrammar newGrammar) {
        Objects.requireNonNull(newGrammar, "New Jankson Grammar must not be null.");
        this.grammar = newGrammar;
    }

    @Override
    public void useFileWatcher(boolean shouldUseFileWatcher) {
        if (shouldUseFileWatcher) {
            FileWatcher.enable(this.path, this);
        } else {
            FileWatcher.disable(this.path);
        }
    }

    public Jankson getJankson() {
        return jankson;
    }
}
