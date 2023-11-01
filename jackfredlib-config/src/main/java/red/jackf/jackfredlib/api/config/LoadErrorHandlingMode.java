package red.jackf.jackfredlib.api.config;

/**
 * <p>Governs how a config handler should respond when the config file fails to load, due to an IO error, syntax error or
 * otherwise.</p>
 * <p>In all of these options, either the last valid instance of the config will be kept, or a temporary default instance
 * will be loaded.</p>
 */
public enum LoadErrorHandlingMode {
    /**
     * Silently ignore the failed config..
     */
    SILENT,

    /**
     * Logs details on the error to the console. This is the default option.
     */
    LOG,

    /**
     * Rethrow the exception, normally crashing the game unless caught by your code.
     */
    RETHROW,

    /*
      Suffix the errored config file with '.errored', and generate a new default file.
    MOVE_ERRORED_AND_RESTORE_DEFAULT,
    */
}
