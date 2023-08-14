package red.jackf.jackfredlib.api.lying.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Display;
import net.minecraft.world.level.block.Blocks;
import red.jackf.jackfredlib.api.colour.Colour;
import red.jackf.jackfredlib.api.lying.entity.builders.EntityBuilders;

/**
 * Common presets for entities to use in mods
 */
public class EntityPresets {
    private EntityPresets() {}

    /**
     * Creates a glowing cube at a given position in the world, in the form of a block display.
     * @param serverLevel Level to create the cube in
     * @param pos Position to create the cube at
     * @param colour Colour for the block outline
     * @param diameter Diameter of the cube to create. It is advised to shrink this a little to avoid Z-fighting with
     *                 actual blocks in the level.
     * @return An invisible, glowing slime at the given position
     */
    public static Display.BlockDisplay highlight(ServerLevel serverLevel, BlockPos pos, Colour colour, float diameter) {
        return EntityBuilders.blockDisplay(serverLevel)
                .glowing(true, colour)
                .state(Blocks.STONE.defaultBlockState())
                .positionCentered(pos)
                .scaleAndCenter(diameter)
                .build();
    }
}
