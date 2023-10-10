package red.jackf.jackfredlib.testmod.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.Commands;
import org.slf4j.Logger;
import red.jackf.jackfredlib.client.api.gps.PlayerListUtils;
import red.jackf.jackfredlib.client.api.gps.ScoreboardUtils;
import red.jackf.jackfredlib.impl.base.LogUtil;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class TestModClient implements ClientModInitializer {
    public static final Logger LOGGER = LogUtil.getLogger("testmod-client");

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

        GPSCoordGrabber.setup();
        LyingTeamsGrabber.setup();

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
            dispatcher.register(
                    literal("jflibtest").then(
                            literal("printtablist").executes(ctx -> {
                                PlayerListUtils.getAll().forEach(LOGGER::info);
                                return 0;
                            })).then(
                            literal("area").executes(ctx -> {
                                LOGGER.info("Area: {}", PlayerListUtils.getPrefixed("Area: "));
                                return 0;
                            })).then(
                            literal("scoreboard").executes(ctx -> {
                                LOGGER.info(ScoreboardUtils.getRows().toString());
                                return 0;
                            }))));
    }
}
