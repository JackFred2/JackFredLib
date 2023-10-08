package red.jackf.jackfredlib.impl.lying.glowing;

import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import red.jackf.jackfredlib.api.lying.ActiveLie;
import red.jackf.jackfredlib.api.lying.Lies;
import red.jackf.jackfredlib.api.lying.glowing.EntityGlowLie;
import red.jackf.jackfredlib.impl.lying.Entrypoint;
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
        List<SynchedEntityData.DataValue<?>> copy = new ArrayList<>(original.packedItems().size());
        for (SynchedEntityData.DataValue<?> packedItem : original.packedItems()) {
            if (packedItem.id() == Entity.DATA_SHARED_FLAGS_ID.getId()) {
                byte value = (byte) packedItem.value();
                value |= 1 << Entity.FLAG_GLOWING;
                copy.add(new SynchedEntityData.DataValue<>(packedItem.id(), Entity.DATA_SHARED_FLAGS_ID.getSerializer(), value));
                Entrypoint.LOGGER.debug("adding glow to packet");
            } else {
                copy.add(packedItem);
            }
        }
        return new ClientboundSetEntityDataPacket(original.id(), copy);
    }

    @Override
    protected void removeFromLie() {
        LiesImpl.INSTANCE.removeEntityGlow(this);
    }
}
