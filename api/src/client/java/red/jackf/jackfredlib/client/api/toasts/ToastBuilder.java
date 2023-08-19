package red.jackf.jackfredlib.client.api.toasts;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import red.jackf.jackfredlib.client.impl.toasts.CustomToastImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ToastBuilder {
    private final Component title;
    private final List<Component> messages = new ArrayList<>();
    private final ToastFormat format;
    @Nullable
    private ImageSpec image;
    // default to expiring after 5000 milliseconds
    private long lifetime = 5000L;
    private long progressCompleteExpiryTime = -1;
    private boolean rainbowProgress = false;
    private boolean expiresOnFullProgress = false;
    private ProgressPuller progressPuller = toast -> Optional.empty();

    ToastBuilder(ToastFormat format, Component title) {
        this.format = format;
        this.title = title;
        this.expiresAfter(lifetime);
    }

    public ToastBuilder withMessage(Component message) {
        this.messages.add(message);
        return this;
    }

    public ToastBuilder withImage(@Nullable ImageSpec image) {
        this.image = image;
        return this;
    }

    public ToastBuilder expiresAfter(long expiryTimeMilliseconds) {
        this.lifetime = expiryTimeMilliseconds;
        return this;
    }

    public ToastBuilder expiresWhenProgressComplete(long displayAfterTime) {
        this.progressCompleteExpiryTime = displayAfterTime;
        this.expiresOnFullProgress = true;
        return this;
    }

    public ToastBuilder progressPuller(ProgressPuller puller) {
        this.progressPuller = puller;
        return this;
    }

    public ToastBuilder progressShowsVisibleTime() {
        this.progressPuller = toast -> Optional.of((float) (toast.getVisibleTime() - toast.getVisibleTimeStart()) / lifetime);
        return this;
    }

    public ToastBuilder rainbowProgressBar(boolean rainbowProgressBar) {
        this.rainbowProgress = rainbowProgressBar;
        return this;
    }

    public CustomToast build() {
        CustomToastImpl.VisibilityChecker visibilityChecker;
        double modifier = Minecraft.getInstance().options.notificationDisplayTime().get();
        if (expiresOnFullProgress) {
            final long expiryTime = (long) (this.progressCompleteExpiryTime * modifier);
            visibilityChecker = toast -> {
                boolean complete = toast.getProgress() >= 1f;
                boolean displayedLongEnoughAfter = toast.getVisibleTime() - toast.getProgressCompleteTime() > expiryTime;
                return complete && displayedLongEnoughAfter ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
            };
        } else {
            final long lifeTime = (long) (this.lifetime * modifier);
            visibilityChecker = toast -> (toast.getVisibleTime() - toast.getVisibleTimeStart()) > lifeTime ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
        }

        return new CustomToastImpl(format, title, messages, image, visibilityChecker, progressPuller, rainbowProgress);
    }

    public interface ProgressPuller {
        Optional<Float> pull(CustomToast toast);
    }
}
