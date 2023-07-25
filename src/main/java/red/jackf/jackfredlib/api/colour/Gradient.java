package red.jackf.jackfredlib.api.colour;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import red.jackf.jackfredlib.impl.colour.HSVUtils;

import java.util.NavigableMap;

/**
 * <p>A gradient that can be sampled for a colour at a given point. Useful for smooth transitions.</p>
 * <p>Transitions between colours are done using RGB lerping by default.</p>
 */
public interface Gradient {

    /**
     * Sample this gradient at a given point in the range [0, 1).
     * @param progress Sample this gradient at the given point. This wraps if outside [0, 1).
     * @return The colour at this point in the gradient.
     */
    @Contract(pure = true)
    Colour sample(float progress);

    /**
     * Creates a builder for a new gradient.
     * @return Builder to help create a new gradient.
     */
    @Contract("-> new")
    static GradientBuilder builder() {
        return new GradientBuilder();
    }

    /**
     * Constructs a gradient based of a set of equally-spaced colours. A copy of <code>points[0]</code> is placed at
     * {@link GradientBuilder#END} in order to have a smooth loop.
     * @param points Colours to build a gradient with, spaced equally apart from each other
     * @return A built gradient made out of these points
     */
    static Gradient of(Colour... points) {
        if (points.length == 0) throw new IllegalArgumentException("Can't build a gradient with 0 points");
        if (points.length == 1) return points[0];
        float factor = 1f / points.length;
        var builder = builder();
        for (int i = 0; i < points.length; i++) {
            builder.add(i * factor, points[i]);
        }
        return builder.add(GradientBuilder.END, points[0]).build();
    }

    /**
     * Returns a simple gradient going from <code>first</code> to <code>second along the entire range.</code> This results
     * in a sharp cut as the progress value loops when sampling.
     * @param first Start colour at 0
     * @param second End colour at 1
     * @return The built linear gradient from the first colour to the second.
     */
    static Gradient linear(Colour first, Colour second, LinearMode mode) {
        return switch (mode) {
            case RGB -> builder()
                        .add(GradientBuilder.START, first)
                        .add(GradientBuilder.END, second)
                        .build();
            case HSV_SHORT -> HSVUtils.doHSVShort(first, second);
            case HSV_LONG -> HSVUtils.doHSVLong(first, second);
       };
    }

    /**
     * Wraps a point into the range [0, 1), i.e. the range of a valid gradient.
     * @param point Point to wrap
     * @return Wrapped point
     */
    @Contract(pure = true)
    static float wrapPoint(float point) {
        float mod = point % 1F;
        if (mod >= 0) return mod;
        return 1F + mod;
    }

    /**
     * <p>Returns a sample of this gradient between the two points, scaled up to fit the range [0, 1).</p>
     * <p>If, after wrapping to [0, 1) it is the case that '<code>end < start</code>' is true, then it is interpreted as
     * two blocks running from <code>start</code> to <code>1F</code> and from <code>0f</code> to <code>end</code>.</p>
     * @param start Start point of the sample.
     * @param end End point of the sample.
     * @return The sliced gradient, scaled up to fit [0, 1).
     */
    Gradient slice(float start, float end);

    /**
     * Returns a version of this gradient with the points reversed.
     * @return A copy of this gradient with the points reversed
     */
    Gradient reversed();

    /**
     * Defines how a linear gradient will be interpolated between two colours.
     */
    enum LinearMode {
        /**
         * Simple lerp between the start and endpoints for each colour component value. This is approximate to going
         * across a colour wheel (i.e. <code>lerp(RED, CYAN, 0.5) ≈ GRAY</code>), and how the alpha component is
         * handled in every case.
         */
        RGB,
        /**
         * <p>Lerp instead by the Hue, Saturation and Value colours. This is approximate to going around the outside of a
         * colour wheel. Alpha is always handled as {@link LinearMode#RGB}.</p>
         * <p>This mode takes the hue through the shortest path around the colour wheel.</p>
         */
        HSV_SHORT,
        /**
         * <p>Lerp instead by the Hue, Saturation and Value colours. This is approximate to going around the outside of a
         * colour wheel. Alpha is always handled as {@link LinearMode#RGB}.</p>
         * <p>This mode takes the hue through the longest path around the colour wheel.</p>
         */
        HSV_LONG
    }

    @ApiStatus.Internal
    NavigableMap<Float, Colour> getPoints();
}
