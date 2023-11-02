package red.jackf.jackfredlib.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.slf4j.helpers.NOPLogger;
import red.jackf.jackfredlib.api.config.ConfigHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigLoadTest {
    private static @TempDir Path testDir;

    private ConfigHandler<TestConfig> makeHandler() {
        var methodName = StackWalker.getInstance().walk(s -> s.skip(1).findFirst().orElseThrow().getMethodName());

        return ConfigHandler.builder(TestConfig.class)
                .path(testDir.resolve(methodName + ".json5"))
                .withLogger(NOPLogger.NOP_LOGGER)
                .build();
    }

    @Test
    void firstIsDefault() {
        var handler = makeHandler();
        TestConfig def = handler.getDefault();
        TestConfig instance = handler.instance();
        assertEquals(def, instance);
        assertNotSame(def, instance);
    }

    @Test
    void saveLoadEqualNotSame() {
        var handler = makeHandler();
        TestConfig old = handler.instance();
        handler.load();
        TestConfig newVal = handler.instance();
        assertEquals(old, newVal);
        assertNotSame(old, newVal);
    }

    @Test
    void differentValues() throws IOException {
        String modified = """
                {
                	range: 34,
                	smol: 2,
                	nullable: "not null!",
                	test: "a different string",
                	pojo: {
                		flag1: true,
                		flag2: false,
                		flag3: false,
                	},
                	idList: [
                	],
                	map: {
                		600: 3.141,
                		"-7000": -34.5,
                	},
                }""";

        var handler = makeHandler();
        Files.writeString(handler.getFilePath(), modified);
        TestConfig instance = handler.instance();

        assertNotEquals(instance, handler.getDefault());

        assertEquals(instance.range, 34);
        assertEquals(instance.smol, 2);
        assertEquals(instance.nullable, "not null!");
        assertEquals(instance.test, "a different string");
        assertTrue(instance.pojo.flag1);
        assertFalse(instance.pojo.flag2);
        assertFalse(instance.pojo.flag3);
        assertTrue(instance.idList.isEmpty());
        assertEquals(instance.map.size(), 2);
        assertEquals(instance.map, Map.of(
                "600", 3.141,
                "-7000", -34.5
        ));
    }
}
