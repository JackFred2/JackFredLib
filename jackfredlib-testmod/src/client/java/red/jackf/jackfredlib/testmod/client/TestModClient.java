package red.jackf.jackfredlib.testmod.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.Commands;
import red.jackf.jackfredlib.client.api.gps.Coordinate;

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

        ToastTest.setup();

        HudRenderCallback.EVENT.register((graphics, tickDelta) -> {
            graphics.drawString(Minecraft.getInstance().font, Coordinate.getCurrent().map(Coordinate::toString).orElse("<no coordinate"), 50, 50, 0xFF_AAFFAA);
        });
    }
}
