package red.jackf.jackfredlib.mixins.lying.entity;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Display.TextDisplay.class)
public interface TextDisplayAccessor {
    @Invoker("setText")
    void jflib$setText(Component text);

    @Invoker("setBackgroundColor")
    void jflib$setBackgroundColor(int colour);

    @Invoker("setTextOpacity")
    void jflib$setTextOpacity(byte opacity);

    @Invoker("setLineWidth")
    void jflib$setLineWidth(int colour);

    @Invoker("setFlags")
    void jflib$setFlags(byte opacity);

    @Invoker("getFlags")
    byte jflib$getFlags();
}
