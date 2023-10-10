package red.jackf.jackfredlib.impl.lying;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.slf4j.Logger;
import red.jackf.jackfredlib.impl.base.LogUtil;
import red.jackf.jackfredlib.impl.lying.faketeams.FakeTeamManager;

public class Entrypoint implements ModInitializer {
    public static final Logger LOGGER = LogUtil.getLogger("Lying Module");

	@Override
	public void onInitialize() {
		LOGGER.debug("JackFredLib Lying setup");

		// Lying
		ServerLifecycleEvents.SERVER_STARTED.register(DebrisImpl.INSTANCE::init);
		ServerTickEvents.END_SERVER_TICK.register(s -> {
			LiesImpl.INSTANCE.tick();
			DebrisImpl.INSTANCE.tick();
		});
		ServerLifecycleEvents.SERVER_STOPPING.register(s -> DebrisImpl.INSTANCE.deinit());

		ServerPlayConnectionEvents.DISCONNECT.register(Entrypoint::onPlayerDisconnect);
	}

	private static void onPlayerDisconnect(
			ServerGamePacketListenerImpl listener,
			MinecraftServer server) {
		FakeTeamManager.INSTANCE.disconnect(listener.getPlayer().getGameProfile());
	}
}