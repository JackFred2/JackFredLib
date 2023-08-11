package red.jackf.jackfredlib.impl.lying;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.mojang.authlib.GameProfile;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import red.jackf.jackfredlib.api.lying.ActiveLie;
import red.jackf.jackfredlib.api.lying.Lies;
import red.jackf.jackfredlib.api.lying.entity.EntityLie;

import java.util.Optional;

public class LiesImpl implements Lies {
    public static final LiesImpl INSTANCE = new LiesImpl();

    private final Multimap<GameProfile, ActiveLie<EntityLie>> activeEntityLies = ArrayListMultimap.create();

    @Override
    public ActiveLie<EntityLie> addEntity(ServerPlayer player, EntityLie entityLie) {
        var active = new ActiveLie<>(player, entityLie);
        activeEntityLies.put(player.getGameProfile(), active);

        player.connection.send(new ClientboundAddEntityPacket(entityLie.entity()));
        var data = entityLie.entity().getEntityData().packDirty();
        if (data != null)
            player.connection.send(new ClientboundSetEntityDataPacket(entityLie.entity().getId(), data));

        return active;
    }

    public Optional<ActiveLie<EntityLie>> getEntityLieFromId(ServerPlayer player, int entityId) {
        return activeEntityLies.get(player.getGameProfile())
                .stream()
                .filter(active -> active.lie().entity().getId() == entityId)
                .findFirst();
    }

    public void remove(ActiveLie<?> activeLie) {
        activeEntityLies.get(activeLie.player().getGameProfile()).remove(activeLie);
    }
}
