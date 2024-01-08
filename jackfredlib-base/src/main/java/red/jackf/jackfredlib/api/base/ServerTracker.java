package red.jackf.jackfredlib.api.base;

import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;
import red.jackf.jackfredlib.impl.base.ServerTrackerImpl;

public interface ServerTracker {
    ServerTracker INSTANCE = new ServerTrackerImpl();

    @Nullable MinecraftServer getServer();
}
