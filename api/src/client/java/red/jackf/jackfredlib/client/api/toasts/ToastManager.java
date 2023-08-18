package red.jackf.jackfredlib.client.api.toasts;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;

public class ToastManager {
    public static final ToastManager INSTANCE = new ToastManager();

    private final ToastComponent toasts = Minecraft.getInstance().getToasts();

    private ToastManager() {}

    public void send(Toast toast) {
        var existing = toasts.getToast(toast.getClass(), toast.getToken());
        if (existing instanceof CustomToast refreshable) {
            refreshable.refresh();
        } else {
            toasts.addToast(toast);
        }
    }
}
