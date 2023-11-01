package red.jackf.jackfredlib.testmod.config;

import blue.endless.jankson.Comment;
import red.jackf.jackfredlib.api.config.Config;
import red.jackf.jackfredlib.api.config.ConfigValidationException;

public class TestConfig implements Config {

    @Comment("Search Range, in Blocks")
    public int searchRange = 12;
}
