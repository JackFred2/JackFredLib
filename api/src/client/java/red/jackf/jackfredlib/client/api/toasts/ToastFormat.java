package red.jackf.jackfredlib.client.api.toasts;

import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.resources.ResourceLocation;

/**
 * Specifies a background texture, padding settings and text colours for a toast.
 */
public record ToastFormat(ResourceLocation image,
                          int vOffset,
                          int leftWidth,
                          int topHeight,
                          int titleColour,
                          int messageColour,
                          int progressBarColour) {
    /**
     * Dark gray background with rounded corners. Used for advancement unlocks.
     */
    public static final ToastFormat DARK = new ToastFormat(Toast.TEXTURE, 0, 6, 6, 0xFF_FFFF00, 0xFF_FFFFFF, 0xFF_FFFF00);
    /**
     * White background with rounded corners. Used for recipe unlocks.
     */
    public static final ToastFormat WHITE = new ToastFormat(Toast.TEXTURE, 32, 6, 6, 0xFF_500050, 0xFF_000000, 0xFF_404040);
    /**
     * Blue background with exclamation mark. Used for system messages
     */
    public static final ToastFormat BLUE_ALERT = new ToastFormat(Toast.TEXTURE, 64, 16, 25, 0xFF_FFFF00, 0xFF_FFFFFF, 0xFF_FFFF00);
    /**
     * White background with square corners. Used for tutorial toasts.
     */
    public static final ToastFormat WHITE_SHARP = new ToastFormat(Toast.TEXTURE, 96, 6, 6, 0xFF_500050, 0xFF_000000, 0xFF_404040);
}
