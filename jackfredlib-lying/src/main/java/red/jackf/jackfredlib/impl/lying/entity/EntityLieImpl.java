package red.jackf.jackfredlib.impl.lying.entity;

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

public final class EntityLieImpl<E extends Entity> implements EntityLie<E> {
    private final E entity;
    private final ServerEntity serverEntity;
    private final List<ServerGamePacketListenerImpl> connections = new ArrayList<>();
    private final LeftClickCallback<E> leftClickCallback;
    private final RightClickCallback<E> rightClickCallback;
    private final TickCallback<E> tickCallback;
    private final FadeCallback<E> fadeCallback;

    public EntityLieImpl(E entity, LeftClickCallback<E> leftClickCallback, RightClickCallback<E> rightClickCallback, TickCallback<E> tickCallback, FadeCallback<E> fadeCallback) {
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

    public E entity() {
        return entity;
    }

    @Override
    public void onLeftClick(ActiveLie<EntityLie<E>> activeEntityLie, boolean shiftDown) {
        if (leftClickCallback != null) {
            var from = activeEntityLie.player().getEyePosition();
            var to = from.add(activeEntityLie.player().getLookAngle().scale(6));
            var collisionPoint = activeEntityLie.lie().entity().getBoundingBox().clip(from, to);
            collisionPoint.ifPresent(hit ->
                leftClickCallback.onLeftClick(activeEntityLie, shiftDown, hit.subtract(activeEntityLie.lie().entity().position())));
        }
    }

    @Override
    public void onRightClick(ActiveLie<EntityLie<E>> activeEntityLie, boolean shiftDown, InteractionHand hand, Vec3 relativeToEntity) {
        if (rightClickCallback != null)
            rightClickCallback.onRightClick(activeEntityLie, shiftDown, hand, relativeToEntity);
    }

    @Override
    public void fade(ActiveLie<EntityLie<E>> activeLie) {
        connections.remove(activeLie.player().connection);
        this.serverEntity.removePairing(activeLie.player());
        if (fadeCallback != null)
            fadeCallback.onFade(activeLie);
    }

    @Override
    public void setup(ServerPlayer player) {
        connections.add(player.connection);
        this.serverEntity.addPairing(player);
    }

    @Override
    public void onTick(ActiveLie<EntityLie<E>> activeEntityLie) {
        this.serverEntity.sendChanges();
        if (tickCallback != null)
            tickCallback.onTick(activeEntityLie);
    }
}
