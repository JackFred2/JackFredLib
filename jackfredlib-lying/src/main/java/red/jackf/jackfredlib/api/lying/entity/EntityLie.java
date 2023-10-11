package red.jackf.jackfredlib.api.lying.entity;

import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import red.jackf.jackfredlib.api.lying.Lie;
import red.jackf.jackfredlib.impl.lying.entity.EntityLieImpl;

public interface EntityLie extends Lie {
    @Nullable ChatFormatting glowColour();

    void setGlowColour(@Nullable ChatFormatting colour);

    Entity entity();

    static Builder builder(Entity entity) {
        return new Builder(entity);
    }

    class Builder {
        private final Entity entity;
        private @Nullable ChatFormatting glowColour = null;
        private @Nullable TickCallback tickCallback = null;
        private @Nullable FadeCallback fadeCallback = null;
        private @Nullable LeftClickCallback leftClickCallback = null;
        private @Nullable RightClickCallback rightClickCallback = null;

        private Builder(Entity entity) {
            this.entity = entity;
        }

        public Builder colour(@Nullable ChatFormatting colour) {
            if (colour == null) {
                this.entity.setGlowingTag(false);
                this.glowColour = null;
                return this;
            } else {
                if (!colour.isColor()) colour = ChatFormatting.WHITE;
                this.entity.setGlowingTag(true);
                this.glowColour = colour;
                return this;
            }
        }

        public Builder onTick(TickCallback callback) {
            this.tickCallback = callback;
            return this;
        }

        public Builder onFade(FadeCallback callback) {
            this.fadeCallback = callback;
            return this;
        }

        public Builder onLeftClick(LeftClickCallback callback) {
            this.leftClickCallback = callback;
            return this;
        }

        public Builder onRightClick(RightClickCallback callback) {
            this.rightClickCallback = callback;
            return this;
        }

        public EntityLie createAndShow(ServerPlayer... players) {
            var lie = new EntityLieImpl(entity, glowColour, tickCallback, fadeCallback, leftClickCallback, rightClickCallback);
            for (ServerPlayer player : players)
                lie.addPlayer(player);
            return lie;
        }
    }

    interface TickCallback {
        void onTick(ServerPlayer player, EntityLie lie);
    }

    interface FadeCallback {
        void onFade(ServerPlayer player, EntityLie lie);
    }

    interface LeftClickCallback {
        void onLeftClick(ServerPlayer player, EntityLie lie, boolean wasSneaking, Vec3 relativeToEntity);
    }

    interface RightClickCallback {
        void onRightClick(ServerPlayer player, EntityLie lie, boolean wasSneaking, InteractionHand hand, Vec3 relativeToEntity);
    }
}
