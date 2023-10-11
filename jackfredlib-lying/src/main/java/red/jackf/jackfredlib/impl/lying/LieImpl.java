package red.jackf.jackfredlib.impl.lying;

import com.google.common.collect.ImmutableSet;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import red.jackf.jackfredlib.api.lying.Lie;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a fake piece of information to be sent to a player.
 */
public abstract class LieImpl implements Lie {
    private final Set<ServerPlayer> players = new HashSet<>();
    private boolean faded = false;

    public LieImpl() {}

    public final void fade() {
        if (this.faded) return;
        this.faded = true;

        for (ServerPlayer player : this.players)
            this.removePlayer(player);
    }

    public final boolean hasFaded() {
        return this.faded;
    }

    @Override
    public Set<ServerPlayer> getViewingPlayers() {
        return ImmutableSet.copyOf(this.players);
    }

    /**
     * Adds a new player to this lie.
     */
    @MustBeInvokedByOverriders
    public void addPlayer(ServerPlayer player) {
        if (this.faded) {
            LieManager.LOGGER.error("Tried adding a player to a faded lie");
            return;
        }
        this.players.add(player);
    }

    @MustBeInvokedByOverriders
    public void removePlayer(ServerPlayer player) {
        this.players.remove(player);
    }

    protected void migratePlayerInstance(ServerPlayer oldPlayer, ServerPlayer newPlayer) {
        if (players.remove(oldPlayer)) players.add(newPlayer);
    }
}
