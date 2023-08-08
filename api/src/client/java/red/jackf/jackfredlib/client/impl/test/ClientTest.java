package red.jackf.jackfredlib.client.impl.test;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.Commands;

public class ClientTest {
    public static void setup() {
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
