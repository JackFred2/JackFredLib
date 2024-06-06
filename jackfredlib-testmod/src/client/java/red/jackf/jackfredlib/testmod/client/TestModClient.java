package red.jackf.jackfredlib.testmod.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;
import red.jackf.jackfredlib.client.api.gps.PlayerListSnapshot;
import red.jackf.jackfredlib.client.api.gps.ScoreboardSnapshot;
import red.jackf.jackfredlib.impl.base.LogUtil;
import red.jackf.jackfredlib.testmod.client.colour.ClosestColourScreen;
import red.jackf.jackfredlib.testmod.client.colour.ColourTestScreen;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class TestModClient implements ClientModInitializer {
    public static final Logger LOGGER = LogUtil.getLogger("testmod-client");

    @Override
    public void onInitializeClient() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            if (true /*environment.includeIntegrated*/) {
                dispatcher.register(Commands.literal("openColourScreen").executes(ctx -> {
                    Minecraft.getInstance()
                            .execute(() -> Minecraft.getInstance().setScreen(new ColourTestScreen()));
                    return 0;
                }));
                dispatcher.register(Commands.literal("openClosestColourScreen").executes(ctx -> {
                    Minecraft.getInstance()
                            .execute(() -> Minecraft.getInstance().setScreen(new ClosestColourScreen()));
                    return 0;
                }));
            }
        });

        ToastTest.setup();

        GPSCoordGrabber.setup();
        LyingTeamsGrabber.setup();

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
            dispatcher.register(
                    literal("jflibtest").then(
                            literal("printtablist").executes(ctx -> {
                                PlayerListSnapshot.take().names().forEach(c -> LOGGER.info(c.getString()));
                                return 0;
                            })).then(
                            literal("area").executes(ctx -> {
                                LOGGER.info("Area: {}", PlayerListSnapshot.take().nameWithPrefix("Area: ").map(Component::getString));
                                return 0;
                            })).then(
                            literal("scoreboard").executes(ctx -> {
                                LOGGER.info(ScoreboardSnapshot.take().toString());
                                return 0;
                            })).then(
                            literal("header").executes(ctx -> {
                                LOGGER.info(PlayerListSnapshot.take().header().map(Component::getString).orElse("no header"));
                                return 0;
                            })).then(
                            literal("footer").executes(ctx -> {
                                LOGGER.info(PlayerListSnapshot.take().footer().map(Component::getString).orElse("no footer"));
                                return 0;
                            }))));
    }
}
