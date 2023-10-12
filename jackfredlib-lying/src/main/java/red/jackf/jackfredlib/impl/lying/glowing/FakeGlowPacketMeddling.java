package red.jackf.jackfredlib.impl.lying.glowing;

import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.List;

public class FakeGlowPacketMeddling {
    private FakeGlowPacketMeddling() {}


    // Modify a packet, so as to force glowing if needed by a lie.
    public static ClientboundSetEntityDataPacket modifyPacket(ClientboundSetEntityDataPacket original, EntityGlowLieImpl entityGlowLie) {
        if (entityGlowLie.glowColour() == null) return original;
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
                                                       forceGlowing(entityGlowLie.entity().getEntityData().get(Entity.DATA_SHARED_FLAGS_ID))));
        }
        return new ClientboundSetEntityDataPacket(original.id(), copy);
    }

    private static byte forceGlowing(byte in) {
        return (byte) (in | 1 << Entity.FLAG_GLOWING);
    }
}
