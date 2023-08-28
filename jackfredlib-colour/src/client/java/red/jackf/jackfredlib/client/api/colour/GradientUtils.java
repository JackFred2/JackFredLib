package red.jackf.jackfredlib.client.api.colour;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import red.jackf.jackfredlib.api.colour.Colour;
import red.jackf.jackfredlib.api.colour.Gradient;

/**
 * Collection of utilities to help with gradients on the client.
 */
public class GradientUtils {
    private GradientUtils() {}

    /**
     * <p>Draws a horizontal gradient to the screen at the given coordinates.</p>
     * <p>If <code>gradientStart > gradientEnd</code>, then the gradient will be sampled backwards, effectively
     * reversing it.</p>
     * <p>Both <code>gradientStart</code> and <code>gradientEnd</code> can be outside the range [0,1). As it is sampled
     * along the range [0,1), this effectively means that the gradient can be repeated. For example, a start of
     * <code>0.0f</code> and an end of <code>3.0f</code> will draw the gradient looping 3 times.</p>
     * @param graphics {@link GuiGraphics} object to draw the gradient with
     * @param x X coordinate of the top left corner of the gradient to draw
     * @param y Y coordinate of the top left corner of the gradient to draw
     * @param width Width of the gradient to draw
     * @param height Height of the gradient to draw
     * @param gradient Gradient to sample from.
     * @param gradientStart Point of the gradient to start sampling at.
     * @param gradientEnd Point of the gradient to end sampling at.
     */
    public static void drawHorizontalGradient(GuiGraphics graphics, int x, int y, int width, int height, Gradient gradient, float gradientStart, float gradientEnd) {
        if (gradientStart == gradientEnd) graphics.fill(x, y, x + width, y + height, gradient.sample(gradientStart).toARGB());
        gradient = generateRepeatedGradient(gradient, gradientStart, gradientEnd);

        var buffer = graphics.bufferSource().getBuffer(RenderType.gui());
        var pose = graphics.pose().last().pose();

        float lastKey = gradient.getPoints().firstKey();
        for (float secondKey : gradient.getPoints().navigableKeySet()) {
            if (secondKey == lastKey) continue; // don't do last
            float x1 = x + (width * lastKey);
            float x2 = x + (width * secondKey);
            drawHorizontalGradient(buffer, pose, x1, y, x2, y + height, gradient.sample(lastKey), gradient.sample(secondKey));
            lastKey = secondKey;
        }

        graphics.flush();
    }

    /**
     * <p>Draws a vertical gradient to the screen at the given coordinates.</p>
     * <p>If <code>gradientStart > gradientEnd</code>, then the gradient will be sampled backwards, effectively
     * reversing it.</p>
     * <p>Both <code>gradientStart</code> and <code>gradientEnd</code> can be outside the range [0,1). As it is sampled
     * along the range [0,1), this effectively means that the gradient can be repeated. For example, a start of
     * <code>0.0f</code> and an end of <code>3.0f</code> will draw the gradient looping 3 times.</p>
     * @param graphics {@link GuiGraphics} object to draw the gradient with
     * @param x X coordinate of the top left corner of the gradient to draw
     * @param y Y coordinate of the top left corner of the gradient to draw
     * @param width Width of the gradient to draw
     * @param height Height of the gradient to draw
     * @param gradient Gradient to sample from.
     * @param gradientStart Point of the gradient to start sampling at.
     * @param gradientEnd Point of the gradient to end sampling at.
     */
    public static void drawVerticalGradient(GuiGraphics graphics, int x, int y, int width, int height, Gradient gradient, float gradientStart, float gradientEnd) {
        if (gradientStart == gradientEnd) graphics.fill(x, y, x + width, y + height, gradient.sample(gradientStart).toARGB());
        gradient = generateRepeatedGradient(gradient, gradientStart, gradientEnd);

        var buffer = graphics.bufferSource().getBuffer(RenderType.gui());
        var pose = graphics.pose().last().pose();

        float lastKey = gradient.getPoints().firstKey();
        for (float secondKey : gradient.getPoints().navigableKeySet()) {
            if (secondKey == lastKey) continue; // don't do last
            float y1 = y + (height * lastKey);
            float y2 = y + (height * secondKey);
            drawVerticalGradient(buffer, pose, x, y1, x + width, y2, gradient.sample(lastKey), gradient.sample(secondKey));
            lastKey = secondKey;
        }

        graphics.flush();
    }

    private static Gradient generateRepeatedGradient(Gradient gradient, float gradientStart, float gradientEnd) {
        if (gradient instanceof Colour) return gradient;
        final boolean reversed = gradientEnd < gradientStart;
        final float start = reversed ? gradientEnd : gradientStart;
        final float end = reversed ? gradientStart : gradientEnd;

        int copies = Mth.floor(end) - Mth.floor(start) + 1;
        if (copies > 1)
            gradient = gradient.repeat(copies);
        if (reversed)
            gradient = gradient.reversed();
        float sliceStart = Gradient.wrapPoint(start) / copies;
        float sliceEnd = (Gradient.wrapPoint(end) + copies - 1) / copies;
        gradient = gradient.slice(sliceStart, sliceEnd);
        return gradient;
    }

    private static void drawVerticalGradient(VertexConsumer buffer, Matrix4f pose,
                                               float x1, float y1, float x2, float y2,
                                               Colour from, Colour to) {
        int r1 = from.r();
        int g1 = from.g();
        int b1 = from.b();
        int a1 = from.a();
        int r2 = to.r();
        int g2 = to.g();
        int b2 = to.b();
        int a2 = to.a();

        buffer.vertex(pose, x1, y1, 0).color(r1, g1, b1, a1).endVertex();
        buffer.vertex(pose, x1, y2, 0).color(r2, g2, b2, a2).endVertex();
        buffer.vertex(pose, x2, y2, 0).color(r2, g2, b2, a2).endVertex();
        buffer.vertex(pose, x2, y1, 0).color(r1, g1, b1, a1).endVertex();
    }

    private static void drawHorizontalGradient(VertexConsumer buffer, Matrix4f pose,
                                               float x1, float y1, float x2, float y2,
                                               Colour from, Colour to) {
        int r1 = from.r();
        int g1 = from.g();
        int b1 = from.b();
        int a1 = from.a();
        int r2 = to.r();
        int g2 = to.g();
        int b2 = to.b();
        int a2 = to.a();

        buffer.vertex(pose, x1, y1, 0).color(r1, g1, b1, a1).endVertex();
        buffer.vertex(pose, x1, y2, 0).color(r1, g1, b1, a1).endVertex();
        buffer.vertex(pose, x2, y2, 0).color(r2, g2, b2, a2).endVertex();
        buffer.vertex(pose, x2, y1, 0).color(r2, g2, b2, a2).endVertex();
    }
}
