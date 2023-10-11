package red.jackf.jackfredlib.impl.lying;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;
import red.jackf.jackfredlib.impl.base.LogUtil;
import red.jackf.jackfredlib.impl.lying.entity.EntityLieImpl;
import red.jackf.jackfredlib.impl.lying.glowing.EntityGlowLieImpl;

import java.util.Optional;

public class LieManager {
    public static final Logger LOGGER = LogUtil.getLogger("Lie Manager");
    public static final LieManager INSTANCE = new LieManager();

    private final Multimap<ServerPlayer, EntityGlowLieImpl> entityGlowLies = MultimapBuilder.hashKeys().hashSetValues().build();
    private final Multimap<ServerPlayer, EntityLieImpl> entityLies = MultimapBuilder.hashKeys().hashSetValues().build();

    //////////////
    // All Lies //
    //////////////

    /**
     * Call the tick callbacks for each lie.
     */
    public void tick() {
        this.entityGlowLies.entries().forEach(entry -> entry.getValue().tick(entry.getKey()));
        this.entityLies.entries().forEach(entry -> entry.getValue().tick(entry.getKey()));
    }

    /**
     * Remove all lies for a given player; called on disconnect. Fade callbacks are called.
     * @param player Player to clear lies for.
     */
    public void removeAll(ServerPlayer player) {
        this.entityGlowLies.removeAll(player).forEach(LieImpl::fade);
        this.entityLies.removeAll(player).forEach(LieImpl::fade);
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

    /////////////////
    // Entity Lies //
    /////////////////
    public void addEntity(ServerPlayer player, EntityLieImpl lie) {
        this.entityLies.put(player, lie);
    }

    public void removeEntity(ServerPlayer player, EntityLieImpl lie) {
        this.entityLies.remove(player, lie);
    }

    public Optional<EntityLieImpl> getEntityLieFromEntityId(ServerPlayer player, int entityId) {
        return this.entityLies.get(player).stream().filter(entityLie -> entityLie.entity().getId() == entityId).findFirst();
    }

    //////////////////////
    // Entity Glow Lies //
    //////////////////////
    public void addEntityGlow(ServerPlayer player, EntityGlowLieImpl lie) {
        this.entityGlowLies.put(player, lie);
    }

    public void removeEntityGlow(ServerPlayer player, EntityGlowLieImpl lie) {
        this.entityGlowLies.remove(player, lie);
    }

    public Optional<EntityGlowLieImpl> getEntityGlowLieFromEntityId(ServerPlayer player, int entityId) {
        return entityGlowLies.get(player).stream().filter(glowLie -> glowLie.entity().getId() == entityId).findFirst();
    }
}
