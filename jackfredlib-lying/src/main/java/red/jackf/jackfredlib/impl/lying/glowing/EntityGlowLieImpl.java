package red.jackf.jackfredlib.impl.lying.glowing;

import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import red.jackf.jackfredlib.api.lying.ActiveLie;
import red.jackf.jackfredlib.api.lying.glowing.EntityGlowLie;

public class EntityGlowLieImpl implements EntityGlowLie {
    private final Entity entity;
    private ChatFormatting colour;

    public EntityGlowLieImpl(Entity entity, ChatFormatting colour) {
        this.entity = entity;
        this.colour = colour;
    }

    @Override
    public void fade(ActiveLie<EntityGlowLie> activeLie) {

    }

    @Override
    public void setup(ServerPlayer player) {
        EntityGlowLie.super.setup(player);
    }
}
