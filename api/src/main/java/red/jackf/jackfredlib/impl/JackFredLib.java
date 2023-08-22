package red.jackf.jackfredlib.impl;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import red.jackf.jackfredlib.impl.lying.DebrisImpl;
import red.jackf.jackfredlib.impl.lying.LiesImpl;

public class JackFredLib implements ModInitializer {
	public static Logger getLogger(String suffix) {
		return LoggerFactory.getLogger(JackFredLib.class.getName() + (!suffix.isEmpty() ? "/" + suffix : ""));
	}
    public static final Logger LOGGER = getLogger("");

	@Override
	public void onInitialize() {
		LOGGER.debug("JackFredLib setup");

		// Lying
		ServerLifecycleEvents.SERVER_STARTED.register(DebrisImpl.INSTANCE::init);
		ServerTickEvents.END_SERVER_TICK.register(s -> {
			LiesImpl.INSTANCE.tick();
			DebrisImpl.INSTANCE.tick();
		});
		ServerLifecycleEvents.SERVER_STOPPING.register(s -> DebrisImpl.INSTANCE.deinit());
	}
}