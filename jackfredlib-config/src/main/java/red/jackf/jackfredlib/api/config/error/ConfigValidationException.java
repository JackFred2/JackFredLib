package red.jackf.jackfredlib.api.config.error;

/**
 * Should be raised if a non-correctable error occurs in config validation. Correction should be preferred, such as
 * clamping an int to a valid range, over throwing, but sometimes this is unavoidable.
 */
public class ConfigValidationException extends Exception {
    /**
     * Create a new config validation exception with the given message.
     * @param message Message to add to the exception.
     */
    public ConfigValidationException(String message) {
        super(message);
    }
}
