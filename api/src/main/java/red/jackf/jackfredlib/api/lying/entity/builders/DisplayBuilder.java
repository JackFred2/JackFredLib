package red.jackf.jackfredlib.api.lying.entity.builders;

import com.mojang.math.Transformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import red.jackf.jackfredlib.api.colour.Colour;

import static net.minecraft.world.entity.Display.*;

/**
 * Entity builder with additional options for display entities.
 * @param <E> Entity type being built, extending {@link Display}
 * @param <B> Class of this builder
 * @see BuilderBase
 */
public abstract class DisplayBuilder<E extends Display, B extends DisplayBuilder<E, B>> extends GenericBuilder<E, B> {
    protected DisplayBuilder(EntityType<E> type, ServerLevel level) {
        super(type, level);
    }

    /**
     * Adds a glowing outline to the display entity with a specific colour. This colour overrides a team colour, if
     * applicable. No effect on Text Displays.
     * @param shouldGlow Whether this display entity should glow.
     * @param colour What colour this display entity should glow, in ARGB format.
     * @return This builder
     */
    public B glowing(boolean shouldGlow, int colour) {
        this.entity.setGlowColorOverride(colour);
        return this.glowing(shouldGlow);
    }

    /**
     * Adds a glowing outline to the display entity with a specific colour. This colour overrides a team colour, if
     * applicable. No effect on Text Displays.
     * @param shouldGlow Whether this display entity should glow.
     * @param colour What colour this display entity should glow, in ARGB format.
     * @see Colour
     * @return This builder
     */
    public B glowing(boolean shouldGlow, Colour colour) {
        return this.glowing(shouldGlow, colour.toARGB());
    }

    /**
     * <p>Changes how a display entity is 'billboarded' regarding a player's camera.</p>
     *
     * <p>The following modes and their explanations are:</p>
     * <table>
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
     * @param billboardMode How a display entity is billboarded. Defaults to {@link BillboardConstraints#FIXED}
     * @return This builder
     */
    public B billboard(BillboardConstraints billboardMode) {
        this.entity.setBillboardConstraints(billboardMode);
        return self();
    }

    /**
     * Sets this display entity's transform matrix. The components are applied in reverse parameter order.
     *
     * @see <a href="https://minecraft.fandom.com/wiki/Display">Minecraft Wiki: Displays</a>
     * @param translation Translation for the transformation. Applied fourth.
     * @param leftRotation Left rotation for the transformation. Applied third.
     * @param scale Scale for the transformation. Applied second.
     * @param rightRotation Right rotation for the transformation. Applied first.
     * @return This builder
     */
    public B transform(Vector3f translation, Quaternionf leftRotation, Vector3f scale, Quaternionf rightRotation) {
        this.entity.setTransformation(new Transformation(
                translation,
                leftRotation,
                scale,
                rightRotation
        ));
        return self();
    }

    /**
     * Sets this display entity's translation.
     * @param translation Translation to set the display's to
     * @return This builder
     */
    public B setTranslation(Vector3f translation) {
        var transform = getTransformation();
        this.entity.setTransformation(new Transformation(
                translation,
                transform.getLeftRotation(),
                transform.getScale(),
                transform.getRightRotation()
        ));
        return self();
    }

    /**
     * Adds to this display entity's translation.
     * @param translation Translation to add to the display's existing
     * @return This builder
     */
    public B addTranslation(Vector3f translation) {
        var transform = getTransformation();
        this.entity.setTransformation(new Transformation(
                transform.getTranslation().add(translation),
                transform.getLeftRotation(),
                transform.getScale(),
                transform.getRightRotation()
        ));
        return self();
    }

    /**
     * Sets this display entity's left rotation.
     * @param leftRotation Rotation to use
     * @return This builder
     */
    public B leftRotation(Quaternionf leftRotation) {
        var transform = getTransformation();
        this.entity.setTransformation(new Transformation(
                transform.getTranslation(),
                leftRotation,
                transform.getScale(),
                transform.getRightRotation()
        ));
        return self();
    }

    /**
     * Sets this display entity's scale.
     * @param scale Scale to use
     * @return This builder
     */
    public B scale(Vector3f scale) {
        var transform = getTransformation();
        this.entity.setTransformation(new Transformation(
                transform.getTranslation(),
                transform.getLeftRotation(),
                scale,
                transform.getRightRotation()
        ));
        return self();
    }

    /**
     * Sets this display entity's right rotation.
     * @param rightRotation Rotation to use
     * @return This builder
     */
    public B rightRotation(Quaternionf rightRotation) {
        var transform = getTransformation();
        this.entity.setTransformation(new Transformation(
                transform.getTranslation(),
                transform.getLeftRotation(),
                transform.getScale(),
                rightRotation
        ));
        return self();
    }

    protected Transformation getTransformation() {
        return Display.createTransformation(this.entity.getEntityData());
    }

    @Override
    @ApiStatus.Internal
    protected abstract B self();
}
