package red.jackf.jackfredlib.mixins.lying.entityglow;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.network.ServerPlayerConnection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import red.jackf.jackfredlib.impl.lying.LieManager;
import red.jackf.jackfredlib.impl.lying.glowing.FakeGlowPacketMeddling;

@Mixin(ChunkMap.TrackedEntity.class)
public class ChunkMapTrackedEntityMixin {

    @WrapOperation(method = "broadcast(Lnet/minecraft/network/protocol/Packet;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerConnection;send(Lnet/minecraft/network/protocol/Packet;)V"))
    private static void jackfredlib$modifyDataPacket(
            ServerPlayerConnection connection,
            Packet<?> packet,
            Operation<Void> original) {
        if (packet instanceof ClientboundSetEntityDataPacket entityData) {
            var lie = LieManager.INSTANCE.getEntityGlowLieFromEntityId(connection.getPlayer(), entityData.id());
            if (lie.isPresent()) {
                original.call(connection, FakeGlowPacketMeddling.modifyPacket(entityData, lie.get().entity()));
                return;
            }
        }
        original.call(connection, packet);
    }
}
