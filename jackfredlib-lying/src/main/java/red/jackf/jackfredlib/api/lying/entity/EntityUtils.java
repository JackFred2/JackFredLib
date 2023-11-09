package red.jackf.jackfredlib.api.lying.entity;

import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import red.jackf.jackfredlib.api.lying.entity.builders.display.TextDisplayBuilder;
import red.jackf.jackfredlib.mixins.lying.entity.BlockDisplayAccessor;
import red.jackf.jackfredlib.mixins.lying.entity.DisplayAccessor;
import red.jackf.jackfredlib.mixins.lying.entity.ItemDisplayAccessor;
import red.jackf.jackfredlib.mixins.lying.entity.TextDisplayAccessor;

/**
 * Collection of common utilities for manipulating entities.
 */
public class EntityUtils {
    private EntityUtils() {}

    /**
     * Change a text display's text content.
     *
     * @param display Display to update
     * @param text    Text to give this display
     */
    public static void setDisplayText(Display.TextDisplay display, Component text) {
        ((TextDisplayAccessor) display).jflib$setText(text);
    }

    /**
     * Change a text display's text alignment.
     *
     * @param display Display to update
     * @param align How text for this display should be justified.
     */
    public static void setDisplayTextAlign(Display.TextDisplay display, Display.TextDisplay.Align align) {
        byte flags = (byte) (((TextDisplayAccessor) display).jflib$getFlags() & 0b00111);
        if (align == Display.TextDisplay.Align.LEFT) flags |= Display.TextDisplay.FLAG_ALIGN_LEFT;
        else if (align == Display.TextDisplay.Align.RIGHT) flags |= Display.TextDisplay.FLAG_ALIGN_RIGHT;
        ((TextDisplayAccessor) display).jflib$setFlags(flags);
    }

    /**
     * Change a text display's default background status.
     *
     * @param display Display to update
     * @param useDefaultBackground Whether this text display should use a client's default background.
     */
    public static void setDisplayTextDefaultBackground(Display.TextDisplay display, boolean useDefaultBackground) {
        ((TextDisplayAccessor) display).jflib$setFlags(TextDisplayBuilder.withFlag(
                ((TextDisplayAccessor) display).jflib$getFlags(),
                Display.TextDisplay.FLAG_USE_DEFAULT_BACKGROUND,
                useDefaultBackground
        ));
    }

    /**
     * Change a text display's see-through status.
     *
     * @param display Display to update
     * @param seeThrough Whether text should be visible through terrain.
     */
    public static void setDisplayTextSeeThrough(Display.TextDisplay display, boolean seeThrough) {
        ((TextDisplayAccessor) display).jflib$setFlags(TextDisplayBuilder.withFlag(
                ((TextDisplayAccessor) display).jflib$getFlags(),
                Display.TextDisplay.FLAG_SEE_THROUGH,
                seeThrough
        ));
    }

    /**
     * Change whether a text display has a shadow.
     *
     * @param display Display to update
     * @param hasShadow Whether text should have a shadow on the ground.
     */
    public static void setDisplayTextHasShadow(Display.TextDisplay display, boolean hasShadow) {
        ((TextDisplayAccessor) display).jflib$setFlags(TextDisplayBuilder.withFlag(
                ((TextDisplayAccessor) display).jflib$getFlags(),
                Display.TextDisplay.FLAG_SHADOW,
                hasShadow
        ));
    }

    /**
     * Change a block display's block state.
     *
     * @param display Display to update
     * @param state   Block state to give this display
     */
    public static void setDisplayBlockState(Display.BlockDisplay display, BlockState state) {
        ((BlockDisplayAccessor) display).jflib$setBlockState(state);
    }

    /**
     * Change an item display's item stack.
     *
     * @param display Display to update
     * @param stack ItemStack to give to the display
     */
    public static void setDisplayItem(Display.ItemDisplay display, ItemStack stack) {
        ((ItemDisplayAccessor) display).jflib$setItemStack(stack);
    }

    /**
     * Set a display entity's transform interpolation to start at a given world tick.
     *
     * @param display         Display to update
     * @param targetStartTime Absolute world time to start the interpolation at. Values below the current world time are clamped
     *                        to the current world time.
     */
    public static void startInterpolationAt(Display display, long targetStartTime) {
        int delay = (int) (Math.max(targetStartTime, display.getLevel().getGameTime()) - display.getLevel().getGameTime());
        ((DisplayAccessor) display).jflib$setTransformationInterpolationDelay(delay);
    }

    /**
     * Set a display entity's transform interpolation to start a given number of world ticks from now.
     *
     * @param display Display to update
     * @param delay   Amount of ticks from the current time to start the interpolation at. Values below 0 are clamped at 0.
     */
    public static void startInterpolationIn(Display display, int delay) {
        delay = Math.max(0, delay);
        ((DisplayAccessor) display).jflib$setTransformationInterpolationDelay(delay);
    }

    /**
     * Face this entity towards another entity.
     *
     * @param source Entity to change the rotation of
     * @param target Target entity to face.
     */
    public static void face(Entity source, Entity target) {
        face(source, target.getEyePosition());
    }

    /**
     * Face this entity towards a given vector.
     *
     * @param source Entity to change the rotation of
     * @param target Target position to face.
     */
    public static void face(Entity source, Vec3 target) {
        var vector = target.subtract(source.position());
        var yRot = (float) (Mth.atan2(vector.z, vector.x) * 180.0F / (float) Math.PI) + 90.0F;
        //noinspection SuspiciousNameCombination
        var xRot = (float) (Mth.atan2(vector.horizontalDistance(), vector.y) * 180.0F / (float) Math.PI) - 90.0F;
        //if (source instanceof Display) { // if this wasn't offset the displays would be backwards
        yRot += 180.0F % 360.0F;
        //}
        source.moveTo(source.getX(),
                      source.getY(),
                      source.getZ(),
                      Mth.positiveModulo(yRot, 360f),
                      Mth.positiveModulo(xRot, 360f));
        if (source instanceof LivingEntity living) {
            living.setYBodyRot(yRot);
            living.setYHeadRot(yRot);
        }
    }
}
