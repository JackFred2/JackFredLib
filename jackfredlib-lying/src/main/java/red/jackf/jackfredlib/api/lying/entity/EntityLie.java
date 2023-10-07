package red.jackf.jackfredlib.api.lying.entity;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import red.jackf.jackfredlib.api.lying.ActiveLie;
import red.jackf.jackfredlib.api.lying.Lie;
import red.jackf.jackfredlib.impl.lying.entity.EntityLieImpl;

/**
 * A lie consisting of a fake entity on the player's client. Has functionality for handling left and right clicks, both
 * with positional
 *
 * @param <E> Type of entity this lie is based around
 */
public interface EntityLie<E extends Entity> extends Lie<EntityLie<E>> {
    /**
     * Fake entity that this lie is displaying.
     * @return Fake entity that this lie is displaying.
     */
    E entity();

    /**
     * Create a new builder for an entity lie, using the given entity as a base.
     * @param entity Entity to use as a base for this lie.
     * @return {@link EntityLie.Builder} to use for creating a lie.
     * @param <E> Type of entity to build a lie around
     */
    static <E extends Entity> Builder<E> builder(E entity) {
        return new Builder<>(entity);
    }

    /**
     * Builder for an entity lie.
     */
    class Builder<E extends Entity> {
        private final E entity;
        private LeftClickCallback<E> leftClickCallback = null;
        private RightClickCallback<E> rightClickCallback = null;
        private TickCallback<E> tickCallback = null;
        private FadeCallback<E> fadeCallback = null;

        private Builder(E entity) {
            this.entity = entity;
        }

        /**
         * Adds a handler for when the player left-clicks the fake entity. Casts a ray on the server-side, so there may
         * be very slight de-sync on the relative position.
         * @param callback Callback to be run when this entity is left-clicked (attacked). See {@link LeftClickCallback}.
         * @return this entity lie builder
         */
        public Builder<E> onLeftClick(LeftClickCallback<E> callback) {
            this.leftClickCallback = callback;
            return this;
        }

        /**
         * Adds a handler for when the player right-clicks the fake entity. Reads the relative position from the packet,
         * so there should be no de-sync.
         * @param callback Callback to be run when this entity is right-clicked (interacted). See {@link RightClickCallback}.
         * @return this entity lie builder
         */
        public Builder<E> onRightClick(RightClickCallback<E> callback) {
            this.rightClickCallback = callback;
            return this;
        }

        /**
         * Adds a handler which is run for every tick this lie exists.
         * @param callback Callback to be run every tick while not faded.
         * @return this entity lie builder
         */
        public Builder<E> onTick(TickCallback<E> callback) {
            this.tickCallback = callback;
            return this;
        }

        /**
         * Adds a handler which is run when this lie is faded from a player.
         * @param callback Callback to be run when faded.
         * @return this entity lie builder
         */
        public Builder<E> onFade(FadeCallback<E> callback) {
            this.fadeCallback = callback;
            return this;
        }

        /**
         * Create a new entity lie from this builder.
         * @return The built entity lie.
         */
        public EntityLie<E> build() {
            return new EntityLieImpl<>(entity, leftClickCallback, rightClickCallback, tickCallback, fadeCallback);
        }
    }

    /**
     * Callback ran when an entity lie is left-clicked (attacked).
     */
    interface LeftClickCallback<E extends Entity> {
        /**
         * Called when this entity lie is left-clicked. <code>relativeToEntity</code> is calculated on the server, so
         * there may be very slight desync between the client's relative position and the calculated.
         * @param activeLie Active lie instance that was triggered. Contains methods to fade this lie and access the player.
         * @param shiftDown Whether the player was shifting when left-clicking this lie. Read from the packet.
         * @param relativeToEntity Position relative to the fake entity's {@link Entity#position()} that was interacted
         *                         with. Useful for position-specific menus.
         */
        void onLeftClick(ActiveLie<EntityLie<E>> activeLie, boolean shiftDown, Vec3 relativeToEntity);
    }

    /**
     * Callback ran when an entity lie is right-clicked (interacted).
     */
    interface RightClickCallback<E extends Entity> {
        /**
         * Called when this entity lie is right-clicked. <code>relativeToEntity</code> is read from the packet.
         * @param activeLie Active lie instance that was triggered. Contains methods to fade this lie and access the player.
         * @param shiftDown Whether the player was shifting when right-clicking this lie. Read from the packet.
         * @param hand Which hand was used when interacting with this entity.
         * @param relativeToEntity Position relative to the fake entity's {@link Entity#position()} that was interacted
         *                         with. Useful for position-specific menus.
         */
        void onRightClick(ActiveLie<EntityLie<E>> activeLie, boolean shiftDown, InteractionHand hand, Vec3 relativeToEntity);
    }

    /**
     * Callback ran every tick for this entity lie while not faded.
     */
    interface TickCallback<E extends Entity> {
        /**
         * Called every tick this entity lie is active
         * @param activeLie Active lie instance that is being ticked. Contains methods for fading, and access to the base
         *                  lie.
         */
        void onTick(ActiveLie<EntityLie<E>> activeLie);
    }

    /**
     * Callback ran when an active lie is faded
     */
    interface FadeCallback<E extends Entity> {
        /**
         * Called when a lie is faded. Do not call {@link EntityLie#fade(ActiveLie)} for the same lie; this will lead
         * to a stack overflow.
         * @param activeLie Lie which is being faded
         */
        void onFade(ActiveLie<EntityLie<E>> activeLie);
    }

    /**
     * Run this entity lie's left click callback, if present.
     * @param activeEntityLie The active lie from which this was triggered. Contains methods to fade this lie and access
     *                        the interacting player.
     * @param shiftDown Whether the player was shifting when interacting.
     */
    @ApiStatus.Internal
    void onLeftClick(ActiveLie<EntityLie<E>> activeEntityLie, boolean shiftDown);

    /**
     * Run this entity lie's right click callback, if present.
     * @param activeEntityLie The active lie from which this was triggered. Contains methods to fade this lie and access
     *                        the interacting player.
     * @param shiftDown Whether the player was shifting when interacting.
     * @param hand Which hand the player was using on this interaction
     * @param relativeToEntity Position relative to the entity's {@link Entity#position()} that was interacted at
     */
    @ApiStatus.Internal
    void onRightClick(ActiveLie<EntityLie<E>> activeEntityLie, boolean shiftDown, InteractionHand hand, Vec3 relativeToEntity);

    /**
     * Run this entity lie's tick callback, if present. This is called for every player that is seeing this lie.
     * @param activeEntityLie The active lie that this tick callback was from.
     */
    @ApiStatus.Internal
    void onTick(ActiveLie<EntityLie<E>> activeEntityLie);
}
