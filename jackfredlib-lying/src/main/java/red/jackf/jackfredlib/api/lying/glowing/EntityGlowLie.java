package red.jackf.jackfredlib.api.lying.glowing;

import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import red.jackf.jackfredlib.api.lying.Lie;
import red.jackf.jackfredlib.impl.lying.glowing.EntityGlowLieImpl;

/**
 * <p>Represents an active Entity Glow Lie being sent to one or more players.</p>
 * <p>An entity glow lie adds a glowing border to an existing entity, via faking an entity's glowing packet and team.
 * This allows you to highlight entities without affecting server-side gameplay.</p>
 */
public interface EntityGlowLie extends Lie {
    /**
     * Gets the current outline colour for this lie's entity.
     *
     * @return The entity's current outline colour.
     */
    ChatFormatting glowColour();

    /**
     * Sets this entity's outline colour to the given colour. If the passed <code>ChatFormatting</code> is not a colour,
     * defaults to white.
     *
     * @param colour Colour to set the outline to.
     */
    void setGlowColour(ChatFormatting colour);

    /**
     * Entity that this lie is highlighting.
     *
     * @return Entity that this lie is highlighting.
     */
    Entity entity();

    /**
     * Create a new builder for an entity glow lie.
     *
     * @param entity Entity to create a fake outline for.
     * @return A new glow lie builder for the given entity.
     */
    static Builder builder(Entity entity) {
        return new Builder(entity);
    }

    class Builder {
        private final Entity entity;
        private ChatFormatting colour = ChatFormatting.WHITE;
        private @Nullable TickCallback tickCallback = null;
        private @Nullable FadeCallback fadeCallback = null;

        private Builder(Entity entity) {
            this.entity = entity;
        }

        /**
         * Sets the colour for this entity's glowing outline.
         *
         * @param colour Colour to set the outline to. If the passed <code>ChatFormatting</code> is not a colour,
         *               defaults to white.
         * @return This entity glow lie builder.
         */
        public Builder colour(ChatFormatting colour) {
            this.colour = colour.isColor() ? colour : ChatFormatting.WHITE;
            return this;
        }

        /**
         * Adds an on-tick callback to this entity lie. This is called once per tick, per player in this lie.
         *
         * @param callback Callback to add to this lie. If <code>null</code>, removes the callback.
         * @return This entity glow lie builder.
         */
        public Builder onTick(TickCallback callback) {
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
         * @return This entity glow lie builder.
         */
        public Builder onFade(FadeCallback callback) {
            this.fadeCallback = callback;
            return this;
        }

        /**
         * Create this entity glow lie, and show it to the given set of initial players.
         *
         * @param players Players to initially send this lie to.
         * @return The constructed entity glow lie.
         */
        public EntityGlowLie createAndShow(ServerPlayer... players) {
            var lie = new EntityGlowLieImpl(entity, colour, tickCallback, fadeCallback);
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
        void onTick(ServerPlayer player, EntityGlowLie lie);
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
        void onFade(ServerPlayer player, EntityGlowLie lie);
    }
}
