package red.jackf.jackfredlib.api.lying.entity;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import red.jackf.jackfredlib.api.lying.ActiveLie;
import red.jackf.jackfredlib.api.lying.Lie;
import red.jackf.jackfredlib.impl.lying.EntityLieImpl;

public interface EntityLie extends Lie {
    Entity entity();

    static Builder builder(Entity entity) {
        return new Builder(entity);
    }

    void onLeftClick(ActiveLie<EntityLie> activeEntityLie);

    void onRightClick(ActiveLie<EntityLie> activeEntityLie, InteractionHand hand);

    void onPositionalRightClick(ActiveLie<EntityLie> activeEntityLie, InteractionHand hand, Vec3 relativeToEntity);

    class Builder {
        private final Entity entity;
        private LeftClickCallback leftClickCallback = null;
        private RightClickCallback rightClickCallback = null;
        private PositionalRightClickCallback positionalRightClickCallback = null;

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

        public Builder onPositionalRightClick(PositionalRightClickCallback callback) {
            this.positionalRightClickCallback = callback;
            return this;
        }

        public EntityLie build() {
            return new EntityLieImpl(entity, leftClickCallback, rightClickCallback, positionalRightClickCallback);
        }
    }

    interface LeftClickCallback {
        void onLeftClick(ActiveLie<EntityLie> activeLie);
    }

    interface RightClickCallback {
        void onRightClick(ActiveLie<EntityLie> activeLie, InteractionHand hand);
    }

    interface PositionalRightClickCallback {
        void onPositionalRightClick(ActiveLie<EntityLie> activeLie, InteractionHand hand, Vec3 relativeToEntity);
    }
}
