package red.jackf.jackfredlib.impl;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import red.jackf.jackfredlib.impl.test.Test;

public class JackFredLib implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger(JackFredLib.class);

	@Override
	public void onInitialize() {
		LOGGER.debug("Hello Fabric world!");

		if (System.getProperty("jackfredlib.test").equals("true"))
			Test.setup();
	}
}