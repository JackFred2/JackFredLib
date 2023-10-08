package red.jackf.jackfredlib.mixins.lying.entityglow;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import red.jackf.jackfredlib.impl.lying.Entrypoint;

import java.util.List;
import java.util.function.Consumer;

@Mixin(ServerEntity.class)
public class ServerEntityMixin {
    @Inject(method = "sendPairingData", at = @At("HEAD"))
    private void jackfredlib$addPlayerToShare(ServerPlayer player,
                                              Consumer<Packet<ClientGamePacketListener>> ignored,
                                              CallbackInfo ci,
                                              @Share("player") LocalRef<ServerPlayer> playerRef) {
        playerRef.set(player);
    }

    @ModifyArg(method = "sendPairingData",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/game/ClientboundSetEntityDataPacket;<init>(ILjava/util/List;)V"), index = 1)
    private List<SynchedEntityData.DataValue<?>> jackfredlib$changeGlowingForLies(int entityId,
                                                                                  List<SynchedEntityData.DataValue<?>> packedItems,
                                                                                  @Share("player") LocalRef<ServerPlayer> playerRef) {
        var player = playerRef.get();
        Entrypoint.LOGGER.debug("{}: {}", player.getGameProfile().getName(), entityId);
        return packedItems;
    }
}
