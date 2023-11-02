package red.jackf.jackfredlib.config;

import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigLoadTest {

    @Test
    void load() {
        System.out.println("loaded");
        ResourceLocation first = new ResourceLocation("br", "uh");
        ResourceLocation second = new ResourceLocation("br", "uv");
        assertNotEquals(first, second);
    }
}
