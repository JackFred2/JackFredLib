package red.jackf.jackfredlib.testmod.config;

import blue.endless.jankson.Comment;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import red.jackf.jackfredlib.api.config.Config;
import red.jackf.jackfredlib.impl.base.LogUtil;

public class TestConfig implements Config<TestConfig> {
    private static final Logger LOGGER = LogUtil.getLogger("Test/Config");

    @Comment("Search Range, in Blocks")
    public int searchRange = 12;

    @Override
    public void onLoad(@Nullable TestConfig oldInstance) {
        LOGGER.info("Loaded config");
        LOGGER.info("Range: " + searchRange);
        if (oldInstance != null)
            LOGGER.info("Old: " + oldInstance.searchRange);
    }
}
