package red.jackf.jackfredlib.api.base;

import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * A memoizer is essentially a lazy initializer - it will only calculate its value when first asked, then cache it and
 * return it on subsequent asks. This lets you delay expensive calculations, or mitigate timing issues.
 *
 * @param <T> Type of value being memoized.
 */
public class Memoizer<T> implements Supplier<T> {
    @Nullable
    private T value;
    private final Supplier<T> factory;

    private Memoizer(Supplier<T> factory) {
        this.factory = factory;
    }

    /**
     * Create a memoized wrapper around a factory.
     *
     * @param factory Factory to run once when asked. The result is cached afterwards.
     * @return Memoizer for the given factory
     * @param <T> Type of value being memoized.
     */
    public static <T> Memoizer<T> of(Supplier<T> factory) {
        return new Memoizer<>(factory);
    }

    /**
     * <p>Get the value from this memoizer. If this is the first call, then the value will be computed from the
     * {@link Memoizer#factory}, otherwise it will return the computed value.</p>
     *
     * <p>It is guaranteed that for any two calls of <code>get</code> will return the same instance.</p>
     *
     * @return Computed or cached value
     */
    public T get() {
        if (value == null) value = factory.get();
        return value;
    }
}
