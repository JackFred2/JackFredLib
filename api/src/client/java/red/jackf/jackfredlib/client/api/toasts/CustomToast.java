package red.jackf.jackfredlib.client.api.toasts;

import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.network.chat.Component;

public interface CustomToast extends Toast {

    void refresh();

    void setProgress(float progress);

    float getProgress();

    long getVisibleTimeStart();

    long getVisibleTime();

    static ToastBuilder builder(ToastFormat format, Component title) {
        return new ToastBuilder(format, title);
    }
}
