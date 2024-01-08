package red.jackf.jackfredlib.testmod.client.colour;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import red.jackf.jackfredlib.api.colour.Colour;
import red.jackf.jackfredlib.api.colour.Colours;
import red.jackf.jackfredlib.api.colour.Gradient;
import red.jackf.jackfredlib.client.api.colour.GradientUtils;

public class ClosestColourScreen extends Screen {
    private DyeColor current = DyeColor.WHITE;
    private Colour currentColour = Colours.WHITE;

    public ClosestColourScreen() {
        super(Component.literal("Closest Colour Screen"));
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(poseStack);

        GradientUtils.drawHorizontalGradient(poseStack, 3, 0, 300, 3, Gradient.of(Colours.BLACK, Colours.RED), 0f, 1f);
        GradientUtils.drawVerticalGradient(poseStack, 0, 3, 3, 300, Gradient.of(Colours.BLACK, Colours.GREEN), 0f, 1f);

        GradientUtils.drawHorizontalGradient(poseStack, 3, 3, 300, 300, currentColour, 0f, 1f);

        GradientUtils.drawHorizontalGradient(poseStack, 3, 340, 300, 60, Gradient.of(Colours.BLACK, Colour.fromRGB(0, 0, 255)), 0f, 1f);

        if (3 <= mouseX && mouseX < 303) {
            if (mouseY >= 3 && mouseY <= 303) { //r, g
                var newRed = (float) (mouseX - 3) / 300;
                var newGreen = (float) (mouseY - 3) / 300;

                this.currentColour = Colour.fromRGB((int) (255 * newRed), (int) (255 * newGreen), currentColour.b());
            } else if (mouseY >= 340 && mouseY < 400) {
                var newBlue = (float) (mouseX - 3) / 300;

                this.currentColour = Colour.fromRGB(currentColour.r(), currentColour.g(), (int) (255 * newBlue));
            }

            this.current = this.currentColour.closestDyeColour();
        }

        this.font.draw(poseStack, "Current: %06x".formatted(this.currentColour.toARGB() & 0xFFFFFF), 320, 50, 0xFFFFFFFF);
        this.font.draw(poseStack, "Current: %02d, %02d, %02d".formatted(this.currentColour.r(), this.currentColour.g(), this.currentColour.b()), 320, 50, 0xFFFFFFFF);
        this.itemRenderer.renderGuiItem(poseStack, DyeItem.byColor(current).getDefaultInstance(), 320, 20);
    }
}
