package red.jackf.jackfredlib.api.lying.entity;

import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import red.jackf.jackfredlib.api.lying.Lie;
import red.jackf.jackfredlib.impl.lying.entity.EntityLieImpl;
import red.jackf.jackfredlib.impl.lying.faketeams.FakeTeamUtil;

/**
 * <p>Represents an active Entity Lie being sent to one or more players.</p>
 * <p>An entity lie is a fake entity that is sent to the player on the packet level. They do not exist on the server, but
 * can be interacted with by players viewing the lie.</p>
 *
 * @see red.jackf.jackfredlib.api.lying.Lie
 */
public interface EntityLie extends Lie {
    /**
     * The colour of this fake entity's glowing outline.
     *
     * @return The colour of this fake entity's glowing outline, or <code>null</code> if there isn't one.
     */
    @Nullable ChatFormatting glowColour();

    /**
     * Sets this fake entity's glowing outline colour, by sending a fake team to the player.
     *
     * @param colour Colour to set the outline colour to. If <code>null</code>, removes the outline and the entity's
     *               glowing tag, otherwise sets the outline to the specified colour and marks the entity's glowing tag
     *               as true. If not null but not {@link ChatFormatting#isColor()}, then defaults to white.
     * @implNote Update's the entity's glowing tag.
     */
    void setGlowColour(@Nullable ChatFormatting colour);

    /**
     * The entity this lie is showing to it's players. Can be updated using other methods, such as the ones in
     * {@link EntityUtils}, and changes will be send accordingly.
     *
     * @return The entity being shown to it's players.
     */
    Entity entity();

    /**
     * Create a new builder for an entity lie.
     *
     * @param entity Entity to base this lie off of.
     * @return A new entity lie builder.
     */
    static Builder builder(Entity entity) {
        return new Builder(entity);
    }

    class Builder {
        private final Entity entity;
        private @Nullable ChatFormatting glowColour = null;
        private @Nullable TickCallback tickCallback = null;
        private @Nullable FadeCallback fadeCallback = null;
        private @Nullable LeftClickCallback leftClickCallback = null;
        private @Nullable RightClickCallback rightClickCallback = null;

        private Builder(Entity entity) {
            this.entity = entity;
        }

        /**
         * Adds or removes a glowing outline to this fake entity.
         *
         * @param colour Colour to set this entity's glowing outline to. If <code>null</code>, removes the outline. If
         *               non-<code>null</code>, but not a colour, sets the outline to white instead.
         * @return This entity lie builder.
         * @implNote Modifies the entity's glowing tag accordingly.
         */
        public Builder glowColour(@Nullable ChatFormatting colour) {
            colour = FakeTeamUtil.ensureValidColour(colour);
            if (colour == null) {
                this.entity.setGlowingTag(false);
                this.glowColour = null;
                return this;
            } else {
                this.entity.setGlowingTag(true);
                this.glowColour = colour;
                return this;
            }
        }

        /**
         * Adds an on-tick callback to this entity lie. This is called once per tick, per player in this lie.
         *
         * @param callback Callback to add to this lie. If <code>null</code>, removes the callback.
         * @return This entity lie builder.
         */
        public Builder onTick(@Nullable TickCallback callback) {
            this.tickCallback = callback;
            return this;
        }

        /**
         * Adds a callback to be ran when this lie is removed from a player. Practically, this is called when:
         * <li>The lie's {@link Lie#fade()} method is called, for each player;</li>
         * <li>A player is removed using {@link Lie#removePlayer(ServerPlayer)};</li>
         * <li>A player disconnects from the server.</li>
         *
         * @param callback Callback to add to this lie. If <code>null</code>, removes the callback.
         * @return This entity lie builder.
         */
        public Builder onFade(@Nullable FadeCallback callback) {
            this.fadeCallback = callback;
            return this;
        }

        /**
         * Called when a player viewing this lie left clicks on it.
         *
         * @param callback Callback to add to this lie. If <code>null</code>, removes the callback.
         * @return This entity lie builder.
         */
        public Builder onLeftClick(@Nullable LeftClickCallback callback) {
            this.leftClickCallback = callback;
            return this;
        }

        /**
         * Called when a player viewing this lie right clicks on it.
         *
         * @param callback Callback to add to this lie. If <code>null</code>, removes the callback.
         * @return This entity lie builder.
         */
        public Builder onRightClick(@Nullable RightClickCallback callback) {
            this.rightClickCallback = callback;
            return this;
        }

        /**
         * Create this entity lie, and show it to the given set of initial players.
         *
         * @param players Players to initially send this lie to.
         * @return The constructed entity lie.
         */
        public EntityLie createAndShow(ServerPlayer... players) {
            var lie = new EntityLieImpl(entity, glowColour, tickCallback, fadeCallback, leftClickCallback, rightClickCallback);
            for (ServerPlayer player : players)
                lie.addPlayer(player);
            return lie;
        }
    }

    /**
     * A callback ran every tick for every player viewing this lie.
     */
    interface TickCallback {
        /**
         * Ran every tick, for every player currently viewing this lie.
         *
         * @param player Player this lie is being ticked with.
         * @param lie    Lie that is being ticked.
         */
        void onTick(ServerPlayer player, EntityLie lie);
    }

    /**
     * A callback ran when this lie is faded. This includes when a player disconnects.
     */
    interface FadeCallback {
        /**
         * Ran when a player is removed from this lie.
         *
         * @param player Player being removed from this lie.
         * @param lie    Lie that the player is being removed from.
         */
        void onFade(ServerPlayer player, EntityLie lie);
    }

    /**
     * A callback ran when this entity lie is left clicked (attacked).
     */
    interface LeftClickCallback {
        /**
         * Ran when a player that is viewing this entity lie left clicks it.
         *
         * @param player           Player that left clicked this entity lie.
         * @param lie              Entity lie that the player left clicked.
         * @param wasSneaking      Whether the player was sneaking when left clicking.
         * @param relativeToEntity Relative position to the entity's position that was left clicked.
         * @implNote <code>relativeToEntity</code> is calculated on the server-side, as this data is not send in the
         * packet. This means there may be a small amount of desync.
         */
        void onLeftClick(ServerPlayer player, EntityLie lie, boolean wasSneaking, Vec3 relativeToEntity);
    }

    /**
     * A callback ran when this entity lie is right clicked (used).
     */
    interface RightClickCallback {
        /**
         * Ran when a player that is viewing this entity lie right clicks it.
         *
         * @param player           Player that right clicked this entity lie.
         * @param lie              Entity lie that the player right clicked.
         * @param wasSneaking      Whether the player was sneaking when right clicking.
         * @param hand             Hand that the player interacted with.
         * @param relativeToEntity Relative position to the entity's position that was right clicked.
         */
        void onRightClick(
                ServerPlayer player, EntityLie lie, boolean wasSneaking, InteractionHand hand, Vec3 relativeToEntity);
    }
}
