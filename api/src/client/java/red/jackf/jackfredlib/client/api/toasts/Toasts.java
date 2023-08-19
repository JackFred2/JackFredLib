package red.jackf.jackfredlib.client.api.toasts;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;

public class Toasts {
    public static final Toasts INSTANCE = new Toasts();

    private final ToastComponent toasts = Minecraft.getInstance().getToasts();

    private Toasts() {}

    public void send(Toast toast) {
        var existing = toasts.getToast(toast.getClass(), toast.getToken());
        if (toast instanceof CustomToast refreshable) {
            refreshable.refresh();
        }
        if (existing == null) toasts.addToast(toast);
    }
}
