package red.jackf.jackfredlib.api.lying;

import net.minecraft.server.level.ServerPlayer;

import java.util.Set;

public interface Lie {
    void fade();

    boolean hasFaded();

    Set<ServerPlayer> getViewingPlayers();

    void addPlayer(ServerPlayer player);

    void removePlayer(ServerPlayer player);
}
