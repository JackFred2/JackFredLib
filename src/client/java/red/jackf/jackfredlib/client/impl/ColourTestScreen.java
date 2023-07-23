package red.jackf.jackfredlib.client.impl;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import red.jackf.jackfredlib.api.colour.Colours;
import red.jackf.jackfredlib.api.colour.Gradient;

public class ColourTestScreen extends Screen {
    private static final Gradient RAINBOW_TEST = Gradient.of(
            Colours.Standard.RED,
            Colours.Standard.ORANGE,
            Colours.Standard.YELLOW,
            Colours.Standard.GREEN,
            Colours.Standard.BLUE,
            Colours.Standard.PURPLE
    );
    private static final Gradient EDGE_TRANSITION_TEST = Gradient.builder()
            .addBlock(0.1f, 0.4f, Colours.Standard.RED)
            .addBlock(0.6f, 0.8f, Colours.Standard.GREEN)
            .build();
    private static final Gradient CUT_TEST = Gradient.builder()
            .addCut(0.25f, Colours.Standard.RED, Colours.Standard.GREEN)
            .addCut(0.75f, Colours.Standard.GREEN, Colours.Standard.RED)
            .build();
    private static final Gradient MERGE_GRADIENT_TEST = Gradient.builder()
            .addBlock(0.9f, 0.1f, Colours.Standard.RED)
            .addBlock(0.25f, 0.45f, RAINBOW_TEST)
            .addBlock(0.55f, 0.75f, RAINBOW_TEST.reversed())
            .build();

    protected ColourTestScreen() {
        super(Component.literal("JackFredLib Test Screen"));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        drawGradient(guiGraphics, 10, 10, 200, 25, RAINBOW_TEST);
        drawGradient(guiGraphics, 10, 40, 200, 25, EDGE_TRANSITION_TEST);
        drawGradient(guiGraphics, 10, 70, 200, 25, CUT_TEST);
        drawGradient(guiGraphics, 10, 100, 400, 25, MERGE_GRADIENT_TEST);
    }

    private void drawGradient(GuiGraphics graphics, int x, int y, int width, int height, Gradient gradient) {
        for (int i = 0; i < width; i++) {
            graphics.fill(x + i, y, x + i + 1, y + height, gradient.sample((float) i / width).value());
        }
    }
}
