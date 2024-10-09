package red.jackf.jackfredlib.client.impl.toasts.icon;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.apache.http.util.Args;
import red.jackf.jackfredlib.client.api.toasts.CustomToast;
import red.jackf.jackfredlib.client.api.toasts.ToastIcon;

import java.util.List;

public class ItemStackIcon implements ToastIcon {
    private static final int INSET = 2;

    private final List<ItemStack> items;
    private final int sizeInSlots;

    public ItemStackIcon(List<ItemStack> items, int sizeInSlots) {
        Args.check(!items.isEmpty(), "Items can't be empty");
        Args.check(sizeInSlots >= 1 && sizeInSlots <= 5, "sizeInSlots must be between 1 and 5");
        this.items = ImmutableList.copyOf(items);
        this.sizeInSlots = sizeInSlots;
    }

    @Override
    public void render(CustomToast toast, GuiGraphics graphics, int x, int y) {
        int scale = 2 * sizeInSlots - 1;

        int index = Mth.clamp((int) (toast.getProgress() * this.items.size()), 0, this.items.size() - 1);
        graphics.pose().pushPose();
        graphics.pose().translate(x + INSET, y + INSET, 0);
        graphics.pose().scale(scale, scale, 1);
        graphics.renderFakeItem(this.items.get(index), 0, 0);
        graphics.pose().popPose();
    }

    @Override
    public int width() {
        return ToastIcon.slotsToHeight(sizeInSlots);
    }

    @Override
    public int height() {
        return ToastIcon.slotsToHeight(sizeInSlots);
    }
}
