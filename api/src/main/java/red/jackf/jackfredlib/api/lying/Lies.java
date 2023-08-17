package red.jackf.jackfredlib.api.lying;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
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
     * @param entityLie EntityLie to send to given player.
     * @return Active lie instance to manage the given lie.
     * @param <E> Type of entity this EntityLie is based around
     */
    <E extends Entity> ActiveLie<EntityLie<E>> addEntity(ServerPlayer player, EntityLie<E> entityLie);
}
