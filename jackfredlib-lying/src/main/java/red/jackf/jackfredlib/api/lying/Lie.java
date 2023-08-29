package red.jackf.jackfredlib.api.lying;

import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.ApiStatus;

/**
 * Represents a fake piece of information to be sent to a player. The same <code>Lie</code> instance can be sent to
 * multiple players safely.
 */
public interface Lie<L extends Lie<L>> {

    /**
     * Remove this lie from a player's client. Should not be called directly, as it does not correctly update tracking
     * in {@link red.jackf.jackfredlib.impl.lying.LiesImpl}
     * @param activeLie Active lie instance that is being faded
     */
    @ApiStatus.Internal
    void fade(ActiveLie<L> activeLie);

    /**
     * Called when this lie is sent to a new player. Used to set up update hooks if needed.
     * @param player Player being set up with this lie
     */
    @ApiStatus.Internal
    default void setup(ServerPlayer player) {}
}
