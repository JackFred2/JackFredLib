package red.jackf.jackfredlib.client.api.colour;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;
import red.jackf.jackfredlib.api.colour.Colour;
import red.jackf.jackfredlib.api.colour.Gradient;

public record GradientRenderState(
        RenderPipeline pipeline,
        TextureSetup textureSetup,
        Matrix3x2f pose,
        int x0,
        int y0,
        int x1,
        int y1,
        Gradient gradient,
        float gradientStart,
        float gradientEnd,
        boolean isVertical,
        @Nullable ScreenRectangle scissorArea,
        @Nullable ScreenRectangle bounds
) implements GuiElementRenderState {

    @Nullable
    private static ScreenRectangle getBounds(int i, int j, int k, int l, Matrix3x2f matrix3x2f, @Nullable ScreenRectangle screenRectangle) {
        ScreenRectangle screenRectangle2 = new ScreenRectangle(i, j, k - i, l - j).transformMaxBounds(matrix3x2f);
        return screenRectangle != null ? screenRectangle.intersection(screenRectangle2) : screenRectangle2;
    }

    public GradientRenderState(RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2f pose, int x0, int y0, int x1, int y1, Gradient gradient, float gradientStart, float gradientEnd, boolean isVertical, @Nullable ScreenRectangle scissorArea) {
        this(pipeline, textureSetup, pose, x0, y0, x1, y1, gradient, gradientStart, gradientEnd, isVertical, scissorArea, getBounds(x0, y0, x1, y1, pose, scissorArea));
    }

    @Override
    public void buildVertices(VertexConsumer vertexConsumer, float z) {
        if (gradientStart == gradientEnd) {
            this.drawVerticalRect(vertexConsumer, this.x0, this.y0, this.x1, this.y1, z, gradient.sample(gradientStart), gradient.sample(gradientStart));
            return;
        }

        final Gradient usedGradient = generateRepeatedGradient(gradient, gradientStart, gradientEnd);

        float height = this.y1 - this.y0;
        float width = this.x1 - this.x0;

        if (this.isVertical) {
            float lastKey = usedGradient.getPoints().firstKey();

            for (float secondKey : usedGradient.getPoints().navigableKeySet()) {
                if (secondKey == lastKey) continue; // don't do last
                float y1 = this.y0 + (height * lastKey);
                float y2 = this.y0 + (height * secondKey);

                this.drawVerticalRect(vertexConsumer, this.x0, y1, this.x1, y2, z, usedGradient.sample(lastKey), usedGradient.sample(secondKey));

                lastKey = secondKey;
            }
        } else {
            float lastKey = usedGradient.getPoints().firstKey();

            for (float secondKey : usedGradient.getPoints().navigableKeySet()) {
                if (secondKey == lastKey) continue; // don't do last
                float x1 = this.x0 + (width * lastKey);
                float x2 = this.x0 + (width * secondKey);

                this.drawHorizontalRect(vertexConsumer, x1, this.y0, x2, this.y1, z, usedGradient.sample(lastKey), usedGradient.sample(secondKey));

                lastKey = secondKey;
            }
        }
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

    private void drawVerticalRect(VertexConsumer buffer,
                                             float x1, float y1, float x2, float y2, float z,
                                             Colour from, Colour to) {
        int r1 = from.r();
        int g1 = from.g();
        int b1 = from.b();
        int a1 = from.a();
        int r2 = to.r();
        int g2 = to.g();
        int b2 = to.b();
        int a2 = to.a();

        buffer.addVertexWith2DPose(this.pose(), x1, y1, z).setColor(r1, g1, b1, a1);
        buffer.addVertexWith2DPose(this.pose(), x1, y2, z).setColor(r2, g2, b2, a2);
        buffer.addVertexWith2DPose(this.pose(), x2, y2, z).setColor(r2, g2, b2, a2);
        buffer.addVertexWith2DPose(this.pose(), x2, y1, z).setColor(r1, g1, b1, a1);
    }

    private void drawHorizontalRect(VertexConsumer buffer,
                                               float x1, float y1, float x2, float y2, float z,
                                               Colour from, Colour to) {
        int r1 = from.r();
        int g1 = from.g();
        int b1 = from.b();
        int a1 = from.a();
        int r2 = to.r();
        int g2 = to.g();
        int b2 = to.b();
        int a2 = to.a();

        buffer.addVertexWith2DPose(this.pose(), x1, y1, z).setColor(r1, g1, b1, a1);
        buffer.addVertexWith2DPose(this.pose(), x1, y2, z).setColor(r1, g1, b1, a1);
        buffer.addVertexWith2DPose(this.pose(), x2, y2, z).setColor(r2, g2, b2, a2);
        buffer.addVertexWith2DPose(this.pose(), x2, y1, z).setColor(r2, g2, b2, a2);
    }
}
