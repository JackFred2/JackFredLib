package red.jackf.jackfredlib.api.config;

public interface Validatable {
    /**
     * Called after an object is loaded. This should verify that all values are valid (i.e. ints within range,
     * @throws ConfigValidationException
     */
    default void validate() throws ConfigValidationException {}
}
