package red.jackf.jackfredlib.testmod;

import net.fabricmc.api.ModInitializer;
import red.jackf.jackfredlib.api.config.ConfigHandler;
import red.jackf.jackfredlib.api.config.migration.MigratorBuilder;
import red.jackf.jackfredlib.api.config.migration.builtin.Identity;
import red.jackf.jackfredlib.testmod.config.TestConfig;

public class TestMod implements ModInitializer {
    private final ConfigHandler<TestConfig> handler
            = ConfigHandler.builder(TestConfig.class)
                           .fileName("testmod")
                           .withMigrator(MigratorBuilder.<TestConfig>forMod("jackfredlib-testmod")
                                                        .addMigration("1.0.5", new Identity<>()))
                           .withFileWatcher()
                           .build();

    @Override
    public void onInitialize() {
        GradientEncode.test();
        LieTest.setup();
        RepeatableArgumentsTest.setup();

        handler.instance();
    }
}
