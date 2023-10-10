package red.jackf.jackfredlib.api.lying;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import red.jackf.jackfredlib.api.lying.entity.EntityLie;
import red.jackf.jackfredlib.api.lying.glowing.EntityGlowLie;
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
     * Add a new fake entity lie to a given player. This entity does not exist on the server, but is instead sent to the
     * player on the packet level.
     *
     * @param player Player to send this lie to.
     * @param entityLie EntityLie to send to given player.
     * @return Active lie instance used to manage the given lie.
     * @param <E> Type of entity this EntityLie is based around
     */
    <E extends Entity> ActiveLie<EntityLie<E>> addEntity(ServerPlayer player, EntityLie<E> entityLie);

    /**
     * Adds a glowing outline to an entity for the given player. The entity may exist on the server or be from an
     * {@link EntityLie}, but in either case the glow is sent to the player on the packet level.
     *
     * @implNote <p>This functions by sending a team with the specified colour to the player and adding the entity to said
     * team. This team does not exist on the server, and so should not interfere with any other team-based operations.</p>
     * <p>This method may temporarily remove an entity from a team from the client's perspective, which may cause issues
     * with collisions and visibility.</p>
     * @param player Player to send the outline to.
     * @param entityGlowLie EntityGlowLie to send to the player, specifying which entity and colour.
     * @return Active lie instance used to manage the given lie.
     */
    ActiveLie<EntityGlowLie> addEntityGlow(ServerPlayer player, EntityGlowLie entityGlowLie);
}
