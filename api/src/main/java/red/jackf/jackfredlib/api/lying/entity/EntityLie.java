package red.jackf.jackfredlib.api.lying.entity;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import red.jackf.jackfredlib.api.lying.ActiveLie;
import red.jackf.jackfredlib.api.lying.Lie;
import red.jackf.jackfredlib.impl.lying.EntityLieImpl;

/**
 * A lie consisting of a fake entity on the player's client. Has functionality for handling left and right clicks, with
 * the latter also
 *
 */
public interface EntityLie extends Lie {
    /**
     * Fake entity that this lie is displaying.
     * @return Fake entity that this lie is displaying.
     */
    Entity entity();

    /**
     * Create a new builder for an entity lie, using the given entity as a base.
     * @param entity Entity to use as a base for this lie.
     * @return {@link EntityLie.Builder} to use for creating a lie.
     */
    static Builder builder(Entity entity) {
        return new Builder(entity);
    }

    class Builder {
        private final Entity entity;
        private LeftClickCallback leftClickCallback = null;
        private RightClickCallback rightClickCallback = null;
        private TickCallback tickCallback = null;

        public Builder(Entity entity) {
            this.entity = entity;
        }

        /**
         * Adds a handler for when the player left-clicks the fake entity. Casts a ray on the server-side, so there may
         * be very slight de-sync on the relative position.
         * @param callback Callback to be run when this entity is left-clicked (attacked). See {@link LeftClickCallback}.
         * @return this entity lie builder
         */
        public Builder onLeftClick(LeftClickCallback callback) {
            this.leftClickCallback = callback;
            return this;
        }

        /**
         * Adds a handler for when the player right-clicks the fake entity. Reads the relative position from the packet,
         * so there should be no de-sync.
         * @param callback Callback to be run when this entity is right-clicked (interacted). See {@link RightClickCallback}.
         * @return this entity lie builder
         */
        public Builder onRightClick(RightClickCallback callback) {
            this.rightClickCallback = callback;
            return this;
        }

        /**
         * Adds a handler which is run for every tick this lie exists.
         * @param callback Callback to be run every tick while not faded.
         * @return this entity lie builder
         */
        public Builder onTick(TickCallback callback) {
            this.tickCallback = callback;
            return this;
        }

        /**
         * Create a new entity lie from this builder.
         * @return The built entity lie.
         */
        public EntityLie build() {
            return new EntityLieImpl(entity, leftClickCallback, rightClickCallback, tickCallback);
        }
    }

    /**
     * Callback ran when an entity lie is left-clicked (attacked).
     */
    interface LeftClickCallback {
        /**
         * Called when this entity lie is left-clicked. <code>relativeToEntity</code> is calculated on the server, so
         * there may be very slight desync between the client's relative position and the calculated.
         * @param activeLie Active lie instance that was triggered. Contains methods to fade this lie and access the player.
         * @param shiftDown Whether the player was shifting when left-clicking this lie. Read from the packet.
         * @param relativeToEntity Position relative to the fake entity's {@link Entity#position()} that was interacted
         *                         with. Useful for position-specific menus.
         */
        void onLeftClick(ActiveLie<EntityLie> activeLie, boolean shiftDown, Vec3 relativeToEntity);
    }

    /**
     * Callback ran when an entity lie is right-clicked (interacted).
     */
    interface RightClickCallback {
        /**
         * Called when this entity lie is right-clicked. <code>relativeToEntity</code> is read from the packet.
         * @param activeLie Active lie instance that was triggered. Contains methods to fade this lie and access the player.
         * @param shiftDown Whether the player was shifting when right-clicking this lie. Read from the packet.
         * @param relativeToEntity Position relative to the fake entity's {@link Entity#position()} that was interacted
         *                         with. Useful for position-specific menus.
         */
        void onRightClick(ActiveLie<EntityLie> activeLie, boolean shiftDown, InteractionHand hand, Vec3 relativeToEntity);
    }

    /**
     * Callback ran every tick for this entity lie while not faded.
     */
    interface TickCallback {
        /**
         * Called every tick this entity lie is active
         * @param activeLie Active lie instance that is being ticked. Contains methods for fading, and access to the base
         *                  lie.
         */
        void onTick(ActiveLie<EntityLie> activeLie);
    }

    /**
     * Run this entity lie's left click callback, if present.
     * @param activeEntityLie The active lie from which this was triggered. Contains methods to fade this lie and access
     *                        the interacting player.
     * @param shiftDown Whether the player was shifting when interacting.
     */
    @ApiStatus.Internal
    void onLeftClick(ActiveLie<EntityLie> activeEntityLie, boolean shiftDown);

    /**
     * Run this entity lie's right click callback, if present.
     * @param activeEntityLie The active lie from which this was triggered. Contains methods to fade this lie and access
     *                        the interacting player.
     * @param shiftDown Whether the player was shifting when interacting.
     */
    @ApiStatus.Internal
    void onRightClick(ActiveLie<EntityLie> activeEntityLie, boolean shiftDown, InteractionHand hand, Vec3 relativeToEntity);

    @ApiStatus.Internal
    void onTick(ActiveLie<EntityLie> activeEntityLie);
}
