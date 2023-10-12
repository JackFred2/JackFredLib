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

    private final E entity;
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
    public void setGlowColour(ChatFormatting colour) {
        var newColour = FakeTeamUtil.ensureValidColour(colour);
        if (newColour != this.colour) {
            if (newColour == null) {
                for (var player : this.getViewingPlayers()) {
                    removeFakeGlowDataFromPlayer(player);
                    FakeTeamManager.INSTANCE.removeFromTeam(player, this.entity, this.colour);
                }
            } else {
                for (var player : this.getViewingPlayers()) {
                    // adding an entity to a team removes it from the old anyway
                    if (this.colour == null) sendFakeGlowDataToPlayer(player);
                    FakeTeamManager.INSTANCE.addToTeam(player, this.entity, newColour);
                }
            }

            this.colour = newColour;
        }
    }

    @Override
    public E entity() {
        return entity;
    }

    private void sendFakeGlowDataToPlayer(ServerPlayer player) {
        var dataValue = new SynchedEntityData.DataValue<>(
            DATA.getId(),
            DATA.getSerializer(),
            (byte) (this.entity.getEntityData().get(DATA) | (1 << Entity.FLAG_GLOWING))
        );
        player.connection.send(new ClientboundSetEntityDataPacket(
                this.entity.getId(),
                List.of(dataValue))
        );
    }

    private void removeFakeGlowDataFromPlayer(ServerPlayer player) {
        var dataValue = new SynchedEntityData.DataValue<>(
                DATA.getId(),
                DATA.getSerializer(),
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

        if (this.colour != null) {
            // fake the glowing data for an entity
            sendFakeGlowDataToPlayer(player);
            FakeTeamManager.INSTANCE.addToTeam(player, this.entity, this.colour);
        }
    }

    @Override
    public void removePlayer(ServerPlayer player) {
        LieManager.INSTANCE.removeEntityGlow(player, this);
        super.removePlayer(player);

        // remove the glowing data for an entity
        removeFakeGlowDataFromPlayer(player);
        FakeTeamManager.INSTANCE.removeFromTeam(player, this.entity, this.colour);

        if (this.fadeCallback != null)
            this.fadeCallback.onFade(player, this);
    }

    public void tick(ServerPlayer player) {
        if (this.tickCallback != null)
            this.tickCallback.onTick(player, this);
    }
}
