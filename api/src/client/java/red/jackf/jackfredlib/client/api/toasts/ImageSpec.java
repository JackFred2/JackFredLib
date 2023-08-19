package red.jackf.jackfredlib.client.api.toasts;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import red.jackf.jackfredlib.client.impl.toasts.ModUtils;

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

    /**
     * Create a new image using an entire image file.
     *
     * @param location Location of the image file, relative to <code>assets/{modid}/</code>.
     * @param textureWidth Width of the texture file in pixels.
     * @param textureHeight Height of the texture file in pixels.
     * @return An ImageSpec representing the given location, width and height.
     */
    public static ImageSpec image(ResourceLocation location, int textureWidth, int textureHeight) {
        return new ImageSpec(location, 0, 0, textureWidth, textureHeight, textureWidth, textureHeight);
    }

    /**
     * Creates an image spec from a mod's icon. If the icon could not be parsed, return null.
     *
     * @param modid Mod ID of the mod whom to use the icon from.
     * @return ImageSpec for the given mod's icon.
     */
    public static @Nullable ImageSpec modIcon(String modid) {
        return ModUtils.specFromModId(modid);
    }
}
