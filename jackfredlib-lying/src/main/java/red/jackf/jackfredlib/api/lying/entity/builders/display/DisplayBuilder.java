package red.jackf.jackfredlib.api.lying.entity.builders.display;

import com.mojang.math.Transformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Brightness;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import red.jackf.jackfredlib.api.colour.Colour;
import red.jackf.jackfredlib.api.lying.entity.EntityUtils;
import red.jackf.jackfredlib.api.lying.entity.builders.BuilderBase;
import red.jackf.jackfredlib.mixins.lying.entity.DisplayAccessor;

import static net.minecraft.world.entity.Display.BillboardConstraints;

/**
 * Entity builder with additional options for display entities.
 *
 * @param <E> Entity type being built, extending {@link Display}
 * @param <B> Class of this builder
 * @see BuilderBase
 */
public abstract class DisplayBuilder<E extends Display, B extends DisplayBuilder<E, B>> extends BuilderBase<E, B> {
    /**
     * Create a new Display builder. Don't use directly, use a method in {@link red.jackf.jackfredlib.api.lying.entity.builders.EntityBuilders}.
     *
     * @param type Type of entity to create.
     * @param level Level to create the fake entity in.
     */
    @ApiStatus.Internal
    public DisplayBuilder(EntityType<E> type, ServerLevel level) {
        super(type, level);
    }

    /**
     * Adds a glowing outline to the display entity with a specific colour. This colour overrides a team colour, if
     * applicable. No effect on Text Displays.
     *
     * @param shouldGlow Whether this display entity should glow.
     * @param colour     What colour this display entity should glow, in ARGB format.
     * @return This builder
     */
    public B glowing(boolean shouldGlow, int colour) {
        ((DisplayAccessor) this.entity).jflib$setGlowColorOverride(colour);
        return this.glowing(shouldGlow);
    }

    /**
     * Adds a glowing outline to the display entity with a specific colour. This colour overrides a team colour, if
     * applicable. No effect on Text Displays.
     *
     * @param shouldGlow Whether this display entity should glow.
     * @param colour     What colour this display entity should glow, in ARGB format.
     * @return This builder
     * @see Colour
     */
    public B glowing(boolean shouldGlow, Colour colour) {
        return this.glowing(shouldGlow, colour.toARGB());
    }

    /**
     * <p>Changes how a display entity is 'billboarded' regarding a player's camera.</p>
     *
     * <table>
     *     <caption>
     *         Types of modes and their descriptions
     *     </caption>
     *     <tr>
     *         <td>{@link BillboardConstraints#FIXED}</td><td>Does not move with the player camera.</td>
     *         </tr>
     *     <tr>
     *         <td>{@link BillboardConstraints#CENTER}</td><td>Moves to always be flat against the player camera.</td>
     *         </tr>
     *     <tr>
     *         <td>{@link BillboardConstraints#VERTICAL}</td><td>Rotates around the Y to face the player's camera.</td>
     *         </tr>
     *     <tr>
     *         <td>{@link BillboardConstraints#HORIZONTAL}</td><td>Rotates up and down to match the player camera.</td>
     *     </tr>
     * </table>
     *
     * @param billboardMode How a display entity is billboarded.
     * @return This builder
     * @implNote Default: {@link BillboardConstraints#FIXED}
     */
    public B billboard(BillboardConstraints billboardMode) {
        ((DisplayAccessor) this.entity).jflib$setBillboardConstraints(billboardMode);
        return self();
    }

    /**
     * <p>Sets a brightness override for this display entity.</p>
     *
     * @param block Block light to use when rendering, in the range [0, 15]
     * @param sky   Sky light level to use when rendering, in the range [0, 15]
     * @return This entity builder
     * @implNote Default: No override, meaning the display entity takes the light level from its current world position
     */
    public B brightness(int block, int sky) {
        ((DisplayAccessor) this.entity).jflib$setBrightnessOverride(new Brightness(Mth.clamp(block, 0, 15), Mth.clamp(sky, 0, 15)));
        return self();
    }

    /**
     * <p>Sets the view range modifier for this display entity. This results in an approximate range of
     * <code>64 * modifier * clientEntityRenderMultiplier</code> for rendering this display.</p>
     *
     * @param modifier View ranger modifier for this entity.
     * @return This entity builder
     * @implNote Default: 1.0
     */
    public B viewRangeModifier(float modifier) {
        ((DisplayAccessor) this.entity).jflib$setViewRange(modifier);
        return self();
    }

