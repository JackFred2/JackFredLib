package red.jackf.jackfredlib.api.base.codecs;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

import java.util.Arrays;
import java.util.Objects;

class FirstInListCodec<T> implements Codec<T> {
    private final Codec<T>[] codecs;

    @SafeVarargs
    protected FirstInListCodec(Codec<T>... codecs) {
        this.codecs = codecs;
    }

    @Override
    public <A> DataResult<Pair<T, A>> decode(DynamicOps<A> ops, A input) {
        for (Codec<T> codec : this.codecs) {
            DataResult<Pair<T, A>> data = codec.decode(ops, input);
            if (data.result().isPresent()) {
                return data;
            }
        }
        return DataResult.error(() -> "No codecs can decode " + input);
    }

    @Override
    public <A> DataResult<A> encode(T input, DynamicOps<A> ops, A prefix) {
        for (Codec<T> codec : this.codecs) {
            DataResult<A> data = codec.encode(input, ops, prefix);
            if (data.result().isPresent()) {
                return data;
            }
        }

        return DataResult.error(() -> "No codecs can encode " + input);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final FirstInListCodec<?> eitherCodec = ((FirstInListCodec<?>) o);
        return Arrays.equals(codecs, eitherCodec.codecs);
    }

    @Override
    public int hashCode() {
        return Objects.hash((Object) codecs);
    }

    @Override
    public String toString() {
        return "FirstInListCodec" + Arrays.toString(codecs);
    }
}
