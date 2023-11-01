package red.jackf.jackfredlib.api.config.error;

import red.jackf.jackfredlib.api.config.LoadErrorHandlingMode;

/**
 * Thrown to signify that an error occurred during loading a {@link red.jackf.jackfredlib.api.config.ConfigHandler}'s
 * config file. This is only thrown if said handler's {@link red.jackf.jackfredlib.api.config.ConfigHandlerBuilder#loadErrorHandling(LoadErrorHandlingMode)}
 * is set to {@link LoadErrorHandlingMode#RETHROW}.
 */
public class ConfigLoadException extends RuntimeException {
    /**
     * Create a new config loading exception.
     * @param err Root cause of the loading error.
     */
    public ConfigLoadException(Exception err) {
        super(err);
    }
}
