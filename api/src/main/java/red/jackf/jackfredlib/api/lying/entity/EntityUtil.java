package red.jackf.jackfredlib.api.lying.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Slime;

/**
 * Common utilities for specifically working with entity lies
 */
public class EntityUtil {
    private EntityUtil() {}

    /**
     * Creates a glowing cube at a given position in the world, in the form of an invisible slime with glowing.
     * @param serverLevel Level to create the cube in
     * @param pos Position to create the cube at
     * @return An invisible, glowing slime at the given position
     */
    public static Slime glowingCube(ServerLevel serverLevel, BlockPos pos) {
        var entity = EntityType.SLIME.create(
                serverLevel,
                null,
                null,
                pos,
                MobSpawnType.COMMAND,
                false,
                false
        );
        if (entity != null) {
            entity.setSize(1, false);
            entity.setXRot(0f);
            entity.setYRot(0f);
            entity.setOldPosAndRot();
            entity.setYHeadRot(entity.getYRot());
            entity.setYBodyRot(entity.getYRot());
            entity.setNoGravity(true);
            entity.setNoAi(true);
            entity.moveTo(pos.getCenter().add(0, - entity.getBbHeight() / 2, 0));
            entity.setGlowingTag(true);
            entity.setInvisible(true);
        }
        return entity;
    }
}
