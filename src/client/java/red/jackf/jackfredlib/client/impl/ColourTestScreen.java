package red.jackf.jackfredlib.client.impl;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import red.jackf.jackfredlib.api.colour.Colours;
import red.jackf.jackfredlib.api.colour.Gradient;
import red.jackf.jackfredlib.api.colour.Gradients;

@SuppressWarnings("SameParameterValue")
public class ColourTestScreen extends Screen {
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

    protected ColourTestScreen() {
        super(Component.literal("JackFredLib Test Screen"));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        drawGradient(graphics, 10, 40, 405, 20, mouseX, Gradients.RAINBOW);

        drawGradients(graphics, 10, 65, 200, 10, mouseX,
                Gradients.GAY,
                Gradients.LESBIAN,
                Gradients.BISEXUAL,
                Gradients.PANSEXUAL,
                Gradients.INTERSEX,
                Gradients.NONBINARY,
                Gradients.TRANS,
                Gradients.ACE,
                Gradients.ARO);

        drawGradients(graphics, 215, 65, 100, 10, mouseX,
                EDGE_TRANSITION_TEST,
                CUT_TEST,
                MERGE_REVERSE_TEST,
                HSV_SHORT_TEST,
                HSV_SHORT_WRAPPING_TEST,
                SQUISH_TEST,
                HSV_SHORT_REVERSE_TEST,
                HSV_FULL_SPECTRUM_TEST,
                HSV_LONG_WRAP_TEST);
    }

    private void drawGradients(GuiGraphics graphics, int x, int startY, int width, int height, int mouseX, Gradient... gradients) {
        for (int i = 0; i < gradients.length; i++)
            drawGradient(graphics, x, startY + (height + 5) * i, width, height, mouseX, gradients[i]);
    }

    private void drawGradient(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, Gradient gradient) {
        for (int i = 0; i < width; i++) {
            graphics.fill(x + i, y, x + i + 1, y + height, gradient.sample((float) (i - mouseX / 3) / width).toARGB());
        }
    }
}
