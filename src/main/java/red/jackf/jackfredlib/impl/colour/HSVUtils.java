package red.jackf.jackfredlib.impl.colour;

import net.minecraft.util.Mth;
import red.jackf.jackfredlib.api.colour.Colour;
import red.jackf.jackfredlib.api.colour.Colours;
import red.jackf.jackfredlib.api.colour.Gradient;
import red.jackf.jackfredlib.api.colour.GradientBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class HSVUtils {
    private static final List<Float> HSV_CHECKPOINTS = IntStream.range(0, 12).mapToObj(i -> i / 6f).toList();

    /**
     * This place is not a place of honor. <br />
     * No highly esteemed deed is commemorated here. <br />
     * Nothing valued is here.
     */
    public static Gradient doHSVShort(Colour firstCol, Colour secondCol) {
        float first = firstCol.hue();
        float second = secondCol.hue();

        boolean ascending = second >= first;

        if (Math.abs(second - first) > 0.5f) {
            if (second > first) {
                ascending = false;
                first += 1f;
            } else {
                ascending = true;
                second += 1f;
            }
        }

        var checkpoints = getCheckpoints(first, second);
        var range = Math.abs(second - first);
        if (range == 0f) return firstCol; // solid

        float adjustment = 0;
        var checkpointsAdjusted = new ArrayList<Float>();
        checkpointsAdjusted.add(checkpoints.get(0));
        for (int i = 1; i < checkpoints.size(); i++) {
            var difference = checkpoints.get(i) - checkpoints.get(i - 1);
            var thisSign = Math.signum(difference);

            if (Math.abs(difference) > 0.25f) {
                adjustment = -thisSign;
            }

            checkpointsAdjusted.add(checkpoints.get(i) + adjustment);
        }

        var builder = Gradient.builder();
        var lowest = Collections.min(checkpointsAdjusted);

        for (Float hue : checkpointsAdjusted) {
            var progress = (hue - lowest) / range;
            if (!ascending) progress = 1f - progress;

            if (progress == 1F) progress = GradientBuilder.END;
            builder.add(progress,
                    new Colour(0xFF_000000 | Mth.hsvToRgb(
                            Gradient.wrapPoint(hue),
                            Mth.lerp(progress, firstCol.saturation(), secondCol.saturation()),
                            Mth.lerp(progress, firstCol.value(), secondCol.value())
                    )));
        }

        return builder.build();
    }

    public static Gradient doHSVLong(Colour firstCol, Colour secondCol) {
        float first = firstCol.hue();
        float second = secondCol.hue();

        boolean ascending = second >= first;

        if (Math.abs(second - first) <= 0.5f) {
            if (second > first) {
                ascending = false;
                first += 1f;
            } else {
                ascending = true;
                second += 1f;
            }
        }

        var checkpoints = getCheckpoints(first, second);
        var range = Math.abs(second - first);
        if (range == 0f) return firstCol; // solid

        float adjustment = 0;
        var checkpointsAdjusted = new ArrayList<Float>();
        checkpointsAdjusted.add(checkpoints.get(0));
        for (int i = 1; i < checkpoints.size(); i++) {
            var difference = checkpoints.get(i) - checkpoints.get(i - 1);
            var thisSign = Math.signum(difference);

            if (Math.abs(difference) > 0.25f) {
                adjustment = -thisSign;
            }

            checkpointsAdjusted.add(checkpoints.get(i) + adjustment);
        }

        var builder = Gradient.builder();
        var lowest = Collections.min(checkpointsAdjusted);

        for (Float hue : checkpointsAdjusted) {
            var progress = (hue - lowest) / range;
            if (!ascending) progress = 1f - progress;

            if (progress == 1F) progress = GradientBuilder.END;
            builder.add(progress,
                    new Colour(0xFF_000000 | Mth.hsvToRgb(
                            Gradient.wrapPoint(hue),
                            Mth.lerp(progress, firstCol.saturation(), secondCol.saturation()),
                            Mth.lerp(progress, firstCol.value(), secondCol.value())
                    )));
        }

        return builder.build();
    }

    private static List<Float> getCheckpoints(float first, float second) {
        boolean flip = first > second;
        if (flip) {
            float tmp = first;
            first = second;
            second = tmp;
        }

        float low = first;
        float high = second;
        var checkpoints = HSV_CHECKPOINTS.stream()
                .filter(f -> f > low && f < high)
                .map(Gradient::wrapPoint)
                .collect(Collectors.toList());
        checkpoints.add(0, Gradient.wrapPoint(first));
        checkpoints.add(Gradient.wrapPoint(second));

        if (flip) Collections.reverse(checkpoints);
        return checkpoints;
    }

    public static void main(String[] args) {
        doHSVShort(Colours.Standard.RED, Colours.Standard.BLUE);
        doHSVShort(Colours.Standard.AQUAMARINE, Colours.Standard.RED);
        doHSVShort(Colours.Standard.RED, Colours.Standard.AQUAMARINE);
        doHSVShort(Colours.Standard.PURPLE, Colours.Standard.ORANGE);
        doHSVShort(Colours.Standard.ORANGE, Colours.Standard.PINK);
    }
}
