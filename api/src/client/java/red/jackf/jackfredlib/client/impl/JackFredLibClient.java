package red.jackf.jackfredlib.client.impl;

import net.fabricmc.api.ClientModInitializer;
import red.jackf.jackfredlib.client.impl.test.ClientTest;

public class JackFredLibClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		if (System.getProperty("jackfredlib.test").equals("true"))
			ClientTest.setup();
	}
}