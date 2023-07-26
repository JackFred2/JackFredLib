package red.jackf.jackfredlib.impl;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import red.jackf.jackfredlib.impl.testers.GradientEncode;

public class JackFredLib implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger(JackFredLib.class);

	@Override
	public void onInitialize() {
		LOGGER.debug("Hello Fabric world!");

		if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
			GradientEncode.test();
		}
	}
}