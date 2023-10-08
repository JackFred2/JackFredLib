package red.jackf.jackfredlib.impl.lying.glowing;

import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import red.jackf.jackfredlib.api.lying.ActiveLie;
import red.jackf.jackfredlib.api.lying.Lies;
import red.jackf.jackfredlib.api.lying.glowing.EntityGlowLie;
import red.jackf.jackfredlib.impl.lying.LiesImpl;

import java.util.ArrayList;
import java.util.List;

public class ActiveEntityGlowLie extends ActiveLie<EntityGlowLie> {
    /**
     * Create a new active lie instance. Should not be used directly; use a method in {@link Lies}
     *
     * @param player Player to send the lie to
     * @param lie    Lie to send to said player
     */
    public ActiveEntityGlowLie(ServerPlayer player, EntityGlowLie lie) {
        super(player, lie);
    }

    public ClientboundSetEntityDataPacket modifyPacket(ClientboundSetEntityDataPacket original) {
        List<SynchedEntityData.DataValue<?>> copy = new ArrayList<>(original.packedItems().size() + 1);
        boolean hasAddedGlowing = false;
        for (SynchedEntityData.DataValue<?> packedItem : original.packedItems()) {
            if (packedItem.id() == Entity.DATA_SHARED_FLAGS_ID.getId()) {
                hasAddedGlowing = true;
                copy.add(new SynchedEntityData.DataValue<>(Entity.DATA_SHARED_FLAGS_ID.getId(),
                                                           Entity.DATA_SHARED_FLAGS_ID.getSerializer(),
                                                           forceGlowing((byte) packedItem.value())));
            } else {
                copy.add(packedItem);
            }
        }
        if (!hasAddedGlowing) {
            copy.add(new SynchedEntityData.DataValue<>(Entity.DATA_SHARED_FLAGS_ID.getId(),
                                              Entity.DATA_SHARED_FLAGS_ID.getSerializer(),
                                              forceGlowing(lie().entity().getEntityData().get(Entity.DATA_SHARED_FLAGS_ID))));
        }
        return new ClientboundSetEntityDataPacket(original.id(), copy);
    }

    private static byte forceGlowing(byte in) {
        return (byte) (in | 1 << Entity.FLAG_GLOWING);
    }

    @Override
    protected void removeFromLie() {
        LiesImpl.INSTANCE.removeEntityGlow(this);
    }
}
