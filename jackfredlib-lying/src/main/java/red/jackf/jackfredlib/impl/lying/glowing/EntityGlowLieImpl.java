package red.jackf.jackfredlib.impl.lying.glowing;

import net.minecraft.ChatFormatting;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import red.jackf.jackfredlib.api.lying.glowing.EntityGlowLie;
import red.jackf.jackfredlib.impl.lying.LieImpl;
import red.jackf.jackfredlib.impl.lying.LieManager;
import red.jackf.jackfredlib.impl.lying.faketeams.FakeTeamManager;

import java.util.List;

public class EntityGlowLieImpl extends LieImpl implements EntityGlowLie {
    private static final EntityDataAccessor<Byte> DATA = Entity.DATA_SHARED_FLAGS_ID;

    private final Entity entity;
    private ChatFormatting colour;
    private final @Nullable TickCallback tickCallback;
    private final @Nullable FadeCallback fadeCallback;

    public EntityGlowLieImpl(Entity entity, ChatFormatting colour, @Nullable TickCallback tickCallback, @Nullable FadeCallback fadeCallback) {
        this.entity = entity;
        this.colour = colour;
        this.tickCallback = tickCallback;
        this.fadeCallback = fadeCallback;
    }

    @Override
    public ChatFormatting glowColour() {
        return colour;
    }

    @Override
    public void setGlowColour(ChatFormatting colour) {
        var newColour = colour.isColor() ? colour : ChatFormatting.WHITE;
        if (newColour != this.colour) {
            this.colour = newColour;

            for (var player : this.getViewingPlayers()) {
                FakeTeamManager.INSTANCE.addToTeam(player, this.entity, this.colour);
            }
        }
    }

    @Override
    public Entity entity() {
        return entity;
    }

    @Override
    public void addPlayer(ServerPlayer player) {
        if (this.getViewingPlayers().contains(player)) return;
        LieManager.INSTANCE.addEntityGlow(player, this);

        super.addPlayer(player);

        var dataValue = new SynchedEntityData.DataValue<>(
                DATA.getId(),
                DATA.getSerializer(),
                (byte) (entity.getEntityData().get(DATA) | (1 << Entity.FLAG_GLOWING))
        );
        player.connection.send(new ClientboundSetEntityDataPacket(
                entity.getId(),
                List.of(dataValue))
        );
        FakeTeamManager.INSTANCE.addToTeam(player, entity, colour);
    }

    @Override
    public void removePlayer(ServerPlayer player) {
        LieManager.INSTANCE.removeEntityGlow(player, this);
        super.removePlayer(player);

        var dataValue = new SynchedEntityData.DataValue<>(
                DATA.getId(),
                DATA.getSerializer(),
                entity.getEntityData().get(DATA)
        );
        player.connection.send(new ClientboundSetEntityDataPacket(
                entity.getId(),
                List.of(dataValue))
        );
        FakeTeamManager.INSTANCE.removeFromTeam(player, entity, colour);

        if (this.fadeCallback != null)
            this.fadeCallback.onFade(player, this);
    }

    public void tick(ServerPlayer player) {
        if (this.tickCallback != null)
            this.tickCallback.onTick(player, this);
    }
}
