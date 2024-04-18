package red.jackf.jackfredlib.impl.lying.glowing;

import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.List;

public class FakeGlowPacketMeddling {
    private FakeGlowPacketMeddling() {}


    // Modify a packet, so as to force glowing if needed by a lie.
    public static ClientboundSetEntityDataPacket modifyPacket(ClientboundSetEntityDataPacket original, EntityGlowLieImpl<?> entityGlowLie) {
        boolean shouldGlow = entityGlowLie.glowColour() != null;
        List<SynchedEntityData.DataValue<?>> copy = new ArrayList<>(original.packedItems().size() + 1);
        boolean hasAddedGlowing = false;
        for (SynchedEntityData.DataValue<?> packedItem : original.packedItems()) {
            if (packedItem.id() == Entity.DATA_SHARED_FLAGS_ID.id()) {
                hasAddedGlowing = true;
                byte data = (byte) packedItem.value();
                copy.add(new SynchedEntityData.DataValue<>(Entity.DATA_SHARED_FLAGS_ID.id(),
                                                           Entity.DATA_SHARED_FLAGS_ID.serializer(),
                                                           shouldGlow ? forceGlowing(data) : forceNotGlowing(data)));
            } else {
                copy.add(packedItem);
            }
        }
        if (!hasAddedGlowing) {
            byte entityData = entityGlowLie.entity().getEntityData().get(Entity.DATA_SHARED_FLAGS_ID);
            copy.add(new SynchedEntityData.DataValue<>(Entity.DATA_SHARED_FLAGS_ID.id(),
                                                       Entity.DATA_SHARED_FLAGS_ID.serializer(),
                                                       shouldGlow ? forceGlowing(entityData) : forceNotGlowing(entityData)));
        }
        return new ClientboundSetEntityDataPacket(original.id(), copy);
    }

    protected static byte forceGlowing(byte in) {
        return (byte) (in | 1 << Entity.FLAG_GLOWING);
    }

    protected static byte forceNotGlowing(byte in) {
        return (byte) (in & ~(1 << Entity.FLAG_GLOWING));
    }
}
