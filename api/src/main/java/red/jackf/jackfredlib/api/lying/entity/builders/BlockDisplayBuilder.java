package red.jackf.jackfredlib.api.lying.entity.builders;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Vector3f;

/**
 * <p>Builder to create a Block Display entity, which displays a block state.</p>
 *
 * <p>Note: The position of this entity is on the lower coordinate corner of the displayed block; use {@link #scaleAndCenter(float)}
 * to update the transform to center on the actual position.</p>
 * <p>Note: Some block states which use a block entity to render, such as chests, will ignore any block state properties
 * when rendering. This is a vanilla limitation.</p>
 * @see <a href="https://minecraft.fandom.com/wiki/Display">Minecraft Wiki: Display</a>
 */
public class BlockDisplayBuilder extends DisplayBuilder<Display.BlockDisplay, BlockDisplayBuilder> {
    protected BlockDisplayBuilder(ServerLevel level) {
        super(EntityType.BLOCK_DISPLAY, level);
    }

    /**
     * Set the block state for this display to render.
     *
     * @param state Block state to render
     * @return This builder
     */
    public BlockDisplayBuilder state(BlockState state) {
        this.entity.setBlockState(state);
        return self();
    }

    /**
     * Sets the scale of this block display to a given factor, then offsets it so the displayed block is centered on
     * the entity's {@link BuilderBase#position(Vec3)}.
     * @param scale Scale for this block display to use
     * @return This builder
     */
    public BlockDisplayBuilder scaleAndCenter(float scale) {
        return this.scale(new Vector3f(scale, scale, scale))
                .setTranslation(new Vector3f(-scale / 2, -scale / 2, -scale / 2));
    }

    @Override
    @ApiStatus.Internal
    protected BlockDisplayBuilder self() {
        return this;
    }
}
