package red.jackf.jackfredlib.mixins.lying.entityglow;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.server.network.ServerPlayerConnection;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import red.jackf.jackfredlib.impl.lying.LieManager;
import red.jackf.jackfredlib.impl.lying.glowing.FakeGlowPacketMeddling;

import java.util.function.Predicate;

/**
 * Modifies an entities' tracking packets to add a glowing tag if needed by an entity glow lie.
 */
@Mixin(ChunkMap.TrackedEntity.class)
public class ChunkMapTrackedEntityMixin {
    @Shadow @Final Entity entity;

    // Redirect inside: public void sendToTrackingPlayers(Packet<? super ClientGamePacketListener> packet)
    @Redirect(method = "sendToTrackingPlayers(Lnet/minecraft/network/protocol/Packet;)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerPlayerConnection;send(Lnet/minecraft/network/protocol/Packet;)V"))
    private void jackfredlib$modifyDataPacketSendToTrackingPlayers(ServerPlayerConnection connection, Packet<?> packet) {
        if (packet instanceof ClientboundSetEntityDataPacket entityData) {
            var lie = LieManager.INSTANCE.getEntityGlowLieFromEntityUuid(connection.getPlayer(), this.entity.getUUID());
            if (lie.isPresent()) {
                connection.send(FakeGlowPacketMeddling.modifyPacket(entityData, lie.get()));
                return;
            }
        }
        connection.send(packet);
    }

    // Redirect inside: public void sendToTrackingPlayersFiltered(Packet<? super ClientGamePacketListener> packet, Predicate<ServerPlayer> filter)
    @Redirect(method = "sendToTrackingPlayersFiltered(Lnet/minecraft/network/protocol/Packet;Ljava/util/function/Predicate;)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerPlayerConnection;send(Lnet/minecraft/network/protocol/Packet;)V"))
    private void jackfredlib$modifyDataPacketSendToTrackingPlayersFiltered(ServerPlayerConnection connection, Packet<?> packet) {
        if (packet instanceof ClientboundSetEntityDataPacket entityData) {
            var lie = LieManager.INSTANCE.getEntityGlowLieFromEntityUuid(connection.getPlayer(), this.entity.getUUID());
            if (lie.isPresent()) {
                connection.send(FakeGlowPacketMeddling.modifyPacket(entityData, lie.get()));
                return;
            }
        }
        connection.send(packet);
    }
}