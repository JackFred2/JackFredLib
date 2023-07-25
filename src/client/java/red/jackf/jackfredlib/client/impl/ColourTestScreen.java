package red.jackf.jackfredlib.client.impl;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import red.jackf.jackfredlib.api.colour.Colours;
import red.jackf.jackfredlib.api.colour.Gradient;
import red.jackf.jackfredlib.api.colour.Gradients;

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
    private static final Gradient MERGE_GRADIENT_TEST = Gradient.builder()
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
    private static final Gradient HSV_SHORT_REVERSE_TEST = Gradient.linear(
            Colours.CYAN,
            Colours.RED,
            Gradient.LinearMode.HSV_SHORT);
    private static final Gradient HSV_LONG_WRAP_TEST = Gradient.linear(
            Colours.ORANGE,
            Colours.PURPLE,
            Gradient.LinearMode.HSV_LONG);
    private static final Gradient HSV_FULL_SPECTRUM_TEST = Gradient.linear(
            Colours.RED,
            Colours.RED,
            Gradient.LinearMode.HSV_LONG);

    protected ColourTestScreen() {
        super(Component.literal("JackFredLib Test Screen"));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        drawGradient(guiGraphics, 10, 10, 405, 20, Gradients.RAINBOW, mouseX);
        drawGradient(guiGraphics, 10, 35, 200, 10, EDGE_TRANSITION_TEST, mouseX);
        drawGradient(guiGraphics, 10, 50, 200, 10, CUT_TEST, mouseX);
        drawGradient(guiGraphics, 10, 65, 200, 10, MERGE_GRADIENT_TEST, mouseX);
        drawGradient(guiGraphics, 10, 80, 200, 10, HSV_SHORT_TEST, mouseX);
        drawGradient(guiGraphics, 10, 95, 200, 10, HSV_SHORT_WRAPPING_TEST, mouseX);
        drawGradient(guiGraphics, 10, 110, 200, 10, HSV_SHORT_REVERSE_TEST, mouseX);
        drawGradient(guiGraphics, 10, 125, 200, 10, HSV_LONG_WRAP_TEST, mouseX);
        drawGradient(guiGraphics, 10, 140, 200, 10, HSV_FULL_SPECTRUM_TEST, mouseX);

        drawGradient(guiGraphics, 215, 35, 200, 10, Gradients.GAY, mouseX);
        drawGradient(guiGraphics, 215, 50, 200, 10, Gradients.LESBIAN, mouseX);
        drawGradient(guiGraphics, 215, 65, 200, 10, Gradients.BISEXUAL, mouseX);
        drawGradient(guiGraphics, 215, 80, 200, 10, Gradients.PANSEXUAL, mouseX);
        drawGradient(guiGraphics, 215, 95, 200, 10, Gradients.INTERSEX, mouseX);
        drawGradient(guiGraphics, 215, 110, 200, 10, Gradients.NONBINARY, mouseX);
        drawGradient(guiGraphics, 215, 125, 200, 10, Gradients.TRANS, mouseX);
        drawGradient(guiGraphics, 215, 140, 200, 10, Gradients.ACE, mouseX);
        drawGradient(guiGraphics, 215, 155, 200, 10, Gradients.ARO, mouseX);
    }

    private void drawGradient(GuiGraphics graphics, int x, int y, int width, int height, Gradient gradient, int mouseX) {
        for (int i = 0; i < width; i++) {
            graphics.fill(x + i, y, x + i + 1, y + height, gradient.sample((float) (i - mouseX / 3) / width).integer());
        }
    }
}
