package red.jackf.jackfredlib.api.lying.entity.builders;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

/**
 * Collection of builders to help create entities with lie-useful properties.
 */
public class EntityBuilders {
    private EntityBuilders() {}

    /**
     * Create a new builder for an arbitrary entity, if no other matches.
     *
     * @param type Type of the entity to create a builder for
     * @param level Level to create this entity in
     * @return A build for a generic entity
     * @param <E> Class of the entity being built
     */
    public static <E extends Entity> GenericBuilder<E> generic(EntityType<E> type, ServerLevel level) {
        return new GenericBuilder<>(type, level);
    }

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

    /**
     * Create a new builder for an <a href="https://minecraft.fandom.com/wiki/Display">Text Display Entity</a>.
     * @param level Level to create the entity in
     * @return Builder for an text display entity
     */
    public static TextDisplayBuilder textDisplay(ServerLevel level) {
        return new TextDisplayBuilder(level);
    }
}
