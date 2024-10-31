package red.jackf.jackfredlib.mixins.lying.entity;

import net.minecraft.world.entity.Interaction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Interaction.class)
public interface InteractionAccessor {

    @Invoker("setWidth")
    void jflib$setWidth(float width);

    @Invoker("setHeight")
    void jflib$setHeight(float height);

    @Invoker("setResponse")
    void jflib$setResponse(boolean hasResponse);
}
