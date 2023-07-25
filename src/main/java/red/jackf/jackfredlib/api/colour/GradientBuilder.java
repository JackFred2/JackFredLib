package red.jackf.jackfredlib.api.colour;

import org.jetbrains.annotations.Contract;
import red.jackf.jackfredlib.impl.colour.GradientImpl;

import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * Aids with the construction of a gradient, providing several methods to slice and splice colours and gradients.
 */
public class GradientBuilder {
    private final NavigableMap<Float, Colour> points = new TreeMap<>();

    GradientBuilder() {}

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
     * use {@link GradientBuilder#END} as the point.
     *
     * @param point  Position on the gradient to add this colour
     * @param colour Colour to be placed at this position
     * @return This gradient builder with the colour point added
     */
    @Contract("_, _ -> this")
    public GradientBuilder add(float point, Colour colour) {
        point = Gradient.wrapPoint(point);
        this.points.put(point, colour);
        return this;
    }

    /**
     * <p>Adds a solid block of colour or another gradient between two points, removing any existing points between the bounds.</p>
     * <p>If, after wrapping to [0, 1) it is the case that '<code>end < start</code>' is true, then it is interpreted as two blocks
     * running from <code>start</code> to <code>1f</code> and from <code>0f</code> to <code>end</code>.</p>
     *
     * @param start    Start position of the solid block
     * @param end      End position of the solid block
     * @param gradient Gradient or colour that this solid block should be
     * @return This gradient builder with the solid block added
     */
    @Contract("_, _, _ -> this")
    public GradientBuilder addBlock(float start, float end, Gradient gradient) {
        start = Gradient.wrapPoint(start);
        end = Gradient.wrapPoint(end);
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
     * @param point  Point to add the transition to
     * @param before Colour to be shown at or before the point
     * @param after  Colour to be shown immediately after the point
     * @return This gradient builder
     */
    @Contract("_, _, _ -> this")
    public GradientBuilder addCut(float point, Colour before, Colour after) {
        point = Gradient.wrapPoint(point);
        float pointAfter = Math.nextUp(point);
        if (pointAfter > END) pointAfter = START;
        this.points.put(point, before);
        this.points.put(pointAfter, after);
        return this;
    }

    /**
     * <p>Construct a gradient from the current builder. A lerped colour is added at {@link GradientBuilder#START} and
     * {@link GradientBuilder#END}, so as to provide a smooth loop. If this is not desirable, add a colour point at
     * {@link GradientBuilder#END}.</p>
     * <p>If zero points were added to this builder, then an {@link IllegalArgumentException} is thrown.</p>
     *
     * @return The built gradient.
     */
    public Gradient build() {
        if (this.points.size() == 0)
            throw new IllegalArgumentException("Cannot construct a gradient with zero colour frames");
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
