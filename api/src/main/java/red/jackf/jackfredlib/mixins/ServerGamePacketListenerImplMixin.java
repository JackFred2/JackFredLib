package red.jackf.jackfredlib.mixins;

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
import red.jackf.jackfredlib.impl.lying.LiesImpl;

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
                    LiesImpl.INSTANCE.getEntityLieFromId(player, packet.entityId)
                            .ifPresent(activeEntityLie -> activeEntityLie.lie().onLeftClick(activeEntityLie));
                }

                @Override
                public void onInteraction(InteractionHand hand) {
                    LiesImpl.INSTANCE.getEntityLieFromId(player, packet.entityId)
                            .ifPresent(activeEntityLie -> activeEntityLie.lie().onRightClick(activeEntityLie, hand));
                }

                @Override
                public void onInteraction(InteractionHand hand, Vec3 interactionLocation) {
                    LiesImpl.INSTANCE.getEntityLieFromId(player, packet.entityId)
                            .ifPresent(activeEntityLie -> activeEntityLie.lie().onPositionalRightClick(activeEntityLie, hand, interactionLocation));
                }
            });
        }
    }
}
