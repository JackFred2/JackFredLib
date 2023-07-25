package red.jackf.jackfredlib.api.colour;

import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import red.jackf.jackfredlib.impl.colour.GradientImpl;

import java.util.NavigableMap;
import java.util.Objects;

/**
 * Represents a single colour value. Implements {@link Gradient} as a single-coloured gradient
 */
public final class Colour implements Gradient {
    private final int integer;

    // Memoized HSV components
    private float h = Float.NaN; // hue, range [0, 1)
    private float s = Float.NaN; // saturation, range [0, 1)
    private float v = Float.NaN; // value, range [0, 1)

    /**
     * Creates a colour from an ARGB integer colour.
     * @param integer Integer representation of a colour, in ARGB format.
     */
    public Colour(int integer) {
        this.integer = integer;
    }

    /**
     * Returns the integer representation of this colour, in ARGB format.
     * @return Integer value of this colour
     */
    public int integer() {
        return integer;
    }

    /**
     * Constructs a new colour from the given components
     *
     * @param a Alpha value of the new colour, in the range [0, 255]
     * @param r Red value of the new colour, in the range [0, 255]
     * @param g Green value of the new colour, in the range [0, 255]
     * @param b Blue value of the new colour, in the range [0, 255]
     * @return Constructed colour from given components
     */
    @Contract(pure = true, value = "_, _, _, _ -> new")
    public static Colour fromARGB(int a, int r, int g, int b) {
        return new Colour(FastColor.ARGB32.color(a, r, g, b));
    }

    /**
     * Constructs a new colour from the given components, and a full alpha value.
     *
     * @param r Red value of the new colour, in the range [0, 255]
     * @param g Green value of the new colour, in the range [0, 255]
     * @param b Blue value of the new colour, in the range [0, 255]
     * @return Constructed colour from given components
     */
    @Contract(pure = true, value = " _, _, _ -> new")
    public static Colour fromRGB(int r, int g, int b) {
        return fromARGB(255, r, g, b);
    }

    /**
     * Get the alpha component of this colour.
     *
     * @return Alpha component of this colour.
     */
    @Contract(pure = true)
    public int a() {
        return FastColor.ARGB32.alpha(integer);
    }

    /**
     * Get the red component of this colour.
     *
     * @return Red component of this colour.
     */
    @Contract(pure = true)
    public int r() {
        return FastColor.ARGB32.red(integer);
    }

    /**
     * Get the green component of this colour.
     *
     * @return Green component of this colour.
     */
    @Contract(pure = true)
    public int g() {
        return FastColor.ARGB32.green(integer);
    }

    /**
     * Get the blue component of this colour.
     *
     * @return Blue component of this colour.
     */
    @Contract(pure = true)
    public int b() {
        return FastColor.ARGB32.blue(integer);
    }

    /**
     * Returns the hue of this colour, in the range [0, 1), calculating and caching the result on first run.
     * @return The hue component of this colour.
     */
    public float hue() {
        if (Float.isNaN(this.h)) {
            float r = r() / 255f;
            float g = g() / 255f;
            float b = b() / 255f;
            float greatest = Math.max(Math.max(r, g), b);
            float least = Math.min(Math.min(r, g), b);
            float range = greatest - least;

            this.h = 0f;
            if (range > 0) {
                if (greatest == r) {
                    this.h = (g - b) / range;
                } else if (greatest == g) {
                    this.h = 2f + (b - r) / range;
                } else {
                    this.h = 4f + (r - g) / range;
                }
                this.h /= 6;
                this.h = Gradient.wrapPoint(this.h);
            }
        }

        return this.h;
    }

    /**
     * Returns the saturation value of this colour in the range [0, 1), calculating and caching the result on first run.
     * @return The saturation component of this colour.
     */
    public float saturation() {
        if (Float.isNaN(this.s)) {
            float r = r() / 255f;
            float g = g() / 255f;
            float b = b() / 255f;
            float greatest = Math.max(Math.max(r, g), b);
            float least = Math.min(Math.min(r, g), b);
            float range = greatest - least;

            this.s = greatest == 0f ? 0f : range / greatest;
        }

        return this.s;
    }

    /**
     * Returns the saturation value of this colour in the range [0, 1), calculating and caching the result on first run.
     * @return The saturation component of this colour.
     */
    public float value() {
        if (Float.isNaN(this.v)) {
            float r = r() / 255f;
            float g = g() / 255f;
            float b = b() / 255f;
            this.v = Math.max(Math.max(r, g), b);
        }

        return this.v;
    }

    /**
     * Scales the brightness of this colour by a certain factor by multiplying each colour component.
     *
     * @param factor Factor to scale brightness by; i.e. 0.5 to darken 50%, 2.0 to brighten 200%
     * @return Brightness-adjusted colour
     */
    @Contract(pure = true)
    public Colour scaleBrightness(float factor) {
        return Colour.fromARGB(this.a(),
                (int) Mth.clamp(this.r() * factor, 0, 255),
                (int) Mth.clamp(this.g() * factor, 0, 255),
                (int) Mth.clamp(this.b() * factor, 0, 255)
        );
    }

    /**
     * Blends this colour towards another at a given factor, giving a smooth transition.
     *
     * @param to    Colour to blend towards
     * @param delta Factor capped at the range [0, 1] to lerp at
     * @return Lerped colour
     */
    @Contract(pure = true)
    public Colour lerp(Colour to, float delta) {
        if (delta <= 0F) return this;
        else if (delta >= 1F) return to;
        else return fromARGB(
                    Mth.lerpInt(delta, this.a(), to.a()),
                    Mth.lerpInt(delta, this.r(), to.r()),
                    Mth.lerpInt(delta, this.g(), to.g()),
                    Mth.lerpInt(delta, this.b(), to.b())
            );
    }

    /**
     * Returns this colour regardless of progress.
     *
     * @param progress Ignored.
     * @return This colour.
     */
    @Override
    @Contract(value = "_ -> this", pure = true)
    public Colour sample(float progress) {
        return this;
    }

    /**
     * Returns this colour
     *
     * @param start Ignored
     * @param end   Ignored
     * @return This colour.
     */
    @Override
    @Contract(value = "_, _ -> this", pure = true)
    public Gradient slice(float start, float end) {
        return this;
    }


    /**
     * Returns this colour
     *
     * @return This colour.
     */
    @Override
    @Contract(value = "-> this", pure = true)
    public Gradient reversed() {
        return this;
    }

    /**
     * Returns this colour
     * @param edgeMargin Ignored
     * @return This colour.
     */
    @Override
    @Contract(value = "_ -> this", pure = true)
    public Gradient squish(float edgeMargin) {
        return this;
    }

    @Override
    @ApiStatus.Internal
    public NavigableMap<Float, Colour> getPoints() {
        var map = GradientImpl.newPointMap();
        map.put(GradientBuilder.START, this);
        map.put(GradientBuilder.END, this);
        return map;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Colour) obj;
        return this.integer == that.integer;
    }

    @Override
    public int hashCode() {
        return Objects.hash(integer);
    }

    @Override
    public String toString() {
        return "Colour[0x%2X_%6X]".formatted(a(), integer & 0xFFFFFF);
    }
}
