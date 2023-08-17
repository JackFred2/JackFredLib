package red.jackf.jackfredlib.api.lying;

import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.ApiStatus;
import red.jackf.jackfredlib.impl.lying.LiesImpl;

/**
 * Represents an active lie being shown to a given player.
 */
public abstract class ActiveLie<L extends Lie<L>> {
    private final ServerPlayer player;
    private final L lie;
    private boolean faded = false;

    /**
     * Create a new active lie instance. Should not be used directly; use a method in {@link Lies}
     *
     * @param player Player to send the lie to
     * @param lie Lie to send to said player
     */
    @ApiStatus.Internal
    public ActiveLie(ServerPlayer player, L lie) {
        this.player = player;
        this.lie = lie;
    }

    /**
     * Fade this lie. Removes this lie from tracking, and from the given player's client.
     */
    public final void fade() {
        if (this.faded) return;
        this.faded = true;
        LiesImpl.LOGGER.debug("Fading lie for {}", player.getName());
        this.lie.fade(this);
        this.removeFromLie();
    }

    /**
     * Whether this active lie instance has already faded; i.e. no longer displayed to the player.
     * @return If this active lie is faded.
     */
    public boolean hasFaded() {
        return this.faded;
    }

    /**
     * The player that this active lie is for (being displayed to).
     * @return Player this active lie instance is for
     */
    public ServerPlayer player() {
        return player;
    }

    /**
     * The lie that is being displayed to the player. May be shared with a different active lie instance.
     * @return Lie being displayed to the given player
     */
    public L lie() {
        return lie;
    }

    @ApiStatus.Internal
    protected abstract void removeFromLie();
}
