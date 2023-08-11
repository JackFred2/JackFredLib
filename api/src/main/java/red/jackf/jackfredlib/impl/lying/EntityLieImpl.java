package red.jackf.jackfredlib.impl.lying;

import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import red.jackf.jackfredlib.api.lying.ActiveLie;
import red.jackf.jackfredlib.api.lying.entity.EntityLie;

public final class EntityLieImpl implements EntityLie {
    private final Entity entity;
    private final LeftClickCallback leftClickCallback;
    private final RightClickCallback rightClickCallback;

    public EntityLieImpl(Entity entity, LeftClickCallback leftClickCallback, RightClickCallback rightClickCallback) {
        this.entity = entity;
        this.leftClickCallback = leftClickCallback;
        this.rightClickCallback = rightClickCallback;
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
    public void fade(ServerPlayer player) {
        player.connection.send(new ClientboundRemoveEntitiesPacket(entity.getId()));
    }
}
