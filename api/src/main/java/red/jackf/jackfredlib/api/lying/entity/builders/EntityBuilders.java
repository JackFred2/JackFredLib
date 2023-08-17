package red.jackf.jackfredlib.api.lying.entity.builders;

import net.minecraft.server.level.ServerLevel;

/**
 * Collection of builders to help create entities with lie-useful properties.
 */
public class EntityBuilders {
    private EntityBuilders() {}

    /**
     * Create a new builder for a <a href="https://minecraft.fandom.com/wiki/Display">Block Display Entity</a>.
     * @param level Level to create the entity in
     * @return Builder for a block display entity
     */
    public static BlockDisplayBuilder blockDisplay(ServerLevel level) {
        return new BlockDisplayBuilder(level);
    }
    /**
     * Create a new builder for an <a href="https://minecraft.fandom.com/wiki/Display">Item Display Entity</a>.
     * @param level Level to create the entity in
     * @return Builder for an item display entity
     */
    public static ItemDisplayBuilder itemDisplay(ServerLevel level) {
        return new ItemDisplayBuilder(level);
    }
}
