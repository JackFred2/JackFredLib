package red.jackf.jackfredlib.client.impl.toasts;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import red.jackf.jackfredlib.client.api.toasts.CustomToast;
import red.jackf.jackfredlib.client.api.toasts.ImageSpec;
import red.jackf.jackfredlib.client.api.toasts.ToastBuilder;
import red.jackf.jackfredlib.client.api.toasts.ToastFormat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CustomToastBuilderImpl implements ToastBuilder {
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

    public CustomToastBuilderImpl(ToastFormat format, Component title) {
        this.format = format;
        this.title = title;
        this.expiresAfter(lifetime);
    }

    public CustomToastBuilderImpl addMessage(Component message) {
        this.messages.add(message);
        return this;
    }

    public CustomToastBuilderImpl withImage(@Nullable ImageSpec image) {
        this.image = image;
        return this;
    }

    public CustomToastBuilderImpl expiresAfter(long expiryTimeMilliseconds) {
        this.lifetime = expiryTimeMilliseconds;
        return this;
    }

    public CustomToastBuilderImpl expiresWhenProgressComplete(long displayAfterTime) {
        this.progressCompleteExpiryTime = displayAfterTime;
        this.expiresOnFullProgress = true;
        return this;
    }

    public CustomToastBuilderImpl progressPuller(ProgressPuller puller) {
        this.progressPuller = puller;
        return this;
    }

    public CustomToastBuilderImpl progressShowsVisibleTime() {
        this.progressPuller = toast -> Optional.of((float) (toast.getTimeVisible()) / lifetime);
        return this;
    }

    public CustomToastBuilderImpl rainbowProgressBar(boolean rainbowProgressBar) {
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
                boolean displayedLongEnoughAfter = toast.getTimeSinceProgressComplete() > expiryTime;
                return complete && displayedLongEnoughAfter ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
            };
        } else {
            final long lifeTime = (long) (this.lifetime * modifier);
            visibilityChecker = toast -> (toast.getTimeVisible()) > lifeTime ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
        }

        return new CustomToastImpl(format, title, messages, image, visibilityChecker, progressPuller, rainbowProgress);
    }
}
