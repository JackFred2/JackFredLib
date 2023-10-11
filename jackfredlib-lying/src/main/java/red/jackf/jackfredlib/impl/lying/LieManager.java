package red.jackf.jackfredlib.impl.lying;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;
import red.jackf.jackfredlib.impl.base.LogUtil;
import red.jackf.jackfredlib.impl.lying.glowing.EntityGlowLieImpl;

import java.util.Optional;

public class LieManager {
    public static final Logger LOGGER = LogUtil.getLogger("Lie Manager");
    public static final LieManager INSTANCE = new LieManager();

    private final Multimap<ServerPlayer, EntityGlowLieImpl> glowLies = MultimapBuilder.hashKeys().hashSetValues().build();

    public void tick() {
        this.glowLies.entries().forEach(entry -> entry.getValue().tick(entry.getKey()));
    }

    //////////////////////
    // Entity Glow Lies //
    //////////////////////
    public void addGlowing(ServerPlayer player, EntityGlowLieImpl lie) {
        this.glowLies.put(player, lie);
    }

    public void remove(ServerPlayer player, EntityGlowLieImpl lie) {
        this.glowLies.remove(player, lie);
    }

    public void removeAll(ServerPlayer player) {
        this.glowLies.removeAll(player).forEach(LieImpl::fade);
    }

    /**
     * Migrate a player's lies from an old ServerPlayer instance to a new one, in the case of death / game victory
     */
    public void migratePlayerInstance(ServerPlayer oldPlayer, ServerPlayer newPlayer) {
        var playersLies = glowLies.removeAll(oldPlayer);
        playersLies.forEach(glowLie -> glowLie.migratePlayerInstance(oldPlayer, newPlayer));
        glowLies.putAll(newPlayer, playersLies);
    }

    public Optional<EntityGlowLieImpl> getEntityGlowLieFromEntityId(ServerPlayer player, int entityId) {
        return glowLies.get(player).stream().filter(glowLie -> glowLie.entity().getId() == entityId).findFirst();
    }
}
