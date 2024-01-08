package red.jackf.jackfredlib.testmod.client.colour;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import red.jackf.jackfredlib.api.colour.Colours;
import red.jackf.jackfredlib.api.colour.Gradient;
import red.jackf.jackfredlib.api.colour.GradientBuilder;
import red.jackf.jackfredlib.api.colour.Gradients;
import red.jackf.jackfredlib.client.api.colour.GradientUtils;

@SuppressWarnings("SameParameterValue")
public class ColourTestScreen extends Screen {
    private long renderCount = 0;
    private static final Gradient RAINBOW_TEST = Gradient.of(
            Colours.RED,
            Colours.ORANGE,
            Colours.YELLOW,
            Colours.GREEN,
            Colours.BLUE,
            Colours.PURPLE,
            Colours.RED
    );
    private static final Gradient EDGE_TRANSITION_TEST = Gradient.builder()
            .addBlock(0.1f, 0.4f, Colours.RED)
            .addBlock(0.6f, 0.8f, Colours.GREEN)
            .build();
    private static final Gradient CUT_TEST = Gradient.builder()
            .addCut(0.25f, Colours.RED, Colours.GREEN)
            .addCut(0.75f, Colours.GREEN, Colours.RED)
            .build();
    private static final Gradient MERGE_REVERSE_TEST = Gradient.builder()
            .addBlock(0.9f, 0.1f, Colours.RED)
            .addBlock(0.25f, 0.45f, RAINBOW_TEST)
            .addBlock(0.55f, 0.75f, RAINBOW_TEST.reversed())
            .build();
    private static final Gradient HSV_SHORT_TEST = Gradient.linear(
            Colours.RED,
            Colours.AQUAMARINE,
            Gradient.LinearMode.HSV_SHORT);
    private static final Gradient HSV_SHORT_WRAPPING_TEST = Gradient.linear(
            Colours.ORANGE,
            Colours.PURPLE,
            Gradient.LinearMode.HSV_SHORT);
    private static final Gradient SQUISH_TEST = HSV_SHORT_WRAPPING_TEST.squish(0.1f);
    private static final Gradient HSV_SHORT_REVERSE_TEST = Gradient.linear(
            Colours.CYAN,
            Colours.RED,
            Gradient.LinearMode.HSV_SHORT);
    private static final Gradient HSV_FULL_SPECTRUM_TEST = Gradient.linear(
            Colours.PURPLE,
            Colours.PURPLE,
            Gradient.LinearMode.HSV_LONG);
    private static final Gradient HSV_LONG_WRAP_TEST = Gradient.linear(
            Colours.ORANGE,
            Colours.PURPLE,
            Gradient.LinearMode.HSV_LONG);

    private static final Gradient JITTER = Gradient.builder()
            .add(0, Colours.BLACK)
            .addCut(0.5f, Colours.BLACK, Colours.WHITE)
            .add(GradientBuilder.END, Colours.WHITE)
            .build()
            .repeat(50);

    public ColourTestScreen() {
        super(Component.literal("JackFredLib Test Screen"));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        GradientUtils.drawHorizontalGradient(graphics, 10, 40, 410, 20, Gradients.RAINBOW, 0f, 1f);

        drawGradients(graphics, 10, 65, 200, 10,
                Gradients.GAY,
                Gradients.LESBIAN,
                Gradients.BISEXUAL,
                Gradients.PANSEXUAL,
                Gradients.INTERSEX_SHARP,
                Gradients.INTERSEX_SMOOTH,
                Gradients.NONBINARY,
                Gradients.TRANS,
                Gradients.ACE,
                Gradients.ARO);

        drawGradients(graphics, 215, 65, 100, 10,
                EDGE_TRANSITION_TEST,
                CUT_TEST,
                MERGE_REVERSE_TEST,
                HSV_SHORT_TEST,
                HSV_SHORT_WRAPPING_TEST,
                SQUISH_TEST,
                HSV_SHORT_REVERSE_TEST,
                HSV_FULL_SPECTRUM_TEST,
                HSV_LONG_WRAP_TEST);

        drawGradients(graphics, 320, 65, 100, 10, Gradients.INTERSEX_SHARP.repeat(20), JITTER);

        GradientUtils.drawHorizontalGradient(graphics, 320, 95, 100, 10, Gradients.ACE, 0f, 3f);
        GradientUtils.drawHorizontalGradient(graphics, 320, 110, 100, 10, Gradients.ACE, 3f, 0f);

        GradientUtils.drawVerticalGradient(graphics, 320, 125, 10, 100, Gradients.ACE, 0f, 3f);
        GradientUtils.drawVerticalGradient(graphics, 335, 125, 10, 100, Gradients.ACE, 3f, 0f);
    }

    private void drawGradients(GuiGraphics graphics, int x, int startY, int width, int height, Gradient... gradients) {
        float offset = (renderCount++ % 2400) / 2400f;
        for (int i = 0; i < gradients.length; i++)
            GradientUtils.drawHorizontalGradient(graphics, x, startY + (height + 5) * i, width, height, gradients[i], 0f - offset, 1f - offset);
    }
}
