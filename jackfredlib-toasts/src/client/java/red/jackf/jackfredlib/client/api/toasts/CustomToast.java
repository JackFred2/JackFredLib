package red.jackf.jackfredlib.client.api.toasts;

import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * A custom toast instance with utility features and an optional progress bar.
 */
public interface CustomToast extends Toast {

    /**
     * Maximum width of the toast, in scaled pixels.
     */
    int MAX_WIDTH = 200;

    /**
     * Refresh the lifetime of this toast. This will not reset the progress bar.
     *
     * @see #setProgress(float)
     */
    void refresh();

    /**
     * Set the image to be displayed on this toast.
     *
     * @see ImageSpec
     * @param image Image to display, or null to remove image
     */
    void setImage(@Nullable ImageSpec image);

    /**
     * Set the title of this toast.
     *
     * @param title Title to display at the top of the toast.
     */
    void setTitle(@NotNull Component title);

    /**
     * Sets the message(s) of this toast.
     *
     * @param messages Sets the messages to be displayed in this toast. Each message is split by maximum width, and are
     *                 all joined with a newline.
     */
    void setMessage(List<Component> messages);

    /**
     * Sets the progress for this toast's progress bar. Set to 0 to hide the bar.
     *
     * @param progress Progress of the toast, clamped to the range [0, 1].
     */
    void setProgress(float progress);

    /**
     * Gets the progress for this toast.
     *
     * @return The progress of this toast, in the range [0, 1].
     */
    float getProgress();

    /**
     * Gets the time in milliseconds since the progress bar hit 100% (1f). If the progress bar is not complete, returns -1.
     *
     * @return Milliseconds since the progress bar was last completed, or -1 if not complete.
     */
    long getTimeSinceProgressComplete();

    /**
     * Gets the time in milliseconds that the toast has been visible.
     * @return Time since the toast has been visible.
     */
    long getTimeVisible();

}
