package red.jackf.jackfredlib.api.lying.entity.builders;

import net.minecraft.server.level.ServerLevel;

public class EntityBuilders {
    private EntityBuilders() {}

    public static BlockDisplayBuilder blockDisplay(ServerLevel level) {
        return new BlockDisplayBuilder(level);
    }
}
