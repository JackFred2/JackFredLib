package red.jackf.jackfredlib.api.lying;

import net.minecraft.server.level.ServerPlayer;
import red.jackf.jackfredlib.impl.lying.LiesImpl;

/**
 * Represents an active lie being shown to a given player.
 */
public record ActiveLie<L extends Lie>(ServerPlayer player, L lie) {

    /**
     * Fade this lie. Removes this lie from tracking, and from the given player's client.
     */
    public void fade() {
        LiesImpl.LOGGER.debug("Fading lie for {}", player.getName());
        this.lie.fade(player);
        LiesImpl.INSTANCE.remove(this);
    }
}
