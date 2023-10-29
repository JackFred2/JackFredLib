package red.jackf.jackfredlib.mixins.lying.entityglow;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import red.jackf.jackfredlib.api.base.Ephemeral;
import red.jackf.jackfredlib.impl.lying.LieManager;
import red.jackf.jackfredlib.impl.lying.glowing.FakeGlowPacketMeddling;

@Mixin(ServerEntity.class)
public class ServerEntityMixin {
    @Unique
    private final Ephemeral<ServerPlayer> playerEphemeral = new Ephemeral<>();

    @Shadow
    @Final
    private Entity entity;

    @ModifyExpressionValue(method = "sendPairingData",
            at = @At(value = "NEW", args = "class=net/minecraft/network/protocol/game/ClientboundSetEntityDataPacket"))
    private ClientboundSetEntityDataPacket jackfredlib$changeGlowingForLies(ClientboundSetEntityDataPacket original) {
        var player = playerEphemeral.pop();
        if (player == null) return original;
        var lie = LieManager.INSTANCE.getEntityGlowLieFromEntityUuid(player, this.entity.getUUID());
        return lie.map(glowLie -> FakeGlowPacketMeddling.modifyPacket(original, glowLie))
                .orElse(original);
    }

    // <=1.19.4, basically surrogate an argument to sendplayerdata
    @Inject(method = "addPairing", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerEntity;sendPairingData(Ljava/util/function/Consumer;)V"))
    private void jackfredlib$saveServerPlayer(ServerPlayer player, CallbackInfo ci) {
        playerEphemeral.push(player);
    }
}