    /**
     * <p>Sets the radius of the shadow on the floor below this display entity.</p>
     *
     * <p>Does not normally appear for text displays. To enable them, see {@link TextDisplayBuilder#hasShadow(boolean)}</p>
     *
     * @param shadowRadius Radius of the shadow for this display entity, in the range [0, 64].
     * @return This entity builder
     * @implNote Default: 0 (no shadow).
     */
    public B shadowRadius(float shadowRadius) {
        ((DisplayAccessor) this.entity).jflib$setShadowRadius(Mth.clamp(shadowRadius, 0, 64));
        return self();
    }

    /**
     * <p>Sets the opacity of the shadow for this display entity, if given a radius (and in the case of a Text Display
     * enabled). Acts as a multiplier for the standard shadow, which is also scaled by distance to the floor.</p>
     *
     * <p>Does not normally appear for text displays. To enable them, see {@link TextDisplayBuilder#hasShadow(boolean)}</p>
     *
     * @param shadowStrength Shadow strength multiplier for this display entity.
     * @return This builder
     * @implNote Default: 1
     */
    public B shadowStrength(float shadowStrength) {
        ((DisplayAccessor) this.entity).jflib$setShadowStrength(shadowStrength);
        return self();
    }

    /**
     * Sets this display entity's transform matrix. The components are applied in reverse parameter order.
     *
     * @param translation   Translation for the transformation. Applied fourth.
     * @param leftRotation  Left rotation for the transformation. Applied third.
     * @param scale         Scale for the transformation. Applied second.
     * @param rightRotation Right rotation for the transformation. Applied first.
     * @return This builder
     * @see <a href="https://minecraft.fandom.com/wiki/Display">Minecraft Wiki: Displays</a>
     */
    public B transform(Vector3f translation, Quaternionf leftRotation, Vector3f scale, Quaternionf rightRotation) {
        EntityUtils.setDisplayTransform(this.entity, new Transformation(
                translation,
                leftRotation,
                scale,
                rightRotation
        ));
        return self();
    }

    /**
     * Sets this display entity's translation.
     *
     * @param translation Translation to set the display's to
     * @return This builder
     */
    public B setTranslation(Vector3f translation) {
        EntityUtils.setDisplayTranslation(this.entity, translation);
        return self();
    }

    /**
     * Adds to this display entity's translation.
     *
     * @param translation Translation to add to the display's existing
     * @return This builder
     */
    public B addTranslation(Vector3f translation) {
        var transform = EntityUtils.getDisplayTransformation(this.entity);
        EntityUtils.setDisplayTranslation(this.entity, transform.getTranslation().add(translation));
        return self();
    }

    /**
     * Sets this display entity's left rotation.
     *
     * @param leftRotation Rotation to use
     * @return This builder
     */
    public B leftRotation(Quaternionf leftRotation) {
        EntityUtils.setDisplayLeftRotation(this.entity, leftRotation);
        return self();
    }

    /**
     * Sets this display entity's scale.
     *
     * @param scale Scale to use
     * @return This builder
     */
    public B scale(Vector3f scale) {
        EntityUtils.setDisplayScale(this.entity, scale);
        return self();
    }

    /**
     * Sets this display entity's right rotation.
     *
     * @param rightRotation Rotation to use
     * @return This builder
     */
    public B rightRotation(Quaternionf rightRotation) {
        EntityUtils.setDisplayRightRotation(this.entity, rightRotation);
        return self();
    }

    /**
     * Sets how long a display entity will interpolate between two transforms.
     *
     * @param interpolationDurationTicks How long interpolation of an entity lie's transform will last, in ticks.
     * @return This builder
     */
    public B transformInterpolationDuration(int interpolationDurationTicks) {
        ((DisplayAccessor) this.entity).jflib$setTransformationInterpolationLength(interpolationDurationTicks);
        return self();
    }

    /**
     * Sets how long a display entity will interpolate between positions and rotations when teleported, for example using
     * {@link net.minecraft.world.entity.Entity#setPos(Vec3)}.
     *
     * @param interpolationDurationTicks How long interpolation of an entity lie's position and rotation when teleported
     *                                  will last, in ticks.
     * @return This builder
     */
    public B teleportInterpolationDuration(int interpolationDurationTicks) {
        ((DisplayAccessor) this.entity).jflib$setTeleportInterpolationLength(interpolationDurationTicks);
        return self();
    }

    @Override
    @ApiStatus.Internal
    protected abstract B self();
}
