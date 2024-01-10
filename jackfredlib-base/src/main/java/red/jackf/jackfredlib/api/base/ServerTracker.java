package red.jackf.jackfredlib.api.base;

import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import red.jackf.jackfredlib.impl.base.ServerTrackerImpl;

/**
 * Static access for the current Minecraft Server instance.
 */
@ApiStatus.AvailableSince("1.3.0")
public interface ServerTracker {
    /**
     * Obtain the current server tracker instance.
     */
    ServerTracker INSTANCE = ServerTrackerImpl.INSTANCE;

    /**
     * Get the current MinecraftServer instance, if available.
     * @return The currently running MinecraftServer instance, or {@code null} if not available.
     */
    @Nullable MinecraftServer getServer();
}
