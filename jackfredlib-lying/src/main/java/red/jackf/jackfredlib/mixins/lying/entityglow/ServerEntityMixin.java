package red.jackf.jackfredlib.mixins.lying.entityglow;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import red.jackf.jackfredlib.impl.lying.LieManager;
import red.jackf.jackfredlib.impl.lying.glowing.FakeGlowPacketMeddling;

@Mixin(ServerEntity.class)
public class ServerEntityMixin {
    @Shadow
    @Final
    private Entity entity;

    @ModifyExpressionValue(method = "sendPairingData",
            at = @At(value = "NEW", args = "class=net/minecraft/network/protocol/game/ClientboundSetEntityDataPacket"))
    private ClientboundSetEntityDataPacket jackfredlib$changeGlowingForLies(ClientboundSetEntityDataPacket original) {
        var lie = LieManager.INSTANCE.getEntityGlowLieFromEntityUuid(player, this.entity.getUUID());
        return lie.map(glowLie -> FakeGlowPacketMeddling.modifyPacket(original, glowLie))
                .orElse(original);
    }
}
