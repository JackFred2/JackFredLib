package red.jackf.jackfredlib.api.lying.glowing;

import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.Entity;
import red.jackf.jackfredlib.api.lying.Lie;
import red.jackf.jackfredlib.impl.lying.glowing.EntityGlowLieImpl;

public interface EntityGlowLie extends Lie<EntityGlowLie> {
    ChatFormatting colour();

    Entity entity();

    static Builder builder(Entity entity) {
        return new Builder(entity);
    }

    class Builder {
        private final Entity entity;
        private ChatFormatting colour = ChatFormatting.WHITE;

        private Builder(Entity entity) {
            this.entity = entity;
        }

        public Builder colour(ChatFormatting colour) {
            this.colour = colour;
            return this;
        }

        public EntityGlowLie build() {
            return new EntityGlowLieImpl(entity, colour);
        }
    }
}
