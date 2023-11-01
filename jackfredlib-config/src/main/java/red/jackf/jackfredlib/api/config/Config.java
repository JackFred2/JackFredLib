package red.jackf.jackfredlib.api.config;

import com.google.gson.JsonParseException;

public interface Config extends Validatable {

    /**
     * Called after a config is successfully validated and loaded.
     */
    default void load() {

    }
}
