package red.jackf.jackfredlib.client.api.toasts;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;
import red.jackf.jackfredlib.client.impl.toasts.CustomToastBuilderImpl;
import red.jackf.jackfredlib.client.impl.toasts.ModUtils;
import red.jackf.jackfredlib.impl.LogUtil;

/**
 * <p>Helper class for creating 'toasts' - notifications at the top right of the screen. Use this class via {@link #INSTANCE}.</p>
 *
 * @see CustomToastBuilderImpl
 */
public class Toasts {
    /**
     * Instance of the Toasts utility class.
     */
    public static final Toasts INSTANCE = new Toasts();

    /**
     * <p>Send a new toast to the player.</p>
     *
     * <p>You can pass the same {@link CustomToast} instance to this method, and it will refresh the lifetime of said
     * toast. A common use case is to create a memoized instance of your toast as a static field, then sending that
     * instance whenever you need to.</p>
     *
     * @param toast Toast to send to the player. It's duration or progress will be refreshed.
     * @see CustomToastBuilderImpl
     * @see red.jackf.jackfredlib.api.Memoizer
     */
    public void send(CustomToast toast) {
        var existing = toasts.getToast(toast.getClass(), toast.getToken());
        LOGGER.debug("Refreshing toast, hash {}", toast.getToken().hashCode());
        toast.refresh();
        if (existing == null) {
            LOGGER.debug("Adding toast, hash {}", toast.getToken().hashCode());
            toasts.addToast(toast);
        }
    }

    /**
     * Send a message to the player, by creating a new toast with the given mod's icon and name, and a progress bar
     * showing the visible time remaining.
     *
     * @param modid Mod ID to use for the icon and title
     * @param message Message to display to the user.
     */
    public void sendFromMod(String modid, Component message) {
        send(ToastBuilder.builder(ToastFormat.WHITE_SHARP, ModUtils.getModTitle(modid))
                .addMessage(message)
                .progressShowsVisibleTime()
                .withImage(ImageSpec.modIcon(modid))
                .expiresAfter(2500L + (Minecraft.getInstance().font.width(message) * 10L))
                .build());
    }

    private static final Logger LOGGER = LogUtil.getLogger("Toasts");

    private final ToastComponent toasts = Minecraft.getInstance().getToasts();

    private Toasts() {}
}
