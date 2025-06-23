package red.jackf.jackfredlib.client.api.colour;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.RenderPipelines;
import org.joml.Matrix3x2f;
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
        graphics.guiRenderState.submitGuiElement(new GradientRenderState(
                RenderPipelines.GUI, TextureSetup.noTexture(), new Matrix3x2f(graphics.pose()), x, y, x + width, y + height, gradient, gradientStart, gradientEnd, false, graphics.scissorStack.peek()
        ));
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
        graphics.guiRenderState.submitGuiElement(new GradientRenderState(
                RenderPipelines.GUI, TextureSetup.noTexture(), new Matrix3x2f(graphics.pose()), x, y, x + width, y + height, gradient, gradientStart, gradientEnd, true, graphics.scissorStack.peek()
        ));
    }
}
