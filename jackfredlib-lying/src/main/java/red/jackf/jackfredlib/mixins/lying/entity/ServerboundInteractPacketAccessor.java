package red.jackf.jackfredlib.mixins.lying.entity;

import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * Get the entity ID of an entity to check if it's an entity lie.
 */
@Mixin(ServerboundInteractPacket.class)
public interface ServerboundInteractPacketAccessor {
    @Invoker("getTarget")
    @Nullable Entity invokeGetTarget(ServerLevel level);
}
