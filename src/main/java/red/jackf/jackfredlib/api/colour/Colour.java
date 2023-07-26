package red.jackf.jackfredlib.api.colour;

import com.mojang.serialization.Codec;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import red.jackf.jackfredlib.impl.colour.ColourImpl;

/**
 * <p>Represents a singular colour value. Provides helper methods for lerping, HSV and RGB operations.</p>
 *
 * <p>Colours implement {@link Gradient} as a single-colour gradient.</p>
 */
public interface Colour extends Gradient {
    /**
     * Codec for serializing a solid colour. Use this if you are directly using colours.
     */
    Codec<Colour> CODEC = Codec.INT.xmap(Colour::fromInt, Colour::toARGB);

    /**
     * <p>Create a colour from given alpha, red, green and blue colour components.</p>
     *
     * @param a Alpha component of the colour
     * @param r Red component of the colour
     * @param g Green component of the colour
     * @param b Blue component of the colour
     * @return Built Colour instance
     */
    static Colour fromARGB(int a, int r, int g, int b) {
        return new ColourImpl(FastColor.ARGB32.color(a, r, g, b));
    }

    /**
     * <p>Create a colour from given red, green and blue colour components, with an alpha value of 255 making it opaque.</p>
     *
     * @param r Red component of the colour
     * @param g Green component of the colour
     * @param b Blue component of the colour
     * @return Built Colour instance, fully opaque
     */
    static Colour fromRGB(int r, int g, int b) {
        return fromARGB(255, r, g, b);
    }

    /**
     * <p>Create a colour from a given alpha, hue, saturation and value.</p>
     *
     * <p>For more information, see <a href="https://en.wikipedia.org/wiki/HSL_and_HSV">the Wikipedia page on HSV</a>.</p>
     *
     * @param h Hue of the colour
     * @param s Saturation of the colour
     * @param v Value of the colour
     * @return Built Colour instance
     */
    static Colour fromAHSV(int a, float h, float s, float v) {
        return new ColourImpl(a << 24 | Mth.hsvToRgb(h, s, v));
    }

    /**
     * <p>Create a colour from a given hue, saturation and value, with an alpha value of 255 making it opaque.</p>
     *
     * <p>For more information, see <a href="https://en.wikipedia.org/wiki/HSL_and_HSV">the Wikipedia page on HSV</a>.</p>
     *
     * @param h Hue of the colour
     * @param s Saturation of the colour
     * @param v Value of the colour
     * @return Built Colour instance, fully opaque
     */
    static Colour fromHSV(float h, float s, float v) {
        return fromAHSV(255, h, s, v);
    }

    /**
     * Create a colour from an existing colour integer, in the format ARGB.
     * @param argb Integer representing a colour, in the format ARGB
     * @return Built Colour instance
     */
    static Colour fromInt(int argb) {
        return new ColourImpl(argb);
    }

    /**
     * Returns the ARGB representation of this colour as an integer
     *
     * @return Integer representation of this colour, in ARGB format.
     */
    int toARGB();

    /**
     * Get the alpha (transparency) component of this colour in the range [0, 255], where 0 is transparent and 255 is
     * opaque.
     *
     * @return Alpha component of this colour.
     */
    int a();

    /**
     * Get the red component of this colour in the range [0, 255].
     *
     * @return Red component of this colour.
     */
    int r();

    /**
     * Get the green component of this colour in the range [0, 255].
     *
     * @return Green component of this colour.
     */
    int g();

    /**
     * Get the blue component of this colour in the range [0, 255].
     *
     * @return Blue component of this colour.
     */
    int b();

    /**
     * <p>Returns the hue of this colour, in the range [0, 1), calculating and caching the result on first run.</p>
     *
     * <p>For more information, see <a href="https://en.wikipedia.org/wiki/HSL_and_HSV">the Wikipedia page on HSV</a>.</p>
     *
     * @return The hue component of this colour.
     */
    float hue();

    /**
     * <p>Returns the saturation of this colour in the range [0, 1], where 0 is grayscale and 1 is full colour brightness.</p>
     *
     * <p>For more information, see <a href="https://en.wikipedia.org/wiki/HSL_and_HSV">the Wikipedia page on HSV</a>.</p>
     *
     * @return The saturation component of this colour.
     */
    float saturation();

    /**
     * <p>Returns the 'value' of this colour in the range [0, 1], where 0 is pure black and 1 is full colour brightness.</p>
     *
     * <p>For more information, see <a href="https://en.wikipedia.org/wiki/HSL_and_HSV">the Wikipedia page on HSV</a>.</p>
     *
     * @return The value component of this colour.
     */
    float value();

    /**
     * Scales the brightness of this colour by a certain factor by multiplying each colour component.
     *
     * @param factor Factor to scale brightness by; i.e. 0.5 to darken 50%, 2.0 to brighten 200%
     * @return Brightness-adjusted colour
     */
    Colour scaleBrightness(float factor);

    /**
     * Blends this colour towards another at a given factor, giving a smooth transition.
     *
     * @param to    Colour to blend towards
     * @param delta Factor capped at the range [0, 1] to lerp at
     * @return Lerped colour
     */
    Colour lerp(Colour to, float delta);
}
