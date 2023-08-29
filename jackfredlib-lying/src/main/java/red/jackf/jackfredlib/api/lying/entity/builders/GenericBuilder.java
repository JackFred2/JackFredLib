package red.jackf.jackfredlib.api.lying.entity.builders;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.ApiStatus;

/**
 * An entity builder with no specific entity methods. Use this if no other builders fit your needs better.
 * @param <E> Entity type to build
 */
public class GenericBuilder<E extends Entity> extends BuilderBase<E, GenericBuilder<E>> {
    protected GenericBuilder(EntityType<E> type, ServerLevel level) {
        super(type, level);
    }

    @Override
    @ApiStatus.Internal
    protected GenericBuilder<E> self() {
        return this;
    }
}
