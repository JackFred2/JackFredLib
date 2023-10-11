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
import red.jackf.jackfredlib.impl.lying.faketeams.FakeTeamManager;

public class EntityLieImpl extends LieImpl implements EntityLie {
    private final Entity entity;
    private final ServerEntity serverEntity;
    private @Nullable ChatFormatting glowColour;
    private final @Nullable TickCallback tickCallback;
    private final @Nullable FadeCallback fadeCallback;
    private final @Nullable LeftClickCallback leftClickCallback;
    private final @Nullable RightClickCallback rightClickCallback;

    public EntityLieImpl(
            Entity entity,
            @Nullable ChatFormatting glowColour,
            @Nullable TickCallback tickCallback,
            @Nullable FadeCallback fadeCallback,
            @Nullable LeftClickCallback leftClickCallback,
            @Nullable RightClickCallback rightClickCallback) {
        this.entity = entity;
        this.glowColour = glowColour;
        this.tickCallback = tickCallback;
        this.fadeCallback = fadeCallback;
        this.leftClickCallback = leftClickCallback;
        this.rightClickCallback = rightClickCallback;

        this.serverEntity = new ServerEntity(
                (ServerLevel) entity.level(),
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
        if (colour != null && !colour.isColor()) colour = ChatFormatting.WHITE;
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
    public Entity entity() {
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
        this.serverEntity.sendChanges();
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
