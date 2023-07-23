package red.jackf.jackfredlib.api.colour;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import red.jackf.jackfredlib.impl.colour.GradientImpl;

import java.util.NavigableMap;
import java.util.TreeMap;

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
    static Builder builder() {
        return new Builder();
    }

    /**
     * Constructs a gradient based of a set of equally-spaced colours. A copy of <code>points[0]</code> is placed at
     * {@link Builder#END} in order to have a smooth loop.
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
        return builder.add(Builder.END, points[0]).build();
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

    @ApiStatus.Internal
    NavigableMap<Float, Colour> getPoints();

    class Builder {
        private final NavigableMap<Float, Colour> points = new TreeMap<>();
        private Builder() {}

        /**
         * Start point of a gradient.
         */
        public static final float START = 0F;
        /**
         * Endpoint of a gradient; this is the float value immediately before 1F.
         */
        public static final float END = Math.nextDown(1F);

        /**
         * Adds a colour point to the given position in the gradient. If you want to add a keyframe at the end of the gradient,
         * use {@link Builder#END} as the point.
         *
         * @param point Position on the gradient to add this colour
         * @param colour Colour to be placed at this position
         * @return This gradient builder with the colour point added
         */
        @Contract("_, _ -> this")
        public Builder add(float point, Colour colour) {
            point = wrapPoint(point);
            this.points.put(point, colour);
            return this;
        }

        /**
         * <p>Adds a solid block of colour or another gradient between two points, removing any existing points between the bounds.</p>
         * <p>If, after wrapping to [0, 1) it is the case that '<code>end < start</code>' is true, then it is interpreted as two blocks
         * running from <code>start</code> to <code>1f</code> and from <code>0f</code> to <code>end</code>.</p>
         *
         * @param start Start position of the solid block
         * @param end End position of the solid block
         * @param gradient Gradient or colour that this solid block should be
         * @return This gradient builder with the solid block added
         */
        @Contract("_, _, _ -> this")
        public Builder addBlock(float start, float end, Gradient gradient) {
            start = wrapPoint(start);
            end = wrapPoint(end);
            if (start == end) {
                this.points.put(start, gradient.sample(START));
            } else if (start < end) {
                this.points.subMap(start, true, end, true).clear();

                var scaleFactor = end - start;
                for (var point : gradient.getPoints().entrySet())
                    this.points.put(start + point.getKey() * scaleFactor, point.getValue());
            } else { // start > end
                var joinPoint = (END - start) / (END - start + end);
                var untilEnd = gradient.slice(START, joinPoint);
                var fromStart = gradient.slice(joinPoint, END);

                this.addBlock(start, END, untilEnd);
                this.addBlock(START, end, fromStart);
            }

            return this;
        }

        /**
         * Adds an instant transition at the given point between two colours; such that <code>sample(point) == before</code>
         * and <code>sample(point + eps) == after</code>.
         *
         * @param point Point to add the transition to
         * @param before Colour to be shown at or before the point
         * @param after Colour to be shown immediately after the point
         * @return This gradient builder
         */
        @Contract("_, _, _ -> this")
        public Builder addCut(float point, Colour before, Colour after) {
            point = wrapPoint(point);
            float pointAfter = Math.nextUp(point);
            if (pointAfter > END) pointAfter = START;
            this.points.put(point, before);
            this.points.put(pointAfter, after);
            return this;
        }

        /**
         * <p>Construct a gradient from the current builder. A lerped colour is added at {@link Builder#START} and
         * {@link Builder#END}, so as to provide a smooth loop. If this is not desirable, add a colour point at
         * {@link Builder#END}.</p>
         * <p>If zero points were added to this builder, then an {@link IllegalArgumentException} is thrown.</p>
         * @return The built gradient.
         */
        public Gradient build() {
            if (this.points.size() == 0) throw new IllegalArgumentException("Cannot construct a gradient with zero colour frames");
            else if (this.points.size() == 1) return this.points.firstEntry().getValue(); // return a solid colour
            else {
                var first = this.points.firstEntry();
                var last = this.points.lastEntry();
                if (first.getKey() != START && last.getKey() != END) {
                    float distance = first.getKey() + (END - last.getKey());
                    var colour = first.getValue().lerp(last.getValue(), first.getKey() / distance);
                    this.points.put(START, colour);
                    this.points.put(END, colour);
                } else if (first.getKey() != START) {
                    this.points.put(START, this.points.get(last.getKey()));
                } else if (last.getKey() != END) {
                    this.points.put(END, this.points.get(first.getKey()));
                }

                return new GradientImpl(this.points);
            }
        }

    }
}
