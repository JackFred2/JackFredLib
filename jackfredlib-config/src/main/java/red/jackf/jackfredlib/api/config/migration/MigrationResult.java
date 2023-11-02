package red.jackf.jackfredlib.api.config.migration;

import blue.endless.jankson.JsonObject;

/**
 * Denotes how a given application of a migration turned out.
 */
public sealed interface MigrationResult {
    /**
     * Indicates that this migration was not applicable to the given config JSON. Any changes to the JSON from this
     * migration will be dropped.
     */
    MigrationResult NOT_APPLICABLE = new Singleton(Type.NOT_APPLICABLE);

    /**
     * Indicates that this migration was applied successfully to the given config JSON. Any changes to the JSON will
     * be kept.
     */
    MigrationResult SUCCESS = new Singleton(Type.SUCCESS);

    /**
     * Indicates that this migration was applied successfully to the given config JSON. Any changes to the JSON will
     * be kept. This method allows you to add message to be logged.
     * @param message Message to be logged.
     * @return A successful migration result with the given message.
     */
    static MigrationResult SUCCESS(String message) {
        return new WithMessage(Type.SUCCESS, message);
    }

    /**
     * Indicates that this migration failed to apply, but is not fatal to the migration. Any changes to the JSON will
     * be dropped.
     */
    MigrationResult FAIL_SOFT = new Singleton(Type.FAIL_SOFT);

    /**
     * Indicates that this migration failed to apply, but is not fatal to the migration. Any changes to the JSON will
     * be dropped. This method allows you to add message to be logged.
     * @param message Message to be logged.
     * @return A soft failure migration result with the given message.
     */
    static MigrationResult FAIL_SOFT(String message) {
        return new WithMessage(Type.FAIL_SOFT, message);
    }

    /**
     * Indicates that this migration failed to apply, and was fatal to the migration. The bad config file will be moved
     * and renamed, while a new default instance will be saved and applied. Terminates migration.
     */
    MigrationResult FAIL_HARD = new Singleton(Type.FAIL_HARD);

    /**
     * Indicates that this migration failed to apply, and was fatal to the migration. The bad config file will be moved
     * and renamed, while a new default instance will be saved and applied. This method allows you to add a message to
     * be logged. Terminates migration.
     * @param message Message to be logged.
     * @return A fatal error result with the given message.
     */
    static MigrationResult FAIL_HARD(String message) {
        return new WithMessage(Type.FAIL_HARD, message);
    }

    /**
     * Indicates that this migration replaces the whole JSON with a new instance. The old config file will be replaced
     * with this instance. Terminates migration.
     * @param replacement Replacement JSON to use.
     * @return A replacement migration result.
     */
    static MigrationResult REPLACE(JsonObject replacement) {
        return new Replacement(replacement);
    }

    /**
     * Get the type of this result.
     * @return Type of this result.
     */
    Type type();

    // impl

    /**
     * Result type of a migration application.
     */
    enum Type {
        /**
         * Does not apply to the passed config JSON.
         */
        NOT_APPLICABLE,
        /**
         * Successfully applied to the config JSON.
         */
        SUCCESS,
        /**
         * Soft failure applying to the config JSON.
         */
        FAIL_SOFT,
        /**
         * Hard failure applying to the config JSON.
         */
        FAIL_HARD,
        /**
         * Replacement of the config JSON with a new value.
         */
        REPLACE
    }

    /**
     * A migration result with no added message.
     * @param type Type of the migration result.
     */
    record Singleton(Type type) implements MigrationResult {}

    /**
     * A migration result with an added message.
     * @param type Type of the migration result.
     * @param message Message of the result.
     */
    record WithMessage(Type type, String message) implements MigrationResult {}

    /**
     * A migration result denoting a JSON replacement.
     * @param replacement JSON to use.
     */
    record Replacement(JsonObject replacement) implements MigrationResult {
        @Override
        public Type type() {
            return Type.REPLACE;
        }
    }
}
