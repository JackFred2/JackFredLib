package red.jackf.jackfredlib.api.colour;

/**
 * Collection of common gradients to be used in mods
 */
public class Gradients {
    /**
     * Rainbow gradient. Seamless loop.
     */
    public static final Gradient RAINBOW = Gradient.linear(Colours.RED, Colours.RED, Gradient.LinearMode.HSV_LONG);

    // Pride Flag Colours
    /**
     * Gay pride flag gradient.
     */
    public static final Gradient GAY = Gradient.builder()
            .add(0f, Colour.fromRGB(63, 137, 133))
            .add(0.4f, Colour.fromRGB(169, 228, 195))
            .add(0.5f, Colours.WHITE)
            .add(0.6f, Colour.fromRGB(113, 171, 223))
            .add(GradientBuilder.END, Colour.fromRGB(57, 30, 116))
            .build();

    /**
     * Lesbian pride flag gradient.
     */
    public static final Gradient LESBIAN = Gradient.builder()
            .add(0f, Colour.fromRGB(213, 45, 0))
            .add(0.4f, Colour.fromRGB(255, 154, 86))
            .add(0.5f, Colour.fromRGB(255, 255, 255))
            .add(0.6f, Colour.fromRGB(209, 98, 164))
            .add(GradientBuilder.END, Colour.fromRGB(163, 2, 98))
            .build();

    /**
     * Bisexual pride flag gradient.
     */
    public static final Gradient BISEXUAL = Gradient.of(
            Colour.fromRGB(208, 0, 112),
            Colour.fromRGB(140, 71, 153),
            Colour.fromRGB(0, 50, 160));

    /**
     * Pansexual pride flag gradient.
     */
    public static final Gradient PANSEXUAL = Gradient.builder()
            .add(0f, Colour.fromRGB(234, 80, 139))
            .addBlock(0.45f, 0.55f, Colour.fromRGB(248, 217, 63))
            .add(GradientBuilder.END, Colour.fromRGB(83, 173, 250))
            .build();

    /**
     * Intersex pride flag gradient, with a sharp transition at the center.
     */
    public static final Gradient INTERSEX_SHARP = Gradient.builder()
            .addCut(0.45f, Colours.YELLOW, Colours.PURPLE)
            .addCut(0.55f, Colours.PURPLE, Colours.YELLOW)
            .build();

    /**
     * Intersex pride flag gradient, with a smooth transition at the center.
     */
    public static final Gradient INTERSEX_SMOOTH = Gradient.builder()
            .add(0.425f, Colours.YELLOW)
            .addBlock(0.475f, 0.525f, Colours.PURPLE)
            .add(0.575f, Colours.YELLOW)
            .build();

    /**
     * Non-binary pride flag gradient.
     */
    public static final Gradient NONBINARY = Gradient.of(
            Colour.fromRGB(253, 243, 82),
            Colours.WHITE,
            Colour.fromRGB(146, 94, 203),
            Colour.fromRGB(45, 45, 45));

    /**
     * Transgender pride flag gradient.
     */
    public static final Gradient TRANS = Gradient.of(
            Colour.fromRGB(120, 202, 246),
            Colour.fromRGB(234, 173, 184),
            Colours.WHITE,
            Colour.fromRGB(234, 173, 184),
            Colour.fromRGB(120, 202, 246));

    /**
     * Asexual pride flag gradient.
     */
    public static final Gradient ACE = Gradient.of(
            Colours.BLACK,
            Colour.fromRGB(162, 162, 162),
            Colours.WHITE,
            Colour.fromRGB(117, 33, 125));

    /**
     * Aromantic pride flag gradient.
     */
    public static final Gradient ARO = Gradient.of(
            Colour.fromRGB(90, 161, 74),
            Colour.fromRGB(175, 208, 127),
            Colours.WHITE,
            Colour.fromRGB(169, 169, 169),
            Colours.BLACK);
}
