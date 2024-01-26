package red.jackf.jackfredlib.client.api.toasts;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.apache.http.util.Args;
import org.jetbrains.annotations.Nullable;
import red.jackf.jackfredlib.client.impl.toasts.ModUtils;
import red.jackf.jackfredlib.client.impl.toasts.icon.ImageIcon;
import red.jackf.jackfredlib.client.impl.toasts.icon.ItemStackIcon;

import java.util.List;

/**
 * An icon displayed at the left side of a toast.
 */
public interface ToastIcon {
    int DEFAULT_SIZE = 20;

    /**
     * Creates a toast icon from a given image file.
     *
     * @param location Path to the file to show.
     * @param textureWidth Width of the texture being rendered.
     * @param textureHeight Height of the texture being rendered.
     * @return An icon rendering the given image.
     */
    static ToastIcon image(ResourceLocation location, int textureWidth, int textureHeight) {
        return new ImageIcon(location, DEFAULT_SIZE, DEFAULT_SIZE, 0, 0, textureWidth, textureHeight, textureWidth, textureHeight);
    }

    /**
     * Creates a toast icon from a given image file. This overload sizes it to square proportions, based on a given number
     * of toast slots to take up.
     *
     * @param location Path to the file to show.
     * @param toastSlots Number of toast slots that should be shown. This is between 1 and 5, and only 5 slots can be
     *                   used at once.
     * @param textureWidth Width of the texture being rendered.
     * @param textureHeight Height of the texture being rendered.
     * @return An icon rendering the given image.
     */
    static ToastIcon image(ResourceLocation location, int toastSlots, int textureWidth, int textureHeight) {
        int size = slotsToHeight(toastSlots);
        return new ImageIcon(location, size, size, 0, 0, textureWidth, textureHeight, textureWidth, textureHeight);
    }

    /**
     * Creates a toast icon from a given image file.
     *
     * @param location Path to the file to show.
     * @param width Width of the texture rendered on the toast.
     * @param height Height of the texture rendered on the toast.
     * @param textureWidth Width of the texture being rendered.
     * @param textureHeight Height of the texture being rendered.
     * @return An icon rendering the given image.
     */
    static ToastIcon image(ResourceLocation location, int width, int height, int textureWidth, int textureHeight) {
        return new ImageIcon(location, width, height, 0, 0, textureWidth, textureHeight, textureWidth, textureHeight);
    }

    /**
     * Creates a toast icon from a given image file.
     *
     * @param location Path to the file to show.
     * @param width Width of the texture rendered on the toast.
     * @param height Height of the texture rendered on the toast.
     * @param uOffset X offset on the source texture to start rendering from.
     * @param vOffset Y offset on the source texture to start rendering from.
     * @param uWidth Width of the region on the source texture to render from.
     * @param vHeight Width of the region on the source texture to render from.
     * @param textureWidth Width of the texture being rendered.
     * @param textureHeight Height of the texture being rendered.
     * @return An icon rendering the given image.
     */
    static ToastIcon image(ResourceLocation location, int width, int height, int uOffset, int vOffset, int uWidth, int vHeight, int textureWidth, int textureHeight) {
        return new ImageIcon(location, width, height, uOffset, vOffset, uWidth, vHeight, textureWidth, textureHeight);
    }

    /**
     * Create a toast icon that displays a given ItemStack.
     *
     * @param stack ItemStack to display.
     * @return An icon rendering the given ItemStack.
     */
    static ToastIcon item(ItemStack stack) {
        return new ItemStackIcon(List.of(stack), 1);
    }


    /**
     * Create a toast icon that displays a given ItemStack.
     *
     * @param stack ItemStack to display.
     * @param sizeInSlots Amount of slots to take up for the toast.
     * @return An icon rendering the given ItemStack.
     */
    static ToastIcon item(ItemStack stack, int sizeInSlots) {
        return new ItemStackIcon(List.of(stack), sizeInSlots);
    }

    /**
     * Create a toast icon that displays a sequence of ItemStacks. Which stack is displayed depends on the {@link CustomToast#getProgress()}.
     *
     * @param stacks ItemStacks to display.
     * @return An icon rendering the given ItemStack.
     */
    static ToastIcon items(List<ItemStack> stacks) {
        return new ItemStackIcon(stacks, 1);
    }

    /**
     * Create a toast icon that displays a sequence of ItemStacks. Which stack is displayed depends on the {@link CustomToast#getProgress()}.
     *
     * @param stacks ItemStacks to display.
     * @param sizeInSlots Amount of slots to take up for the toast.
     * @return An icon rendering the given ItemStack.
     */
    static ToastIcon items(List<ItemStack> stacks, int sizeInSlots) {
        return new ItemStackIcon(stacks, sizeInSlots);
    }

    /**
     * Creates a toast icon from a given mod's icon, sized to 20x20 (1 toast slot).
     *
     * @param modid Mod ID to use the icon for.
     * @return An icon with the given mod's icon, or null if no mod or the mod doesn't have an icon.
     */
    static @Nullable ToastIcon modIcon(String modid) {
        return ModUtils.iconFromModId(modid);
    }

    /**
     * Returns the height for an icon in a given toast taking up X slots.
     *
     * @param toastSlots Number of toast slots to take up. Must be between 1 and 5.
     * @return The size of an icon that takes up X slots.
     */
    static int slotsToHeight(int toastSlots) {
        Args.check(toastSlots >= 1 && toastSlots <= 5, "Toast Slots must be between 1 and 5");
        return Mth.clamp(toastSlots, 1, 5) * 32 - 12;
    }

    //////////////////
    // CUSTOM ICONS //
    //////////////////

    /**
     * Render the given icon at a given screen position.
     *
     * @param toast Toast currently rendering this icon.
     * @param component ToastComponent that owns and is rendering the toast.
     * @param graphics Graphics being used to render.
     * @param x X position of the top left corner of the toast.
     * @param y Y position of the top left corner of the toast.
     */
    void render(CustomToast toast, ToastComponent component, GuiGraphics graphics, int x, int y);

    /**
     * Return the width of the icon. Used to calculate the size of the toast background.
     *
     * @return The width of the icon.
     */
    default int width() {
        return DEFAULT_SIZE;
    }

    /**
     * Return the height of the icon. Used to calculate the size of the toast background.
     *
     * @return The height of the icon.
     */
    default int height() {
        return DEFAULT_SIZE;
    }
}
