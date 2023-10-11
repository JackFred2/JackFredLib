package red.jackf.jackfredlib.api.lying;

import net.minecraft.server.level.ServerPlayer;

import java.util.Set;

/**
 * Represents an active lie that is being sent to one or more players.
 */
public interface Lie {
    /**
     * Remove this lie. This removes this lie from all players, and prevents this lie from activating again.
     */
    void fade();

    /**
     * Whether this lie has been {@link #fade()}d.
     * @return Whether this lie has been faded.
     */
    boolean hasFaded();

    /**
     * Get an unmodifiable set of all players currently seeing this lie.
     * @return A set of all players who are currently seeing this lie.
     */
    Set<ServerPlayer> getViewingPlayers();

    /**
     * Add a player to this lie. Fails if this lie has been faded.
     * @param player Player to add to this lie.
     */
    void addPlayer(ServerPlayer player);

    /**
     * Remove a player from viewing this lie. Also called for all visible players when {@link #fade()} is called.
     * @param player Player to remove from viewing this lie.
     */
    void removePlayer(ServerPlayer player);
}
