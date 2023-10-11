package red.jackf.jackfredlib.mixins.lying.entity;

import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import red.jackf.jackfredlib.impl.lying.LieManager;

import java.util.Optional;

/**
 * <p>Used to intercept interaction events on fake entities in the lying module.</p>
 * <p>See {@link red.jackf.jackfredlib.api.lying.entity.EntityLie.Builder}</p>
 */
@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin {
    @Shadow public ServerPlayer player;

    @Inject(method = "handleInteract",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;setShiftKeyDown(Z)V", shift = At.Shift.AFTER),
            locals = LocalCapture.CAPTURE_FAILHARD)
    private void handleFakeEntities(ServerboundInteractPacket packet, CallbackInfo ci, final ServerLevel serverLevel, final Entity entity) {
        if (entity == null) {
            packet.dispatch(new ServerboundInteractPacket.Handler() {

                @Override
                public void onAttack() {
                    LieManager.INSTANCE.getEntityLieFromEntityId(player, ((ServerboundInteractPacketAccessor) packet).getEntityId())
                            .ifPresent(entityLie -> {
                                Vec3 from = player.getEyePosition();
                                Vec3 to = from.add(player.getLookAngle().scale(6));
                                Optional<Vec3> collision = entityLie.entity().getBoundingBox().clip(from, to);
                                collision.ifPresent(vec3 -> entityLie.leftClick(player, packet.isUsingSecondaryAction(), vec3));
                            });
                }

                @Override
                public void onInteraction(InteractionHand hand) {
                    // no op, handle right clicks in onInteraction(InteractionHand, Vec3)
                }

                @Override
                public void onInteraction(InteractionHand hand, Vec3 interactionLocation) {
                    LieManager.INSTANCE.getEntityLieFromEntityId(player, ((ServerboundInteractPacketAccessor) packet).getEntityId())
                            .ifPresent(entityLie -> entityLie.rightClick(player, packet.isUsingSecondaryAction(), hand, interactionLocation));
                }
            });
        }
    }
}
