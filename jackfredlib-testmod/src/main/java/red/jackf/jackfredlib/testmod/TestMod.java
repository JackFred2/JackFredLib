package red.jackf.jackfredlib.testmod;

import net.fabricmc.api.ModInitializer;
import red.jackf.jackfredlib.api.config.ConfigHandler;
import red.jackf.jackfredlib.testmod.config.TestConfig;

public class TestMod implements ModInitializer {
    private final ConfigHandler<TestConfig> handler
            = ConfigHandler.builder(TestConfig.class)
                           .fileName("testmod")
                           .build();

    @Override
    public void onInitialize() {
        GradientEncode.test();
        LieTest.setup();
        RepeatableArgumentsTest.setup();

        handler.instance();
    }
}
