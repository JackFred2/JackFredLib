package red.jackf.jackfredlib.api.lying.glowing;

import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import red.jackf.jackfredlib.api.lying.Lie;
import red.jackf.jackfredlib.impl.lying.faketeams.FakeTeamUtil;
import red.jackf.jackfredlib.impl.lying.glowing.EntityGlowLieImpl;

/**
 * <p>Represents an active Entity Glow Lie being sent to one or more players.</p>
 * <p>An entity glow lie adds a glowing border to an existing entity, via faking an entity's glowing packet and team.
 * This allows you to highlight entities without affecting server-side gameplay.</p>
 */
public interface EntityGlowLie<E extends Entity> extends Lie {
    /**
     * Gets the current outline colour for this lie's entity.
     *
     * @return The entity's current outline colour, or <code>null</code> if none.
     */
    @Nullable ChatFormatting glowColour();

    /**
     * Sets this entity's outline colour to the given colour. If the passed <code>ChatFormatting</code> is not a colour,
     * defaults to white. If the passed colour is <code>null</code>, removes the outline.
     *
     * @param colour Colour to set the outline to. Pass <code>null</code> to remove the outline.
     */
    void setGlowColour(@Nullable ChatFormatting colour);

    /**
     * Entity that this lie is highlighting. This may not return the same instance over the lie's lifespan due to chunk
     * load/unloading.
     *
     * @return Entity that this lie is highlighting.
     */
    E entity();

    /**
     * Create a new builder for an entity glow lie.
     *
     * @param entity Entity to create a fake outline for.
     * @return A new glow lie builder for the given entity.
     * @param <E> Type of entity to make a lie of.
     */
    static <E extends Entity> Builder<E> builder(E entity) {
        return new Builder<>(entity);
    }

    /**
     * Class for building an entity glow lie.
     */
    class Builder<E extends Entity> {
        private final E entity;
        private @Nullable ChatFormatting initialColour = ChatFormatting.WHITE;
        private @Nullable TickCallback<E> tickCallback = null;
        private @Nullable FadeCallback<E> fadeCallback = null;

        private Builder(E entity) {
            this.entity = entity;
        }

        /**
         * Sets the initial colour for this entity's glowing outline.
         *
         * @param colour Colour to set the outline to. If the passed <code>ChatFormatting</code> is not a colour,
         *               defaults to white. If the passed colour is <code>null</code>, removes the outline.
         * @return This entity glow lie builder.
         */
        public Builder<E> colour(@Nullable ChatFormatting colour) {
            this.initialColour = FakeTeamUtil.ensureValidColour(colour);
            return this;
        }

        /**
         * Adds an on-tick callback to this entity lie. This is called once per tick, per player in this lie.
         *
         * @param callback Callback to add to this lie. If <code>null</code>, removes the callback.
         * @return This entity glow lie builder.
         */
        public Builder<E> onTick(TickCallback<E> callback) {
            this.tickCallback = callback;
            return this;
        }

        /**
         * Adds a callback to be ran when this lie is removed from a player. Practically, this is called when:
         * <ul>
             * <li>The lie's {@link Lie#fade()} method is called, for each player;</li>
             * <li>A player is removed using {@link Lie#removePlayer(ServerPlayer)};</li>
             * <li>A player disconnects from the server.</li>
         * </ul>
         *
         * @param callback Callback to add to this lie. If <code>null</code>, removes the callback.
         * @return This entity glow lie builder.
         */
        public Builder<E> onFade(FadeCallback<E> callback) {
            this.fadeCallback = callback;
            return this;
        }

        /**
         * Create this entity glow lie, and show it to the given set of initial players.
         *
         * @param players Players to initially send this lie to.
         * @return The constructed entity glow lie.
         */
        public EntityGlowLie<E> createAndShow(ServerPlayer... players) {
            var lie = new EntityGlowLieImpl<>(entity, initialColour, tickCallback, fadeCallback);
            for (ServerPlayer player : players)
                lie.addPlayer(player);
            return lie;
        }
    }

    /**
     * A callback ran every tick for every player viewing this lie.
     */
    interface TickCallback<E extends Entity> {
        /**
         * Ran every tick, for every player currently viewing this lie.
         *
         * @param player Player this lie is being ticked with.
         * @param lie    Lie that is being ticked.
         */
        void onTick(ServerPlayer player, EntityGlowLie<E> lie);
    }

    /**
     * A callback ran when this lie is faded. This includes when a player disconnects.
     */
    interface FadeCallback<E extends Entity> {
        /**
         * Ran when a player is removed from this lie.
         *
         * @param player Player being removed from this lie.
         * @param lie    Lie that the player is being removed from.
         */
        void onFade(ServerPlayer player, EntityGlowLie<E> lie);
    }
}
