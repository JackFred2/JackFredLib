package red.jackf.jackfredlib.client.api.toasts;

import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import red.jackf.jackfredlib.client.impl.toasts.CustomToastImpl;

import java.util.ArrayList;
import java.util.List;

public class ToastBuilder {
    private final Component title;
    private final List<Component> messages = new ArrayList<>();
    private final ToastFormat format;
    @Nullable
    private ImageSpec image;

    ToastBuilder(ToastFormat format, Component title) {
        this.format = format;
        this.title = title;
    }

    public ToastBuilder withMessage(Component message) {
        this.messages.add(message);
        return this;
    }

    public ToastBuilder withImage(@Nullable ImageSpec image) {
        this.image = image;
        return this;
    }

    public CustomToast build() {
        return new CustomToastImpl(format, title, messages, image);
    }
}
