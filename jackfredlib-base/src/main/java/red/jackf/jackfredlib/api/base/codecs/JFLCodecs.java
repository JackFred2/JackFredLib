package red.jackf.jackfredlib.api.base.codecs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A collection of possibly-useful codecs.
 */
@ApiStatus.AvailableSince("1.3.0")
public class JFLCodecs {
    private JFLCodecs() {}

    /**
     * <p>Returns a codec that, for both encoding and decoding, tries each codec in the order they are passed in.</p>
     * <p>If all of the codecs fail on encode/decode then an error data result is returned.</p>
     * @param codecs Codecs to try in order.
     * @return A codec that tries the given codecs in order both directions.
     * @param <T> Type that the codecs are handling.
     */
    @SafeVarargs
    public static <T> Codec<T> firstInList(Codec<T>... codecs) {
        return new FirstInListCodec<>(codecs);
    }

    /**
     * Returns a codec that decodes/encodes an enum to a String based on it's {@link Enum#name()}. Throws an error if
     * the passed constant isn't part of the enum definition.
     * @param enumClass Class of the enum being decoded/encoded.
     * @return A codec handling the given enum.
     * @param <E> Enum being handled.
     */
    public static <E extends Enum<E>> Codec<E> forEnum(Class<E> enumClass) {
        return Codec.STRING.comapFlatMap(str -> {
            try {
                return DataResult.success(Enum.valueOf(enumClass, str));
            } catch (IllegalArgumentException ex) {
                return DataResult.error(() -> "Unknown enum constant for " + enumClass.getSimpleName() + ": " + str);
            }
        }, Enum::name);
    }

    /**
     * Modifies a codec to test with a predicate after decoding, and throw an error if it fails.
     * @param codec Codec to use as a base.
     * @param predicate Predicate to test decoded items against.
     * @return A codec that filters decoded results.
     * @param <T> Type of element being filtered.
     */
    public static <T> Codec<T> filtering(Codec<T> codec, Predicate<T> predicate) {
        return codec.comapFlatMap(t -> {
            if (predicate.test(t)) {
                return DataResult.success(t);
            } else {
                return DataResult.error(() -> "Disallowed value: " + t);
            }
        }, Function.identity());
    }

    /**
     * Modifies a codec so that a decoded value must be contained within the given collection.
     * @param codec Codec to use as a base.
     * @param allowedValues Collection containing all allowed values for decoded items.
     * @return A codec that filters decoded results.
     * @param <T> Type of element being filtered.
     */
    public static <T> Codec<T> oneOf(Codec<T> codec, Collection<T> allowedValues) {
        final Set<T> copy = Set.copyOf(allowedValues);
        return filtering(codec, copy::contains);
    }

    /**
     * Modifies a list codec so that the decoded list is mutable instead of the standard immutable list.
     * @param codec List codec to modify.
     * @return A list codec that returns a mutable list.
     * @param <T> Element type of the list being decoded.
     */
    public static <T> Codec<List<T>> mutableList(Codec<List<T>> codec) {
        return codec.xmap(ArrayList::new, Function.identity());
    }

    /**
     * Modifies a set codec so that the decoded set is mutable instead of the standard immutable set.
     * @param codec Set codec to modify.
     * @return A set codec that returns a mutable set.
     * @param <T> Element type of the set being decoded.
     */

    public static <T> Codec<Set<T>> mutableSet(Codec<Set<T>> codec) {
        return codec.xmap(HashSet::new, Function.identity());
    }

    /**
     * Modifies a map codec so that the decoded map is mutable instance of the standard immutable map.
     * @param codec Map codec to modify.
     * @return A map codec that returns a mutable map.
     * @param <T> Key type of the map being decoded
     * @param <U> Value type of the map being decoded
     */
    public static <T, U> Codec<Map<T, U>> mutableMap(Codec<Map<T, U>> codec) {
        return codec.xmap(HashMap::new, Function.identity());
    }
}
