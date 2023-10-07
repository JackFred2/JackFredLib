package red.jackf.jackfredlib.api.lying.glowing;

import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.Entity;
import red.jackf.jackfredlib.api.lying.Lie;
import red.jackf.jackfredlib.impl.lying.glowing.EntityGlowLieImpl;

public interface EntityGlowLie extends Lie<EntityGlowLie> {

    static EntityGlowLie of(Entity entity, ChatFormatting colour) {
        return new EntityGlowLieImpl(entity, colour);
    }
}
