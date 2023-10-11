package red.jackf.jackfredlib.api.lying.entity.builders;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Display;
import net.minecraft.world.level.block.Blocks;
import red.jackf.jackfredlib.api.colour.Colour;

public class EntityPresets {
    private EntityPresets() {}

    public static Display.BlockDisplay highlight(ServerLevel level, BlockPos pos, Colour colour, float diameter) {
        return EntityBuilders.blockDisplay(level)
                .glowing(true, colour)
                .state(Blocks.STONE.defaultBlockState())
                .positionCentered(pos)
                .scaleAndCenter(diameter)
                .build();
    }
}
