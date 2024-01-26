package red.jackf.jackfredlib.client.impl.toasts.icon;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.resources.ResourceLocation;
import org.apache.http.util.Args;
import red.jackf.jackfredlib.client.api.toasts.CustomToast;
import red.jackf.jackfredlib.client.api.toasts.ToastIcon;

import java.util.Objects;

/**
 * Draws an image from a given path as an icon
 */
public class ImageIcon implements ToastIcon {
    private final ResourceLocation location;
    private final int width;
    private final int height;
    private final int uOffset;
    private final int vOffset;
    private final int uWidth;
    private final int vHeight;
    private final int textureWidth;
    private final int textureHeight;

    /**
     * @param location Location of the source image file, with a .png prefix.
     * @param uOffset How many pixels from the left of the source image to start rendering from.
     * @param vOffset How many pixels from the top of the source image to start rendering from.
     * @param uWidth Width in pixels in the source image to render.
     * @param vHeight Height in pixels in the source image to render.
     * @param textureWidth Width of the whole source image.
     * @param textureHeight Height of the whole source image.
     */
    public ImageIcon(ResourceLocation location,
                     int width,
                     int height,
                     int uOffset,
                     int vOffset,
                     int uWidth,
                     int vHeight,
                     int textureWidth,
                     int textureHeight) {
        this.location = Objects.requireNonNull(location);
        this.width = Args.notNegative(width, "width");
        this.height = Args.notNegative(height, "height");
        this.uOffset = Args.notNegative(uOffset, "uOffset");
        this.vOffset = Args.notNegative(vOffset, "vOffset");
        this.uWidth = Args.notNegative(uWidth, "uWidth");
        this.vHeight = Args.notNegative(vHeight, "vHeight");
        this.textureWidth = Args.positive(textureWidth, "textureWidth");
        this.textureHeight = Args.positive(textureHeight, "textureHeight");
    }

    @Override
    public void render(CustomToast toast, ToastComponent component, PoseStack pose, int x, int y) {
        RenderSystem.enableBlend();
        RenderSystem.setShaderTexture(0, location);
        GuiComponent.blit(pose,
                x, y,
                width, height,
                uOffset, vOffset,
                uWidth, vHeight,
                textureWidth, textureHeight);
        RenderSystem.disableBlend();
    }

    @Override
    public int width() {
        return width;
    }

    @Override
    public int height() {
        return height;
    }
}
