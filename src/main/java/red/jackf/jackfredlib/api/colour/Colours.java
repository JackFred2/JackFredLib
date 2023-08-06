package red.jackf.jackfredlib.api.colour;

import net.minecraft.world.item.DyeColor;

/**
 * Collection of common colour values.
 */
@SuppressWarnings("unused")
public class Colours {
    public static final Colour RED = Colour.fromRGB(255, 0, 0);
    public static final Colour ORANGE = Colour.fromRGB(255, 106, 0);
    public static final Colour YELLOW = Colour.fromRGB(255, 216, 0);
    public static final Colour LIME = Colour.fromRGB(182, 255, 0);
    public static final Colour GREEN = Colour.fromRGB(0, 255, 0);
    public static final Colour DARK_GREEN = Colour.fromRGB(0, 153, 0);
    public static final Colour AQUAMARINE = Colour.fromRGB(0, 255, 144);
    public static final Colour CYAN = Colour.fromRGB(0, 255, 255);
    public static final Colour LIGHT_BLUE = Colour.fromRGB(0, 148, 255);
    public static final Colour BLUE = Colour.fromRGB(0, 38, 255);
    public static final Colour PURPLE = Colour.fromRGB(170, 0, 255);
    public static final Colour MAGENTA = Colour.fromRGB(255, 0, 255);
    public static final Colour PINK = Colour.fromRGB(255, 127, 237);
    public static final Colour HOT_PINK = Colour.fromRGB(255, 0, 110);
    public static final Colour WHITE = Colour.fromRGB(255, 255, 255);
    public static final Colour LIGHT_GRAY = WHITE.scaleBrightness(0.75f);
    public static final Colour GRAY = WHITE.scaleBrightness(0.5f);
    public static final Colour DARK_GRAY = WHITE.scaleBrightness(0.25f);
    public static final Colour BLACK = Colour.fromRGB(0, 0, 0);

    /**
     * Set of colours matching the colours for Minecraft's {@link DyeColor}s, using their text colour value.
     */
    public static class TextColours {
        private static Colour make(DyeColor dye) {
            return Colour.fromInt(dye.getTextColor());
        }

        public static final Colour RED = make(DyeColor.RED);
        public static final Colour ORANGE = make(DyeColor.ORANGE);
        public static final Colour YELLOW = make(DyeColor.YELLOW);
        public static final Colour LIME = make(DyeColor.LIME);
        public static final Colour GREEN = make(DyeColor.GREEN);
        public static final Colour CYAN = make(DyeColor.CYAN);
        public static final Colour LIGHT_BLUE = make(DyeColor.LIGHT_BLUE);
        public static final Colour BLUE = make(DyeColor.BLUE);
        public static final Colour PURPLE = make(DyeColor.PURPLE);
        public static final Colour MAGENTA = make(DyeColor.MAGENTA);
        public static final Colour PINK = make(DyeColor.PINK);
        public static final Colour BROWN = make(DyeColor.BROWN);
        public static final Colour WHITE = make(DyeColor.WHITE);
        public static final Colour LIGHT_GRAY = make(DyeColor.LIGHT_GRAY);
        public static final Colour GRAY = make(DyeColor.GRAY);
        public static final Colour BLACK = make(DyeColor.BLACK);
    }
}
