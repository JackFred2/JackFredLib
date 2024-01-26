package red.jackf.jackfredlib.client.api.toasts;

import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import red.jackf.jackfredlib.client.impl.toasts.CustomToastBuilderImpl;

import java.util.Optional;

/**
 * Builder to help create new toasts.
 */
public interface ToastBuilder {

    /**
     * Creates a new toast builder.
     *
     * @param format Format of this toast - controls the default text and progress bar colours, and the background texture.
     * @param title Title to display of this toast. <b>Not wrapped if too long.</b>
     * @return A new CustomToast builder.
     */
    static ToastBuilder builder(ToastFormat format, Component title) {
        return new CustomToastBuilderImpl(format, title);
    }

    /**
     * <p>Adds a message to this toast. Multiple messages will be joined with a new line, and individual lines will be
     * wrapped if it's width exceeds {@link CustomToast#MAX_WIDTH}.</p>
     *
     * <p>Note that adding enough messages will cause the toast to stretch vertically. This will work fine, however
     * due to how Minecraft's toast system works the height will be rounded up to the nearest 32 pixels, so there may be
     * some empty space below the messages.</p>
     *
     * @apiNote Default: no messages
     * @param message Message line to add to the toast.
     * @return This toast builder.
     */
    ToastBuilder addMessage(Component message);

    /**
     * <p>Adds an icon to be displayed on the left side of the toast. Text will be shifted right if an icon exists.</p>
     *
     * <p>Images will be scaled to render a 20x20 pixel image.</p>
     *
     * @see ToastIcon
     * @apiNote Default: no icon (null).
     * @param icon Icon to display.
     * @return This toast builder.
     */
    ToastBuilder withIcon(@Nullable ToastIcon icon);

    /**
     * <p>Sets how long this toast should display for, in milliseconds.</p>
     *
     * <p>Ignored if {@link #expiresWhenProgressComplete(long)} has been called.</p>
     *
     * @apiNote Default: 5000 milliseconds
     * @param expiryTimeMilliseconds How long, in milliseconds, that this toast should display for.
     * @return This toast builder.
     */
    ToastBuilder expiresAfter(long expiryTimeMilliseconds);

    /**
     * <p>Sets this toast to expire a set time after the progress bar has completed. Overrides {@link #expiresAfter(long)}.</p>
     *
     * @param displayAfterTime How long after the progress bar completes this toast should fade.
     * @return This toast builder
     */
    ToastBuilder expiresWhenProgressComplete(long displayAfterTime);

    /**
     * <p>Allows this toast to actively request the progress percentage, instead of waiting for {@link CustomToast#setProgress(float)}
     * calls.</p>
     *
     * <p>Called every frame that the toast is visible.</p>
     *
     * @apiNote Default: no-op; use {@link CustomToast#setProgress(float)} in this case
     * @param puller Progress pulling function to use.
     * @return This toast builder
     */
    ToastBuilder progressPuller(ProgressPuller puller);

    /**
     * Sets this toast to use the progress bar as a lifetime indicator, instead of an indication of work.
     * 
     * @return This toast builder
     */
    ToastBuilder progressShowsVisibleTime();

    /**
     * Sets the colour of this toast's progress bar to use a rainbow colour depending on progress - from red at 0% to
     * green at 100%.
     * 
     * @apiNote Default: false
     * @param rainbowProgressBar Whether to use a rainbow progress bar instead of the format's default.
     * @return This toast builder
     */
    ToastBuilder rainbowProgressBar(boolean rainbowProgressBar);

    /**
     * Create the custom toast instance.
     * 
     * @return The built custom toast.
     */
    CustomToast build();

    /**
     * A function that returns a new progress value for a toast. Intended to allow the toast to pull progress values from
     * some sources, such as a loading method, instead of getting written to using {@link CustomToast#setProgress(float)}.
     */
    interface ProgressPuller {
        /**
         * <p>Retrieve a new progress value for a toast. You do not need to call {@link CustomToast#setProgress(float)} on
         * the passed toast.</p>
         *
         * <p>If you do not wish to update the progress value, you should return an {@link Optional#empty()}</p>; otherwise
         * you should return an optional containing the progress between 0 and 1.
         *
         * @param toast Toast that is being updated, if you wish to update image/message as you do this.
         * @return An optional containing a new progress value, or an empty optional if no update.
         */
        Optional<Float> pull(CustomToast toast);
    }
}
