package red.jackf.jackfredlib.client.api.toasts;

import net.minecraft.resources.ResourceLocation;

/**
 * Specifies a background texture, padding settings and text colours for a toast.
 */
public record ToastFormat(ResourceLocation texture,
                          int titleColour,
                          int messageColour,
                          int progressBarColour) {
    private static ResourceLocation tex(String path) {
        return new ResourceLocation("jackfredlib-toasts", path);
    }

    /**
     * Dark gray background with rounded corners. Used for advancement unlocks.
     */
    public static final ToastFormat DARK = new ToastFormat(tex("advancement"), 0xFF_FFFF00, 0xFF_FFFFFF, 0xFF_FFFF00);
    /**
     * White background with rounded corners. Used for recipe unlocks.
     */
    public static final ToastFormat WHITE = new ToastFormat(tex("recipe"), 0xFF_500050, 0xFF_000000, 0xFF_404040);
    /**
     * Blue background with exclamation mark. Used for system messages
     */
    public static final ToastFormat BLUE_ALERT = new ToastFormat(tex("system"), 0xFF_FFFF00, 0xFF_FFFFFF, 0xFF_FFFF00);
    /**
     * White background with square corners. Used for tutorial toasts.
     */
    public static final ToastFormat WHITE_SHARP = new ToastFormat(tex("tutorial"), 0xFF_500050, 0xFF_000000, 0xFF_404040);
}
