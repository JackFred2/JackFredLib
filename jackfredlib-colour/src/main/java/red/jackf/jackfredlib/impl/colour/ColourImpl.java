package red.jackf.jackfredlib.impl.colour;

import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import red.jackf.jackfredlib.api.colour.Colour;
import red.jackf.jackfredlib.api.colour.Gradient;
import red.jackf.jackfredlib.api.colour.GradientBuilder;

import java.util.NavigableMap;
import java.util.Objects;

/**
 * Implementation of {@link Colour}. Caches HSV values as these are relatively expensive.
 */
public final class ColourImpl implements Colour {
    private final int integer;

    // Memoized HSV components
    private float h = Float.NaN; // hue, range [0, 1)
    private float s = Float.NaN; // saturation, range [0, 1]
    private float v = Float.NaN; // value, range [0, 1]

    public ColourImpl(int integer) {
        this.integer = integer;
    }

    @Override
    public int toARGB() {
        return integer;
    }

    @Override
    public int a() {
        return FastColor.ARGB32.alpha(integer);
    }

    @Override
    public int r() {
        return FastColor.ARGB32.red(integer);
    }

    @Override
    public int g() {
        return FastColor.ARGB32.green(integer);
    }

    @Override
    public int b() {
        return FastColor.ARGB32.blue(integer);
    }

    @Override
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

    @Override
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

    @Override
    public float value() {
        if (Float.isNaN(this.v)) {
            float r = r() / 255f;
            float g = g() / 255f;
            float b = b() / 255f;
            this.v = Math.max(Math.max(r, g), b);
        }

        return this.v;
    }

    @Override
    public Colour scaleBrightness(float factor) {
        return Colour.fromARGB(this.a(),
                (int) Mth.clamp(this.r() * factor, 0, 255),
                (int) Mth.clamp(this.g() * factor, 0, 255),
                (int) Mth.clamp(this.b() * factor, 0, 255)
        );
    }

    @Override
    @Contract(pure = true)
    public Colour lerp(Colour to, float delta) {
        if (delta <= 0F) return this;
        else if (delta >= 1F) return to;
        else return Colour.fromARGB(
                    Mth.lerpInt(delta, this.a(), to.a()),
                    Mth.lerpInt(delta, this.r(), to.r()),
                    Mth.lerpInt(delta, this.g(), to.g()),
                    Mth.lerpInt(delta, this.b(), to.b())
            );
    }

    // Gradient methods

    @Override
    public Colour sample(float progress) {
        return this;
    }

    @Override
    public Gradient slice(float start, float end) {
        return this;
    }

    @Override
    public Gradient reversed() {
        return this;
    }

    @Override
    public Gradient squish(float edgeMargin) {
        return this;
    }

    @Override
    public Gradient repeat(int copies) {
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
        var that = (ColourImpl) obj;
        return this.integer == that.integer;
    }

    @Override
    public int hashCode() {
        return Objects.hash(integer);
    }

    @Override
    public String toString() {
        return "Colour[0x%02X_%06X]".formatted(a(), integer & 0xFFFFFF);
    }
}
