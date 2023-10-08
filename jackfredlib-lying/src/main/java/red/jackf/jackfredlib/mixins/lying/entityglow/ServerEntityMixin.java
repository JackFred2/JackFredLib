package red.jackf.jackfredlib.mixins.lying.entityglow;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import red.jackf.jackfredlib.impl.lying.LiesImpl;

@Mixin(ServerEntity.class)
public class ServerEntityMixin {

    @ModifyExpressionValue(method = "sendPairingData",
            at = @At(value = "NEW", args = "class=net/minecraft/network/protocol/game/ClientboundSetEntityDataPacket"))
    private ClientboundSetEntityDataPacket jackfredlib$changeGlowingForLies(ClientboundSetEntityDataPacket originalPacket, ServerPlayer player) {
        var lie = LiesImpl.INSTANCE.getEntityGlowLieFromId(player, originalPacket.id());
        if (lie.isPresent()) {
            return lie.get().modifyPacket(originalPacket);
        }
        return originalPacket;
    }
}
