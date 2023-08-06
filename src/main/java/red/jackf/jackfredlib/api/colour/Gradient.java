package red.jackf.jackfredlib.api.colour;

import com.mojang.serialization.Codec;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import red.jackf.jackfredlib.impl.colour.GradientImpl;
import red.jackf.jackfredlib.impl.colour.HSVUtils;

import java.util.NavigableMap;

/**
 * <p>A gradient that can be sampled for a colour at a given point. Useful for smooth transitions.</p>
 * <p>Transitions between colours are done using RGB lerping; for HSV functionality see the <code>mode</code> parameter
 * for {@link #linear(Colour, Colour, LinearMode)}.</p>
 * <p>For extra client-specific utilities, see the client API section.</p>
 */
public interface Gradient {
    /**
     * Codec for serializing gradient types. Gradients are converted into a map of float -> int pairs. A solid colour
     * is serialized into a map from -42.0F to the colour.
     */
    Codec<Gradient> CODEC = Codec.unboundedMap(Codec.STRING.xmap(Float::parseFloat, Object::toString), Colour.CODEC).comapFlatMap(GradientImpl::decode, GradientImpl::encode);

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
     * <p>Constructs a gradient based of a set of equally-spaced colours, ranging from 0 to {@link GradientBuilder#END}.</p>
     * <p>If you wish to have a smooth loop, have both the first and last colour be the same, or use a method such as
     * {@link Gradient#squish(float)}.</p>
     * @param points Colours to build a gradient with, spaced equally apart from each other
     * @return A built gradient made out of these points
     */
    static Gradient of(Colour... points) {
        if (points.length == 0) throw new IllegalArgumentException("Can't build a gradient with 0 points");
        if (points.length == 1) return points[0];
        float factor = 1f / (points.length - 1);
        var builder = builder();
        for (int i = 0; i < points.length; i++) {
            builder.add(Math.min(i * factor, GradientBuilder.END), points[i]);
        }
        return builder.build();
    }

    /**
     * Returns a simple gradient going from <code>first</code> to <code>second along the entire range.</code> This results
     * in a sharp cut as the progress value loops when sampling.
     * @param first Start colour at 0
     * @param second End colour at 1
     * @param mode How the transition between the two colours is handled. For more information, see {@link LinearMode}.
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
        if (point == -1.0F) return 0.0F;
        float mod = point % 1F;

        // since -0.0f > 0f equals true, we always want a positive result
        if (mod == -0.0f) return 0.0f;

        if (mod > 0f) return mod;
        return 1F + mod;
    }

    /**
     * <p>Returns a sample of this gradient between the two points, scaled up to fit the range [0, 1).</p>
     * <p>If, after wrapping to [0, 1) it is the case that '<code>end &lt; start</code>' is true, then it is interpreted as
     * two blocks running from <code>start</code> to <code>1F</code> and from <code>0f</code> to <code>end</code>.</p>
     * @param start Start point of the sample, in the range [0, 1).
     * @param end End point of the sample, in the range [0, 1).
     * @return The sliced gradient, scaled up to fit [0, 1).
     */
    Gradient slice(float start, float end);

    /**
     * Returns a version of this gradient with the points reversed.
     * @return A copy of this gradient with the points reversed
     */
    Gradient reversed();

    /**
     * Scales the gradient inwards a set amount, centered at the midpoint. This generates a smooth transition at the
     * edges of the gradient.
     * @param edgeMargin How far in to push the gradient at each edge. Must be in the range [0, 0.5).
     * @return Squished gradient, with smooth edge transitions generated.
     */
    Gradient squish(float edgeMargin);

    /**
     * Repeats this gradient <code>copies</code> times, shrinking this one to make them fit.
     * @param copies How many times to repeat this gradient to produce.
     * @return A gradient composes of <code>copies</code> instances of this gradient.
     * @throws IllegalArgumentException If copies &lt; 1
     */
    Gradient repeat(int copies);

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

    /**
     * @return Get all RGB points on this gradient
     */
    @ApiStatus.Internal
    NavigableMap<Float, Colour> getPoints();
}
