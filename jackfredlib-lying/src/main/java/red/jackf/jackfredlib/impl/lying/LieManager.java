package red.jackf.jackfredlib.impl.lying;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.slf4j.Logger;
import red.jackf.jackfredlib.impl.base.LogUtil;
import red.jackf.jackfredlib.impl.lying.entity.EntityLieImpl;
import red.jackf.jackfredlib.impl.lying.glowing.EntityGlowLieImpl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Handles all lies and their integrity.
 * <p>
 * Note to self:
 * Currently not an issue, but migrating entities for entity glow lies is currently O(N) of the number of active lies,
 * for every new entity loaded. If it becomes an issue, possibly use a Multiset to reduce this
 */
public class LieManager {
    public static final Logger LOGGER = LogUtil.getLogger("Lie Manager");
    public static final LieManager INSTANCE = new LieManager();

    private final Multimap<ServerPlayer, EntityGlowLieImpl<?>> entityGlowLies = MultimapBuilder.hashKeys().hashSetValues().build();
    private final Multimap<ServerPlayer, EntityLieImpl<?>> entityLies = MultimapBuilder.hashKeys().hashSetValues().build();

    //////////////
    // All Lies //
    //////////////

    /**
     * Call the tick callbacks for each lie.
     */
    public void tick() {
        if (!this.entityGlowLies.isEmpty())
            List.copyOf(this.entityGlowLies.entries()).forEach(entry -> entry.getValue().tick(entry.getKey()));
        if (!this.entityLies.isEmpty())
            List.copyOf(this.entityLies.entries()).forEach(entry -> entry.getValue().tick(entry.getKey()));
    }

    /**
     * Remove all lies for a given player; called on disconnect. Fade callbacks are called.
     * @param player Player to clear lies for.
     */
    public void removeAll(ServerPlayer player) {
        this.entityGlowLies.removeAll(player).forEach(lie -> lie.removePlayer(player));
        this.entityLies.removeAll(player).forEach(lie -> lie.removePlayer(player));
    }

    /**
     * Migrate a player's lies from an old ServerPlayer instance to a new one, in the case of death / game victory
     */
    public void migratePlayerInstance(ServerPlayer oldPlayer, ServerPlayer newPlayer) {
        _migratePlayerInstance(this.entityGlowLies, oldPlayer, newPlayer);
        _migratePlayerInstance(this.entityLies, oldPlayer, newPlayer);
    }

    private static <L extends LieImpl> void _migratePlayerInstance(Multimap<ServerPlayer, L> lieMap, ServerPlayer oldPlayer, ServerPlayer newPlayer) {
        var playersLies = lieMap.removeAll(oldPlayer);
        for (L glowLie : playersLies) {
            glowLie.migratePlayerInstance(oldPlayer, newPlayer);
        }
        lieMap.putAll(newPlayer, playersLies);
    }

    /**
     * Update all entity glow lies to use the new entity instance by checking via UUID; used in case of entity reload.
     * @param entity Entity to update to
     */
    public void migrateEntity(Entity entity) {
        for (EntityGlowLieImpl<?> entityGlowLie : entityGlowLies.values())
            if (entityGlowLie.entity().getUUID().equals(entity.getUUID()) && entityGlowLie.entity().getClass() == entity.getClass())
                LieManager.doMigrateEntity(entityGlowLie, entity);
    }

    private static <E extends Entity> void doMigrateEntity(EntityGlowLieImpl<E> lie, Entity entity) {
        //noinspection unchecked
        lie.setEntity((E) entity);
    }

    /////////////////
    // Entity Lies //
    /////////////////
    public void addEntity(ServerPlayer player, EntityLieImpl<?> lie) {
        this.entityLies.put(player, lie);
    }

    public void removeEntity(ServerPlayer player, EntityLieImpl<?> lie) {
        this.entityLies.remove(player, lie);
    }

    public Optional<EntityLieImpl<?>> getEntityLieFromEntityUuid(ServerPlayer player, UUID uuid) {
        return this.entityLies.get(player).stream().filter(entityLie -> entityLie.entity().getUUID().equals(uuid)).findFirst();
    }

    public Optional<EntityLieImpl<?>> getEntityLieFromId(ServerPlayer player, int id) {
        return this.entityLies.get(player).stream().filter(entityLie -> entityLie.entity().getId() == id).findFirst();
    }

    //////////////////////
    // Entity Glow Lies //
    //////////////////////
    public void addEntityGlow(ServerPlayer player, EntityGlowLieImpl<?> lie) {
        this.entityGlowLies.put(player, lie);
    }

    public void removeEntityGlow(ServerPlayer player, EntityGlowLieImpl<?> lie) {
        this.entityGlowLies.remove(player, lie);
    }

    public Optional<EntityGlowLieImpl<?>> getEntityGlowLieFromEntityUuid(ServerPlayer player, UUID uuid) {
        return entityGlowLies.get(player).stream().filter(glowLie -> glowLie.entity().getUUID().equals(uuid)).findFirst();
    }
}
