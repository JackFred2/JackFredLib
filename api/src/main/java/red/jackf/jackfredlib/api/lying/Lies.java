package red.jackf.jackfredlib.api.lying;

import net.minecraft.server.level.ServerPlayer;
import red.jackf.jackfredlib.api.lying.entity.EntityLie;
import red.jackf.jackfredlib.impl.lying.LiesImpl;

/**
 * Main access for creating and managing lies.
 */
public interface Lies {
    /**
     * Instance of the lie manager. Use this as an access point.
     */
    Lies INSTANCE = LiesImpl.INSTANCE;

    /**
     * Add a new entity lie to a given player.
     * @param player Player to send this lie to.
     * @param entityLie Lie to send to given player.
     * @return Active lie instance to manage the given lie.
     */
    ActiveLie<EntityLie> addEntity(ServerPlayer player, EntityLie entityLie);
}
