package red.jackf.jackfredlib.testmod;

import net.fabricmc.api.ModInitializer;

public class TestMod implements ModInitializer {
    @Override
    public void onInitialize() {
        GradientEncode.test();
        LieTest.setup();
        RepeatableArgumentsTest.setup();
    }
}
