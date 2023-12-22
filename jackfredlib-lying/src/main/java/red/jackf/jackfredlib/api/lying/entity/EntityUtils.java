package red.jackf.jackfredlib.api.lying.entity;

import com.mojang.math.Transformation;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Brightness;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import red.jackf.jackfredlib.api.colour.Colour;
import red.jackf.jackfredlib.api.lying.entity.builders.display.TextDisplayBuilder;
import red.jackf.jackfredlib.impl.lying.entity.TransformUtil;
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
     * @param align   How text for this display should be justified.
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
     * @param display              Display to update
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
     * @param display    Display to update
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
     * @param display   Display to update
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
     * @param stack   ItemStack to give to the display
     */
    public static void setDisplayItem(Display.ItemDisplay display, ItemStack stack) {
        ((ItemDisplayAccessor) display).jflib$setItemStack(stack);
    }

    /**
     * <p>Change a display entity's view range multiplier. This is scaled with a client's Entity View Distance modifier.</p>
     * <p>The default rangefor a display entity with a modifier of {@code 1.0} and a client Entity View Distance of {@code 100%},
     * is 64 blocks.</p>
     *
     * @param display Display to update
     * @param viewRangeModifier The new view range modifier to use.
     */
    public static void setDisplayViewRange(Display display, float viewRangeModifier) {
        ((DisplayAccessor) display).jflib$setViewRange(viewRangeModifier);
    }

    /**
     * Sets the glowing outline colour of a display entity. You will need to make the entity glow separately.
     *
     * @param display Display to update the glow colour of.
     * @param colour Colour to change the glowing outline to, in ARGB format.
     * @see Entity#setGlowingTag(boolean)
     */
    public static void setDisplayGlowOverride(Display display, int colour) {
        ((DisplayAccessor) display).jflib$setGlowColorOverride(colour);
    }


    /**
     * Sets the glowing outline colour of a display entity. You will need to give the entity the glowing tag
     * separately.
     *
     * @param display Display to update the glow colour of.
     * @param colour Colour to change the glowing outline to.
     * @see Entity#setGlowingTag(boolean)
     */
    public static void setDisplayGlowOverride(Display display, Colour colour) {
        ((DisplayAccessor) display).jflib$setGlowColorOverride(colour.toARGB());
    }

    /**
     * Sets the light level override for a display entity.
     *
     * @param display Display to update the brightness for.
     * @param brightness New brightness object to use for the display.
     */
    public static void setDisplayBrightnessOverride(Display display, Brightness brightness) {
        ((DisplayAccessor) display).jflib$setBrightnessOverride(brightness);
    }

    /**
     * Sets the light level override for a display entity.
     *
     * @param display Display to update the brightness for.
     * @param block Block light level to use for the display (e.g. from glowstone, torches)
     * @param sky Sky light level to use for the display (e.g. from the sun, darkened by storms)
     */
    public static void setDisplayBrightnessOverride(Display display, int block, int sky) {
        ((DisplayAccessor) display).jflib$setBrightnessOverride(new Brightness(block, sky));
    }

    /**
     * Sets the shadow radius for a display entity.
     *
     * @param display Display to update the shadow for.
     * @param radius New radius for this display's shadow.
     */
    public static void setDisplayShadowRadius(Display display, float radius) {
        ((DisplayAccessor) display).jflib$setShadowRadius(radius);
    }

    /**
     * Sets the shadow strength for a display entity.
     *
     * @param display Display to update the shadow for.
     * @param strength New strength for this display's shadow.
     */
    public static void setDisplayShadowStrength(Display display, float strength) {
        ((DisplayAccessor) display).jflib$setShadowStrength(strength);
    }

    /**
     * Sets the transformation matrix of the given display entity.
     *
     * @param display        Display to update
     * @param transformation New transformation matrix to give the display
     */
    public static void setDisplayTransform(Display display, Transformation transformation) {
        ((DisplayAccessor) display).jflib$setTransformation(transformation);
    }

    /**
     * Sets the translation for the given display entity's transformation.
     *
     * @param display     Display to update
     * @param translation New translation vector to use
     */
    public static void setDisplayTranslation(Display display, Vector3f translation) {
        Transformation original = DisplayAccessor.jflib$createTransformation(display.getEntityData());
        ((DisplayAccessor) display).jflib$setTransformation(TransformUtil.update(
                original,
                translation,
                null,
                null,
                null
        ));
    }

    /**
     * Get the given display entity's transformation.
     *
     * @param display Display to get the transform for
     * @return Transformation for the given display entity
     */
    public static Transformation getDisplayTransformation(Display display) {
        return DisplayAccessor.jflib$createTransformation(display.getEntityData());
    }

    /**
     * Sets the left rotation for the given display entity's transformation.
     *
     * @param display      Display to update
     * @param leftRotation New left rotation to use
     */
    public static void setDisplayLeftRotation(Display display, Quaternionf leftRotation) {
        Transformation original = DisplayAccessor.jflib$createTransformation(display.getEntityData());
        ((DisplayAccessor) display).jflib$setTransformation(TransformUtil.update(
                original,
                null,
                leftRotation,
                null,
                null
        ));
    }

    /**
     * Sets the scale for the given display entity's transformation.
     *
     * @param display Display to update
     * @param scale   New scale vector to use
     */
    public static void setDisplayScale(Display display, Vector3f scale) {
        Transformation original = DisplayAccessor.jflib$createTransformation(display.getEntityData());
        ((DisplayAccessor) display).jflib$setTransformation(TransformUtil.update(
                original,
                null,
                null,
                scale,
                null
        ));
    }

    /**
     * Sets the right rotation for the given display entity's transformation.
     *
     * @param display       Display to update
     * @param rightRotation New right rotation to use
     */
    public static void setDisplayRightRotation(Display display, Quaternionf rightRotation) {
        Transformation original = DisplayAccessor.jflib$createTransformation(display.getEntityData());
        ((DisplayAccessor) display).jflib$setTransformation(TransformUtil.update(
                original,
                null,
                null,
                null,
                rightRotation
        ));
    }

    /**
     * Update multiple parts of a display entity's transformation matrix. Any parameter passed <code>null</code> will
     * result in the original transform's field being kept.
     *
     * @param display       Display entity to update the transform for.
     * @param translation   Translation vector to use. Pass <code>null</code> to keep original.
     * @param leftRotation  Left rotation to use. Pass <code>null</code> to keep original.
     * @param scale         Scale vector to use. Pass <code>null</code> to keep original.
     * @param rightRotation Right rotation to use. Pass <code>null</code> to keep original.
     */
    public static void updateDisplayTransformation(
            Display display,
            @Nullable Vector3f translation,
            @Nullable Quaternionf leftRotation,
            @Nullable Vector3f scale,
            @Nullable Quaternionf rightRotation) {
        Transformation original = DisplayAccessor.jflib$createTransformation(display.getEntityData());
        ((DisplayAccessor) display).jflib$setTransformation(TransformUtil.update(
                original,
                translation,
                leftRotation,
                scale,
                rightRotation
        ));
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
