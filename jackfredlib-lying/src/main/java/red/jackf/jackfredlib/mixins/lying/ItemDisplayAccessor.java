package red.jackf.jackfredlib.mixins.lying;

import net.minecraft.world.entity.Display;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Display.ItemDisplay.class)
public interface ItemDisplayAccessor {
    @Invoker("setItemStack")
    void callSetItemStack(ItemStack stack);

    @Invoker("setItemTransform")
    void callSetItemTransform(ItemDisplayContext context);
}
