package red.jackf.jackfredlib.impl.lying;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.slf4j.Logger;
import red.jackf.jackfredlib.impl.base.LogUtil;
import red.jackf.jackfredlib.impl.lying.faketeams.FakeTeamManager;
import red.jackf.jackfredlib.impl.lying.tracker.TrackerRunner;

public class ModuleEntrypoint implements ModInitializer {
    public static final Logger LOGGER = LogUtil.getLogger("Lying Module");

	@Override
	public void onInitialize() {
		LOGGER.debug("JackFredLib Lying setup");

		// Lying
		ServerLifecycleEvents.SERVER_STARTED.register(DebrisImpl.INSTANCE::init);

		ServerTickEvents.END_WORLD_TICK.register(TrackerRunner::tickLevel);

		ServerTickEvents.END_SERVER_TICK.register(s -> {
			LieManager.INSTANCE.tick();
			DebrisImpl.INSTANCE.tick();
		});

		ServerLifecycleEvents.SERVER_STOPPING.register(s -> DebrisImpl.INSTANCE.deinit());

		ServerPlayConnectionEvents.DISCONNECT.register(ModuleEntrypoint::onPlayerDisconnect);

		ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> LieManager.INSTANCE.migrateEntity(entity));

		ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> LieManager.INSTANCE.migratePlayerInstance(oldPlayer, newPlayer));

		ServerWorldEvents.LOAD.register((server, level) -> {
			TrackerRunner.loadLevel(level);
		});
		ServerWorldEvents.UNLOAD.register((server, level) -> {
			TrackerRunner.unloadLevel(level);
		});
	}

	private static void onPlayerDisconnect(
			ServerGamePacketListenerImpl listener,
			MinecraftServer server) {
		LieManager.INSTANCE.removeAll(listener.getPlayer());
		FakeTeamManager.INSTANCE.disconnect(listener.getPlayer().getGameProfile());
	}
}