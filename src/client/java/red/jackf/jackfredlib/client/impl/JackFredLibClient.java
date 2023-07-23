package red.jackf.jackfredlib.client.impl;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.Commands;

public class JackFredLibClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
			CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
				if (environment.includeIntegrated)
					dispatcher.register(Commands.literal("openColourGui").executes(ctx -> {
						Minecraft.getInstance()
								.execute(() -> Minecraft.getInstance().setScreen(new ColourTestScreen()));
						return 0;
					}));
			});
		}
	}
}