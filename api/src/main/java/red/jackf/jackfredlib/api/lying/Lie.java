package red.jackf.jackfredlib.api.lying;

import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.ApiStatus;

/**
 * Represents a fake piece of information to be sent to a player. The same <code>Lie</code> instance can be sent to
 * multiple players safely.
 */
public interface Lie {
    /**
     * Remove this lie from a player's client. Should not be called directly, as it does not correctly update tracking
     * in {@link red.jackf.jackfredlib.impl.lying.LiesImpl}
     * @param player Player to fade this lie from.
     */
    @ApiStatus.Internal
    void fade(ServerPlayer player);
}
