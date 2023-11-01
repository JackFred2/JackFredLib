package red.jackf.jackfredlib.api.config;

/**
 * Should be raised if a non-correctable error occurs in config validation. Correction should be preferred, such as
 * clamping an int to a valid range, over throwing, but sometimes this is unavoidable.
 */
public class ConfigValidationException extends Exception {
}
