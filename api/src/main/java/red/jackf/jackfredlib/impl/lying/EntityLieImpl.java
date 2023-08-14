package red.jackf.jackfredlib.impl.lying;

import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import red.jackf.jackfredlib.api.lying.ActiveLie;
import red.jackf.jackfredlib.api.lying.entity.EntityLie;

import java.util.ArrayList;
import java.util.List;

public final class EntityLieImpl implements EntityLie {
    private final Entity entity;
    private final ServerEntity serverEntity;
    private final List<ServerGamePacketListenerImpl> connections = new ArrayList<>();
    private final LeftClickCallback leftClickCallback;
    private final RightClickCallback rightClickCallback;
    private final TickCallback tickCallback;
    private final FadeCallback fadeCallback;

    public EntityLieImpl(Entity entity, LeftClickCallback leftClickCallback, RightClickCallback rightClickCallback, TickCallback tickCallback, FadeCallback fadeCallback) {
        this.entity = entity;
        this.serverEntity = new ServerEntity((ServerLevel) entity.level(),
                entity,
                entity.getType().updateInterval(),
                entity.getType().trackDeltas(),
                packet -> connections.forEach(c -> c.send(packet)));
        this.leftClickCallback = leftClickCallback;
        this.rightClickCallback = rightClickCallback;
        this.tickCallback = tickCallback;
        this.fadeCallback = fadeCallback;
    }

    public Entity entity() {
        return entity;
    }

    @Override
    public void onLeftClick(ActiveLie<EntityLie> activeEntityLie, boolean shiftDown) {
        if (leftClickCallback != null) {
            var from = activeEntityLie.player().getEyePosition();
            var to = from.add(activeEntityLie.player().getLookAngle().scale(6));
            var collisionPoint = activeEntityLie.lie().entity().getBoundingBox().clip(from, to);
            collisionPoint.ifPresent(hit ->
                leftClickCallback.onLeftClick(activeEntityLie, shiftDown, hit.subtract(activeEntityLie.lie().entity().position())));
        }
    }

    @Override
    public void onRightClick(ActiveLie<EntityLie> activeEntityLie, boolean shiftDown, InteractionHand hand, Vec3 relativeToEntity) {
        if (rightClickCallback != null)
            rightClickCallback.onRightClick(activeEntityLie, shiftDown, hand, relativeToEntity);
    }

    @Override
    public void fade(ActiveLie<EntityLie> activeLie) {
        activeLie.player().connection.send(new ClientboundRemoveEntitiesPacket(entity.getId()));
        connections.remove(activeLie.player().connection);
        if (fadeCallback != null)
            fadeCallback.onFade(activeLie);
    }

    @Override
    public void setup(ServerPlayer player) {
        connections.add(player.connection);
    }

    @Override
    public void onTick(ActiveLie<EntityLie> activeEntityLie) {
        this.serverEntity.sendChanges();
        if (tickCallback != null)
            tickCallback.onTick(activeEntityLie);
    }
}
