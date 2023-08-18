package red.jackf.jackfredlib.client.api.toasts;

import net.minecraft.resources.ResourceLocation;

/**
 * Represents a shorthand 'definition' for an image. For a toast, always renders as a 20x20 blit.
 *
 * @param location Location of the source image file, with a .png prefix.
 * @param uOffset How many pixels from the left to start rendering from.
 * @param vOffset How many pixels from the top to start rendering from.
 * @param uWidth Width in pixels in the source image to render.
 * @param vHeight Height in pixels in the source image to render.
 * @param textureWidth Width of the whole source image.
 * @param textureHeight Height of the whole source image.
 */
public record ImageSpec(ResourceLocation location,
                        int uOffset,
                        int vOffset,
                        int uWidth,
                        int vHeight,
                        int textureWidth,
                        int textureHeight) {

    public static ImageSpec image(ResourceLocation location, int textureWidth, int textureHeight) {
        return new ImageSpec(location, 0, 0, textureWidth, textureHeight, textureWidth, textureHeight);
    }
}
