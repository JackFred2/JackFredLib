package red.jackf.jackfredlib.testmod;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import net.minecraft.nbt.NbtOps;
import org.slf4j.Logger;
import red.jackf.jackfredlib.api.colour.Colours;
import red.jackf.jackfredlib.api.colour.Gradient;
import red.jackf.jackfredlib.api.colour.Gradients;
import red.jackf.jackfredlib.impl.base.LogUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class GradientEncode {
    private static final Logger LOGGER = LogUtil.getLogger("Test/Gradient Encoding");

    public static void test() {
        var errors = new ArrayList<String>();

        encodeDecodeEquals(Gradients.RAINBOW, errors::add);
        encodeDecodeEquals(Gradients.INTERSEX_SHARP, errors::add);
        encodeDecodeEquals(Gradients.ARO, errors::add);
        encodeDecodeEquals(Colours.HOT_PINK, errors::add);

        if (errors.isEmpty())
            LOGGER.debug("Gradient encodes OK");
        else
            errors.forEach(LOGGER::debug);
    }

    private static void encodeDecodeEquals(Gradient gradient, Consumer<String> errConsumer) {
        var ops = List.of(JsonOps.INSTANCE, JsonOps.COMPRESSED, NbtOps.INSTANCE);

        for (DynamicOps<?> op : ops)
            doOps(op, gradient, errConsumer);
    }

    private static <T> void doOps(DynamicOps<T> op, Gradient gradient, Consumer<String> errConsumer) {
        DataResult<T> encoded = Gradient.CODEC.encodeStart(op, gradient);
        encoded.ifSuccess(t -> {
            var decoded = Gradient.CODEC.decode(op, t);
            decoded.map(Pair::getFirst).ifSuccess(gradient2 -> {
                if (!gradient.equals(gradient2)) {
                    errConsumer.accept(op.getClass().getSimpleName() + " ERR");
                    errConsumer.accept("Before: %s".formatted(gradient));
                    errConsumer.accept("After: %s".formatted(gradient2));
                }
            }).ifError(err -> errConsumer.accept("Error decode: " + err.message()));
        }).ifError(err -> errConsumer.accept("Error encode: " + err.message()));
    }
}
