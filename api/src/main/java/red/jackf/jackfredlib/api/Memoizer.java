package red.jackf.jackfredlib.api;

import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class Memoizer<T> implements Supplier<T> {
    @Nullable
    private T value;
    private final Supplier<T> factory;

    private Memoizer(Supplier<T> factory) {
        this.factory = factory;
    }

    public static <T> Memoizer<T> of(Supplier<T> factory) {
        return new Memoizer<>(factory);
    }

    public T get() {
        if (value == null) value = factory.get();
        return value;
    }
}
