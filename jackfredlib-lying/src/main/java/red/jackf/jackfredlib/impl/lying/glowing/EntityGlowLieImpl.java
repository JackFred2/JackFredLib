package red.jackf.jackfredlib.impl.lying.glowing;

import net.minecraft.ChatFormatting;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import red.jackf.jackfredlib.api.lying.ActiveLie;
import red.jackf.jackfredlib.api.lying.glowing.EntityGlowLie;

import java.util.List;

public class EntityGlowLieImpl implements EntityGlowLie {
    private static final EntityDataAccessor<Byte> DATA = Entity.DATA_SHARED_FLAGS_ID;

    private final Entity entity;
    private ChatFormatting colour;

    public EntityGlowLieImpl(Entity entity, ChatFormatting colour) {
        this.entity = entity;
        this.colour = colour;
    }

    @Override
    public void fade(ActiveLie<EntityGlowLie> activeLie) {
        var dataValue = new SynchedEntityData.DataValue<>(
                DATA.getId(),
                DATA.getSerializer(),
                entity.getEntityData().get(DATA)
        );
        activeLie.player().connection.send(new ClientboundSetEntityDataPacket(
                entity.getId(),
                List.of(dataValue))
        );
    }

    @Override
    public void setup(ServerPlayer player) {
        var dataValue = new SynchedEntityData.DataValue<>(
                DATA.getId(),
                DATA.getSerializer(),
                (byte) (entity.getEntityData().get(DATA) | (1 << Entity.FLAG_GLOWING))
        );
        player.connection.send(new ClientboundSetEntityDataPacket(
                entity.getId(),
                List.of(dataValue))
        );
    }

    @Override
    public Entity entity() {
        return entity;
    }
}
