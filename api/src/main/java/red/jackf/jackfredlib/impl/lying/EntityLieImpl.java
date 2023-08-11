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
    private final PositionalRightClickCallback positionalRightClickCallback;

    public EntityLieImpl(Entity entity, LeftClickCallback leftClickCallback, RightClickCallback rightClickCallback, PositionalRightClickCallback positionalRightClickCallback) {
        this.entity = entity;
        this.leftClickCallback = leftClickCallback;
        this.rightClickCallback = rightClickCallback;
        this.positionalRightClickCallback = positionalRightClickCallback;
    }

    public Entity entity() {
        return entity;
    }

    @Override
    public void onLeftClick(ActiveLie<EntityLie> activeEntityLie) {
        if (leftClickCallback != null)
            leftClickCallback.onLeftClick(activeEntityLie);
    }

    @Override
    public void onRightClick(ActiveLie<EntityLie> activeEntityLie, InteractionHand hand) {
        if (rightClickCallback != null)
            rightClickCallback.onRightClick(activeEntityLie, hand);
    }

    @Override
    public void onPositionalRightClick(ActiveLie<EntityLie> activeEntityLie, InteractionHand hand, Vec3 relativeToEntity) {
        if (positionalRightClickCallback != null)
            positionalRightClickCallback.onPositionalRightClick(activeEntityLie, hand, relativeToEntity);
    }

    @Override
    public void fade(ServerPlayer player) {
        player.connection.send(new ClientboundRemoveEntitiesPacket(entity.getId()));
    }
}
