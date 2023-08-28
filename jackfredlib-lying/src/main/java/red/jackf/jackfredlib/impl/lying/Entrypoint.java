package red.jackf.jackfredlib.impl.lying;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import org.slf4j.Logger;
import red.jackf.jackfredlib.impl.LogUtil;

public class Entrypoint implements ModInitializer {
    public static final Logger LOGGER = LogUtil.getLogger("Lies");

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
	}
}