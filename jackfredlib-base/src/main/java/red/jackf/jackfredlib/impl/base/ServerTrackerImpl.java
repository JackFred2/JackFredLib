package red.jackf.jackfredlib.impl.base;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import red.jackf.jackfredlib.api.base.ServerTracker;

@ApiStatus.AvailableSince("1.3.0")
public class ServerTrackerImpl implements ServerTracker, ModInitializer {
    @Nullable
    private MinecraftServer instance = null;

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> instance = server);
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> instance = null);
    }

    @Nullable
    public MinecraftServer getServer() {
        return instance;
    }
}
