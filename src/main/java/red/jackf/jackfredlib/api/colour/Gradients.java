package red.jackf.jackfredlib.api.colour;

/**
 * Collection of common gradients to be used in mods
 */
public class Gradients {
    public static Gradient RAINBOW = Gradient.linear(Colours.RED, Colours.RED, Gradient.LinearMode.HSV_LONG);

    // Pride Flag Colours
    public static Gradient GAY = Gradient.builder()
            .add(0f, Colour.fromARGB(255, 63, 137, 133))
            .add(0.5f, Colours.WHITE)
            .add(GradientBuilder.END, Colour.fromARGB(255, 57, 30, 116))
            .build();
    public static Gradient LESBIAN = Gradient.builder()
            .add(0f, Colour.fromARGB(255, 150, 41, 95))
            .add(0.5f, Colours.WHITE)
            .add(GradientBuilder.END, Colour.fromARGB(255, 127, 43, 20))
            .build();
    public static Gradient BISEXUAL = Gradient.builder()
            .addBlock(0f, 0.35f, Colour.fromARGB(255, 196, 53, 111))
            .add(0.5f, Colours.WHITE)
            .addBlock(0.65f, GradientBuilder.END, Colour.fromARGB(255, 24, 56, 163))
            .build();
}
