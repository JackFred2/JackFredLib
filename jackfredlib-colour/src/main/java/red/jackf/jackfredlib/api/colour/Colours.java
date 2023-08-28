package red.jackf.jackfredlib.api.colour;

import net.minecraft.world.item.DyeColor;

/**
 * Collection of common colour values.
 */
@SuppressWarnings("unused")
public class Colours {
    /**
     * The colour red.
     */
    public static final Colour RED = Colour.fromRGB(255, 0, 0);
    /**
     * The colour orange.
     */
    public static final Colour ORANGE = Colour.fromRGB(255, 106, 0);
    /**
     * The colour yellow.
     */
    public static final Colour YELLOW = Colour.fromRGB(255, 216, 0);
    /**
     * The colour lime.
     */
    public static final Colour LIME = Colour.fromRGB(182, 255, 0);
    /**
     * The colour green.
     */
    public static final Colour GREEN = Colour.fromRGB(0, 255, 0);
    /**
     * The colour dark green.
     */
    public static final Colour DARK_GREEN = Colour.fromRGB(0, 153, 0);
    /**
     * The colour aquamarine.
     */
    public static final Colour AQUAMARINE = Colour.fromRGB(0, 255, 144);
    /**
     * The colour cyan.
     */
    public static final Colour CYAN = Colour.fromRGB(0, 255, 255);
    /**
     * The colour light blue.
     */
    public static final Colour LIGHT_BLUE = Colour.fromRGB(0, 148, 255);
    /**
     * The colour blue.
     */
    public static final Colour BLUE = Colour.fromRGB(0, 38, 255);
    /**
     * The colour purple.
     */
    public static final Colour PURPLE = Colour.fromRGB(170, 0, 255);
    /**
     * The colour magenta.
     */
    public static final Colour MAGENTA = Colour.fromRGB(255, 0, 255);
    /**
     * The colour pink.
     */
    public static final Colour PINK = Colour.fromRGB(255, 127, 237);
    /**
     * The colour hot pink.
     */
    public static final Colour HOT_PINK = Colour.fromRGB(255, 0, 110);
    /**
     * The colour white.
     */
    public static final Colour WHITE = Colour.fromRGB(255, 255, 255);
    /**
     * The colour light gray.
     */
    public static final Colour LIGHT_GRAY = WHITE.scaleBrightness(0.75f);
    /**
     * The colour gray.
     */
    public static final Colour GRAY = WHITE.scaleBrightness(0.5f);
    /**
     * The colour dark gray.
     */
    public static final Colour DARK_GRAY = WHITE.scaleBrightness(0.25f);
    /**
     * The colour black.
     */
    public static final Colour BLACK = Colour.fromRGB(0, 0, 0);

    /**
     * Set of colours matching the colours for Minecraft's {@link DyeColor}s, using their text colour value.
     */
    public static class TextColours {
        private static Colour make(DyeColor dye) {
            return Colour.fromInt(dye.getTextColor());
        }

        /**
         * Equal to Minecraft's red dye text colour.
         */
        public static final Colour RED = make(DyeColor.RED);
        /**
         * Equal to Minecraft's orange dye text colour.
         */
        public static final Colour ORANGE = make(DyeColor.ORANGE);
        /**
         * Equal to Minecraft's yellow dye text colour.
         */
        public static final Colour YELLOW = make(DyeColor.YELLOW);
        /**
         * Equal to Minecraft's lime dye text colour.
         */
        public static final Colour LIME = make(DyeColor.LIME);
        /**
         * Equal to Minecraft's green dye text colour.
         */
        public static final Colour GREEN = make(DyeColor.GREEN);
        /**
         * Equal to Minecraft's cyan dye text colour.
         */
        public static final Colour CYAN = make(DyeColor.CYAN);
        /**
         * Equal to Minecraft's light blue dye text colour.
         */
        public static final Colour LIGHT_BLUE = make(DyeColor.LIGHT_BLUE);
        /**
         * Equal to Minecraft's blue dye text colour.
         */
        public static final Colour BLUE = make(DyeColor.BLUE);
        /**
         * Equal to Minecraft's purple dye text colour.
         */
        public static final Colour PURPLE = make(DyeColor.PURPLE);
        /**
         * Equal to Minecraft's magenta dye text colour.
         */
        public static final Colour MAGENTA = make(DyeColor.MAGENTA);
        /**
         * Equal to Minecraft's pink dye text colour.
         */
        public static final Colour PINK = make(DyeColor.PINK);
        /**
         * Equal to Minecraft's brown dye text colour.
         */
        public static final Colour BROWN = make(DyeColor.BROWN);
        /**
         * Equal to Minecraft's white dye text colour.
         */
        public static final Colour WHITE = make(DyeColor.WHITE);
        /**
         * Equal to Minecraft's light gray dye text colour.
         */
        public static final Colour LIGHT_GRAY = make(DyeColor.LIGHT_GRAY);
        /**
         * Equal to Minecraft's gray dye text colour.
         */
        public static final Colour GRAY = make(DyeColor.GRAY);
        /**
         * Equal to Minecraft's black dye text colour.
         */
        public static final Colour BLACK = make(DyeColor.BLACK);
    }
}
