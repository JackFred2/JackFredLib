package red.jackf.jackfredlib.api.lying.entity;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import red.jackf.jackfredlib.api.lying.ActiveLie;
import red.jackf.jackfredlib.api.lying.Lie;
import red.jackf.jackfredlib.impl.lying.EntityLieImpl;

/**
 * A lie consisting of a fake entity on the player's client. Has functionality for handling left and right clicks, with
 * the latter also
 *
 */
public interface EntityLie extends Lie {
    Entity entity();

    static Builder builder(Entity entity) {
        return new Builder(entity);
    }

    class Builder {
        private final Entity entity;
        private LeftClickCallback leftClickCallback = null;
        private RightClickCallback rightClickCallback = null;

        public Builder(Entity entity) {
            this.entity = entity;
        }

        public Builder onLeftClick(LeftClickCallback callback) {
            this.leftClickCallback = callback;
            return this;
        }

        public Builder onRightClick(RightClickCallback callback) {
            this.rightClickCallback = callback;
            return this;
        }

        public EntityLie build() {
            return new EntityLieImpl(entity, leftClickCallback, rightClickCallback);
        }
    }

    interface LeftClickCallback {
        void onLeftClick(ActiveLie<EntityLie> activeLie, boolean shiftDown, Vec3 relativeToEntity);
    }

    interface RightClickCallback {
        void onRightClick(ActiveLie<EntityLie> activeLie, boolean shiftDown, InteractionHand hand, Vec3 relativeToEntity);
    }

    @ApiStatus.Internal
    void onLeftClick(ActiveLie<EntityLie> activeEntityLie, boolean shiftDown);

    @ApiStatus.Internal
    void onRightClick(ActiveLie<EntityLie> activeEntityLie, boolean shiftDown, InteractionHand hand, Vec3 relativeToEntity);
}
