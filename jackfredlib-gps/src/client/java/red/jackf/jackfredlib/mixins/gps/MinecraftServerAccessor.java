package red.jackf.jackfredlib.mixins.gps;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Used to grab the world folder name
 */
@Mixin(MinecraftServer.class)
public interface MinecraftServerAccessor {
    @Accessor
    LevelStorageSource.LevelStorageAccess getStorageSource();
}
