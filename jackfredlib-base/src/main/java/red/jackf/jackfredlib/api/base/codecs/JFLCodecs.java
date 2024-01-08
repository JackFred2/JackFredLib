package red.jackf.jackfredlib.api.base.codecs;

import com.mojang.serialization.Codec;

/**
 * A collection of possibly-useful codecs.
 */
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
}
