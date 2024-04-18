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
import red.jackf.jackfredlib.impl.lying.faketeams.FakeTeamUtil;

import java.util.List;

public class EntityGlowLieImpl<E extends Entity> extends LieImpl implements EntityGlowLie<E> {
    private static final EntityDataAccessor<Byte> DATA = Entity.DATA_SHARED_FLAGS_ID;

    private E entity;
    private ChatFormatting colour;
    private final @Nullable TickCallback<E> tickCallback;
    private final @Nullable FadeCallback<E> fadeCallback;

    public EntityGlowLieImpl(E entity, ChatFormatting colour, @Nullable TickCallback<E> tickCallback, @Nullable FadeCallback<E> fadeCallback) {
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
    public void setGlowColour(@Nullable ChatFormatting colour) {
        var oldColour = this.colour;
        var newColour = FakeTeamUtil.ensureValidColour(colour);
        if (newColour != this.colour) {
            this.colour = newColour;

            for (var player : this.getViewingPlayers()) {
                sendFakeGlowingTagToPlayer(player);
                if (newColour == null) { // old != null
                    FakeTeamManager.INSTANCE.removeFromColourTeam(player, this.entity, oldColour);
                } else {
                    // adding an entity to a team removes it from the old anyway
                    FakeTeamManager.INSTANCE.addToColourTeam(player, this.entity, newColour);
                }
            }
        }
    }

    @Override
    public E entity() {
        return entity;
    }

    public void setEntity(E entity) {
        this.entity = entity;
    }

    private void sendFakeGlowingTagToPlayer(ServerPlayer player) {
        byte data = this.entity.getEntityData().get(DATA);
        SynchedEntityData.DataValue<Byte> dataValue = new SynchedEntityData.DataValue<>(
            DATA.id(),
            DATA.serializer(),
            this.colour != null ? FakeGlowPacketMeddling.forceGlowing(data) : FakeGlowPacketMeddling.forceNotGlowing(data)
        );
        player.connection.send(new ClientboundSetEntityDataPacket(
                this.entity.getId(),
                List.of(dataValue))
        );
    }

    private void restoreOriginalGlowingTagToPlayer(ServerPlayer player) {
        SynchedEntityData.DataValue<Byte> dataValue = new SynchedEntityData.DataValue<>(
                DATA.id(),
                DATA.serializer(),
                this.entity.getEntityData().get(DATA)
        );
        player.connection.send(new ClientboundSetEntityDataPacket(
                this.entity.getId(),
                List.of(dataValue))
        );
    }

    @Override
    public void addPlayer(ServerPlayer player) {
        if (this.getViewingPlayers().contains(player)) return;
        LieManager.INSTANCE.addEntityGlow(player, this);

        super.addPlayer(player);

        sendFakeGlowingTagToPlayer(player);
        if (this.colour != null) {
            // fake the glowing data for an entity
            FakeTeamManager.INSTANCE.addToColourTeam(player, this.entity, this.colour);
        } else {
            FakeTeamManager.INSTANCE.hideOriginalTeam(player, this.entity);
        }
    }

    @Override
    public void removePlayer(ServerPlayer player) {
        LieManager.INSTANCE.removeEntityGlow(player, this);
        super.removePlayer(player);

        // restore the glowing data for an entity
        restoreOriginalGlowingTagToPlayer(player);
        if (this.entity.getTeam() != null) {
            FakeTeamManager.INSTANCE.restoreOriginalTeam(player, this.entity);
        } else if (this.colour != null) {
            FakeTeamManager.INSTANCE.removeFromColourTeam(player, this.entity, this.colour);
        }

        if (this.fadeCallback != null)
            this.fadeCallback.onFade(player, this);
    }

    public void tick(ServerPlayer player) {
        if (this.tickCallback != null)
            this.tickCallback.onTick(player, this);
    }
}
