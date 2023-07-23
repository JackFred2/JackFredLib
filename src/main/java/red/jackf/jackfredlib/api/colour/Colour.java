package red.jackf.jackfredlib.api.colour;

import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * Represents a single colour value. Implements {@link Gradient} as a single-coloured gradient
 * @param value Integer representation of a colour, in ARGB format.
 */
public record Colour(int value) implements Gradient {
    /**
     * Constructs a new colour from the given components
     * @param a Alpha value of the new colour, in the range [0, 255]
     * @param r Red value of the new colour, in the range [0, 255]
     * @param g Green value of the new colour, in the range [0, 255]
     * @param b Blue value of the new colour, in the range [0, 255]
     * @return Constructed colour from given components
     */
    @Contract(pure = true)
    public static Colour fromARGB(int a, int r, int g, int b) {
        return new Colour(FastColor.ARGB32.color(a, r, g, b));
    }

    /**
     * Get the alpha component of this colour.
     * @return Alpha component of this colour.
     */
    @Contract(pure = true)
    public int a() {
        return FastColor.ARGB32.alpha(value);
    }

    /**
     * Get the red component of this colour.
     * @return Red component of this colour.
     */
    @Contract(pure = true)
    public int r() {
        return FastColor.ARGB32.red(value);
    }

    /**
     * Get the green component of this colour.
     * @return Green component of this colour.
     */
    @Contract(pure = true)
    public int g() {
        return FastColor.ARGB32.green(value);
    }

    /**
     * Get the blue component of this colour.
     * @return Blue component of this colour.
     */
    @Contract(pure = true)
    public int b() {
        return FastColor.ARGB32.blue(value);
    }

    /**
     * Scales the brightness of this colour by a certain factor by multiplying each colour component.
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
     * @param to Colour to blend towards
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
     * @param start Ignored
     * @param end Ignored
     * @return This colour.
     */
    @Override
    @Contract(value = "_, _ -> this", pure = true)
    public Gradient slice(float start, float end) {
        return this;
    }


    /**
     * Returns this colour
     * @return This colour.
     */
    @Override
    @Contract(value = "-> this", pure = true)
    public Gradient reversed() {
        return this;
    }

    @Override
    @ApiStatus.Internal
    public NavigableMap<Float, Colour> getPoints() {
        var map = new TreeMap<Float, Colour>();
        map.put(Builder.START, this);
        map.put(Builder.END, this);
        return map;
    }
}
