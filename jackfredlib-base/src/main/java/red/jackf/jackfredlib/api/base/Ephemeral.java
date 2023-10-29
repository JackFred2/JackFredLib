package red.jackf.jackfredlib.api.base;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * A holder for ephemerally storing a value; you can add an object but only retrieve it once. Used internally to surrogate
 * arguments for mixins.
 * @param <T> Type of value being stored.
 */
@ApiStatus.AvailableSince("1.2.0")
public class Ephemeral<T> {
    @Nullable
    private T value = null;

    /**
     * Set the currently held value to the given value.
     * @param value Value to store.
     */
    public void push(T value) {
        this.value = value;
    }

    /**
     * Retrieve the currently held value, or <code>null</code> if not available. Afterwards, remove the currently held
     * value.
     * @return The currently held value, or <code>null</code> if not present.
     */
    public @Nullable T pop() {
        var temp = this.value;
        this.value = null;
        return temp;
    }
}
