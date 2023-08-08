package red.jackf.jackfredlib.testmod.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.Commands;

public class TestModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            if (environment.includeIntegrated)
                dispatcher.register(Commands.literal("openColourScreen").executes(ctx -> {
                    Minecraft.getInstance()
                            .execute(() -> Minecraft.getInstance().setScreen(new ColourTestScreen()));
                    return 0;
                }));
        });
    }
}
