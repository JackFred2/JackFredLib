package red.jackf.jackfredlib.api.lying.glowing;

import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import red.jackf.jackfredlib.api.lying.Lie;
import red.jackf.jackfredlib.impl.lying.glowing.EntityGlowLieImpl;

public interface EntityGlowLie extends Lie {
    ChatFormatting glowColour();

    void setGlowColour(ChatFormatting colour);

    Entity entity();

    static Builder builder(Entity entity) {
        return new Builder(entity);
    }

    class Builder {
        private final Entity entity;
        private ChatFormatting colour = ChatFormatting.WHITE;
        private @Nullable TickCallback tickCallback = null;
        private @Nullable FadeCallback fadeCallback = null;

        private Builder(Entity entity) {
            this.entity = entity;
        }

        public Builder colour(ChatFormatting colour) {
            this.colour = colour;
            return this;
        }

        public Builder onTick(TickCallback callback) {
            this.tickCallback = callback;
            return this;
        }

        public Builder onFade(FadeCallback callback) {
            this.fadeCallback = callback;
            return this;
        }

        public EntityGlowLie createAndShow(ServerPlayer... players) {
            var lie = new EntityGlowLieImpl(entity, colour, tickCallback, fadeCallback);
            for (ServerPlayer player : players)
                lie.addPlayer(player);
            return lie;
        }
    }

    interface TickCallback {
        void onTick(ServerPlayer player, EntityGlowLie lie);
    }

    interface FadeCallback {
        void onFade(ServerPlayer player, EntityGlowLie lie);
    }
}
