package red.jackf.jackfredlib.api.config;

import red.jackf.jackfredlib.api.config.error.ConfigValidationException;

public interface Validatable {
    /**
     * Called after an object is loaded. This should verify that all values are valid (i.e. ints within range), and correct
     * if possible. If correction can't be done safely, throw.
     * @throws ConfigValidationException If validation and correction can't be done safely.
     */
    default void validate() throws ConfigValidationException {}
}
