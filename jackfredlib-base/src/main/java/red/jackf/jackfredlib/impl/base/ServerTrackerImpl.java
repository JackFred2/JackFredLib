package red.jackf.jackfredlib.impl.base;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import red.jackf.jackfredlib.api.base.ServerTracker;

@ApiStatus.AvailableSince("1.3.0")
public class ServerTrackerImpl implements ServerTracker {
    public static final ServerTrackerImpl INSTANCE = new ServerTrackerImpl();

    @Nullable
    private MinecraftServer server = null;

    public static void setup() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> INSTANCE.server = server);
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> INSTANCE.server = null);
    }

    @Nullable
    public MinecraftServer getServer() {
        return server;
    }
}
