package red.jackf.jackfredlib.api.base;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * <p>Holds a (possibly nullable) value, and can return said value a given number of times. Useful for adding 'surrogate
 * arguments' for mixins. This is designed to hold multiple copies so that {@link #pop()} can be used within looped
 * methods.</p>
 *
 * <p>For examples of this class in use, see <a href="https://github.com/JackFred2/JacksServerSideTweaks/blob/0850b22ef2ea1932342bdec525086376b5659a0c/src/main/java/red/jackf/jsst/mixins/qualityoflife/BlockMixin.java">JSST's usage</a></a></p>
 * @param <T> Type of value being held; supports @Nullable values.
 */
@ApiStatus.AvailableSince("1.3.0")
public class Ephemeral2<T>  {
    @Nullable
    private T value = null;
    private int count = 0;

    /**
     * Set the current value to a single copy of {@code value}.
     * @param value Value to be held.
     */
    public synchronized void push(T value) {
        this.push(value, 1);
    }

    /**
     * Sets the current value to a given amount of {@code value}.
     * @param value Value to be held.
     * @param count Number of copies to be held.
     */
    public synchronized void push(T value, int count) {
        if (Math.max(0, count) == 0) return;
        this.value = value;
        this.count = count;
    }

    /**
     * Check whether this ephemeral is currently holding a value.
     * @return Whether this ephemeral is currently holding a value.
     */
    public synchronized boolean hasValue() {
        return this.count > 0;
    }

    /**
     * Retrieve a copy of the value currently being held.
     * @return The value currently being held by this ephemeral.
     * @throws IllegalStateException If there are no values currently being held.
     */
    public synchronized T pop() {
        if (count == 0) throw new IllegalStateException("No value available - use .hasValue()");
        return popNullable();
    }

    /**
     * Retrieve a copy of the value currently being held, or {@code null} if no value is currently held.
     * @return The value currently being held, or {@code null} if no value to being held.
     */
    public synchronized @Nullable T popNullable() {
        if (count == 0) return null;
        this.count--;
        if (count == 0) {
            @Nullable T value = this.value;
            this.value = null;
            return value;
        } else {
            return value;
        }
    }
}
