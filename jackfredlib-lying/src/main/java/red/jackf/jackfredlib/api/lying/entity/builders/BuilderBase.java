package red.jackf.jackfredlib.api.lying.entity.builders;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import red.jackf.jackfredlib.api.lying.entity.EntityUtils;

/**
 * Helper builder class for creating entities, optimized for creating {@link red.jackf.jackfredlib.api.lying.entity.EntityLie}s.
 * @param <E> Entity being created
 * @param <B> Builder class for extension purposes
 */
public abstract class BuilderBase<E extends Entity, B extends BuilderBase<E, B>> {
    protected final E entity;

    protected BuilderBase(EntityType<E> type, ServerLevel level) {
        this.entity = type.create(level);
        if (this.entity == null) throw new IllegalArgumentException("Could not create entity; likely caused by being" +
                "part of a disabled feature.");
    }

    /**
     * Finish building this entity.
     * @return The built entity
     */
    public E build() {
        return entity;
    }

    /**
     * Set the position of this entity's 'feet'.
     * @param position Position to move this entity's feet to
     * @return This builder
     */
    public B position(Vec3 position) {
        this.entity.moveTo(position);
        return self();
    }

    /**
     * Move this entity, so it is standing at the bottom center of this BlockPos.
     * @param position Position to move this entity to
     * @return This builder
     */
    public B position(BlockPos position) {
        return position(Vec3.atBottomCenterOf(position));
    }

    /**
     * Moves this entity so that it is centered on a given position.
     * @param position Position to move this entity's center to
     * @return This builder
     */
    public B positionCentered(Vec3 position) {
        this.entity.moveTo(position.subtract(0, -this.entity.getBbHeight() / 2, 0));
        return self();
    }

    /**
     * Moves this entity so that it is centered on a given block pos.
     * @param position Position to move this entity's center to
     * @return This builder
     */
    public B positionCentered(BlockPos position) {
        return positionCentered(position.getCenter());
    }

    /**
     * Rotates this entity with the given X and Y rotation.
     * @param xRot X rotation (pitch, facing up or down). Clamped to the range [-90, 90]
     * @param yRot Y rotation (horizontal facing). Modulus to the range [0, 360)
     * @return This builder
     */
    public B rotation(float xRot, float yRot) {
        this.entity.moveTo(this.entity.getX(),
                this.entity.getY(),
                this.entity.getZ(),
                Mth.positiveModulo(yRot, 360.0F),
                Mth.clamp(xRot, -90.0F, 90.0F));
        return self();
    }

    /**
     * Rotates this entity with the given X rotation.
     * @param xRot X rotation (pitch, facing up or down). Clamped to the range [-90, 90]
     * @return This builder
     */
    public B xRotation(float xRot) {
        return rotation(xRot, this.entity.getYRot());
    }

    /**
     * Rotates this entity with the given Y rotation.
     * @param yRot Y rotation (horizontal facing). Modulus to the range [0, 360)
     * @return This builder
     */
    public B yRotation(float yRot) {
        return rotation(this.entity.getXRot(), yRot);
    }

    /**
     *
     * <p>Rotates this entity to face the given position.</p>
     * <p><b>Make sure to call this after the position has been set</b></p>
     *
     * @param target Position for this entity to look at
     * @return This builder
     */
    public B facing(Vec3 target) {
        EntityUtils.face(this.entity, target);
        return self();
    }

    /**
     * <p>Rotates this entity to face the given entity. Uses the other entity's {@link Entity#getEyePosition()}.</p>
     *
     * <p><b>Make sure to call this after the position has been set</b></p>
     *
     * @param target Position for this entity to look at
     * @return This builder
     */
    public B facing(Entity target) {
        return facing(target.getEyePosition());
    }

    /**
     * <p>Adds a glowing outline to this entity. Colour is pulled from an entity's team, or white as default.</p>
     * <p>If using this builder for an entity lie, then a glowing outline can be added using
     * {@link red.jackf.jackfredlib.api.lying.entity.EntityLie.Builder#glowColour(ChatFormatting)} instead.</p>
     *
     * @apiNote Does not show up for all entities (e.g. text displays, which have their own glowing mechanics).
     * @param shouldGlow Whether this entity have a glowing outline
     * @return This builder
     */
    public B glowing(boolean shouldGlow) {
        this.entity.setGlowingTag(shouldGlow);
        return self();
    }

    /**
     * Adds a custom name to this entity. Applies to any entity.
     * @param name Name to give this entity, or <code>null</code> to remove a custom name
     * @return This builder
     */
    public B customName(@Nullable Component name) {
        this.entity.setCustomName(name);
        return self();
    }

    /**
     * Forces a name plate to show above this entity.
     * @param alwaysRenderName Whether this entity's name should always display
     * @return This builder
     */
    public B alwaysRenderName(boolean alwaysRenderName) {
        this.entity.setCustomNameVisible(alwaysRenderName);
        return self();
    }

    /**
     * Returns <code>this</code> with the correct type, for Java generics shenanigans.
     * @return <code>this</code>
     */
    @ApiStatus.Internal
    protected abstract B self();
}
