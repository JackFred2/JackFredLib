package red.jackf.jackfredlib.impl.lying.entity;

import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import red.jackf.jackfredlib.api.lying.entity.EntityLie;
import red.jackf.jackfredlib.impl.lying.LieImpl;
import red.jackf.jackfredlib.impl.lying.LieManager;
import red.jackf.jackfredlib.impl.lying.compat.imm_ptl.Compatibility;
import red.jackf.jackfredlib.impl.lying.compat.imm_ptl.ImmersivePortalsCompat;
import red.jackf.jackfredlib.impl.lying.faketeams.FakeTeamManager;
import red.jackf.jackfredlib.impl.lying.faketeams.FakeTeamUtil;

public class EntityLieImpl<E extends Entity> extends LieImpl implements EntityLie<E> {
    private final E entity;
    private final ServerEntity serverEntity;
    private @Nullable ChatFormatting glowColour;
    private final @Nullable TickCallback<E> tickCallback;
    private final @Nullable FadeCallback<E> fadeCallback;
    private final @Nullable LeftClickCallback<E> leftClickCallback;
    private final @Nullable RightClickCallback<E> rightClickCallback;

    public EntityLieImpl(
            E entity,
            @Nullable ChatFormatting glowColour,
            @Nullable TickCallback<E> tickCallback,
            @Nullable FadeCallback<E> fadeCallback,
            @Nullable LeftClickCallback<E> leftClickCallback,
            @Nullable RightClickCallback<E> rightClickCallback) {
        this.entity = entity;
        this.glowColour = glowColour;
        this.tickCallback = tickCallback;
        this.fadeCallback = fadeCallback;
        this.leftClickCallback = leftClickCallback;
        this.rightClickCallback = rightClickCallback;

        this.serverEntity = new ServerEntity(
                (ServerLevel) entity.getLevel(),
                entity,
                entity.getType().updateInterval(),
                entity.getType().trackDeltas(),
                packet -> getViewingPlayers().forEach(player -> player.connection.send(packet))
        );
    }

    @Override
    public @Nullable ChatFormatting glowColour() {
        return glowColour;
    }

    @Override
    public void setGlowColour(@Nullable ChatFormatting colour) {
        colour = FakeTeamUtil.ensureValidColour(colour);
        if (colour == this.glowColour) return;
        if (colour == null) {
            this.entity.setGlowingTag(false);
            for (ServerPlayer viewingPlayer : this.getViewingPlayers()) {
                FakeTeamManager.INSTANCE.removeFromTeam(viewingPlayer, this.entity, this.glowColour);
            }
        } else {
            this.entity.setGlowingTag(true);
            for (ServerPlayer viewingPlayer : this.getViewingPlayers()) {
                FakeTeamManager.INSTANCE.addToTeam(viewingPlayer, this.entity, colour);
            }
        }
        this.glowColour = colour;
    }

    @Override
    public E entity() {
        return entity;
    }

    @Override
    public void addPlayer(ServerPlayer player) {
        if (this.getViewingPlayers().contains(player)) return;
        LieManager.INSTANCE.addEntity(player, this);

        super.addPlayer(player);

        this.serverEntity.addPairing(player);
        if (this.glowColour != null)
            FakeTeamManager.INSTANCE.addToTeam(player, this.entity, this.glowColour);
    }

    @Override
    public void removePlayer(ServerPlayer player) {
        LieManager.INSTANCE.removeEntity(player, this);
        super.removePlayer(player);
        this.serverEntity.removePairing(player);

        if (this.glowColour != null)
            FakeTeamManager.INSTANCE.removeFromTeam(player, this.entity, this.glowColour);

        if (this.fadeCallback != null)
            this.fadeCallback.onFade(player, this);
    }

    public void tick(ServerPlayer player) {
        if (Compatibility.IMM_PTL) {
            ImmersivePortalsCompat.runWrapped((ServerLevel) this.entity.getLevel(), this.serverEntity::sendChanges);
        } else {
            this.serverEntity.sendChanges();
        }
        if (this.tickCallback != null)
            this.tickCallback.onTick(player, this);
    }

    public void leftClick(ServerPlayer player, boolean usingSecondaryAction, Vec3 relativeToEntity) {
        if (this.leftClickCallback != null)
            this.leftClickCallback.onLeftClick(player, this, usingSecondaryAction, relativeToEntity);
    }

    public void rightClick(ServerPlayer player, boolean usingSecondaryAction, InteractionHand hand, Vec3 relativeToEntity) {
        if (this.rightClickCallback != null)
            this.rightClickCallback.onRightClick(player, this, usingSecondaryAction, hand, relativeToEntity);
    }
}
