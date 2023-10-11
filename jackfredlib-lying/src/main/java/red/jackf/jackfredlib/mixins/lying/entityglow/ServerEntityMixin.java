package red.jackf.jackfredlib.mixins.lying.entityglow;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import red.jackf.jackfredlib.impl.lying.LieManager;
import red.jackf.jackfredlib.impl.lying.glowing.FakeGlowPacketMeddling;

@Mixin(ServerEntity.class)
public class ServerEntityMixin {

    @ModifyExpressionValue(method = "sendPairingData",
            at = @At(value = "NEW", args = "class=net/minecraft/network/protocol/game/ClientboundSetEntityDataPacket"))
    private ClientboundSetEntityDataPacket jackfredlib$changeGlowingForLies(ClientboundSetEntityDataPacket originalPacket, ServerPlayer player) {
        var lie = LieManager.INSTANCE.getEntityGlowLieFromEntityId(player, originalPacket.id());
        return lie.map(glowLie -> FakeGlowPacketMeddling.modifyPacket(originalPacket, glowLie.entity()))
                .orElse(originalPacket);
    }
}
