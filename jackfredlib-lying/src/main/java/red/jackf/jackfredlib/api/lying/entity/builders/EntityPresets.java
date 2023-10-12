package red.jackf.jackfredlib.api.lying.entity.builders;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Display;
import net.minecraft.world.level.block.Blocks;
import red.jackf.jackfredlib.api.colour.Colour;

/**
 * Collection of commonly-used entities as a basis for a lie.
 */
public class EntityPresets {
    private EntityPresets() {}

    /**
     * Create a glowing cube at a given {@link BlockPos}, useful for highlighting a location.
     *
     * @param level Level to create the highlight in.
     * @param pos Position to create the highligh at.
     * @param colour Colour that the highlight will be.
     * @param diameter Diameter of the highlight cube.
     * @return A Block Display entity highlighting a given position.
     */
    public static Display.BlockDisplay highlight(ServerLevel level, BlockPos pos, Colour colour, float diameter) {
        return EntityBuilders.blockDisplay(level)
                .glowing(true, colour)
                .state(Blocks.STONE.defaultBlockState())
                .positionCentered(pos)
                .scaleAndCenter(diameter)
                .build();
    }
}
